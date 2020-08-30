package com.runtimeoverflow.UI;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.runtimeoverflow.Window;
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
		
		bar = new MenuBar(Properties.settingsIcon, Properties.devicesIcon, "Notifications");
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
				int y = (int)Math.round(content.getY() - (e.getPreciseWheelRotation() * Properties.multiplier * 24));
				
				//Limit the y coordinate to top  and bottom
				y = Math.max(Math.min(y, 0), -content.getHeight() + contentFrame.getHeight());
				if(content.getHeight() > contentFrame.getHeight()) content.setLocation(content.getX(), y);
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
				Notification clone = group.get(0).copy();
				if(group.size() > 1) {
					clone.body += "\n\n" + Integer.toString(group.size() - 1) + " more notification" + (group.size() - 1 != 1 ? "s" : "");
				}
				
				JPanel notificationPanel = clone.createPanel();
				
				if(groupCount > 0) height += Properties.multiplier * 10;
				notificationPanel.setLocation(0, height);
				height += notificationPanel.getHeight();
				
				notificationPanel.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
						NotificationGroupScene groupScene = new NotificationGroupScene(group);
						Window.get().setScene(groupScene);
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