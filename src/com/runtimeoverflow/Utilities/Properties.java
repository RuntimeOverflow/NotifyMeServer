package com.runtimeoverflow.Utilities;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import com.runtimeoverflow.Main;
import com.runtimeoverflow.NotificationPopup;
import com.runtimeoverflow.Objects.Device;

public class Properties {
	//Variables, which get saved
	public static ArrayList<Device> devices = new ArrayList<Device>();
	public static int port = 1337;
	
	//Other variables
	public transient static Thread server = null;
	public transient static Thread udpListener = null;
	public transient static ArrayList<NotificationPopup> popups = new ArrayList<NotificationPopup>();
	public transient static ArrayList<InetAddress> broadcastAddresses = new ArrayList<InetAddress>();
	public transient static Font font;
	public transient static Font boldFont;
	public transient static int multiplier = 1;
	
	public static void init() {
		try {
			//Loads the tweak logo
			logo = ImageIO.read(Main.class.getResourceAsStream("/resources/Icon.png"));
			
			//Loads the San Francisco font (=iOS Font)
			font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/resources/SF-Pro-Text-Light.otf"));
			boldFont = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/resources/SF-Pro-Text-Medium.otf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(boldFont);
			
			//Calculates the broadcast address from each network interface (and filters invalid results)
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
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
	}
	
	//Getting the tweak logo in the passed color
	private static BufferedImage logo = null;
	public static BufferedImage getIconWithColor(Color color) {
		int w = logo.getWidth();
		int h = logo.getHeight();
		
		BufferedImage icon = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = icon.createGraphics();
		
		g.drawImage(logo, 0, 0, null);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0,0,w,h);
		g.dispose();
		
		return icon;
	}
	
	//Calculates the font size in points for the specified pixel height
	public static float getPointSizeForHeight(int pxSize) {
		JLabel tester = new JLabel();
		float pointSize = 0;
		
		for(float i = 1; i < 20; i++) {
			if(tester.getFontMetrics(Properties.font.deriveFont(i)).getHeight() > pxSize) {
				if(Math.abs(tester.getFontMetrics(Properties.font.deriveFont(pointSize)).getHeight() - pxSize) > tester.getFontMetrics(Properties.font.deriveFont(i)).getHeight() - pxSize) pointSize = i;
				break;
			} else pointSize = i;
		}
		
		return pointSize;
	}
}