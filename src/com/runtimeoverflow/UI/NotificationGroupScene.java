package com.runtimeoverflow.UI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.runtimeoverflow.Window;
import com.runtimeoverflow.Objects.Action;
import com.runtimeoverflow.Objects.Notification;
import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class NotificationGroupScene extends JPanel {
	private JPanel content = new JPanel();
	private JPanel contentFrame = new JPanel();
	private MenuBar bar = null;
	
	private ArrayList<Notification> group = new ArrayList<Notification>();
	private ArrayList<Boolean> expanded = new ArrayList<Boolean>();
	
	public NotificationGroupScene(ArrayList<Notification> group) {
		this.group = group;
		
		for(int i = 0; i < group.size(); i++) expanded.add(false);
		
		setLayout(null);
		setSize(Properties.multiplier * 359, Properties.multiplier * 78 * 6 + Properties.multiplier * 32);
		
		bar = new MenuBar(Properties.backIcon, Properties.clearIcon, group.size() > 0 ? group.get(0).app : "Notifications");
		bar.setBounds(0, 0, getWidth(), Properties.multiplier * 32);
		bar.setBackground(Color.WHITE);
		add(bar);
		
		bar.setLeftActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window.get().setScene(Properties.mainScene);
			}
		});
		
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
		
		int height = 0;
		for(Notification n : group) {
			JPanel notificationPanel = n.createPanel();
			
			if(group.indexOf(n) != 0) height += Properties.multiplier * 10;
			notificationPanel.setLocation(0, height);
			height += notificationPanel.getHeight();
			
			notificationPanel.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					expanded.set(group.indexOf(n), !expanded.get(group.indexOf(n)));
					refreshNotificationList();
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
			
			if(expanded.get(group.indexOf(n))) {
				ArrayList<Action> actions = new ArrayList<Action>();
				if(n.dismissAction != null) actions.add(n.dismissAction);
				actions.addAll(n.actions);
				
				for(Action a : actions) {
					JPanel actionPanel = a.createPanel();
					actionPanel.setLocation(0, height + 10);
					height += 10 * Properties.multiplier + actionPanel.getHeight();
					
					content.add(actionPanel);
				}
			}
			
			content.add(notificationPanel);
		}
		
		content.setBounds(0, 0, contentFrame.getWidth(), height);
		repaint();
	}
}
