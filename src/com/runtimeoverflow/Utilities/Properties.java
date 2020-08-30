package com.runtimeoverflow.Utilities;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import com.runtimeoverflow.Main;
import com.runtimeoverflow.NotificationPopup;
import com.runtimeoverflow.Objects.Device;
import com.runtimeoverflow.UI.NotificationsScene;

public class Properties {
	//Variables, which get saved
	public static ArrayList<Device> devices = new ArrayList<Device>();
	public static int port = 1337;
	
	//Other variables
	public transient static Thread server = null;
	public transient static Thread udpListener = null;
	public transient static ArrayList<Device> discoveredDevices = new ArrayList<Device>();
	public transient static ArrayList<NotificationPopup> popups = new ArrayList<NotificationPopup>();
	public transient static NotificationsScene mainScene;
	public transient static Font font;
	public transient static Font boldFont;
	public transient static int multiplier = 1;
	
	//Images and icons
	public transient static BufferedImage logo = null;
	public transient static BufferedImage indicatorImage = null;
	public transient static BufferedImage settingsIcon = null;
	public transient static BufferedImage devicesIcon = null;
	public transient static BufferedImage backIcon = null;
	public transient static BufferedImage clearIcon = null;
	
	public static void init() {
		try {
			//Loads the tweak logo
			logo = ImageIO.read(Main.class.getResourceAsStream("/resources/Icon.png"));
			indicatorImage = ImageIO.read(Main.class.getResourceAsStream("/resources/Indicator.png"));
			settingsIcon = ImageIO.read(Main.class.getResourceAsStream("/resources/Settings.png"));
			devicesIcon = ImageIO.read(Main.class.getResourceAsStream("/resources/Devices.png"));
			backIcon = ImageIO.read(Main.class.getResourceAsStream("/resources/Back.png"));
			clearIcon = ImageIO.read(Main.class.getResourceAsStream("/resources/Clear.png"));
			
			//Loads the San Francisco font (=iOS Font)
			font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/resources/SF-Pro-Text-Light.otf"));
			boldFont = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/resources/SF-Pro-Text-Medium.otf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(boldFont);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
	}
	
	//Calculates the broadcast address from each network interface
	/*public static ArrayList<InetAddress> calculateBroadcastAddress() {
		ArrayList<InetAddress> broadcastAddresses = new ArrayList<InetAddress>();
		
		try {
			Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces();
			while(list.hasMoreElements()) {
				NetworkInterface iface = (NetworkInterface) list.nextElement();

				if(iface == null) continue;

				if(!iface.isLoopback() && iface.isUp()) {
					Iterator<InterfaceAddress> it = iface.getInterfaceAddresses().iterator();
					while (it.hasNext()) {
						InterfaceAddress address = (InterfaceAddress) it.next();
						
						if(address == null || address.getBroadcast() == null) continue;
						InetAddress broadcast = address.getBroadcast();
						
						if(!broadcast.isAnyLocalAddress()) broadcastAddresses.add(broadcast);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return broadcastAddresses;
	}*/
	
	//Changes an image to the specified color
	public static BufferedImage getImageWithColor(Image img, Color color) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		
		BufferedImage icon = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = icon.createGraphics();
		
		g.drawImage(img, 0, 0, null);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0,0,w,h);
		g.dispose();
		
		return icon;
	}
	
	//Calculates the font size in points for the specified pixel height
	public static float getPointSizeForHeight(int pxSize, Font font) {
		JLabel tester = new JLabel();
		float pointSize = 0;
		
		for(float i = 1; i < 20; i++) {
			if(tester.getFontMetrics(font.deriveFont(i)).getHeight() > pxSize) {
				if(Math.abs(tester.getFontMetrics(font.deriveFont(pointSize)).getHeight() - pxSize) > tester.getFontMetrics(font.deriveFont(i)).getHeight() - pxSize) pointSize = i;
				break;
			} else pointSize = i;
		}
		
		return pointSize;
	}
	
	public static String filterSpecialCharacters(String text, Font font) {
		StringBuilder sb = new StringBuilder();
		
		for(char c : text.toCharArray()) if(font.canDisplay(c)) sb.append(c);
		
		return sb.toString();
	}
}