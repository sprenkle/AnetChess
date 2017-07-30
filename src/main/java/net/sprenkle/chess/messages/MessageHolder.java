/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 * @param <T>
 */
public class MessageHolder <T> implements Serializable {
    private final String jsonObject;
    private final String className;
    private final String id;
    
    public MessageHolder(T object){
        this.className = object.getClass().getName();
        Gson gson = new GsonBuilder().create();
        jsonObject = gson.toJson(object);
        id = UUID.randomUUID().toString();
    }
    
    public T getObject(){
        try {
            Gson gson = new GsonBuilder().create();
            return (T)gson.fromJson(jsonObject, Class.forName(className));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MessageHolder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String getClassName(){
        return className;
    }

    @Override
    public String toString(){
        return jsonObject;
    }
    
    public byte[] toBytes() throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(this);

        return json.getBytes();
    }

    public static MessageHolder fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        Gson gson = new GsonBuilder().create();
        String message = new String(bytes, "UTF-8");
        return gson.fromJson(message, MessageHolder.class);
    }
    
    public String getId(){
        return id;
    }

    public static void main(String arg[]) throws IOException, ClassNotFoundException{
        MessageHolder<String> messageHolder = new MessageHolder<>("Hello World!");
        byte by[] = messageHolder.toBytes();
        MessageHolder<String> m2 = MessageHolder.fromBytes(by);
        String s = m2.getObject();
        System.out.println(s);
        System.out.println(m2.className);
    }
}
