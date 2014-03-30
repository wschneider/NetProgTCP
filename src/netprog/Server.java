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
import java.nio.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.Channel;

public class Server implements Runnable{
    int port;
    ServerManager manager;
    ServerSocket TCPSocket;
    DatagramSocket UDPSocket;
    
    ServerSocketChannel tcp;
    DatagramChannel udp;
    Selector selector;
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
            SocketAddress localport = new InetSocketAddress(port);
            
            //CREATE THE PORTS
            //this.TCPSocket = new ServerSocket(port);
            //this.UDPSocket = new DatagramSocket(port);
            this.tcp = ServerSocketChannel.open();
            this.udp = DatagramChannel.open();
            
            tcp.socket().bind(localport);
            udp.socket().bind(localport);
            
            tcp.configureBlocking(false);
            udp.configureBlocking(false);
            
            this.selector = Selector.open();
            
            tcp.register(selector, SelectionKey.OP_ACCEPT);
            udp.register(selector, SelectionKey.OP_READ);  
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
            try
            {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                for(Iterator<SelectionKey> i = keys.iterator(); i.hasNext();)
                {
                    SelectionKey key = i.next();
                    i.remove();
                    
                    Channel c = key.channel();
                    
                    if(key.isAcceptable() && c == tcp)
                    {
                        System.out.println("Acceptable TCP, passing thread");
                        new Thread(new Handler(this, tcp.accept().socket(), "TCP")).start();
                    }
                    
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        
        
    }
    
    
    
    
}