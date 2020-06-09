package com.runtimeoverflow.Utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.runtimeoverflow.Objects.Device;

public class Server implements Runnable {
	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(Properties.port);
			
			while(true) {
				if(ss.isClosed()) break;
				
				Socket s = ss.accept();
				
				String ip = s.getInetAddress().getHostAddress();
				Device device = null;
				for(Device d : Properties.devices) {
					if(d.ip.equals(ip)) {
						device = d;
						break;
					}
				}
				
				if(device == null) {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					bw.write("[NotifyMe] UNKNOWN DEVICE\r");
					bw.flush();
					bw.close();
					s.close();
				} else {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					bw.write("[NotifyMe] VERIFY " + Encryption.encrypt("Manufactured in Switzerland", device.key) + "\r");
					bw.flush();
					
					Calendar timeout = Calendar.getInstance();
					timeout.add(Calendar.SECOND, 5);
					Scanner reader = new Scanner(s.getInputStream(), "UTF-8");
					reader.useDelimiter(Pattern.compile("\r"));
					
					while(!reader.hasNext() && !Calendar.getInstance().after(timeout));
					
					if(reader.hasNext()) {
						String msg = reader.next();
						if(msg.equals("[NotifyMe] VERIFICATION TRUE")) {
							device.currentSocket = s;
							Thread t = new Thread(device);
							t.start();
						}
					} else reader.close();
				}
			}
			
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}