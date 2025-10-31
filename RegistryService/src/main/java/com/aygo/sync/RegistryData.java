package com.aygo.sync;

import java.sql.Timestamp;

public class RegistryData {
    private String key;
    private Timestamp timestamp;
    
    public RegistryData() {
    }
    
    public RegistryData(String key, Timestamp timestamp) {
        this.key = key;
        this.timestamp = timestamp;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

