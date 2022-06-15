package com.th6.util;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;

import java.io.Serializable;

public class HazelCastHelper implements Serializable {

    private static HazelcastInstance instance;
    private static final ClientConfig clientConfig;

    static {
        // java Client
        clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getConnectionStrategyConfig().getConnectionRetryConfig().setMaxBackoffMillis(5000);
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        networkConfig.addAddress("10.1.6.216:5701")
                .setSmartRouting(true)
                //.addOutboundPortDefinition("34700-34710")
                .setRedoOperation(true)
                .setConnectionTimeout(5000);
//        NetworkConfig network = clientConfig.getNetworkConfig();
//        network.setPort(5701).setPortCount(100);
//        network.setPortAutoIncrement(true);
//        JoinConfig join = new clientConfig = new Config();
//        ork.getJoin();
//        join.getMulticastConfig().setEnabled(false);
//        join.getTcpIpConfig()
//                .addMember("localhost").setEnabled(true);

    }

    public static HazelcastInstance getInstance() {
        if (instance == null) {
            instance = HazelcastClient.newHazelcastClient(clientConfig);
        }
        return instance;
    }


}
