package com.th6;


import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class OrderPayment {
    private int customerId;
    private int brandID;
    private String saleOrderId;
    private int transactionType;
    private  int transactionId;
    private long payTime;

    @Override
    public String toString() {
        return "[{" +
                "\"customerId\":" + customerId +
                ", \"brandID\":" + brandID +
                ", \"saleOrderId\":\"" + saleOrderId + '\"' +
                ", \"transactionType\":" + transactionType +
                ", \"transactionId\":" + transactionId +
                ", \"payTime\":" + payTime +
                ", \"totalAmount\":" + totalAmount +
                "}]";

    }

    private float totalAmount;

    private OrderPayment(int customerId, int brandID, String saleOrderId, int transactionType, int transactionId, long payTime, float totalAmount) {
        this.customerId = customerId;
        this.brandID = brandID;
        this.saleOrderId = saleOrderId;
        this.transactionType = transactionType;
        this.transactionId = transactionId;
        this.payTime = payTime;
        this.totalAmount = totalAmount;
    }

    public OrderPayment() {
    }

    public static OrderPayment getOne() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int customerId = random.nextInt(1,20);
        int brandID = random.nextInt(1,1000);
        String saleOrderId = String.valueOf(random.nextInt(100000,999999));
        int transactionType = random.nextInt(1,5);
        int transactionId = transactionType == 1 ? random.nextInt(1,11) : -1;
        long payTime = Instant.now().toEpochMilli();
        float totalAmount = Math.round((100000 + random.nextDouble(50000,500000)));
        return new OrderPayment(customerId,brandID,saleOrderId,transactionType,transactionId,payTime,totalAmount);
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getBrandID() {
        return brandID;
    }

    public void setBrandID(int brandID) {
        this.brandID = brandID;
    }

    public String getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(String saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public long getPayTime() {
        return payTime;
    }

    public void setPayTime(long payTime) {
        this.payTime = payTime;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
}
