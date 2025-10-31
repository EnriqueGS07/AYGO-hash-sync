package com.aygo.sync;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiConfiguration {
    
    @Value("${api.backend1.url}")
    private String backend1Url;
    
    @Value("${api.backend2.url}")
    private String backend2Url;
    
    @Value("${api.backend3.url}")
    private String backend3Url;
    
    public String[] getApiUrls() {
        return new String[]{backend1Url, backend2Url, backend3Url};
    }
}

