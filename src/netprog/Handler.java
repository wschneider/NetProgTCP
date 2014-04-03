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
    SocketAddress connection;
    
    /*
    TODO: IMPLEMENT CONSTRUCTOR
    */
    public Handler(Server top, Socket clientSocket, String protocol)
    {
        /*TCP CONSTRUCTOR*/
        this.server = top;
        this.clientSocket = clientSocket;
        this.UDPClientSocket = null;
        this.PROTOCOL = protocol;
    }

    public Handler(Server top, /*DatagramSocket UDPSocket,*/ DatagramChannel UDPChannel , String protocol)
    {
        /*UDP CONSTRUCTOR*/
        this.server = top;
        this.clientSocket = null;
        this.UDPClientChannel = UDPChannel;
        this.PROTOCOL = protocol;

    }
    
    public int getPort()
    {
        if(this.PROTOCOL.equals("TCP"))
        {
            return this.clientSocket.getPort();
        }
        else
        {
            return Integer.parseInt(this.connection.toString().split(":")[1]);
        }
    }

    public InetAddress getIP()
    {
        if(PROTOCOL.equals("TCP"))
        {
            return this.clientSocket.getInetAddress();
        }
        else
        {
            String arg = this.connection.toString();
            try
            {
                return InetAddress.getByAddress(this.connection.toString().getBytes());
            }
            catch(UnknownHostException e)
            {
                System.err.println("This broke");
                return null;
            }        
        }
    }

    /*
    TODO: IMPLEMENT VOID RUN
    */
    public void run()
    {
        try{
            if(PROTOCOL.toUpperCase() == "TCP")
            {
                this.handleTCP();
            }
            else if(PROTOCOL.toUpperCase() == "UDP")
            { 
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
            //error?
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
                
                this.server.manager.vPrint("RCVD from " + this.getIP() + ": " + requestLine);
                clientSocket.getOutputStream().write(reply.getBytes());
            }
            else if(c.startsWith("WHO HERE "))
            {
                /*
                (4c): c is a WHO HERE request
                        This is also a one line request, just push it up to the manager
                */
                requestLine = c;
                String reply = this.server.manager.WHO_HERE(requestLine, this);
                
                this.server.manager.vPrint("RCVD from " + this.getIP() + ": " + requestLine);
                
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
                
                String reply = this.server.manager.SEND(requestLine, this);
                
                this.server.manager.vPrint("RCVD from " + this.getIP() + ": " + requestLine);
                
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
                this.server.manager.vPrint("RCVD from " + this.getIP() + ": " + requestLine);
                
                
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
    void handleUDP:
        Handles the server-side functionality for a client connecting via UDP.
        
        The Function operates as such:
            (1) Retrieve the bytes from the DatagramChannel, and convert to 
                String
            (2) Handles each command case conditionally, retreiving a reply
                from the ServerManager
            (3) Submits a reply to the user.  
               
    */
    public void handleUDP() throws IOException
    {
        /*
            (1): Retrieve input from Channel
        */
        ByteBuffer input = ByteBuffer.allocate(512);
        this.connection = UDPClientChannel.receive(input);
        String requestLine = new String(input.array());

        if(requestLine.length() == 0)
        {
            //Clients used for testing had a tendency to submit null strings, so
            //  this is to handle those. 
            return;
        }

        String[] lines = requestLine.split("\n");
        String reply = "";        

        /*
            (2): Handle each command. Since UDP comes in all at once via the 
                 ByteBuffer, there is no need to do additional streaming. 
        */
        if(lines[0] == null)
        {
            return;
        }
        else if(lines[0].startsWith("ME IS "))
        {
            System.out.println(this.connection.toString());
            reply = this.server.manager.ME_IS(requestLine, this); 
        }
        else if(lines[0].startsWith("WHO HERE "))
        {
            reply = this.server.manager.WHO_HERE(requestLine, this);
        }
        else if(lines[0].startsWith("LOGOUT "))
        {
            reply = this.server.manager.LOGOUT(requestLine, this);
        }
        else if(lines[0].startsWith("SEND "))
        {
            reply = this.server.manager.SEND(requestLine, this);
        }
        else if(lines[0].startsWith("BROADCAST "))
        {
            reply = this.server.manager.BROADCAST(requestLine, this);
        }
        else
        {
            return;
        }
        
        //Optional output
        this.server.manager.vPrint("RCVD from " + this.getIP() + ": " + 
            requestLine);
        
        try
        {        
            /*
                (3): Write a reply. This is tricky on account of UDP. We're just
                     going to pop open a new socket for writing. 
            */
            byte[] output = new byte[1024];
            output = reply.getBytes();
            
            DatagramSocket outSock = new DatagramSocket();
            DatagramPacket replyPack = new DatagramPacket(output, output.length, 
                this.getIP(), this.getPort());
            
            outSock.send( replyPack );
        }
        catch( IOException e)
        {
            System.err.println("Failed to write message due to IOException");
        }
    }
    
}
