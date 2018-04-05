package my.tcp;

import java.io.Closeable;

public class CloseUtil {
	public static void closeall(Closeable... all) {
		for(Closeable io : all) {
			try {
				if(io != null) {
					io.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}