package my.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private Socket client = null;
	private String name = null;
	public static void main(String[] args) {
		new Client().connect();
	}
	
	public void connect() {
		try {
			client = new Socket("localhost", 8888);
			Scanner in = new Scanner(System.in);
			System.out.print("请输入名字:");
			name = in.next();
			new Thread(new Send(client, name)).start();
			new Thread(new Receive(client)).start();
		} catch(BindException e) {
			System.out.println("端口已被占用");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}