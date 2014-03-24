/*
    ServerManager.java
    Authors: 
        Will Schneider
        Will Kelly

    Purpose:
        ServerManager sits as the overhead manager of several Servers (one for 
        each specified port in the command line arguments). 
        
    Methods:
        To Be Described Later
*/
package netprog;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerManager
{
    //Singleton representation of the server because reasons
    ServerManager INSTANCE;
    Thread[] POOL;
    
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
        ServerManager a = new ServerManager(ports);
        a.start();
        
    }
    
    /*
    CONSTRUCTOR:
        initializes a pool of threads to each be running a server listening on
        a specified port. It does not start the threads until start() is called.
        This is to delay running in case any more processing need be done.
    */
    public ServerManager(int ports[])
    {
        
        POOL = new Thread[ports.length];
        for(int i=0;i<ports.length;i++)
        {
            POOL[i] = new Thread(new Server(ports[i], this));
        }   
    }
    
    /*
    void start():
        Starts all the threads running the servers
    */
    public void start()
    {
        for(Thread thread : POOL)
        {
            thread.start();
        }
    }
    
}