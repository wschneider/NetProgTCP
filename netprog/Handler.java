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
    String PROTOCOL;
    Server server;
    Socket clientSocket;
    
    /*
    TODO: IMPLEMENT CONSTRUCTOR
    */
    public Handler(Server top, Socket clientSocket, String protocol)
    {
        this.server = top;
        this.clientSocket = clientSocket;
        this.PROTOCOL = protocol;
    }
    
    /*
    TODO: IMPLEMENT VOID RUN
    */
    public void run()
    {
        System.out.println("RECEIVED CONNECTION WITH " + this.PROTOCOL);
        try{
            if(PROTOCOL.toUpperCase() == "TCP")
            {
                System.out.println("TCP");
                this.handleTCP();
            }
            else if(PROTOCOL.toUpperCase() == "UDP")
            {
                System.out.println("UDP");    
                this.handleUDP();
            }
            else
            {
                System.out.println("ERROR?");
            }
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
    }
    
    public void handleTCP() throws IOException
    {
        // Should theoretically loop indefinitely. 
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        while(clientSocket.isConnected())
        {
            try
            {
                Thread.sleep(2000);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            System.out.println("Waiting for Input");

            System.out.println("Received input:" + input.readLine());

        }
        ;
    }
    
    public void handleUDP()
    {
        ;
    }
    
    
}