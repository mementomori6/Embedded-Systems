package com.nibonn.model;

/**
 * Created by GuYifan on 2014/5/1.
 */
public class PincheRecord {

    private String otherUser;
    private String startAddress;
    private String arriveAddress;
    private String startTime;
    private String endTime;
    private String otherUserId;

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

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

    public void fromMatchResult(MatchResult result) {
        setOtherUserId(result.getUserid());
        setStartAddress(result.getSrc());
        setStartTime(result.getTime());
        setArriveAddress(result.getDes());
        setEndTime(result.getLasttime());
    }

    public void fromSubmitResult(SubmitResult result) {
        setStartAddress(result.getSrc());
        setArriveAddress(result.getDes());
        setStartTime(result.getTime());
        setEndTime(result.getLasttime());
    }

    @Override
    public String toString() {
        return String.format("%s到%s", startAddress, arriveAddress);
    }

    public String info() {
        return String.format("用户：%s\n起点：%s\n终点：%s\n开始时间：%s\n结束时间：%s",
                otherUserId, startAddress, arriveAddress, startTime, endTime);
    }
}
