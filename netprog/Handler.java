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
                //System.out.println("TCP");
                this.handleTCP();
            }
            else if(PROTOCOL.toUpperCase() == "UDP")
            {
                //System.out.println("UDP");    
                this.handleUDP();
            }
            else
            {
                //System.out.println("ERROR?");
            }
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
    }
    
    /*
    void handleTCP:
        Handles the server-side functionality for a client connecting via TCP.
        
        The Function operates as such:
            (1) Open the input stream from the clientSocket
            (2) Loop indefinitely (Until the client terminates):
                (3) Read an input line from the client
                (4) Depending on the content of the first line, keep reading to fit
                        protocol
                (5) Send request to server, wait for reply
                (6) Write reply to client. 
    */
    public void handleTCP() throws IOException
    {
        /*
        (1): Open input stream from Client
        */
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
     
        /*
        (2): Loop Until Client terminates
        */
        while(clientSocket.isConnected())
        {
            /*
            (3): Read input line
            */
            String c = input.readLine();
            String requestLine;
            
            /*
            (4): Depending on content of first line, handle according to protocol:
            */
            if(c == null)
            {
                /*
                (4a): c is null
                        Do nothing
                */
            }
            else if(c.startsWith("ME IS "))
            {
                /*
                (4b): c is a ME IS request
                        Since this is a one line request, just push the one line up to the manager. 
                */
                requestLine = c;
                String reply = this.server.manager.ME_IS(requestLine, this);
                clientSocket.getOutputStream().write(reply.getBytes());
            }
            else if(c.startsWith("WHO HERE "))
            {
                /*
                (4c): c is a WHO HERE request
                        This is also a one line request, just push it up to the manager
                */
                requestLine = c;
                System.out.println("WHO HERE RECEIVED");
                String reply = this.server.manager.WHO_HERE(requestLine, this);
                
                clientSocket.getOutputStream().write(reply.getBytes());
            }
            else if(c.startsWith("LOGOUT "))
            {
                /*
                (4d) c is a LOGOUT request
                */
                /*
                TODO: LOGOUT FUNCTIONALITY
                */
            }
            else if(c.startsWith("SEND"))
            {
                /*
                (4e) c is a SEND request
                        This is a multiline request, the end of which is indicated by an 
                        empty line. Use a string builder to construct the request, 
                        then push it up to the manager. 
                */
                StringBuilder a = new StringBuilder();
                a.append(c + '\n');
                while(c.length() != 0)
                {
                    c = input.readLine();
                    a.append(c + '\n');
                }
                
                requestLine = a.toString();
                System.out.println("Message: " + requestLine);
                
                String reply = this.server.manager.SEND(requestLine, this);
                
                if (!reply.equals(null)) {
                    clientSocket.getOutputStream().write(reply.getBytes());
                }

            }
            else if(c.startsWith("BROADCAST"))
            {
                /*
                (4f) c is a BROADCAST request
                */
                StringBuilder a = new StringBuilder();
                a.append(c + '\n');
                while(c.length() != 0)
                {
                    c = input.readLine();
                    a.append(c + '\n');
                }
                
                requestLine = a.toString();
                System.out.println("Message: " + requestLine);
                
                String reply = this.server.manager.BROADCAST(requestLine, this);
                
                if (!reply.equals(null)) {
                    clientSocket.getOutputStream().write(reply.getBytes());
                }
            }
            else
            {
                /*
                (4g) c is an unrecognized request
                        Do nothing.
                */
            }
            
        }
    }
    
    
    /*
    DEPRECATED: use handleTCP
                left here for reference. 
    */
    public void OLDhandleTCP0() throws IOException
    {
        // Should theoretically loop indefinitely. 
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //while(clientSocket.)
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

            System.out.println("input is ready?: " + input.ready());
            
            String c = input.readLine();
            System.out.println("C is '" + c + "'");
            //while(input.)
            {
                System.out.println(c);
                c = input.readLine();
            }
            //System.out.println("Received input:" + input.readLine());


        }
        ;
    }
    
    
    /*
    TODO: Implement UDP handler function. 
    */
    public void handleUDP()
    {
        ;
    }
    
    
}