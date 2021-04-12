/**
* Message class for encoding and decoding messages between the server and client
*
* @author  Lufuno Tshishivhiri
* @version 1.0
* @since   02/04/2021 
*/
package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Message {
    
    private User user;
    private boolean command;
    private String text;
    private String destination;

    public Message(User user, boolean command, String text, String dest){
        this.user = user;
        this.command = command;
        this.text = text;
        this.destination = dest;
    }

    public Message(String encoded){
        parseString(encoded);
    }

    public String getString(){
        return user.getPort()+"~&~"+user.getAddress()+"~&~"+ user.getUsername() + "~&~"+ command +"~&~" + text + "~&~" + destination;
    }

    public void parseString(String message){
        String [] data = message.split("~&~");
        try {
            user = new User(Integer.parseInt(data[0]),data[1],data[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        command = Boolean.parseBoolean(data[3]);
        text = data[4];
        destination = data[5];
    }

    public User getUser(){
        return user;
    }

    public String getText(){
        return text;
    }

    public boolean getCommand(){
        return command;
    }

    public String getDestination(){
        return destination;
    }
}
