package com.runtimeoverflow;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import javax.swing.JScrollPane;
import javax.swing.Timer;

import com.runtimeoverflow.Utilities.Properties;
import com.runtimeoverflow.Utilities.Server;
import com.runtimeoverflow.Utilities.UDPServer;

public class Main {
	public static JScrollPane pane = new JScrollPane();
	
	private static TrayIcon tray = null;
	
	//Start
	public static void main(String[] args) {
		//Initialise all properties
		Properties.init();
		
		Properties.server = new Thread(new Server());
		Properties.server.start();
		
		Properties.udpListener = new Thread(new UDPServer());
		Properties.udpListener.start();
		
		if(SystemTray.isSupported()) {
			try {
				SystemTray sysTray = SystemTray.getSystemTray();
				
				//Create a system tray icon
				int size = new TrayIcon(Properties.getIconWithColor(Color.WHITE)).getSize().width;
				tray = new TrayIcon(Properties.getIconWithColor(Color.WHITE).getScaledInstance(size, size, Image.SCALE_SMOOTH), "NotifyMe");
				tray.setImageAutoSize(true);
				
				//Adding a click listener to toggle the window visibility
				tray.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
						Window.get().setVisible(!Window.get().isVisible());
						
						if(Window.get().isVisible()) {
							searchDevice();
						}
					}
					
					@Override
					public void mousePressed(MouseEvent e) {}
					
					@Override
					public void mouseExited(MouseEvent e) {}
					
					@Override
					public void mouseEntered(MouseEvent e) {}
					
					@Override
					public void mouseClicked(MouseEvent e) {}
				});
				
				sysTray.add(tray);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		
		//Initializing the window
		Window.get().setIconImages(Arrays.asList(new Image[] {Properties.getIconWithColor(Color.WHITE).getScaledInstance(128, 128, Image.SCALE_SMOOTH), Properties.getIconWithColor(Color.BLACK).getScaledInstance(16, 16, Image.SCALE_SMOOTH)}));
		Window.get().setTitle("NotifyMe");
		
		Scene s = new Scene();
		Window.get().setScene(s);
		
		//Starting the timer, which will handle moving the notifications up and down. This gets created here and will run the whole time to stay synchronized
		Timer t = new Timer(25, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(NotificationPopup popup : Properties.popups) {
					if(popup.targetY != popup.getY() && popup.targetY != -1) {
						int offset = ((popup.getY() - popup.targetY) > 0 ? -1 : 1) * Math.min(40, Math.abs(popup.getY() - popup.targetY));
						popup.setLocation(popup.getX(), popup.getY() + offset);
					}
				}
			}
		});
		t.start();
	}
	
	//Search for devices by broadcasting a message using UDP
	public static void searchDevice() {
		try {
			byte[] content = "[NotifyMe] SEARCH DEVICE".getBytes();
			
			for(InetAddress address : Properties.broadcastAddresses) {
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
	
	//Old function, which is currently unused
	/*public static void connected(String name) {
		try {
			Notification cn = new Notification();
			
			int length = 0;
			byte[] imgBytes = new byte[16384 * 2];
			byte[] buffer = new byte[4096];
			
			DataInputStream di = new DataInputStream(Main.class.getResourceAsStream("/resources/Icon.png"));
			int readLength = di.read(buffer);
			while(readLength > 0) {
				System.arraycopy(buffer, 0, imgBytes, length, readLength);
				length += readLength;
				readLength = di.read(buffer);
			}
			di.close();
			
			cn.icon = Base64.getEncoder().encodeToString(imgBytes);
			cn.app = "NotifyMe";
			cn.date = Calendar.getInstance().getTimeInMillis();
			cn.title = name + " Connected";
			cn.body = "You will now receive all notifications from your device.";
			Notification.presentNotification(cn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}