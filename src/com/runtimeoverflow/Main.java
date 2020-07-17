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
import java.util.Arrays;

import javax.swing.JScrollPane;
import javax.swing.Timer;

import com.runtimeoverflow.UI.NotificationsScene;
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
		
		//Create the tray icon, if the system supports it
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
							UDPServer.searchDevice();
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
		
		NotificationsScene s = new NotificationsScene();
		Window.get().setScene(s);
		
		//Starting the timer, which will move the notifications up and down. This gets created here and will run the whole time to stay synchronised
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
}