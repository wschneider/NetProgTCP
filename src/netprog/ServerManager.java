/*
    ServerManager.java
    Authors: 
        Will Schneider
        Will Kelly

    Purpose:
        ServerManager sits as the overhead manager of several Servers (one for 
        each specified port in the command line arguments). 
        
    Completed Methods:
        public ServerManager():
            Public Constructor
        public static void main():
            Main function for running this Server
        public void start():
            Starts the server(s)
        public String ME_IS():
            Handles client-operation ME IS
        public String WHO_HERE():
            Handles client-operation WHO HERE

    Incomplete Methods:
        public String SEND():
        public String BROADCAST():
        public String LOGOUT():

    Protocol:
        <SEND msg id>: Send msg to the client represented by id
        <BROADCAST msg>: Send msg to EACH client 
        <ME IS id>: Declare identity
*/

package netprog;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.jndi.ldap.Connection;

public class ServerManager
{
    //Singleton representation of the server because reasons
    ServerManager INSTANCE;
    Thread[] POOL;
    boolean VERBOSEOUTPUT;
    ArrayList<Connection> clients;
    
    String OK_REPLY = "OK";
    String ERR_REPLY = "ERROR";
    
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
        clients = new ArrayList<Connection>();
        VERBOSEOUTPUT = true;
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
            System.out.println("Starting a Server thread");
            thread.start();
        }
    }
    
    /*
    String ME_IS():
        Manager handles a ME IS request from a client. 
        
        Operation:
            (1) Splits out the user id from the rest of the request line
            (2) Loops over the list of existing clients to look for a duplicate
                userid or IP. 
            (3) If none is found, a new client connection is added to the list 
                of users. 
            (3) Replies with OK or ERROR depending on status. 
    
    */
    public String ME_IS(String requestLine, Handler handle)
    {
        /*
        (1): Split out the user id
        */
        String targetId = requestLine.split(" ")[2];
        
        /*
        (2): Loop over clients
        */
        for(Connection c : clients)
        {
            /*
            (2a): Check user id match; if fail, return error
            */
            if(c.userid.equals(targetId))
            {
                return ERR_REPLY;
            }
            /*
            (2b): Check IP address match
            REMOVED FOR DEBUG PURPOSES:
            else if(c.InetAddr.equals(handle.clientSocket.getInetAddress()))
            {
                return ERR_REPLY;
            }
            */
        }
        
        /*
        (3): Add client connection to list of users
        */
        Connection toAdd = new Connection(targetId, handle);
        clients.add(toAdd);
        
        /*
        (4): Return OK
        */
        return OK_REPLY;
    }
    
    
    /*
    String WHO_HERE():
        Manager handles a WHO HERE request from a client.
    
        Operation:
            (1) Builds a StringBuilder to add users to
            (2) Loops over client connections, adding each user
            (3) Returns the string list. 
    */
    public String WHO_HERE(String requestLine, Handler handle)
    {
        StringBuilder reply = new StringBuilder();
        
        for(Connection c : clients)
        {
            reply.append( c.userid + ": <" + c.InetAddr.toString() +  ">\n" );
        }
        
        return reply.toString();
    }
    
    
    /*
     * String SEND():
     *      Manager handles a SEND request from a client.
     *      Operation:
     *          (1) Builds a String to be sent to the target client.
     *          (2) Searches clients for the target client and, if found, writes the string to that client.
     *          (3) Returns the response for the sender (null if successful, an error message if not).
     */
    public String SEND(String requestLine, Handler handle)
    {
        boolean userFound = false;
        
        // Note: first split will only isolate the first line, since it is limited to 2 substrings.
        // The rest of the message will be unaffected.
        String[2] requestLines = requestLine.split("\n", 2);
        String[] firstLineParts = requestLines[0].split(" ");
        String outgoingMessage = "FROM " + firstLineParts[1] + "\n" + requestLines[1];
        
        for(Connection c : clients) {
            if (c.userid.equals(firstLineParts[2])) {
                c.write(outgoingMessage);
                userFound = true;
                break;
            }
        }
        
        if (userFound) {
            return null;
        }
        return "ERROR no such user";
    }
    
    /*
     * String BROADCAST():
     *      Manager handles a BROADCAST request from a client.
     *      Operation:
     *          (1) Builds a String to be sent to the target client.
     *          (2) Writes the string to all clients.
     *          (3) Returns the response for the sender (null if successful, an error message if not).
     */
    public String BROADCAST()
    {
        String[2] requestLines = requestLine.split("\n", 2);
        String[] firstLineParts = requestLines[0].split(" ");
        String outgoingMessage = "FROM " + firstLineParts[1] + "\n" + requestLines[1];
        
        for(Connection c : clients) {
            c.write(outgoingMessage);
        }
        
        return null;
    }
    
    /*
     * String LOGOUT():
     *      Manager handles a LOGOUT request from a client.
     *      Operation:
     *          (1) Finds the Connection in the clients array and remove it.
     *          Additional steps required by TCP connections should be handled by the Handler object.
     */
    public String LOGOUT()
    {
        String targetId = requestLine.split(" ")[2];

        for(Connection c : clients) {
            if (c.userid.equals(targetId)) {
                clients.remove(c);
                return null;
            }
        }
        
        return "ERROR no such user\n";        
    }
}