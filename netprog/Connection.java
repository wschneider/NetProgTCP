/*
    Connection.java
    Authors: 
        Will Schneider
        Will Kelly

    Purpose:
        Represents one client connection to the server. Seriously, simple class...
        
    Methods:
        To Be Described Later
*/

package netprog;

import java.io.*;
import java.net.*;
import java.util.*;


public class Connection{
    
    String PROTOCOL;
    String userid;
    InetAddress InetAddr;
    int numMessages;
    Handler handler;
    
    public Connection(String uid, InetAddress ip, Handler h)
    {
        this.InetAddr = ip;
        this.userid = uid;
        this.handler = h;
        this.numMessages = 0;
        this.PROTOCOL = h.PROTOCOL;
    }
    
    public void writeTCP(String msg)
    {
        try
        {
            handler.clientSocket.getOutputStream().write(msg.getBytes());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
}