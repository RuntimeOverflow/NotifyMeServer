package com.runtimeoverflow.Objects;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Scanner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.runtimeoverflow.Main;
import com.runtimeoverflow.Window;
import com.runtimeoverflow.UI.NotificationsScene;
import com.runtimeoverflow.Utilities.Encryption;

public class Device implements Runnable {
	//Variables, which get saved
	public String uuid = "";
	public String key = "CHANGE THIS";
	
	//Even though these variables may change, they will be saved
	public String name = "";
	public String ip = "";
	
	//Other variables
	public transient boolean connected = false;
	public transient ArrayList<ArrayList<Notification>> groups = new ArrayList<ArrayList<Notification>>();
	
	//Variable used to transfer the socket from the server thread to this thread
	public transient Socket currentSocket;
	public transient Scanner currentReader;
	public transient BufferedWriter currentWriter;
	
	public Device(String uuid, String name, String ip) {
		this.uuid = uuid;
		this.name = name;
		this.ip = ip;
	}
	
	@Override
	public void run() {
		//Transfers the socket from the temporary property to this local variable
		Socket socket = currentSocket;
		Scanner scanner = currentReader;
		BufferedWriter writer = currentWriter;
		
		//Sets the temporary property to null
		currentSocket = null;
		currentReader = null;
		currentWriter = null;
		
		if(socket == null || scanner == null || writer == null) return;
		
		try {
			//Timeout, if the device is suddenly unavailable
			Calendar timeout = Calendar.getInstance();
			timeout.add(Calendar.SECOND, 30);
			
			//This while loop will force the thread to listen all the time
			while(timeout.after(Calendar.getInstance())) {
				if(!socket.isConnected() || socket.isClosed()) break;
				
				//Wait until the full message arrived (=wait until the \r arrived)
				if(scanner.hasNext()) {
					timeout = Calendar.getInstance();
					timeout.add(Calendar.SECOND, 30);
					
					String rawMsg = scanner.next();
					
					if(!rawMsg.startsWith("[NotifyMe] CLOSE")) {
						//Send back "CONFIRMED", so the device knows, that the message was delivered
						writer.write("[NotifyMe] CONFIRMED\r");
						writer.flush();
					}
					
					//These if blocks will process the different commands
					if(rawMsg.startsWith("[NotifyMe] SYNC")) {
						String msg = rawMsg.substring("[NotifyMe] SYNC ".length());
						
						//Decrypting the message
						msg = Encryption.decrypt(msg, key);
						
						//The notifications get sent in json and they get parsed here
						Gson g = new GsonBuilder().create();
						groups = g.fromJson(msg, new TypeToken<ArrayList<ArrayList<Notification>>>(){}.getType());
						
						//Refreshes the list, which is displayed when you click on the tray icon
						if(Window.get().getScene().getClass() == NotificationsScene.class) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									((NotificationsScene)Window.get().getScene()).refreshNotificationList();
								}
							});
						}
					} else if(rawMsg.startsWith("[NotifyMe] NOTIFICATION")) {
						String msg = rawMsg.substring("[NotifyMe] NOTIFICATION ".length());
						
						//Splits the message into 3 parts
						String[] parts = msg.split(" ");
						if(parts.length < 3) return;
						
						//The 1. part is whether there is a popup or not
						boolean popup = Objects.equals(parts[0], "VISIBLE");
						
						//The 2. part is whether there is a sound or not
						boolean sound = Objects.equals(parts[1], "SOUND");
						
						//The 3. part is the actual notification
						msg = parts[2];
						
						//Decrypting the message
						msg = Encryption.decrypt(msg, key);
						
						//The notification gets sent in json and it gets parsed here
						Gson g = new GsonBuilder().serializeNulls().create();
						Notification notification = g.fromJson(msg, Notification.class);
						
						//Play the notification audio
						if(sound) {
							try {
								Clip clip = AudioSystem.getClip();
								clip.open(AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("/resources/Notification.wav")));
								
								LineListener ll = new LineListener() {
									@Override
									public void update(LineEvent event) {}
								};
								clip.addLineListener(ll);
								
								clip.start();
							} catch (LineUnavailableException | UnsupportedAudioFileException e) {
								e.printStackTrace();
							}
						}
						
						//Call the function that will display the notification
						if(popup) Notification.presentNotification(notification);
					} else if(rawMsg.startsWith("[NotifyMe] CLOSE")) break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//Closing everything
				if(scanner != null) scanner.close();
				if(writer != null) writer.close();
				if(socket != null) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
