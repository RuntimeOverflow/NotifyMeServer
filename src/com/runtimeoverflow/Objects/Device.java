package com.runtimeoverflow.Objects;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.runtimeoverflow.Scene;
import com.runtimeoverflow.Window;
import com.runtimeoverflow.Utilities.Encryption;

public class Device implements Runnable {
	//Variables, which get saved
	public String uuid = "";
	public String key = "12341234";
	
	//Even though these variables may change, they will be saved
	public String name = "";
	public String ip = "";
	
	//Other variables
	public transient boolean connected = false;
	public transient ArrayList<ArrayList<Notification>> groups = new ArrayList<ArrayList<Notification>>();
	
	//Variable used to transfer the socket from the server thread to this thread
	public transient Socket currentSocket;
	
	public Device(String uuid, String name, String ip) {
		this.uuid = uuid;
		this.name = name;
		this.ip = ip;
	}

	@Override
	public void run() {
		Scanner reader = null;
		
		//Transfers the socket from the temporary property to this local variable
		Socket socket = currentSocket;
		
		//Sets the temporary property to null
		currentSocket = null;
		
		if(socket == null) return;
		
		try {
			//Since I don't maintain an active connection I set the timeout to 10 seconds
			socket.setSoTimeout(10 * 1000);
			
			//Creates a new Scanner, which reads until it finds a \r (This is my separator, so I know when a message is finished)
			reader = new Scanner(socket.getInputStream(), "UTF-8");
			reader.useDelimiter(Pattern.compile("\r"));
			
			//This while loop will force the thread to listen all the time
			while(true) {
				if(!socket.isConnected() || socket.isClosed()) break;
				
				//Wait until the full message arrived (=wait until the \r arrived)
				if(reader.hasNext()) {
					String rawMsg = reader.next();
					
					//These if blocks will process the different commands
					if(rawMsg.startsWith("[NotifyMe] SYNC")) {
						String msg = rawMsg.substring("[NotifyMe] SYNC ".length());
						msg = Encryption.decrypt(msg, key); //Decrypting the message
						
						//The notifications get sent in json and they get parsed here
						Gson g = new GsonBuilder().create();
						groups = g.fromJson(msg, new TypeToken<ArrayList<ArrayList<Notification>>>(){}.getType());
						
						//Refreshes the list, which is displayed when you click on the tray icon
						if(Window.get().getScene().getClass() == Scene.class) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									((Scene)Window.get().getScene()).refreshNotificationList();
								}
							});
						}
					} else if(rawMsg.startsWith("[NotifyMe] NOTIFICATION")) {
						String msg = rawMsg.substring("[NotifyMe] NOTIFICATION ".length());
						msg = Encryption.decrypt(msg, key); //Decrypting the message
						
						//The notification gets sent in json and it gets parsed here
						Gson g = new GsonBuilder().serializeNulls().create();
						Notification notification = g.fromJson(msg, Notification.class);
						
						//Call the function that will display the notification
						Notification.presentNotification(notification);
					} else if(rawMsg.startsWith("[NotifyMe] CLOSE")) break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null) reader.close();
				if(socket != null && !socket.isClosed()) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
