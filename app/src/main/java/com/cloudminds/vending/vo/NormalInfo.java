package com.cloudminds.vending.vo;

public class NormalInfo {

    private String rcuCode;
    private String eventId;
    private String monitorFile;

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

    public String getMonitorFile() {
        return monitorFile;
    }

    public void setMonitorFile(String monitorFile) {
        this.monitorFile = monitorFile;
    }

    @Override
    public String toString() {
        return "NormalInfo{" +
                "rcuCode='" + rcuCode + '\'' +
                ", eventId='" + eventId + '\'' +
                ", monitorFile='" + monitorFile + '\'' +
                '}';
    }
}
