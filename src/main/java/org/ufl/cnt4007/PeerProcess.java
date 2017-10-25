package org.ufl.cnt4007;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.net.*;




class Host{
    int id;
    String hostname;
    int port;
    boolean hasFile;
    
    BitSet pieces;

    public String toString() {
    	return hostname + " " + port;
    }
    
}

class Process{
    int prefNeighbors;
    int unchokingInterval;
    int oUnchokingInterval;
    String fileName;
    long fileSize;
    long pieceSize;
    ArrayList<Host> hosts;
    
    int id;
    BitSet pieces;
    
    public Process(int id) throws Exception {
        hosts = new ArrayList<Host>();
        
        readCommon(); //reads common.cfg file to init variables.
        
        //calculate size of bitset and initialize structure
        int bit_size = (int) Math.ceil((float)fileSize / pieceSize);
        this.pieces = new BitSet(bit_size);
        
        this.id = -1; //read peer list and set up hosts
        readPeers(id);
        if(this.id == -1){ //If the id isn't on the list, will still be -1.
            throw new Exception("id not in list");
        } 
        //hosts arraylist now setup 
        
    }
   
    private void readCommon() throws IOException{
        try{
            List<String> configLines = Files.readAllLines(Paths.get("Common.cfg"), Charset.forName("US-ASCII"));
            for(String line : configLines){
                String[] words = line.split(" ");
                String var = words[0];
                String value = words[1];
                switch(var){
                    case "NumberOfPreferredNeighbors":
                        prefNeighbors = Integer.parseInt(value);
                        break;
                    case "UnchokingInterval":
                        unchokingInterval = Integer.parseInt(value);
                        break;
                    case "OptimisticUnchokingInterval":
                        oUnchokingInterval = Integer.parseInt(value);
                        break;
                    case "FileName":
                        fileName = value;
                        break;
                    case "FileSize":
                        fileSize = Long.parseLong(value);
                        break;
                    case "PieceSize":
                        pieceSize = Long.parseLong(value);
                        break;          
                }               
            }
        } catch (IOException e){
            System.out.println("ERROR, no config file found");
            throw new IOException();
        }
    
    }
    private void readPeers(int id) throws IOException{
        List<String> peerLines = Files.readAllLines(Paths.get("PeerInfo.cfg"), Charset.forName("US-ASCII"));
        for(String line : peerLines){
            //parsing each line for [peer id] [host-name] [port] [has-file]
            String[] tokens = line.split(" ");
            //for now, just parse the string into a host object.
            //TODO: Add connections
            Host h = new Host();
            h.id = Integer.parseInt(tokens[0]);
            h.hostname = tokens[1];
            h.port = Integer.parseInt(tokens[2]);
            h.hasFile = Integer.parseInt(tokens[3]) == 1;

            //calculate size of bitset and initialize structure
            int bit_size = (int) Math.ceil((float)fileSize / pieceSize);
            h.pieces = new BitSet(bit_size);
            if(h.hasFile){
                h.pieces.flip(0, h.pieces.length());
            }
            if(id == h.id){
                //reading current host entry
                this.id = id;
                this.pieces = h.pieces;
                hosts.add(h); //need this here for now to separate list into who to connect to.
            } else {
                //not current host
                hosts.add(h);
            }
            if(PeerProcess.DEBUG){
            	System.out.println("Host tokenizing");
            for (String t : tokens){
                System.out.print(t + " "); //just checking tokenizing works
            }
            }
        }
    }
    
}
class Handler extends Thread{
	Host host;
	ObjectInputStream incoming;
	ObjectOutputStream outgoing;
	Socket socket;
	boolean initiator;
	Handler(Host h, Socket s, boolean initator){
		this.host = h;
		this.socket = s;
		this.initiator = initiator; //initiator is supposed to send first handshake?
	}
	public void run() {
		System.out.println("Handler started for: " + host.hostname);
		try {
			this.incoming = new ObjectInputStream(socket.getInputStream());
			this.outgoing = new ObjectOutputStream(socket.getOutputStream()); 
			outgoing.flush();
			String msg = "Hello";
			if(initiator) {
			
				outgoing.writeObject(msg);
				outgoing.flush();
				System.out.println("incoming message: " + (String)incoming.readObject());
			} else {
				System.out.println("incoming message: " + (String)incoming.readObject());
				outgoing.writeObject(msg);
				outgoing.flush();
			}
//			
//			outgoing.writeObject("Hello");
//			outgoing.flush();
//			String msg = (String)incoming.readObject();
//			System.out.println(msg);
		} catch(IOException | ClassNotFoundException e){
			
			e.printStackTrace();
			
		}	finally {
			try {
				this.socket.close();
				this.incoming.close();
				this.outgoing.close();
			} catch (Exception e) {
				
				//e.printStackTrace();
			}
		
		}

	}
}
public class PeerProcess {
    public static final boolean DEBUG = false;
    public static void main(String[] args) {

        System.out.println("Starting");
        Process p;
        try{
            p = new Process(Integer.parseInt(args[0])); //args start from 0 in java (program name not included)
            //System.out.println(p.fileName);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        System.out.println("Config files read");
        
        //now do connections -- should probably move this inside Process constructor and use try w/ resource
        boolean foundCurrent = false;
        for(Host currentHost : p.hosts) {
        	if(currentHost.id == p.id) {
        		foundCurrent = true;
        		continue; //don't connect if it's the same host
        	}
        	Socket socket;
        	if(!foundCurrent) { //connect outwards
        		try {
					socket = new Socket(currentHost.hostname, currentHost.port);
					socket.setTcpNoDelay(true);
	        		new Handler(currentHost,socket, true).start();
	        		
				} catch (UnknownHostException e) {
					System.out.println("Unable to find host " + currentHost);
					return;
				} catch (IOException e) {	
					e.printStackTrace();
					return;
				}
        	} else { //listen for an incoming connection
        		try {
        			System.out.println("Listening for " + currentHost.hostname);
					ServerSocket serverSocket = new ServerSocket(currentHost.port);
					Socket temp = serverSocket.accept();
					temp.setTcpNoDelay(true);
					
					new Handler(currentHost, temp, false).start();
					System.out.println("Accepted");
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		
        	}
//        	try {
//        		Socket s = new Socket(currentHost.hostname, currentHost.port);
//        		
//        	} catch (Exception e) {
//        		e.printStackTrace();
//        	}
        	
        }
        //end host loop
        System.out.println("Finishing");
        
        
    }
    
}
