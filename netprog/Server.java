/*
    Server.java
    Authors: 
        Will Schneider
        Will Kelly

    Purpose:
        Chat Server receives and handles TCP/UDP connections from clients. 
        
    Methods:
        To Be Described Later
*/
package netprog;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable{
    int port;
    ServerManager manager;
    ServerSocket TCPSocket;
    DatagramSocket UDPSocket;
    //Socket clientSocket;
    /*
    CONSTRUCTOR:
        Initializes the server-side socket connection. Literally nothing else.
    */
    public Server(int port, ServerManager top)
    {
        this.manager = top;
        this.port = port;
        try
        {
            this.TCPSocket = new ServerSocket(port);
           
            this.UDPSocket = new DatagramSocket(port);
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
    }
    
    /*
    void run:
        This server is called upon from the top-level ServerManager in its own 
        thread (so it implements Runnable). The run function therefor does the 
        bulk of the server work, itself popping off a new thread for each 
        connection
    */
    public void run()
    {
        System.out.println("Server Running on port " + this.port);
        
        while(true)
        {
           Socket clientSocket;
           try
            {
                clientSocket = TCPSocket.accept();
                new Thread(new Handler(this, clientSocket, "TCP")).start();
            }
            catch(IOException e)
            {
                System.err.println(e);
            }
           
        }
        
        
        
    }
    
    
    
    
}