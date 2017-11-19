package org.ufl.cnt4007;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileManager {

	private static File file;
	private static int pieceSize;
	private static int numPieces;
	private static int peerID;
	private static byte[] piece;
	
	public FileManager (){
		
	}
	
	public FileManager(File file, int pieceSize, int numPieces, int peerID, byte[] piece){
		this.file = file;
		this.pieceSize = pieceSize;
		this.numPieces = numPieces;
		this.peerID = peerID;
		this.piece = piece;
	}
	
	public static void makePieces () throws IOException{

		try{
			byte[] b = new byte[pieceSize];
			FileInputStream f = new FileInputStream(file);
			
			for(int i = 0; i < numPieces; i++){
				//Read in the data from main file
				f.read(b);
				//Create new file for the piece
				File piece = new File("./peer_"+String.valueOf(peerID),"piece" + String.valueOf(i+1));
				try{
					FileOutputStream outputStream = new FileOutputStream(piece);
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
	
	public static void savePiece(int pieceNum) throws IOException{
		try {
			File file = new File("./peer_"+String.valueOf(peerID),"piece"+String.valueOf(pieceNum));
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(piece);

		}catch(IOException e){
			System.out.println("ERROR savePiece");
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	
	public static void writePiecesToFile(String fileName) throws IOException{
		try{
			File file = new File("./peer_"+String.valueOf(peerID), fileName);
			FileOutputStream f = new FileOutputStream(file);
			byte[] b = new byte[pieceSize];
			
			for(int i = 0; i < numPieces; i++){
				//Find the file representing the piece
				FileInputStream inputStream = new FileInputStream("./peer_"+String.valueOf(peerID)+"/"+"piece"+String.valueOf(i+1));
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
