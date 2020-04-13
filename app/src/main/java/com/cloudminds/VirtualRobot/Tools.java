package com.cloudminds.VirtualRobot;

public class Tools {
    public  static  String getJsonFromTTSInfo(String[] fileURLListIn, String moodIn, String[] textListIn,int timeoutIn,boolean isShowTxtIn){
        String ret="{\"URLList\":[\"";
        int idx = 0;
        String isShowTxtStr = isShowTxtIn ? "true" : "false";
        for(String temp : fileURLListIn)
        {
            ret += temp;
            ret += "\"";
            if(idx< (fileURLListIn.length-1)) ret += ",\"";
            idx++;
        }
        ret += "],\"mood\":\"";
        ret += moodIn;
        ret += "\",\"isShowTxt\":";
        ret += isShowTxtStr;
        ret += ",\"timeout\":";
        ret += timeoutIn;
        ret += ",\"txtList\":[\"";
        idx = 0;
        for(String temp : textListIn)
        {
            ret += temp;
            ret += "\"";
            if (idx < (textListIn.length - 1)) ret += ",\"";
            idx++;
        }
        ret += "]}";
        return ret;
    }

    public static String getJsonFromAvatarInfo(String urlIn, int timeoutIn)
    {
        String ret = "{\"avatarurl\":\""+ urlIn + "\",\"timeout\":"+ timeoutIn + "}";

        return ret;
    }
}
