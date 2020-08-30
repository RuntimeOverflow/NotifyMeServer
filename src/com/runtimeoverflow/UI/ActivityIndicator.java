package com.runtimeoverflow.UI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class ActivityIndicator extends JPanel {
	private Timer timer;
	private int state = 0;
	
	public ActivityIndicator() {
		super();
		
		setLayout(null);
		setOpaque(false);
		
		timer = new Timer(96, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state++;
				state %= 8;
				repaint();
			}
		});
		timer.start();
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if(aFlag)timer.start();
		else timer.stop();
		
		super.setVisible(aFlag);
	}
	
	@Override
	public void paintComponent(Graphics g2) {
		super.paintComponent(g2);
		
		Graphics2D g = (Graphics2D) g2;
		g.rotate(Math.toRadians(360 / 8 * state), getWidth() / 2,  getHeight() / 2);
		g.drawImage(Properties.indicatorImage, 0, 0, getWidth(), getHeight(), null);
	}
}