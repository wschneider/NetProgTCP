/*
    Connection.java
    Authors: 
        Will Schneider
        Will Kelly

    Purpose:
        Represents one client connection to the server. Seriously, simple class...
        
    Methods:
        To Be Described Later
*/

package netprog;

import java.io.*;
import java.net.*;
import java.util.*;


public class Connection{
    String IPAddress;
    String userid;
    int numMessages;
    
    public Connection(String uid, String IP)
    {
        this.IPAddress = IP;
        this.userid = uid;
        this.numMessages = 0;
    }
    
    
    
    
}