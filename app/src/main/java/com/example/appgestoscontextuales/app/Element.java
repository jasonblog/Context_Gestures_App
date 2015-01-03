package com.example.appgestoscontextuales.app;

public class Element {
    private String title;
    private String subtitle;
    private String ip;
    private String bssid;
    private String rssi;
    private String mac;

    public Element(String t, String s, String i, String b, String r, String m){
        this.title=t;
        this.subtitle=s;
        this.ip=i;
        this.bssid=b;
        this.rssi=r;
        this.mac=m;
    }

    public String getTitle(){
        return this.title;
    }

    public String getSubtitle(){
        return this.subtitle;
    }

    public String getIp(){
        return this.ip;
    }

    public String getBssid(){
        return this.bssid;
    }

    public String getRssi(){
        return this.rssi;
    }

    public String getMac(){
        return this.mac;
    }

}
