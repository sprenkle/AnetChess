/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 *
 * @author david
 */
public class RequestImage1  implements Serializable{
    private final LocalTime createDate = LocalTime.now();
    
    public LocalTime getCreateTime(){
        return createDate;
    }
}
