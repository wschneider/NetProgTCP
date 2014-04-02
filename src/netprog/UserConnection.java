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


public class UserConnection{
    
    String PROTOCOL;
    String userid;
    InetAddress InetAddr;
    int numMessages;
    Handler handler;
    int port;
    
    public UserConnection(String uid, Handler h)
    {
        this.InetAddr = h.getIP();
        this.userid = uid;
        this.handler = h;
        this.numMessages = 0;
        this.PROTOCOL = h.PROTOCOL;
        this.port = h.getPort();
    }
    
    public void write(String msg)
    {
        if(this.PROTOCOL.equals("TCP"))
        {
            this.writeTCP(msg);
        }
        else if(this.PROTOCOL.equals("UDP"))
        {
            this.writeUDP(msg);
        }
        else
        {

        }

    }

    private void writeUDP(String msg)
    {
        //The handler for the UDP connection cannot be assumed to be valid after one
        //  message has been sent, so we'll just create a new connection. 
        try
        {        
            DatagramSocket outSock = new DatagramSocket();

            outSock.connect(this.InetAddr, this.port);  
            outSock.send( new DatagramPacket (msg.getBytes(), msg.length()) );
        }
        catch( IOException e)
        {
            System.err.println("Failed to write message due to IOException");
        }    
    }


    private void writeTCP(String msg)
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
