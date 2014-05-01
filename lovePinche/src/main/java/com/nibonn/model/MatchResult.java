package com.nibonn.model;

/**
 * Created by GuYifan on 2014/5/1.
 */
public class MatchResult {
    private String requestid;
    private String userid;
    private String src;
    private String srcid;
    private String des;
    private String desid;
    private String distance;
    private String time;
    private String lasttime;
    private String peoplenumber;

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSrcid() {
        return srcid;
    }

    public void setSrcid(String srcid) {
        this.srcid = srcid;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getDesid() {
        return desid;
    }

    public void setDesid(String desid) {
        this.desid = desid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getPeoplenumber() {
        return peoplenumber;
    }

    public void setPeoplenumber(String peoplenumber) {
        this.peoplenumber = peoplenumber;
    }
}
