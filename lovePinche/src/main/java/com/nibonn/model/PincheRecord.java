package com.nibonn.model;

import java.math.BigDecimal;

/**
 * Created by GuYifan on 2014/5/1.
 */
public class PincheRecord {

    private String otherUser;
    private String startAddress;
    private String arriveAddress;
    private String startTime;
    private String endTime;
    private BigDecimal money;

    public String getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(String otherUser) {
        this.otherUser = otherUser;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getArriveAddress() {
        return arriveAddress;
    }

    public void setArriveAddress(String arriveAddress) {
        this.arriveAddress = arriveAddress;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        // TODO format
        return "PincheRecord{" +
                "otherUser=" + otherUser +
                ", startAddress='" + startAddress + '\'' +
                ", arriveAddress='" + arriveAddress + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", money=" + money +
                '}';
    }
}
