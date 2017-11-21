package org.ufl.cnt4007;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileManager {

	//private File file;
	private int pieceSize;
	private int numPieces;
	private int peerID;
	//private byte[] piece;
	
	public FileManager (){
		
	}
	
	public FileManager(int pieceSize, int numPieces, int peerID){
		//this.file = file;
		this.pieceSize = pieceSize;
		this.numPieces = numPieces;
		this.peerID = peerID;
		//this.piece = piece;
	}
	
	public void makePieces (File file) throws IOException{

		try{
			byte[] b = new byte[pieceSize];
			FileInputStream f = new FileInputStream(file);
			
			for(int i = 0; i < numPieces; i++){
				//Read in the data from main file
				f.read(b);
				//Create new file for the piece
				//File piece = new File("./peer_"+String.valueOf(peerID)+"/","piece" + String.valueOf(i+1));
				try{
					FileOutputStream outputStream = new FileOutputStream("./peer_"+String.valueOf(peerID)+"/"+"piece" + String.valueOf(i), false);
					//Write the data to the file representing the piece
					outputStream.write(b);
				
				}catch (IOException e){
					System.out.println("ERROR in creating piece");
					e.printStackTrace();
					throw new IOException();
				}	
				
			}
			
		}catch (IOException e){
			System.out.println("ERROR in reading file");
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	public byte[] createPieceByteArray(int pieceNum) throws IOException{
		try{
		byte[] piece = new byte[pieceSize];
		
		FileInputStream inputStream = new FileInputStream("./peer_"+String.valueOf(peerID)+"/piece"+String.valueOf(pieceNum));
		inputStream.read(piece);
		
		return piece;
		
		}catch(IOException e){
			System.out.println("ERROR in creating piece - byte array");
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	public void savePiece(byte[] piece, int pieceNum) throws IOException{
		try {
			//File file = new File("./peer_"+String.valueOf(peerID),"piece"+String.valueOf(pieceNum));
			FileOutputStream outputStream = new FileOutputStream("./peer_"+String.valueOf(peerID)+"/piece"+String.valueOf(pieceNum), false);
			outputStream.write(piece);

		}catch(IOException e){
			System.out.println("ERROR savePiece");
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	
	public void writePiecesToFile(String fileName) throws IOException{
		try{
			File file = new File("./peer_"+String.valueOf(peerID), fileName);
			FileOutputStream f = new FileOutputStream(file);
			byte[] b = new byte[pieceSize];
			
			for(int i = 0; i < numPieces; i++){
				//Find the file representing the piece
				FileInputStream inputStream = new FileInputStream("./peer_"+String.valueOf(peerID)+"/"+"piece"+String.valueOf(i));
				//Read its data
				inputStream.read(b);
				//Then write it to the main file
				f.write(b);
				
				}
			
		}catch (IOException e){
			System.out.println("ERROR in writing to file");
			e.printStackTrace();
			throw new IOException();
		}
	
	}

	
	
	/*public static void main(String[] args) {
		
		File f = new File("Hello.rtf");
		
		File f2 = new File("Hello2.rtf");
		
		
		int pieceSize = 119;
		int numPieces = 3;
		int peerID = 1001;
		try{
			makePieces();
			//writePiecesToFile(f2, pieceSize, numPieces);
		}catch(IOException e){
			System.out.println("Error!");
			e.printStackTrace();
		}

	}*/

}
