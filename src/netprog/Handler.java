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
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.DatagramChannel;
import java.nio.ByteBuffer;

public class Handler implements Runnable
{
    String PROTOCOL;
    Server server;
    Socket clientSocket;
    DatagramSocket UDPClientSocket;
    DatagramChannel UDPClientChannel;
    
    /*
    TODO: IMPLEMENT CONSTRUCTOR
    */
    public Handler(Server top, Socket clientSocket, String protocol)
    {
        this.server = top;
        this.clientSocket = clientSocket;
        this.UDPClientSocket = null;
        this.PROTOCOL = protocol;
    }

    public Handler(Server top, /*DatagramSocket UDPSocket,*/ DatagramChannel UDPChannel , String protocol)
    {
        this.server = top;
        this.clientSocket = null;
        //this.UDPClientSocket = UDPSocket;
        this.UDPClientChannel = UDPChannel;
        this.PROTOCOL = protocol;

    }
    
    public InetAddress getIP()
    {
        if(PROTOCOL.equals("TCP"))
        {
            return this.clientSocket.getInetAddress();
        }
        else
        {
            return this.UDPClientChannel.socket().getInetAddress();
        }
    }

    /*
    TODO: IMPLEMENT VOID RUN
    */
    public void run()
    {
        //System.out.println("RECEIVED CONNECTION WITH " + this.PROTOCOL);
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
        catch(IllegalBlockingModeException e)
        {
            //
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
    
    public void handleUDP() throws IOException
    {
        ByteBuffer input = ByteBuffer.allocate(512);
        //ByteBuffer output = ByteBuffer.allocate(512);

        UDPClientChannel.receive(input);

        //System.out.println("Received a packed via UDP");

        String requestLine = new String(input.array());

        if(requestLine.length() == 0)
        {
            return;
        }

        String[] lines = requestLine.split("\n");
        String reply = "nuttin";        

        if(lines[0] == null)
        {
            return;
        }
        else if(lines[0].startsWith("ME IS "))
        {
            reply = this.server.manager.ME_IS(requestLine, this); 
        }
        else if(lines[0].startsWith("WHO HERE "))
        {
            reply = this.server.manager.WHO_HERE(requestLine, this);
        }
        else if(lines[0].startsWith("LOGOUT "))
        {

        }
        else if(lines[0].startsWith("SEND "))
        {

        }
        else if(lines[0].startsWith("BROADCAST "))
        {

        }
        else
        {
            return;
        }
    
        ByteBuffer output = ByteBuffer.wrap(reply.getBytes());
        //UDPClientChannel.write(output);        

        //System.out.println("REQUEST: " + requestLine);
    }


    /*
    TODO: Implement UDP handler function. 
    */
    public void handleUDP0() throws IOException, IllegalBlockingModeException
    {
        byte[] dataIn = new byte[1024]; 
        byte[] dataOut = new byte[1024]; 
        //while(UDPClientSocket.isConnected()) 
        //{ 
            DatagramPacket requestPacket = new DatagramPacket(dataIn, dataIn.length); 
            //System.out.println("Blocking on receive"); 

            UDPClientSocket.receive(requestPacket); 
            System.out.println("Received a packet"); 
            String fullRequest = new String( requestPacket.getData() ); 
            /* UDP handles messages with content fields up to 100 bytes. They 
                are formatted in lines though, so lets just use that. */ 
            String[] lines = fullRequest.split("\n"); 
            System.out.println("RECEIVED FROM USER: \n" + fullRequest); 
            String reply = "ACK"; 
            DatagramPacket replyPacket = new DatagramPacket(reply.getBytes(), reply.getBytes().length, requestPacket.getAddress(), requestPacket.getPort()); 
            UDPClientSocket.send(replyPacket); 
        //}
    }
    
    
}