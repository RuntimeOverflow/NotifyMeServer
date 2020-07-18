package com.runtimeoverflow.UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class MenuBar extends JPanel {
	private JPanel leftComponent;
	private JPanel rightComponent;
	private JLabel centerLabel;
	
	private boolean leftHovered = false;
	private boolean rightHovered = false;
	
	private ActionListener leftListener;
	private ActionListener rightListener;
	
	public MenuBar(Image leftIcon, Image rightIcon, String title) {
		setLayout(null);
		
		leftComponent = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Image img = leftIcon.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_AREA_AVERAGING);
				if(leftHovered) img = Properties.getImageWithColor(img, Color.GRAY);
				
				g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
			}
		};
		leftComponent.setLayout(null);
		leftComponent.setOpaque(false);
		leftComponent.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(leftListener != null) leftListener.actionPerformed(new ActionEvent(leftComponent, 0, ""));
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				leftHovered = false;
				leftComponent.repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				leftHovered = true;
				leftComponent.repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		add(leftComponent);
		
		rightComponent = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Image img = rightIcon.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_AREA_AVERAGING);
				if(rightHovered) img = Properties.getImageWithColor(img, Color.GRAY);
				
				g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
			}
		};
		rightComponent.setLayout(null);
		rightComponent.setOpaque(false);
		rightComponent.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(rightListener != null) rightListener.actionPerformed(new ActionEvent(rightComponent, 0, ""));
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				rightHovered = false;
				rightComponent.repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				rightHovered = true;
				rightComponent.repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		add(rightComponent);
		
		centerLabel = new JLabel(title);
		centerLabel.setHorizontalAlignment(JLabel.CENTER);
		add(centerLabel);
	}
	
	public void setLeftActionListener(ActionListener listener) {
		this.leftListener = listener;
	}
	
	public void setRightActionListener(ActionListener listener) {
		this.rightListener = listener;
	}
	
	public void setTitle(String title) {
		centerLabel.setText(title);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		
		leftComponent.setBounds(0, 0, height, height);
		rightComponent.setBounds(getWidth() - height, 0, height, height);
		
		centerLabel.setBounds(height, 0, getWidth() - 2 * height, height);
		float fontSize = Properties.getPointSizeForHeight((int)(0.8 * height), centerLabel.getFont());
		centerLabel.setFont(centerLabel.getFont().deriveFont(fontSize));
	}
}