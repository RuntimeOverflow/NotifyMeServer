package com.runtimeoverflow;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.runtimeoverflow.Objects.Device;
import com.runtimeoverflow.Objects.Notification;
import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class Scene extends JPanel {
	JPanel content = new JPanel();
	JPanel contentFrame = new JPanel();
	
	public Scene() {
		setLayout(null);
		setSize(Properties.multiplier * 359, Properties.multiplier * 78 * 6 + Properties.multiplier * 32);
		
		JPanel bar = new JPanel();
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
		int height = 0;
		for(Device device : Properties.devices) {
			for(ArrayList<Notification> group : device.groups) {
				//for(Notification notification : group) {
					JPanel notificationPanel = NotificationPopup.createPanel(group.get(0));
					
					if(count > 0) height += Properties.multiplier * 10;
					notificationPanel.setLocation(0, height);
					height += notificationPanel.getHeight();
					
					count++;
					content.add(notificationPanel);
				//}
			}
		}
		
		content.setBounds(0, 0, contentFrame.getWidth(), height);
		repaint();
	}
}