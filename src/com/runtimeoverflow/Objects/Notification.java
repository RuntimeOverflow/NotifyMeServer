package com.runtimeoverflow.Objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import com.runtimeoverflow.NotificationPopup;
import com.runtimeoverflow.Utilities.Properties;

public class Notification {
	//Properties of a notification
	public String title = "";
	public String subtitle = "";
	public String body = "";
	public String bundleId = "";
	public String threadId = "";
	public String id = "";
	public String category = "";
	public String app = "";
	public String icon = "";
	public String attachment = "";
	public long date = 0;
	
	//Creates the app icon from the base64 encoded string
	public Image getIcon() {
		if(icon.isEmpty()) {
			BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setColor(new Color(0, 0, 0, 0));
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.dispose();
			return img;
		}
		
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(icon));
			Image img = ImageIO.read(stream);
			stream.close();
			return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Creates the attachment image from the base64 encoded string (if there is one)
	public Image getAttachment() {
		//If there is no image, return a transparent one
		if(attachment.isEmpty()) {
			BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setColor(new Color(0, 0, 0, 0));
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.dispose();
			return img;
		}
		
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(attachment));
			Image img = ImageIO.read(stream);
			stream.close();
			return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Creates a duplicate of this notification, which can be modified later
	public Notification copy() {
		Notification n = new Notification();
		
		n.title = title;
		n.subtitle = subtitle;
		n.body = body;
		n.bundleId = bundleId;
		n.threadId = threadId;
		n.id = id;
		n.category = category;
		n.app = app;
		n.icon = icon;
		n.attachment = attachment;
		n.date = date;
		
		return n;
	}
	
	//Creates a relative string from the date property
	public String stringFromDate() {
		Calendar now = Calendar.getInstance();
		Calendar published = Calendar.getInstance();
		published.setTimeInMillis(date);
		
		Calendar copy = Calendar.getInstance();
		
		//If the date is today
		if(published.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && published.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
			//Return "now" if less than a minute passed
			copy.setTimeInMillis(published.getTimeInMillis());
			copy.add(Calendar.MINUTE, 1);
			if(copy.after(now)) {
				return "now";
			}
			
			//Return "Xm ago" if less than an hour passed
			copy.setTimeInMillis(published.getTimeInMillis());
			copy.add(Calendar.HOUR, 1);
			if(copy.after(now)) {
				return Integer.toString((int)((now.getTimeInMillis() - published.getTimeInMillis()) / 1000 / 60)) + "m ago";
			}
			
			//Return "Xh ago" if less than 4 hours passed
			copy.setTimeInMillis(published.getTimeInMillis());
			copy.add(Calendar.HOUR, 4);
			if(copy.after(now)) {
				return Integer.toString((int)((now.getTimeInMillis() - published.getTimeInMillis()) / 1000 / 60 / 60)) + "h ago";
			}
			
			//Return "HH:mm" if it was today
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
			return sdf.format(published.getTime());
		}
		
		//Return "Yesterday, HH:mm" if it was yesterday
		copy.setTimeInMillis(published.getTimeInMillis());
		copy.add(Calendar.DAY_OF_YEAR, 1);
		if(copy.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && copy.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
			return "Yesterday, " + sdf.format(published.getTime());
		}
		
		//Return "EEE HH:mm" if less than a week passed
		copy.setTimeInMillis(published.getTimeInMillis());
		copy.add(Calendar.DAY_OF_YEAR, 7);
		copy.set(Calendar.HOUR_OF_DAY, 0);
		copy.set(Calendar.MINUTE, 0);
		copy.set(Calendar.SECOND, 0);
		if(copy.after(now)) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE HH:mm", Locale.ENGLISH);
			return sdf.format(published.getTime());
		}
		
		//Return "Xw ago" if less than a month passed
		copy.setTimeInMillis(published.getTimeInMillis());
		copy.add(Calendar.MONTH, 1);
		copy.set(Calendar.HOUR_OF_DAY, 0);
		copy.set(Calendar.MINUTE, 0);
		copy.set(Calendar.SECOND, 0);
		if(copy.after(now)) {
			return Integer.toString((int)((now.getTimeInMillis() - published.getTimeInMillis()) / 1000 / 60 / 60 / 24 / 7)) + "w ago";
		}
		
		//Return "Xmo ago" if less than a year passed
		copy.setTimeInMillis(published.getTimeInMillis());
		copy.add(Calendar.YEAR, 1);
		copy.set(Calendar.HOUR_OF_DAY, 0);
		copy.set(Calendar.MINUTE, 0);
		copy.set(Calendar.SECOND, 0);
		if(copy.after(now)) {
			return Integer.toString((now.get(Calendar.YEAR) * 12 + now.get(Calendar.MONTH)) - (published.get(Calendar.YEAR) * 12 + published.get(Calendar.MONTH))) + "mo ago";
		}
		
		//Return "Xy ago" if more than a year passed
		return Integer.toString(now.get(Calendar.YEAR) - published.get(Calendar.YEAR)) + "y ago";
	}
	
	//Function, which will create a popup for the passed notification and display it
	public static void presentNotification(Notification notification) {
		NotificationPopup popup = new NotificationPopup(notification);
		int height = popup.getHeight();
		
		//Making all popups go up
		for(NotificationPopup np : Properties.popups) np.targetY -= height + 10;
		
		popup.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width - popup.getWidth() - 10, GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - popup.getHeight() - 10);
		
		Properties.popups.add(popup);
		
		//Timer, which will wait until there is enough space to display the notification
		Timer t = new Timer(25, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Properties.popups.indexOf(popup) == 0 || Properties.popups.get(Properties.popups.indexOf(popup) - 1).getY() + Properties.popups.get(Properties.popups.indexOf(popup) - 1).getHeight() <= popup.getY() - 10) {
					popup.setOpacity(0);
					popup.setVisible(true);
					
					((Timer)e.getSource()).stop();
					
					//Timer, which will fade in the notification
					Timer t = new Timer(50, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							popup.setOpacity(popup.getOpacity() + 0.25f);
							
							if(popup.getOpacity() == 1f) ((Timer) e.getSource()).stop();
						}
					});
					t.start();
				}
			}
		});
		t.start();
	}
	
	//Function, which will remove a displayed popup
	public static void removeNotification(NotificationPopup popup) {
		final int height = popup.getHeight();
		
		//Timer, which will fade out the notification
		Timer t = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popup.setOpacity(popup.getOpacity() - 0.25f);
				
				if(popup.getOpacity() == 0f) {
					((Timer) e.getSource()).stop();
					
					popup.setVisible(false);
					
					//Making all popups, which were above this one, go down
					for(NotificationPopup np : Properties.popups.subList(0, Properties.popups.indexOf(popup))) np.targetY += height + 10;
					
					Properties.popups.remove(popup);
				}
			}
		});
		t.start();
	}
}
