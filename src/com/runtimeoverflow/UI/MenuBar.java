package com.runtimeoverflow.UI;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class MenuBar extends JPanel {
	private JComponent leftComponent;
	private JComponent rightComponent;
	private JLabel centerLabel;
	
	public MenuBar(JComponent left, JComponent right, String title) {
		setLayout(null);
		
		leftComponent = left;
		add(leftComponent);
		
		rightComponent = right;
		add(rightComponent);
		
		centerLabel = new JLabel(title);
		centerLabel.setHorizontalAlignment(JLabel.CENTER);
		add(centerLabel);
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