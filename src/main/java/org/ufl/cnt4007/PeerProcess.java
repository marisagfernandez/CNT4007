package org.ufl.cnt4007;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;


import org.ufl.cnt4007.Handshake;

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
	int listenPort;

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
	public void start() {
		doConnects();
		//threads are running
	}
	private void doConnects() {

		Iterator<Host> it = this.hosts.iterator();
		while(it.hasNext()) {
			Host host = it.next();
			if(host.id == this.id) {
				break;
			}
			try {
				Socket socket = new Socket(host.hostname, host.port);
				socket.setTcpNoDelay(true);
				new Handler(host,socket, true).start();
			} catch (UnknownHostException e) {
				System.out.println("Unable to connect to host : " + host.hostname);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try(ServerSocket ss = new ServerSocket(this.listenPort)){
			while(it.hasNext()) {
				Host host = it.next();
				Socket tempSocket = ss.accept();
				tempSocket.setTcpNoDelay(true);

				new Handler(host,tempSocket,true).start();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//shouldn't reach here, look into adding code to fix if it does
			e.printStackTrace();
		}
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
				this.listenPort = h.port;
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
	class Handler extends Thread{
		Host host;
		DataInputStream incoming;
		DataOutputStream outgoing;
		Socket socket;
		boolean initiator;
		Handler(Host h, Socket s, boolean initiator){
			this.host = h;
			this.socket = s;
			this.initiator = initiator; //initiator is supposed to send first handshake? or can this be done async?	
			try {
				this.incoming = new DataInputStream(socket.getInputStream());
				this.outgoing = new DataOutputStream(socket.getOutputStream());
				outgoing.flush();
			} catch (IOException e) {
				
				e.printStackTrace();
			} 
		}
		private void send(byte[] data) {
			try {
				outgoing.writeInt(data.length);
				outgoing.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private byte[] receive() throws IOException {
			int length = incoming.readInt();
			byte[] message = null;
			if (length > 0) {
				message = new byte[length];
				incoming.readFully(message, 0, message.length);
			}
			return message;
		} 
		public void run() {
			System.out.println("Handler started for: " + host.hostname);
			try {
				System.out.println("initiating connection");

				send(Handshake.makeHandshake(id));
				
				byte[] recv = receive(); //can block
				if(recv != null) {
					//validate handshake
					boolean valid = Handshake.verifyHandshake(recv, host.id);
					if(valid) {
						System.out.println("Handshake is valid!");
					}
				}

				System.out.println("Wrote message");


				//System.out.println("incoming message: " + (String)incoming.readObject());

				//    			
				//    			outgoing.writeObject("Hello");
				//    			outgoing.flush();
				//    			String msg = (String)incoming.readObject();
				//    			System.out.println(msg);
			} catch(IOException e){

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
		//System.out.println("Config files read");
		
		//now that config files are read need to start the process

		p.start();
		//end host loop
		System.out.println("Finishing");


	}

}
