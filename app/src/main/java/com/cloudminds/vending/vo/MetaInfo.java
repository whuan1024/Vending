package com.cloudminds.vending.vo;

public class MetaInfo {

    private String rcuCode;
    private String eventId;

    public String getRcuCode() {
        return rcuCode;
    }

    public void setRcuCode(String rcuCode) {
        this.rcuCode = rcuCode;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "MetaInfo{" +
                "rcuCode='" + rcuCode + '\'' +
                ", eventId='" + eventId + '\'' +
                '}';
    }
}
