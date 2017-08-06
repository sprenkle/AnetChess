/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.chess.messages.RabbitConfigurationInterface;

/**
 *
 * @author david
 */
public class RabbitConfiguration implements RabbitConfigurationInterface{
    private final String server;
    private final String user;
    private final String password;
    
    public RabbitConfiguration() throws FileNotFoundException, IOException{
            Properties prop = new Properties();
            InputStream input = null;
            
            input = new FileInputStream("rabbitMq.properties");
            // load a properties file
            prop.load(input);
            server = prop.getProperty("server");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}
