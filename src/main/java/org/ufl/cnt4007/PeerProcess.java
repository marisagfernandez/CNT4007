package org.ufl.cnt4007;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import org.ufl.cnt4007.Handshake;


import java.net.*;




class Host{
	int id;
	String hostname;
	int port;
	boolean hasFile;

	BitSet pieces;
	boolean isChoked; //is this peer choking this host
	boolean isInterested;

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
	private ArrayList<Handler> handlers;
	int listenPort;

	int id;
	BitSet pieces;

	public Process(int id) throws Exception {
		hosts = new ArrayList<Host>();
		handlers = new ArrayList<Handler>();

		readCommon(); //reads common.cfg file to init variables.
		//System.out.println(fileSize);
		//System.out.println(pieceSize);

		//calculate size of bitset and initialize structure
		int bit_size = (int) Math.ceil((float)fileSize / pieceSize);
		//System.out.println(bit_size);
		this.pieces = new BitSet(bit_size);
		//System.out.println(this.pieces);
		//System.out.println(bit_size);
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
		
		//now need to handle passing info to threads/
		//What needs handled...?
		//When a piece finishes downloading.. need all threads to send have message
		class oUnchockedNeighbor{
			Host value = null;
		}
		
		final oUnchockedNeighbor oUnchockedNeighbor = new oUnchockedNeighbor();
		
		TimerTask setPreferredNeighbors = new TimerTask(){
			public synchronized void run(){
				PriorityQueue<Handler> q = new PriorityQueue<Handler>(new Comparator<Handler>(){
					public int compare(Handler a, Handler b){
						int x = a.pieces_received;
						int y = b.pieces_received;	
						return x - y;
					}
				});
				for(Handler h: handlers){
					if(h.host.isInterested){
						q.add(h);
					} else {
						if(!h.host.isChoked) {
							h.addMessage(ActualMsg.makeChoke());
						}
						h.host.isChoked = true; //choke the host if not interested
					}
				}
				
				for(int i = 0; i < prefNeighbors; i++){
					if(q.isEmpty()) { //in case we don't have enough neighbors who can be unchoked
						break;
					}
					Handler handler = q.poll();
					if(handler.host.isChoked){ //if this top host is choked
						//create unchoking message
						handler.addMessage(ActualMsg.makeUnchoke()); //send unchoke message
						handler.host.isChoked = false;  //unchoke the top k download rates
					}
				}
				while(!q.isEmpty()) { //choke the remaining hosts
					Handler h = q.poll();
					if(h.host == oUnchockedNeighbor.value) {
						//don't choke
						continue;
					}
					if(!h.host.isChoked) { //send message if needed
						h.addMessage(ActualMsg.makeChoke());
						h.host.isChoked = true;
					}
					
				}
	
			}
		};
		
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(setPreferredNeighbors, 0, unchokingInterval);

		TimerTask setOUnchokedNeighbor = new TimerTask(){

			public void run(){
				ArrayList<Handler> interestedHosts = new ArrayList<Handler>();
				for(Handler h: handlers){
					if(h.host.isInterested && h.host.isChoked){

						interestedHosts.add(h);
					}
				}
				if(interestedHosts.size() > 0) {
					Random r = new Random();
					int x = r.nextInt(interestedHosts.size());
					oUnchockedNeighbor.value = interestedHosts.get(x).host;
					oUnchockedNeighbor.value.isChoked = false;
					interestedHosts.get(x).addMessage(ActualMsg.makeUnchoke());
				}
			}
		};
		
		timer.scheduleAtFixedRate(setOUnchokedNeighbor, 0, oUnchokingInterval);

	}
	
	private synchronized void hasPiece(int f) {
		//create the message to be sent and then run notify peers
		//also need to update my bitset
		Process.this.pieces.set(f);
		byte[] have = ActualMsg.makeHave(f);
		notifyPeers(have);
	}
	private void notifyPeers(byte[] msg) {
		for (Handler h : handlers) {
			h.addMessage(msg);
		}
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
				Handler handler = new Handler(host,socket, true);
				handler.start();
				handlers.add(handler);
				
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

				Handler handler = new Handler(host,tempSocket,true);
				handler.start();
				handlers.add(handler);

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
			for(int i = 0; i < bit_size; ++i) {
				h.pieces.set(i,h.hasFile);
			}
			if(h.hasFile){
				System.out.println(h.hostname + " has the file");
				
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
		boolean choked;
		Queue<byte[]> msgQ;
		Host host;
		DataInputStream incoming;
		DataOutputStream outgoing;
		Socket socket;
		boolean initiator;
		private boolean requesting;
		boolean interested;
		int pieces_received;
		Handler(Host h, Socket s, boolean initiator){
			this.choked = false;
			msgQ = new ArrayBlockingQueue<byte[]>(1024); //arbitrarily chosen data structure
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
		void addMessage(byte [] msg) {
			msgQ.add(msg);
		}
		private boolean receiveHandshake() throws IOException {
			byte[] expected = Handshake.makeHandshake(this.host.id); //expecting host's id
			byte[] message = new byte[expected.length];
			incoming.readFully(message, 0, message.length);
			return Arrays.equals(expected, message);
		}
		private void send(byte[] data) {
			try {
				//outgoing.writeInt(data.length);
				System.out.print("Sending data: ");
				for(byte b : data) {
					System.out.print(b + " ");
				}
				outgoing.write(data); //all messages are auto prefixed with length (no need to add here)
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private byte[] receive() throws IOException {
			System.out.print("receive called with: " + incoming.available() + " bytes available.");
			if(incoming.available() == 0) {
				return null;
			}
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
				System.out.println("Wrote msg");
				boolean valid = false;
				try {
					valid = receiveHandshake();
				} catch (Exception e) {
					System.out.println("Handshake with host " + this.host.hostname + " failed!");
				}
				if(!valid) { 
					System.out.println("Invalid handshake with host: " + this.host.hostname);
					return; //terminate thread
				}

				//now send and accept bitfield
				//System.out.println(pieces);
				
				send(ActualMsg.makeBitfield(pieces));
				/*
				byte[] recv = receive(); //receiving bitfield message
				for(byte b : recv) {
					System.out.print(b + " ");
				}
				System.out.println("Done printing out byte field");
				*/
				//now entering message handling loop
				while(true) {
					//System.out.println("Loop start");
					//check for messages to send from main process
					while(!msgQ.isEmpty()) {
						send(msgQ.poll());
					}
					//check for incoming messages
					if(incoming.available() > 0) {
						//System.out.println("Something is available");
						byte[] recv = receive();
						//System.out.println("received!");
						if (recv == null) { //TODO: pull msg receiving into another function
							continue;
						}
						ActualMsg m = new ActualMsg(recv);
						ActualMsg.Type msgType = m.getMsgType();
						
						if(msgType == ActualMsg.Type.BITFIELD) {
							//respond with interested or not interested
							this.interested = this.host.pieces.get(0); //checks if first bit is set on other host
							if(interested) {
								send(ActualMsg.makeInterested());
							} else {
								send(ActualMsg.makeNotInterested());
							}
						}
						if(msgType == ActualMsg.Type.INTERESTED) {
							System.out.println(host.hostname + " is interested.");
							this.host.isInterested = true;
						}
						if(msgType == ActualMsg.Type.NONINTERESTED) {
							System.out.println(host.hostname + " is not interested.");
							this.host.isInterested = false;
						}
						if(msgType == ActualMsg.Type.REQUEST) {
							//TODO Actually get the piece
							byte[] req_payload = m.getPayload();
							//System.out.println("request payload size: " + payload.length);
							if(req_payload.length != 4) {
								System.out.println("Improper request payload received from + " + this.host.hostname);
								continue;
							}
							int index = ByteBuffer.wrap(req_payload).getInt(); //index of request
							System.out.println("Received request for piece " + index);
							
							if(!this.host.isChoked) { //checks if peer should be sending to this host
								//TODO: Get payload from filemanager
								send(ActualMsg.makePiece(index,"payloadsfordays".getBytes()));
							} else {
								System.out.println("Received request from choked host");
							}
						}
						if(msgType == ActualMsg.Type.PIECE) {
							this.requesting = false;
							//piece should have an index and a payload
							byte[] payload = m.getPayload();
							if(payload.length < 5) {
								System.out.println("Strange PIECE message received");
								continue;
							}
							ByteBuffer wrapped = ByteBuffer.wrap(payload);
							int index = wrapped.getInt();
							byte[] piece = new byte[wrapped.remaining()];
							wrapped.get(piece);
							this.pieces_received++; //keeps track of pieces received during a time period
							System.out.println("DEBUG: received piece " + index);
							System.out.println("DEBUG: Piece contents " + new String(piece));
							
							Process.this.hasPiece(index);
						}
						if(msgType == ActualMsg.Type.HAVE) {

							byte[] payload = m.getPayload();
							int index = ByteBuffer.wrap(payload).getInt();
							BitSet b = this.host.pieces;
							b.set(index);
							System.out.println("DEBUG: received have for index: " + index);
						}
						if(msgType == ActualMsg.Type.UNCHOKE) {
							this.choked = false;
							System.out.println("Debug: received UNCHOKE");
						}
						if(msgType == ActualMsg.Type.CHOKE) {
							this.choked = true;
							System.out.println("Debug: received CHOKE");
						}
						
						
						
						
						
					}
					//done handling received messages
					//don't send a request if choked, or if already requesting or if not interested.
					if(!this.choked && !this.requesting && this.interested) { //send a request for a piece
						this.requesting = true;
						//decide on index
						BitSet r = (BitSet) this.host.pieces.clone();
						r.andNot(Process.this.pieces); //r is now a set of pieces they have and we don't
						ArrayList<Integer> indices = new ArrayList<Integer>();
						for(int i = r.nextSetBit(0); i>=0; i = r.nextSetBit(i+1)) {
							indices.add(i);
							if(i == Integer.MAX_VALUE) {
								break;
							}
						}
						//select randomly
						Random rando = new Random();
						//System.out.print((indices.size()));
						int n = rando.nextInt(indices.size());
						byte [] msg = ActualMsg.makeRequest(n);
						send(msg);
						System.out.println("sent request for piece: " + n);
					}
					
					//otherwise check for choke/unchoke -- not implemented yet
				}

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
