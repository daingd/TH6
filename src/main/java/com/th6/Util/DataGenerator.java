package com.th6;

import java.io.Serializable;

public class DataGenerator  implements Serializable {

    private  boolean isRunning;

    public DataGenerator() {
        this.isRunning = true;
    }
    public OrderPayment next() {
        return OrderPayment.getOne();
    }

    public boolean isRunning() {
        return isRunning;
    }
    public void stop() {
        this.isRunning = false;
    }
}
