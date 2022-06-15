package com.th6.pojo;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class CustomerPayment  implements Serializable {
    private String customerPaymentId;
    private int customerId;
    private int brandID;
    private int paymentType;
    private  int transactionTypeId;
    private  int useTime;
    private float totalAmount;

    public CustomerPayment(int customerId, int brandID, int paymentType, int transactionTypeId, int useTime, float totalAmount) {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.customerPaymentId = String.valueOf(random.nextInt(100000,999999));
        this.customerId = customerId;
        this.brandID = brandID;
        this.paymentType = paymentType;
        this.transactionTypeId = transactionTypeId;
        this.useTime = useTime;
        this.totalAmount = totalAmount;
    }

    public String getCustomerPaymentId() {
        return customerPaymentId;
    }

    public void setCustomerPaymentId(String customerPaymentId) {
        this.customerPaymentId = customerPaymentId;
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

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "[{" +
                "\"customerPaymentId=\"" + customerPaymentId + '\"' +
                ", \"customerId\":" + customerId +
                ", \"brandID\":" + brandID +
                ", \"paymentType\":" + paymentType +
                ", \"transactionTypeId\":" + transactionTypeId +
                ", \"useTime\":" + useTime +
                ", \"totalAmount\":" + totalAmount +
                "}]";
    }
}
