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

/**
 *
 * @author david
 */
public class MessageHolder implements Serializable {
    private final String jsonObject;
    private final String className;
    
    public MessageHolder(String className, Object object){
        this.className = className;
        Gson gson = new GsonBuilder().create();
        jsonObject = gson.toJson(object);
    }
    
    public Object getObject(Class className){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonObject, className);
    }

    public String toString(){
        return jsonObject;
    }
    
    public String getClassName(){
        return this.className;
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

    public static void main(String arg[]) throws IOException, ClassNotFoundException{
        System.out.println(String.class.getTypeName());
        MessageHolder messageHolder = new MessageHolder("mystring", "object");
        byte by[] = messageHolder.toBytes();
        MessageHolder m2 = MessageHolder.fromBytes(by);
        System.out.println(m2.className);
    }
}
