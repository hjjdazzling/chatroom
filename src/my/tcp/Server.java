package my.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ServerSocket server = null;
	private Socket client = null;
	private List<Client> list = new ArrayList<>();
	
	private ExecutorService es = Executors.newFixedThreadPool(20);

	public static void main(String[] args) {
		
		new Server().connect();
	}

	public void connect() {
		try {
			server = new ServerSocket(8888);
			while (true) {
				client = server.accept();
				System.out.println("一个客户端连接上来了");
				Client c = new Client(client);
				es.submit(new Thread(c));
			}
		} catch (BindException e) {
			System.out.println("端口已被占用");
		} catch (Exception e) {
			e.printStackTrace();
			CloseUtil.closeall(server);
		}
	}

	class Client implements Runnable {
		private DataOutputStream dos = null;
		private DataInputStream dis = null;
		private boolean isRunning = true;
		private String name;

		public Client(Socket client) {
			try {
				dos = new DataOutputStream(client.getOutputStream());
				dis = new DataInputStream(client.getInputStream());
				this.name = dis.readUTF();
				try {
					lock.writeLock().lock();
					list.add(this);
				} finally {
					lock.writeLock().unlock();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isRunning = false;
				CloseUtil.closeall(dos, dis);
			}
		}

		@Override
		public void run() {
			while (isRunning) {
				sendOthers();
			}
		}

		public String receive() {
			String message = "";
			try {
				message = dis.readUTF();
			} catch (EOFException e) {
				System.out.println("一个用户退出");
				isRunning = false;
				CloseUtil.closeall(dos, dis);
				list.remove(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return message;
		}

		public void sendOthers() {
			String msg = receive();
			/**
			 * 单聊
			 */
			if (msg.startsWith("@") && msg.indexOf(":") > 0) {
				String name = msg.substring(1, msg.indexOf(":"));
				String message = msg.substring(msg.indexOf(":") + 1);
				for (Client c : list) {
					if (name.equals(c.name)) {
						c.send(this.name + "悄悄的给你说 -----> " + message);
					}
				}
			} else {
				/**
				 * 群聊
				 */
				try {
					lock.readLock().lock();
					for (Client c : list) {
						if (c == this) {
							continue;
						}
						c.send(this.name + ":" + msg);
					}
				} finally {
					lock.readLock().unlock();
				}
			}
		}

		public void send(String msg) {
			try {
				dos.writeUTF(msg);
				dos.flush();
			} catch (Exception e) {
				e.printStackTrace();
				isRunning = false;
				CloseUtil.closeall(dos, dis);
				list.remove(this);
			}
		}
	}
}
