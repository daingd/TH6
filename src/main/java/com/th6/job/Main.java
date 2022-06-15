package com.th6.job;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.th6.pojo.CustomerPayment;
import com.th6.util.HazelCastHelper;
import scala.Tuple2;

import java.util.Map;


public class Main {
    public static void main(String[] args) throws Exception {
        HazelcastInstance instance = HazelCastHelper.getInstance();
        IMap<Tuple2<Integer,Integer>,CustomerPayment> customerPayment = instance.getMap("customerPayment");
        for (Map.Entry<Tuple2<Integer, Integer>, CustomerPayment> cp : customerPayment.entrySet()){
                    System.out.println(cp.getValue());
        }

    }
}
