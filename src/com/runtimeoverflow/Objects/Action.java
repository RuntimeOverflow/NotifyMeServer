package com.runtimeoverflow.Objects;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.runtimeoverflow.Utilities.Properties;

public class Action {
	//Properties of an action
	public String id = "";
	public boolean text = false;
	public String title = "";
	
	public JPanel createPanel() {
		@SuppressWarnings("serial")
		JPanel actionPanel = new JPanel() {
			boolean mouseListenerAdded = false;
			boolean hovered = false;
			
			@Override
			public void setBounds(int x, int y, int width, int height) {
				super.setBounds(x, y, width, height);
				
				if(!mouseListenerAdded) {
					addMouseListener(new MouseListener() {
						@Override
						public void mouseReleased(MouseEvent e) {}
						
						@Override
						public void mousePressed(MouseEvent e) {}
						
						@Override
						public void mouseExited(MouseEvent e) {
							hovered = false;
							repaint();
						}
						
						@Override
						public void mouseEntered(MouseEvent e) {
							hovered = true;
							repaint();
						}
						
						@Override
						public void mouseClicked(MouseEvent e) {}
					});
					
					mouseListenerAdded = true;
				}
			}
			
			@Override
			protected void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				Graphics2D g = (Graphics2D) g2;
				
				//Enabling anti aliasing
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				int cornerRadius = 13 * Properties.multiplier;
				
				//Creating the rounded rectangle with rectangles and circles
				Area a = new Area();
				a.add(new Area(new Rectangle2D.Double(cornerRadius, 0, getWidth() - 2 * cornerRadius, getHeight())));
				a.add(new Area(new Rectangle2D.Double(0, cornerRadius, getWidth(), getHeight() - 2 * cornerRadius)));
				
				a.add(new Area(new Ellipse2D.Double(0, 0, 2 * cornerRadius, 2 * cornerRadius)));
				a.add(new Area(new Ellipse2D.Double(getWidth() - 2 * cornerRadius, 0, 2 * cornerRadius, 2 * cornerRadius)));
				a.add(new Area(new Ellipse2D.Double(0, getHeight() - 2 * cornerRadius, 2 * cornerRadius, 2 * cornerRadius)));
				a.add(new Area(new Ellipse2D.Double(getWidth() - 2 * cornerRadius, getHeight() - 2 * cornerRadius, 2 * cornerRadius, 2 * cornerRadius)));
				
				//Setting the color and drawing the rounded rectangle
				g.setColor(!text && hovered ? new Color(200, 200, 200, 224) : new Color(240, 240, 240, 224));
				g.fill(a);
			}
		};
		
		actionPanel.setLayout(null);
		actionPanel.setBounds(0, 0, Properties.multiplier * 359, Properties.multiplier * 55);
		actionPanel.setBackground(new Color(0, 0, 0, 0));
		actionPanel.setOpaque(false);
		
		if(!text) {
			//Create a label, which will display the title (for some reason, the clear action doesn't have a title, so it needs to be set manually)
			JLabel label = new JLabel(!title.isEmpty() ? title : "Clear");
			label.setBounds(Properties.multiplier * 10, Properties.multiplier * 10, actionPanel.getWidth() - 2 * Properties.multiplier * 10, actionPanel.getHeight() - 2 * Properties.multiplier * 10);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(Properties.font.deriveFont(Properties.getPointSizeForHeight(Properties.multiplier * 14, Properties.font)));
			actionPanel.add(label);
			
			actionPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			@SuppressWarnings("serial")
			JPanel button = new JPanel() {
				boolean mouseListenerAdded = false;
				boolean hovered = false;
				
				@Override
				public void setBounds(int x, int y, int width, int height) {
					super.setBounds(x, y, width, height);
					
					if(!mouseListenerAdded) {
						addMouseListener(new MouseListener() {
							@Override
							public void mouseReleased(MouseEvent e) {}
							
							@Override
							public void mousePressed(MouseEvent e) {}
							
							@Override
							public void mouseExited(MouseEvent e) {
								hovered = false;
								repaint();
							}
							
							@Override
							public void mouseEntered(MouseEvent e) {
								hovered = true;
								repaint();
							}
							
							@Override
							public void mouseClicked(MouseEvent e) {}
						});
						
						mouseListenerAdded = true;
					}
				}
				
				@Override
				protected void paintComponent(Graphics g2) {
					super.paintComponent(g2);
					Graphics2D g = (Graphics2D) g2;
					
					//Enabling anti aliasing
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					int cornerRadius = Properties.multiplier * 8;
					
					//Creating the rounded rectangle with rectangles and circles
					Area a = new Area();
					a.add(new Area(new Rectangle2D.Double(cornerRadius, 0, getWidth() - 2 * cornerRadius, getHeight())));
					a.add(new Area(new Rectangle2D.Double(0, cornerRadius, getWidth(), getHeight() - 2 * cornerRadius)));
					
					a.add(new Area(new Ellipse2D.Double(0, 0, 2 * cornerRadius, 2 * cornerRadius)));
					a.add(new Area(new Ellipse2D.Double(getWidth() - 2 * cornerRadius, 0, 2 * cornerRadius, 2 * cornerRadius)));
					a.add(new Area(new Ellipse2D.Double(0, getHeight() - 2 * cornerRadius, 2 * cornerRadius, 2 * cornerRadius)));
					a.add(new Area(new Ellipse2D.Double(getWidth() - 2 * cornerRadius, getHeight() - 2 * cornerRadius, 2 * cornerRadius, 2 * cornerRadius)));
					
					//Setting the color and drawing the rounded rectangle, when the cursor is over the button
					g.setColor(new Color(180, 180, 180));
					if(hovered) g.fill(a);
					
					//Draw the String in the middle with the set font and color
					g.setColor(getForeground());
					g.setFont(getFont());
					g.drawString(title, getWidth() / 2 - getFontMetrics(getFont()).stringWidth(title) / 2, getHeight() / 2 - getFontMetrics(getFont()).getHeight() / 2 + getFontMetrics(getFont()).getAscent());
				}
			};
			button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			button.setFont(Properties.font.deriveFont(Properties.getPointSizeForHeight(Properties.multiplier * 14, Properties.font)));
			button.setSize(button.getFontMetrics(button.getFont()).stringWidth(title) + 2 * Properties.multiplier * 10, actionPanel.getHeight() - 2 * Properties.multiplier * 10);
			button.setLocation(actionPanel.getWidth() - button.getWidth() - Properties.multiplier * 10, Properties.multiplier * 10);
			actionPanel.add(button);
			
			@SuppressWarnings("serial")
			JTextField field = new JTextField() {
				boolean mouseListenerAdded = false;
				boolean hovered = false;
				
				@Override
				public void setBounds(int x, int y, int width, int height) {
					super.setBounds(x, y, width, height);
					
					if(!mouseListenerAdded) {
						addMouseListener(new MouseListener() {
							@Override
							public void mouseReleased(MouseEvent e) {}
							
							@Override
							public void mousePressed(MouseEvent e) {}
							
							@Override
							public void mouseExited(MouseEvent e) {
								hovered = false;
								repaint();
							}
							
							@Override
							public void mouseEntered(MouseEvent e) {
								hovered = true;
								repaint();
							}
							
							@Override
							public void mouseClicked(MouseEvent e) {}
						});
						
						mouseListenerAdded = true;
					}
				}
				
				@Override
				protected void paintComponent(Graphics g2) {
					Graphics2D g = (Graphics2D) g2;
					
					//Enabling anti aliasing
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					int cornerRadius = Properties.multiplier * 3;
					
					int x = 0;
					int y = (getHeight() - getFontMetrics(getFont()).getHeight()) / 2 - 2 * Properties.multiplier;
					int width = getWidth();
					int height = getFontMetrics(getFont()).getHeight() + 4 * Properties.multiplier;
					
					//Creating the rounded rectangle with rectangles and circles
					Area a = new Area();
					a.add(new Area(new Rectangle2D.Double(x + cornerRadius, y, width - 2 * cornerRadius, height)));
					a.add(new Area(new Rectangle2D.Double(x, y + cornerRadius, width, height - 2 * cornerRadius)));
					
					a.add(new Area(new Ellipse2D.Double(x, y, 2 * cornerRadius, 2 * cornerRadius)));
					a.add(new Area(new Ellipse2D.Double(x + width - 2 * cornerRadius, y, 2 * cornerRadius, 2 * cornerRadius)));
					a.add(new Area(new Ellipse2D.Double(x, y + height - 2 * cornerRadius, 2 * cornerRadius, 2 * cornerRadius)));
					a.add(new Area(new Ellipse2D.Double(x + width - 2 * cornerRadius, y + height - 2 * cornerRadius, 2 * cornerRadius, 2 * cornerRadius)));
					
					//Setting the color and drawing the rounded rectangle
					g.setColor(hovered ? new Color(180, 180, 180) : new Color(200, 200, 200));
					g.fill(a);
					
					super.paintComponent(g2);
				}
			};
			field.setOpaque(false);
			field.setBounds(Properties.multiplier * 10, Properties.multiplier * 10, actionPanel.getWidth() - button.getWidth() - 3 * Properties.multiplier * 10, actionPanel.getHeight() - 2 * Properties.multiplier * 10);
			field.setFont(Properties.font.deriveFont(Properties.getPointSizeForHeight(Properties.multiplier * 14, Properties.font)));
			field.setBorder(BorderFactory.createEmptyBorder());
			field.setBackground(new Color(0, 0, 0, 0));
			actionPanel.add(field);
		}
	
		return actionPanel;
	}
}