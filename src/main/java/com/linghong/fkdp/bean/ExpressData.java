package com.linghong.fkdp.bean;

import java.io.Serializable;

/**
 * 快递详情
 */
public class ExpressData implements Serializable {
    private String time;
    private String ftime;
    private String location;
    private String context;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFtime() {
        return ftime;
    }

    public void setFtime(String ftime) {
        this.ftime = ftime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "ExpressData{" +
                "time='" + time + '\'' +
                ", ftime='" + ftime + '\'' +
                ", location='" + location + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
