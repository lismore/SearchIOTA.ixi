package com.searchiota.ixi.model;

//IOTA Packages
import com.iota.curl.IotaCurlHash;
import org.iota.ict.model.Transaction;
import org.iota.ict.model.TransactionBuilder;
import org.iota.ict.utils.Trytes;

//Search IOTA 
import com.searchiota.ixi.SearchIotaIxi;
import com.searchiota.ixi.utils.RSA;
import com.searchiota.ixi.utils.KeyPair;

//External Packages
import org.json.JSONException;
import org.json.JSONObject;

//Java packages
import java.util.Set;

public class Submission {

	// long variables
	public final long timestamp;
	
	// string variables
    public final String username;
	public final IotaNode mynode;
	
	public String serviceprovider;
	public String serviceProviderUrl;
	public String serviceNodeUrl;
	public String serviceNodePort;
	public String serviceName;
    public String serviceDescription;
	public String servicePrice;
    public String serviceType;
	public String serviceKeywords;
	
	public final String channel;
    public final String userid;
    public final String signature;
    public final String publicKey;
	
	//boolean variables 
    public final boolean isTrusted;
    public final boolean isOwn;
	
	public Submission(){
		
		timestamp = 0;
        username = "";
        userid = "";
		mynode = new IotaNode();
        serviceprovider = "";
        serviceproviderurl = "";
		servicenodeurl = "";
		servicenodeport = "";
		servicename = "";
		servicedescription = "";
		serviceprice = "";
		servicetype = "";
		servicekeywords = "";
		servicekeywords = "";
		channel = "";
        publicKey = "";
        signature = "";
        isTrusted = false;
        isOwn = false;
		
	}
	
	 public Submission(Transaction transaction, Set<String> contacts, String ownUserid) throws JSONException, RSA.RSAException {
        
		final JSONObject jsonObject = new JSONObject(transaction.decodedSignatureFragments);
        
			timestamp = transaction.issuanceTimestamp;
        
		username = jsonObject.getString(Fields.username.name());
		
			SearchIotaIxi.validateUsername(username);
		
        serviceprovider = jsonObject.getString(Fields.mynode.serviceprovider.name());
		serviceproviderurl = jsonObject.getString(Fields.mynode.serviceproviderurl.name());
		servicenodeurl = jsonObject.getString(Fields.mynode.servicenodeurl.name());
		servicenodeport = jsonObject.getString(Fields.mynode.servicenodeport.name());
		servicename = jsonObject.getString(Fields.mynode.servicename.name());
		servicedescription = jsonObject.getString(Fields.mynode.servicedescription.name());
		serviceprice = jsonObject.getString(Fields.mynode.serviceprice.name());
		servicetype = jsonObject.getString(Fields.mynode.servicetype.name());
		servicekeywords = jsonObject.getString(Fields.mynode.servicekeywords.name());
		
			channel = transaction.address;
		
        publicKey = jsonObject.getString(Fields.public_key.name());
		
			userid = createUserid(publicKey);
		
        signature = jsonObject.getString(Fields.signature.name());
		
			RSA.verify(getSignedData(), signature, KeyPair.publicKeyFromString(publicKey));
		
			isTrusted = contacts.contains(userid);
		
			isOwn = userid.equals(ownUserid);
		
    }
	
	Message(MessageBuilder builder) {
		
        timestamp = System.currentTimeMillis();
		
        username = builder.username;
        channel = builder.channel;
		
		serviceprovider = builder.mynode.serviceprovider;
		serviceproviderurl = builder.mynode.serviceproviderurl;
		servicenodeurl = builder.mynode.servicenodeurl;
		servicenodeport = builder.mynode.servicenodeport;
		servicename = builder.mynode.servicename;
		servicedescription = builder.mynode.servicedescription;
		serviceprice = builder.mynode.serviceprice;
		servicetype = builder.mynode.servicetype;
		servicekeywords = builder.mynode.servicekeywords;
		
        publicKey = builder.keyPair.getPublicAsString();
		
        userid = createUserid(publicKey);
		
        try {
            signature = RSA.sign(getSignedData(), builder.keyPair.privateKey);
        } catch (RSA.RSAException e) {
            throw new RuntimeException(e);
        }
        isOwn = true;
        isTrusted = true;
    }
	
	public static String createUserid(String publicKey) {
		
        String publicKeyTrytes = Trytes.fromAscii(publicKey);
        String publicKeyHash = IotaCurlHash.iotaCurlHash(publicKeyTrytes, publicKeyTrytes.length(), 123);
		
        return publicKeyHash.substring(0, 8);
		
    }
	
	private String getSignedData() {
        return username+mynode.toString()+channel;
    }
	
	public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Fields.timestamp.name(), timestamp);
        jsonObject.put(Fields.username.name(), username);
        jsonObject.put(Fields.user_id.name(), userid);
        jsonObject.put(Fields.mynode.name(), mynode);
        jsonObject.put(Fields.channel.name(), channel);
        jsonObject.put(Fields.is_trusted.name(), isTrusted);
        jsonObject.put(Fields.is_own.name(), isOwn);
        return jsonObject;
    }

	
	public Transaction toTransaction() {
		
        JSONObject jsonObject = new JSONObject();
		
        jsonObject.put(Fields.username.name(), username);
        jsonObject.put(Fields.mynode.name(), mynode);
        jsonObject.put(Fields.signature.name(), signature);
        jsonObject.put(Fields.public_key.name(), publicKey);

        TransactionBuilder builder = new TransactionBuilder();
		
        builder.address = channel;
		
        if(jsonObject.toString().length() > Transaction.Field.SIGNATURE_FRAGMENTS.tryteLength / 3 * 2) {
            System.err.println("Message to long, doesn't fit into transaction.");
            return null;
        }
		
        builder.asciiMessage(jsonObject.toString());
		
        builder.tag = SearchIotaIxi.calcLifeSignTag(System.currentTimeMillis());
		
        return builder.build();
    }
	
	 private enum Fields {
        username, user_id, timestamp,mynode,channel, public_key, signature, is_trusted, is_own
    }
}