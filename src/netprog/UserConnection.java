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
    Handler handler; //DOES NOT PERSIST FOR UDP CONNECTIONS
    int port;
    
    /*
        CONSTRUCTOR:
            initializes the class-local variables. 
            It is important to note that while these variables are the same for
            TCP and UDP, they are used in entirely different ways. 
            The Handler WILL NOT persist for UDP connections, and instead a 
            DatagramSocket must be used for message transmission
    */
    public UserConnection(String uid, Handler h)
    {
        this.InetAddr = h.getIP();
        this.userid = uid;
        this.handler = h;
        this.numMessages = 0;
        this.PROTOCOL = h.PROTOCOL;
        this.port = h.getPort();
    }
    
    /*
        void write():
            Driver function for general-purpose writing. Calls the appropriate
            write function depending on protocol. 
    */
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
            //error?
        }
    }

    /*
        int increment():
            increments the number of messages associated with this user, and
            returns the total number of messages they have sent. 
    */
    public int increment()
    {
        this.numMessages += 1;
        return this.numMessages;
    }

    /*
        void writeUDP():
            Writes a message to a UDP connected user
            
            Operation:
                (1) Confirms appropriate message length
                (2) Opens a DatagramSocket
                (3) Writes to the Socket
    */
    private void writeUDP(String msg)
    {
        if(msg.length() > 105)
        {
            System.err.println("Cannot write this message, It is too long");
            return;
        }
        
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

    /*
        void writeTCP():
            Writes a message to a TCP connected user. 
            Handlers persist on TCP, so this is as easy as grabbing the 
            handler's socket
    */
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
