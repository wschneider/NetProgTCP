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
        <SEND from to\nmsg>: Send msg to the client represented by id
        <BROADCAST from\nmsg>: Send msg to EACH client 
        <ME IS id>: Declare identity
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
    boolean VERBOSEOUTPUT;
    ArrayList<UserConnection> clients;
    
    String OK_REPLY = "OK";
    String ERR_REPLY = "ERROR";
    
    String[] FunResponses = {"~~~Simon the Server Says: \"All Glory To The Hypnotoad!\"",
        "~~~Simon the Server Says: \"Lord Helix will forgive your sins\"",
        "~~~Simon the Server Says: \"OH NO, YOU DIDNT RELEASE THE MEEPITS DID YOU?\"",
        "~~~Simon the Server Says: \"KNOCK KNOCK\"",
        "~~~Simon the Server Says: \"If peeing in your pants is cool, consider me Miles Davis\"",
        "~~~Simon the Server Says: \"Gentlemen, you can't fight in here! This is the War Room!\"",
        "~~~Simon the Server Says: \"I'd love to stay here and talk with you... but I'm not going to\"",
        "~~~Simon the Server Says: \"Everybody knows you never go full retard!\"",
        "~~~Simon the Server Says: \"Bratwurst? Aren't we the optimist...\"",
        "~~~Simon the Server Says: \"Hello. My name is Inigo Montoya. You killed my father. Prepare to die\""};
        
    
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
        
        if(args.length == 0)
        {
            System.out.println("Port numbers required");
            return;
        }
        
        int[] ports;
        int j;
        boolean verbose;
        
        if(args[0].equals("-v"))
        {
            if(args.length == 1)
            {
                System.out.println("Port numbers required");
                return;
            }
            ports = new int[args.length-1];
            j = 1;
            verbose = true;
        }
        else
        {
            ports = new int[args.length];
            j = 0;
            verbose = false;
        }
        
        for(int i = j ;i < args.length;i++)
        {
            ports[i-j] = Integer.parseInt(args[i]);
        }
        
        /*
        TODO: CONTINUE MAIN FUNCTION.
        */
        ServerManager a = new ServerManager(ports);
        
        if(verbose)
        {
            a.setVerbose();
        }
        
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
        this.VERBOSEOUTPUT = false;
        clients = new ArrayList<UserConnection>();
        POOL = new Thread[ports.length];
        for(int i=0;i<ports.length;i++)
        {
            POOL[i] = new Thread(new Server(ports[i], this));
        }   
    }
    
    /*
    void setVerbose:
        sets the verbose output boolean to true, allowing output to be displayed 
        from the server
    */
    public void setVerbose()
    {
        this.VERBOSEOUTPUT = true;
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
    
    /*
    void vPrint():
        Prints to the console only if this.verbose is true.
    */
    public void vPrint(String msg)
    {
        if(this.VERBOSEOUTPUT)
        {
            System.out.println(msg);
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
        for(UserConnection c : clients)
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

        UserConnection toAdd = new UserConnection(targetId, handle);
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
        
        for(UserConnection c : clients)
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
     *          (2) Searches clients for the target client and, if found, writes 
     *              the string to that client.
     *          (3) Returns the response for the sender (null if successful, an 
     *              error message if not).
     */
    public String SEND(String requestLine, Handler handle)
    {
        boolean userFound = false;
        String fromUser;
        String toUser;
        
        // Note: first split will only isolate the first line, since it is 
        //           limited to 2 substrings.
        //       The rest of the message will be unaffected.
        String[] requestLines = requestLine.split("\n", 2);
        String[] firstLineParts = requestLines[0].split(" ");
        
        fromUser = firstLineParts[1];
        toUser   = firstLineParts[2];
        
        String outgoingMessage = "FROM " + fromUser + "\n" + requestLines[1];
        
        for(UserConnection c : clients) {
            if (c.userid.equals(toUser)) {
                c.write(outgoingMessage);
                userFound = true;
                handleMessage(fromUser);
                
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
     *          (3) Returns the response for the sender (null if successful, an 
     *              error message if not).
     */
    public String BROADCAST(String requestLine, Handler handle)
    {
        String fromUser;
        String[] requestLines = requestLine.split("\n", 2);
        String[] firstLineParts = requestLines[0].split(" ");
        fromUser = firstLineParts[1];
        
        String outgoingMessage = "FROM " + fromUser + "\n" + requestLines[1];
        
        for(UserConnection c : clients) {
            c.write(outgoingMessage);
        }
        
        handleMessage(fromUser);
        
        return null;
    }
    
    /*
     * String LOGOUT():
     *      Manager handles a LOGOUT request from a client.
     *      Operation:
     *          (1) Finds the Connection in the clients array and remove it.
     *          Additional steps required by TCP connections should be handled 
     *          by the Handler object.
     */
    public String LOGOUT(String requestLine, Handler handle)
    {
        String targetId = requestLine.split(" ")[2];

        for(UserConnection c : clients) {
            if (c.userid.equals(targetId)) {
                clients.remove(c);
                return null;
            }
        }
        
        return "ERROR no such user\n";        
    }

    /*
        String chunkMessage():
            takes a string representing a message that is to be sent out to 
            users. If it is under 100 characters it is sent as is, prefixed with
            the number of bytes. If it is more, it is converted to chunked style
    */
    private String chunkMessage(String msg)
    {
        StringBuilder reply = new StringBuilder();
        
        if(msg.length() < 100)
        {
            reply.append(msg.length());
            reply.append("\n");
            reply.append(msg);
            return reply.toString();
        }
        
        int p = 0;
        while(p <= msg.length())
        {
            int q = Math.min(99,msg.length()-p);
            String toAdd = msg.substring(p, p+q);
            p = q+1;
            
            reply.append("c" + toAdd.length() + "\n");
            reply.append(toAdd);
            reply.append("\n");
        }
        
        return reply.toString();    
    }
    
    /*
        void handleMessage():
            When a user sends 3 messages, they are supposed to be pinged with a
            random reply. This method is called every time a user sends a 
            message, and when they have %3=0 messages, they are pinged. 
    */
    private void handleMessage(String targetId)
    {
        for(UserConnection c : clients)
        {
            if(c.userid.equals(targetId))
            {
                int a = c.increment();
                if(a%3==0)
                {
                    c.write(randomMessage());
                }
            }
        }
    }
    
    /*
        String randomMessage():
            Retrieves a random message for the handleMessage method.
    */
    private String randomMessage()
    {
        int random = (int)(Math.random() * 10);
        return this.FunResponses[random];
    }

    /*
        void doToAll():
            sends an unformatted message to each client. FOR TESTING PURPOSES 
            ONLY
    */
    public void doToAll(String msg)
    {
        for(UserConnection c : clients)
        {
            c.write("msg");
        }
    }
}
