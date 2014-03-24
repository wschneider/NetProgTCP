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
    ServerManager manager;
    ServerSocket serverSocket;
    Socket clientSocket;
    /*
    CONSTRUCTOR:
        Initializes the server-side socket connection. Literally nothing else.
    */
    public Server(int port, ServerManager top)
    {
        this.manager = top;
        
        try
        {
            this.serverSocket = new ServerSocket( port );
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
        while(true)
        {
           try
            {
                this.clientSocket = serverSocket.accept();
                
            }
            catch(IOException e)
            {
                System.err.println(e);
            }
           
           new Thread(new Handler(this)).start();
        }
        
        
        
    }
    
    
    
    
}