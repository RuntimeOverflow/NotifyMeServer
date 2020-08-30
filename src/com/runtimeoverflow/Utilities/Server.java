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
import com.runtimeoverflow.Objects.Notification;

public class Server implements Runnable {
	@Override
	public void run() {
		try {
			//Creates a socket, which listens for incoming connections on the specified port
			ServerSocket ss = new ServerSocket(Properties.port);
			
			//Constantly waits for a new connection
			while(true) {
				if(ss.isClosed()) break;
				
				//Waits for a connection and accepts it
				Socket s = ss.accept();
				
				//CHecks if the device is known to this computer
				String ip = s.getInetAddress().getHostAddress();
				Device device = null;
				for(Device d : Properties.devices) {
					if(d.ip.equals(ip)) {
						device = d;
						break;
					}
				}
				
				if(device == null) {
					//If the device is not known to this computer, send back "UNKNOWN DEVICE"
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					bw.write("[NotifyMe] UNKNOWN DEVICE\r");
					bw.flush();
					bw.close();
					s.close();
				} else {
					//If the device is known, the computer will encrypt an example String and send it back to the device
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					bw.write("[NotifyMe] VERIFY " + Utilities.encrypt("Manufactured in Switzerland", device.key) + "\r");
					bw.flush();
					
					//Timeout, if the the device is suddenly unavailable
					Calendar timeout = Calendar.getInstance();
					timeout.add(Calendar.SECOND, 5);
					Scanner reader = new Scanner(s.getInputStream(), "UTF-8");
					reader.useDelimiter(Pattern.compile("\r"));
					
					//Wait for the response of the device
					while(!reader.hasNext() && !Calendar.getInstance().after(timeout));
					
					if(reader.hasNext()) {
						String msg = reader.next();
						
						//If the device could successfully decrypt the string, a new Thread gets started, which handles the communication
						if(msg.equals("[NotifyMe] VERIFICATION TRUE")) {
							device.currentSocket = s;
							device.currentReader = reader;
							device.currentWriter = bw;
							Thread t = new Thread(device);
							t.start();
						} else if(msg.equals("[NotifyMe] VERIFICATION FALSE")) {
							Notification n = new Notification();
							n.app = "NotifyMe";
							n.body = "Invalid password";
							n.bundleId = "com.runtimeoverflow.notifyme";
							n.date = Calendar.getInstance().getTimeInMillis();
							n.dismissAction = null;
							n.title = "Couldn't connect to " + device.name;
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