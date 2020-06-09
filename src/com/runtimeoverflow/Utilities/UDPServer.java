package com.runtimeoverflow.Utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Base64;

import com.runtimeoverflow.Objects.Device;

public class UDPServer implements Runnable {
	@Override
	public void run() {
		try {
			DatagramSocket ds = new DatagramSocket(Properties.port);
			while(true) {
				if(ds.isClosed()) break;
				
				byte[] buf = new byte[4096];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				ds.receive(packet);
				
				String msg = new String(packet.getData()).trim();
				if(msg.startsWith("[NotifyMe] SEARCH COMPUTER")) {
					String[] params = msg.substring("[NotifyMe] SEARCH COMPUTER ".length()).split(" ");
					
					if(params.length >= 2) {
						String uuid = new String(Base64.getDecoder().decode(params[0]));
						String name = new String(Base64.getDecoder().decode(params[1]));
						
						boolean alreadyAdded = false;
						Device device = null;
						for(Device d : Properties.devices) {
							if(d.uuid.equals(uuid)) {
								alreadyAdded = true;
								device = d;
								break;
							}
						}
						
						if(!alreadyAdded) Properties.devices.add(new Device(uuid, name, packet.getAddress().getHostAddress()));
						else {
							device.ip = packet.getAddress().getHostAddress();
							device.name = name;
						}
						
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
}