package com.runtimeoverflow;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.runtimeoverflow.Objects.Action;
import com.runtimeoverflow.Objects.Notification;
import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class NotificationPopup extends JFrame {
	public Notification notification;
	public int targetY = -1; //This variable sets the target position, to which it will animate to
	public boolean expanded = false;
	
	public ArrayList<JFrame> actionWindows = new ArrayList<JFrame>();
	
	//Constructor
	public NotificationPopup(Notification notification) {
		super();
		
		this.notification = notification;
		
		//Set background invisible
		getContentPane().setBackground(new Color(0, 0, 0, 0));
		getRootPane().setBackground(new Color(0, 0, 0, 0));
		
		JPanel panel = notification.createPanel();
		panel.setLocation(0, 0);
		add(panel);
		
		//Click passthrough
		for(Component c : panel.getComponents()) {
			for(Component c2 : ((JPanel)c).getComponents()) {
				c2.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
						for(MouseListener ml : getMouseListeners()) ml.mouseReleased(e);
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
			}
		}
		
		//Setting the window properties
		setLayout(null);
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		setSize(panel.getSize());
		setAlwaysOnTop(true);
		setType(JFrame.Type.UTILITY);
		setFocusableWindowState(false);
		setFocusable(false);
		
		//Creating the timer, which will make the notification disappear after 10 seconds
		final NotificationPopup self = this;
		final Timer t = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isVisible() && getOpacity() == 1f && !expanded) {
					self.removeNotification();
				}
				
				if(expanded) {
					((Timer)e.getSource()).setDelay(50);
				} else ((Timer)e.getSource()).stop();
			}
		});
		t.setInitialDelay(10000);
		t.start();
		
		//Creating the click listener, which will hide the notification on right click
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					self.toggleNotification();
				} else if(e.getButton() == MouseEvent.BUTTON3) {
					if(isVisible() && getOpacity() == 1f) {
						t.stop();
						self.removeNotification();
					}
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
		
		ArrayList<Action> actions = new ArrayList<Action>();
		actions.add(notification.dismissAction);
		actions.addAll(notification.actions);
		
		for(Action action : actions) {
			JFrame frame = new JFrame();
			
			//Set background invisible
			frame.getContentPane().setBackground(new Color(0, 0, 0, 0));
			frame.getRootPane().setBackground(new Color(0, 0, 0, 0));
			
			frame.setLayout(null);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setAlwaysOnTop(true);
			frame.setType(JFrame.Type.UTILITY);
			
			JPanel actionPanel = action.createPanel();
			frame.setSize(actionPanel.getSize());
			
			frame.add(actionPanel);
			
			frame.setVisible(false);
			actionWindows.add(frame);
		}
	}
	
	public void showActions() {
		int y = getY() + getHeight() + 10 * Properties.multiplier;
		for(JFrame frame : actionWindows) {
			frame.setLocation(getX(), y);
			y += (55 + 10) * Properties.multiplier;
			
			frame.repaint();
			frame.setVisible(true);
			frame.repaint();
		}
	}
	
	public void hideActions() {
		for(JFrame frame : actionWindows) frame.setVisible(false);
	}
	
	//Simple override to adjust the targetY when changing the position
	@Override
	public void setLocation(int x, int y) {
		if(targetY == getY() || targetY == -1) {
			targetY = y;
		}
		
		super.setLocation(x, y);
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
	
	//Expands the notification, if it is contracted
	public void expandNotification() {
		if(!expanded) toggleNotification();
	}
	
	//Contracts the notification, if it is expanded
	public void contractNotification() {
		if(expanded) toggleNotification();
	}
	
	//Function, which will expand/contract a displayed popup to display/hide the quick actions
	public void toggleNotification() {
		final int height = ((notification.actions.size() + 1) * Properties.multiplier * 65) * (expanded ? -1 : 1);
		boolean expanding = !expanded;
		expanded = !expanded;
		
		//Making all popups, which were above this one, go up or down
		for(NotificationPopup np : Properties.popups.subList(0, Properties.popups.indexOf(this) + 1)) np.targetY -= height;
		
		//Repaint to remove the actions when contracting
		if(!expanding) hideActions();
		
		//Timer, which will wait until there is enough space to display the notification
		Timer t = new Timer(25, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(targetY == getY()) {
					//Repaint to add the actions when expanding and stops the timer
					if(expanding) showActions();
					((Timer)e.getSource()).stop();
				}
			}
		});
		if(expanding) t.start();
	}
	
	//Function, which will remove a displayed popup
	public void removeNotification() {
		final int height = getHeight();
		
		//If the notification shows action, hide them
		contractNotification();
		
		//Variable for accessing this instance inside of the ActionListener
		NotificationPopup self = this;
		
		//Timer, which will fade out the notification
		Timer t = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setOpacity(getOpacity() - 0.25f);
				
				if(getOpacity() == 0f) {
					((Timer) e.getSource()).stop();
					
					setVisible(false);
					
					//Making all popups, which were above this one, go down
					for(NotificationPopup np : Properties.popups.subList(0, Properties.popups.indexOf(self))) np.targetY += height + 10;
					
					Properties.popups.remove(self);
				}
			}
		});
		t.start();
	}
}