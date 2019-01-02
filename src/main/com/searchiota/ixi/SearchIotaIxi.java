package com.searchiota.ixi;

//IOTA Packages
import org.iota.ict.ixi.IxiModule;
import org.iota.ict.network.event.GossipFilter;
import org.iota.ict.network.event.GossipReceiveEvent;
import org.iota.ict.network.event.GossipSubmitEvent;

public class SearchIotaIxi extends IxiModule {
	
	public static final String NAME = "SearchIota.ixi";
	private final String userid;
	private final org.iota.ixi.utils.KeyPair keyPair;
	private GossipFilter gossipFilter = new GossipFilter();

    public static void main(String[] args) {
        
		try {
            
			new SearchIotaIxi(args.length >= 1 ? args[0] : "ict");
			
        } catch (RuntimeException e) {
            
			if(e.getCause() instanceof NotBoundException) {
                System.err.println("Failed to connect to Ict: " + e.getMessage());
                System.err.println();
                System.err.println("Please make sure that:");
                System.err.println("    - your Ict is running");
                System.err.println("    - the name of your Ict is '"+ictName+"'");
                System.err.println("    - you set ixi_enabled=true in your ict.cfg");
                System.err.println("    - SearchIota.ixi runs on the same device as your Ict");
                System.err.println();
            }
            e.printStackTrace();
        }
    }

    public SearchIotaIxi(String ictName) {
        super(NAME, ictName);
        
		System.out.println(NAME + " started");
		
        setGossipFilter(new GossipFilter().setWatchingAll(true));
        
		System.out.println("submitting message ...");
        
		this.keyPair = KeyManager.loadKeyPair();
		this.userid = Message.generateUserid(keyPair.getPublicAsString());
		
		//init();
		
		submit("Hello SearchIOTA - Please add me to your search engine!");
		
		//submitMessage("", new IotaNode());
    }
	
	public void init() {

        before((Filter) (request, response) -> {
            //String queryPassword = request.queryParams("password");

           // if (queryPassword == null || !queryPassword.equals(password)) {
           //     halt(401, "Access denied: password incorrect.");
           // }
        });

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        post("/init", (request, response) -> {
            synchronized(this) {
                try { historySize = Integer.parseInt(request.queryParams("history_size")); } catch (Throwable t) { ; }
                messages.add(new Message());
                for(String channel : channelNames) {
                    String channelAddress = deriveChannelAddressFromName(channel);
                    gossipFilter.watchAddress(channelAddress);
                    pullChannelHistory(channelAddress);
                }
                setGossipFilter(gossipFilter);
            }
            
			Thread.sleep(100);
            return new JSONArray(channelNames).toString();
        });

        post("/addContact/:userid", (request, response) -> {
            contacts.add(request.params(":userid"));
            storeContacts();
            return "";
        });

        post("/removeContact/:userid", (request, response) -> {
            contacts.remove(request.params(":userid"));
            storeContacts();
            return "";
        });

        post("/removeChannel/", (request, response) -> {

            synchronized (this) { // synchronized necessary for correct order of setGossipFilter()
                String channelName = request.queryParams("name");
                String channelAddress = deriveChannelAddressFromName(channelName);

                Set<String> channelNamesToRemove = new HashSet<>();
                for(String c : channelNames)
                    if(deriveChannelAddressFromName(c).equals(channelAddress))
                        channelNamesToRemove.add(c);
                channelNames.removeAll(channelNamesToRemove);
                storeChannels();

                gossipFilter.unwatchAddress(channelAddress);
                setGossipFilter(gossipFilter);
                return "";
            }
        });

        post("/addChannel/", (request, response) -> {
            // Due to the delay of setGossipFilter(), it is important to ensure that setGossipFilter() is called in the correct order.
            // Otherwise the newest GossipFilter with N channels might be replaced by an older GossipFilter with L channels (L<N).
            // This would result in missing channels. The synchronized block ensures the correct order.

            synchronized (this) { // synchronized necessary for correct order of setGossipFilter()
                String channelName = request.queryParams("name");
                channelNames.add(channelName);
                storeChannels();

                String channelAddress = deriveChannelAddressFromName(channelName);
                gossipFilter.watchAddress(channelAddress);
                setGossipFilter(gossipFilter);
                pullChannelHistory(channelAddress);
                return "";
            }
        });

        post("/getMessage/", (request, response) -> {
            JSONArray array = new JSONArray();
            synchronized (messages) {
                do {
                    array.put(messages.take().toJSON());
                }
                while (!messages.isEmpty() && array.length() < 100);
            }
            return array.toString();
        });

        post("/getOnlineUsers", (request, response) -> {
            return getOnlineUsers().toString();
        });

        post("/submitMessage/:channel/", (request, response) -> {
            String channel = request.params(":channel");
            String message = request.queryParams("message");
            submitMessage(channel, message);
            return "";
        });
    }
	
	public static String calcLifeSignTag(long unixMs) {
        long minuteIndex = unixMs/1000/160;
        String prefix = "SEARCHIOTA9";
        return prefix + Trytes.fromNumber(BigInteger.valueOf(minuteIndex), Transaction.Field.TAG.tryteLength - prefix.length());
    }
	
	 private void submitMessage(String channel, IotaNode mynode) {
        Submission toSend = createSubmission(channel, mynode);
        Transaction transaction = toSend.toTransaction();
        if(transaction != null)
            submit(transaction);
    }

    private Submission createSubmission(String channel, IotaNode mynode) {
        SubmissionBuilder builder = new SubmissionBuilder();
        builder.keyPair = keyPair;
        builder.username = username;
        builder.mynode = mynode;
        builder.channel = channel;
        return builder.build();
    }

    @Override
    public void onTransactionReceived(GossipReceiveEvent event) {
        System.out.println("Received message:  " + event.getTransaction().decodedSignatureFragments);
    }

    @Override
    public void onTransactionSubmitted(GossipSubmitEvent event) {
        System.out.println("Submitted message: " + event.getTransaction().decodedSignatureFragments);
    }
	
}