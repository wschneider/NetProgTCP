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

import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{
    //Singleton representation of the server because reasons
    Server INSTANCE;
    ServerSocket[] Sockets;
    
    /*
        void main:
            (1) Converts arguments to strings
            (2) Creates an instance of the server
            (3) Runs the server
    */
    public static void main(String args[])
    {
        /*
            Args are the ports the server is going to listen on... 
            Those really should be ints, shouldn't they...
        */
        int[] ports = new int[args.length];
        
        for(int i = 0;i < args.length;i++)
        {
            ports[i] = Integer.parseInt(args[i]);
        }
        
        /*
        TODO: CONTINUE MAIN FUNCTION.
        */
        
    }
    
    /*
        Server:
            (1) If an instance of the server already exists, quit.
            (2) Create one ServerSocket for each port specified, place in 
                global array
            (3) Set global instance to refer to first initialiaze server.
    */
    public Server(int ports[])
    {
        if(INSTANCE != null)
        {
            return;
        }
        System.out.println("Server started at " + new Date() );
        
        try
        {
            Sockets = new ServerSocket[ports.length];
            for(int i = 0;i < Sockets.length;i++)
            {
                Sockets[i] = new ServerSocket( ports[i] );
            }
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
        
        System.out.println("DEBUG: Server Setup Completed");
    
        INSTANCE = this;
    }
    
    
    /*
    TODO:
        void run:
            (1) For each ServerSocket, create a new thread to handle one port's 
                connections. 
            (2) For a ServerSocket, block that port on accept
            (3) Handle a connection to that port (consider another layer of 
                threading?)
            (4) Terminate, ever?
    */
    public void run()
    {
        ;
    }
    
}