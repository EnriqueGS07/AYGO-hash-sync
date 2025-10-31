package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private SimpleChat simpleChat;

    @PostMapping("/registry")
    public ResponseEntity<String> addMessage(@RequestBody MessageRequest request) {
        try {
            simpleChat.addValue(request.getKey());
            return ResponseEntity.ok("Message added successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/state")
    public ResponseEntity<HashMap<String, Timestamp>> getState() {
        return ResponseEntity.ok(simpleChat.getCurrentState());
    }

    public static class MessageRequest {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}

