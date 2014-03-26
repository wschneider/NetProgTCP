/*
    Handler.java
    Authors: 
        Will Schneider
        Will Kelly

    Purpose:
        a Handler is the primary processing unit after popping off the server 
        threads
        
    Methods:
        To Be Described Later
*/
package netprog;
import java.io.*;
import java.net.*;
import java.util.*;

public class Handler implements Runnable
{
    Server server;
    Socket clientSocket;
    
    /*
    TODO: IMPLEMENT CONSTRUCTOR
    */
    public Handler(Server top, Socket clientSocket)
    {
        this.server = top;
        this.clientSocket = clientSocket;
    }
    
    /*
    TODO: IMPLEMENT VOID RUN
    */
    public void run()
    {
        System.out.println("RECEIVED CONNECTION");
    }
    
    
}