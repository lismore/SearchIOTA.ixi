# SearchIOTA.ixi

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

SearchIOTA.ixi is an IOTA eXtension Interface module to integrate IOTA data providers nodes with SearchIOTA so data service consumers can find them and transact.

  - Automate the submission of your IOTA Data Service node to the SearchIOTA search engine
  - Regularly update in real time
  - Automate payment with MIOTA

# New Features!

  - Initial Proof of Concept

### Tech

SearchIOTA.ixi uses a number of open source projects to work properly:

* [IOTA ixi] -  IOTA Extension Interface https://github.com/iotaledger/ixi 
* [Java] - Java

### Installation

### Step 1: Install and Run Ict

Please find instructions on [iotaledger/ict](https://github.com/iotaledger/ict#installation). There are a few
community made resources for the Ict installation. Use them at your own risk:
* [IOTA Omega-Ict tutorial: noob edition](https://medium.com/@lambtho/iota-omega-ict-tutorial-noob-edition-ff9e1e6d6c2f) (Guide) by Lambtho
* [ict-install](https://github.com/phschaeff/ict-install) (Script) by phschaeff

Make sure you are connected to the main network and not to an island, otherwise you won't be able to message anyone in the main network.

### Step 2: Install SearchIota.ixi

There are two ways to do this:

#### Simple Method

Go to [releases](https://github.com/lismorer/SearchIota.ixi/releases) and download the **searchiota.ixi-{VERSION}.zip**
from the most recent release. Unzip its content into any directory.

#### Advanced Method

You can also build the .jar file from the source code yourself. You will need **Git** and **Gradle**.

```shell
# download the source code from github to your local machine
git clone https://github.com/lismore/searchiota.ixi
# if you don't have git, you can also do this instead:
#   wget https://github.com/lismore/searchiota.ixi/archive/master.zip
#   unzip master.zip

# change into the just created local copy of the repository
cd searchiota.ixi

# build the searchiota.ixi-{VERSION}.jar file
gradle fatJar
```

### Step 3: Run SearchIota.ixi

```shell
# Please replace {ICT} with the name of your Ict. You can find it in your ict.cfg file. The default setting is 'ict'.
# Set 'ixi_enabled=true' in your ict.cfg configuration file.
java -jar searchiota.ixi-{VERSION}.jar {servicename} {servicedescription} {serviceprovider} {serviceproviderurl} {servicenodeurl} {servicenodeport} {serviceprice} {servicetype} {servicekeywords}
# EXAMPLE: java -jar searchiota.ixi-1.2.3.jar servicename servicedescription serviceprovider serviceproviderurl servicenodeurl servicenodeport serviceprice servicetype servicekeywords
```

### Step 4: Open the Web GUI

Open web/index.html in your web browser. If you are running Ict locally, it should immediately connect you. If no SearchIOTA.ixi user interface is running on localhost, it will ask you for the ip address of your Ict node.

## Disclaimer

While having the SearchIOTA.ixi web GUI open, a heartbeat will be submitted to the network in regular
time intervals, so that other users can see that you are online.

We are not responsible for any damage caused by running this software. Please use it at your own risk.

### Development

Want to contribute? Great!

Fork, submit a pull request

License
----

Apache 2.0
