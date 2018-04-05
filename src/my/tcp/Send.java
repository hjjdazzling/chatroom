package my.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Send implements Runnable {
	private String name;
	private DataOutputStream dos = null;
	private boolean isRunning = true;
	
	public Send(Socket client,String name) {
		try {
			dos = new DataOutputStream(client.getOutputStream());
			this.name = name;
			dos.writeUTF(name);
		} catch (Exception e) {
			e.printStackTrace();
			isRunning = false;
			CloseUtil.closeall(dos);
		} 
	}
	public void send() {
		Scanner in = new Scanner(System.in);
		String msg = in.next();
		try {
			dos.writeUTF(msg);
		} catch (Exception e) {
			e.printStackTrace();
			isRunning = false;
			CloseUtil.closeall(dos);
		} 
	}
	@Override 
	public void run() {
		while(isRunning) {
			send();
		}
	}
}

