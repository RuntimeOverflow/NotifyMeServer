package com.runtimeoverflow.Utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Base64;

import com.runtimeoverflow.Objects.Device;

public class UDPServer implements Runnable {
	@Override
	public void run() {
		try {
			//Creates a new UDP Socket, which listens on the specified port
			DatagramSocket ds = new DatagramSocket(Properties.port);
			
			//Loop, which constantly waits for a new message
			while(true) {
				if(ds.isClosed()) break;
				
				byte[] buf = new byte[4096];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				
				//Waits for a new message and receives it
				ds.receive(packet);
				
				String msg = new String(packet.getData()).trim();
				if(msg.startsWith("[NotifyMe] SEARCH COMPUTER")) {
					String[] params = msg.substring("[NotifyMe] SEARCH COMPUTER ".length()).split(" ");
					
					if(params.length >= 2) {
						//Decodes the name and uuid of the device
						String uuid = new String(Base64.getDecoder().decode(params[0]));
						String name = new String(Base64.getDecoder().decode(params[1]));
						
						//Checks if the device is already known
						boolean alreadyAdded = false;
						Device device = null;
						for(Device d : Properties.devices) {
							if(d.uuid.equals(uuid)) {
								alreadyAdded = true;
								device = d;
								break;
							}
						}
						
						//If the device is not known, create it and add it to the list of devices
						if(!alreadyAdded) Properties.devices.add(new Device(uuid, name, packet.getAddress().getHostAddress()));
						else {
							//If the device is already known, update the information, which might have changed
							device.ip = packet.getAddress().getHostAddress();
							device.name = name;
						}
						
						//Send a response to the device, so it knows, that this computer is ready
						byte[] content = "[NotifyMe] READY".getBytes();
						DatagramPacket dp = new DatagramPacket(content, content.length, packet.getAddress(), Properties.port);
						DatagramSocket ms = new DatagramSocket();
						ms.setBroadcast(true);
						ms.send(dp);
						ms.close();
					}
				}
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Search for devices by broadcasting a message using UDP
	public static void searchDevice() {
		try {
			byte[] content = "[NotifyMe] SEARCH DEVICE".getBytes();
			
			for(InetAddress address : Properties.calculateBroadcastAddress()) {
				DatagramPacket dp = new DatagramPacket(content, content.length, address, Properties.port);
				DatagramSocket ms = new DatagramSocket();
				ms.setBroadcast(true);
				ms.send(dp);
				ms.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}