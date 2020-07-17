package com.runtimeoverflow.UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.runtimeoverflow.NotificationPopup;
import com.runtimeoverflow.Objects.Device;
import com.runtimeoverflow.Objects.Notification;
import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class NotificationsScene extends JPanel {
	private JPanel content = new JPanel();
	private JPanel contentFrame = new JPanel();
	private MenuBar bar = null;
	
	public NotificationsScene() {
		setLayout(null);
		setSize(Properties.multiplier * 359, Properties.multiplier * 78 * 6 + Properties.multiplier * 32);
		
		JPanel settingsButton = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				g.drawImage(Properties.settingsIcon, 0, 0, this.getWidth(), this.getHeight(), null);
			}
		};
		
		settingsButton.setLayout(null);
		settingsButton.setOpaque(false);
		
		JPanel devicesButton = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				g.drawImage(Properties.devicesIcon, 0, 0, this.getWidth(), this.getHeight(), null);
			}
		};
		
		devicesButton.setLayout(null);
		devicesButton.setOpaque(false);
		
		bar = new MenuBar(settingsButton, devicesButton, "Notifications");
		bar.setBounds(0, 0, getWidth(), Properties.multiplier * 32);
		bar.setBackground(Color.WHITE);
		add(bar);
		
		contentFrame.setBounds(0, Properties.multiplier * 32, getWidth(), getHeight() - Properties.multiplier * 32);
		contentFrame.setLayout(null);
		contentFrame.setBackground(Color.WHITE);
		add(contentFrame);
		
		content.setLayout(null);
		content.setBackground(Color.WHITE);
		content.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int y = (int)Math.round(content.getY() - (e.getPreciseWheelRotation() * Properties.multiplier * 16));
				
				//Limit the y coordinate to top  and bottom
				y = Math.max(Math.min(y, 0), -content.getHeight() + contentFrame.getHeight());
				content.setLocation(content.getX(), y);
			}
		});
		
		refreshNotificationList();
		contentFrame.add(content);
	}
	
	public void refreshNotificationList() {
		content.removeAll();
		
		int count = 0;
		
		int groupCount = 0;
		int height = 0;
		for(Device device : Properties.devices) {
			for(ArrayList<Notification> group : device.groups) {
				JPanel notificationPanel = NotificationPopup.createPanel(group.get(0));
				
				if(groupCount > 0) height += Properties.multiplier * 10;
				notificationPanel.setLocation(0, height);
				height += notificationPanel.getHeight();
				
				groupCount++;
				content.add(notificationPanel);
				
				count += group.size();
			}
		}
		
		bar.setTitle(count + " Notification" + (count != 1 ? "s" : ""));
		
		content.setBounds(0, 0, contentFrame.getWidth(), height);
		repaint();
	}
}