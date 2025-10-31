package com.aygo.sync;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@CrossOrigin
public class RegistryService {
    
    private final ApiConfiguration apiConfiguration;
    private final AtomicInteger currentApiIndex;
    private final RestClient restClient;
    private final ConcurrentHashMap<String, String> registry;
    
    public RegistryService(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
        this.currentApiIndex = new AtomicInteger(0);
        this.restClient = RestClient.create();
        this.registry = new ConcurrentHashMap<>();
    }
    
    @PostMapping("/registry")
    public String registry(@RequestBody RegistryData registryData) {
        String key = registryData.getKey();
        registry.put(key, key);
        
        System.out.println("Registro guardado: " + key);
        
        String[] apiUrls = apiConfiguration.getApiUrls();
        int index = getNextApiIndex();
        String selectedApi = apiUrls[index];
        
        System.out.println("Round-robin seleccionó API " + (index + 1) + " (" + selectedApi + ")");
        
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("key", key);
            
            String response = restClient.post()
                    .uri(selectedApi)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error forwarding request to API: " + selectedApi, e);
        }
    }
    
    @GetMapping("/registry")
    public ConcurrentHashMap<String, String> getRegistry() {
        return registry;
    }
    
    private int getNextApiIndex() {
        int current = currentApiIndex.get();
        int next = (current + 1) % 3;
        currentApiIndex.compareAndSet(current, next);
        return current;
    }
}
