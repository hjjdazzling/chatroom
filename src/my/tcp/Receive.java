package my.tcp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Receive implements Runnable {
	private DataInputStream dis = null;
	private boolean isRunning = true;
	
	public Receive(Socket client) {
		try {
			dis = new DataInputStream(client.getInputStream());
		} catch(Exception e) {
			e.printStackTrace();
			isRunning = false;
			CloseUtil.closeall(dis);
		} 
	}
	
	public void receive() {
		try {
			String msg = dis.readUTF();
			System.out.println(msg);
		} catch(EOFException e) {
			isRunning = false;
			CloseUtil.closeall(dis);
		}
		catch (Exception e) {
		
			e.printStackTrace();
			isRunning = false;
			CloseUtil.closeall(dis);
		} 
	}
	@Override 
	public void run() {
		while(isRunning) {
			receive();
		}
	}
}
