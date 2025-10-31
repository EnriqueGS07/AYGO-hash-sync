package org.example;

import org.jgroups.*;
import org.jgroups.util.Util;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;

@Service
public class SimpleChat implements Receiver {
    JChannel channel;
    final HashMap<String, Timestamp> state = new HashMap<>();

    public SimpleChat() throws Exception {
        channel=new JChannel().setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
    }

    public void addValue(String key) throws Exception {
        Timestamp timestamp = Timestamp.from(Instant.now());
        HashMap<String, Timestamp> dataMap = new HashMap<>();
        dataMap.put(key, timestamp);
        Message msg = new ObjectMessage(null, dataMap);
        channel.send(msg);
    }

    public HashMap<String, Timestamp> getCurrentState() {
            return new HashMap<>(state);
    }

    private void printState() {
        System.out.println("HashMap: " + state);
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    @Override
    public void receive(Message msg) {
        HashMap<String, Timestamp> receivedMap = (HashMap<String, Timestamp>) msg.getObject();
        synchronized(state) {
            state.putAll(receivedMap);
            printState();
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        HashMap<String, Timestamp> receivedState = (HashMap<String, Timestamp>) Util.objectFromStream(new DataInputStream(input));
        synchronized(state) {
            state.clear();
            state.putAll(receivedState);
            printState();
        }
    }

}
