package com.runtimeoverflow.Objects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.runtimeoverflow.Utilities.Properties;
import com.runtimeoverflow.Utilities.Utilities;

public class Notification {
	//Properties of a notification
	public String title = "";
	public String subtitle = "";
	public String body = "";
	public String bundleId = "";
	public String threadId = "";
	public String id = "";
	public String category = "";
	public String app = "";
	public String icon = "";
	public String attachment = "";
	public long date = 0;
	
	public Action dismissAction = null;
	public ArrayList<Action> actions = null;
	
	//Creates the app icon from the base64 encoded string
	public Image getIcon() {
		if(icon.isEmpty()) {
			BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setColor(new Color(0, 0, 0, 0));
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.dispose();
			return img;
		}
		
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(icon));
			Image img = ImageIO.read(stream);
			stream.close();
			return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Creates the attachment image from the base64 encoded string (if there is one)
	public Image getAttachment() {
		//If there is no image, return a transparent one
		if(attachment.isEmpty()) {
			BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setColor(new Color(0, 0, 0, 0));
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.dispose();
			return img;
		}
		
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(attachment));
			Image img = ImageIO.read(stream);
			stream.close();
			return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Creates a calendar object from the date variable
	public Calendar getCalendar() {
		Calendar published = Calendar.getInstance();
		published.setTimeInMillis(date);
		
		return published;
	}
	
	//Creates a duplicate of this notification, which can be modified later
	public Notification copy() {
		Notification n = new Notification();
		
		n.title = title;
		n.subtitle = subtitle;
		n.body = body;
		n.bundleId = bundleId;
		n.threadId = threadId;
		n.id = id;
		n.category = category;
		n.app = app;
		n.icon = icon;
		n.attachment = attachment;
		n.date = date;
		
		return n;
	}
	
	//Creates a view, which displays the notification
	public JPanel createPanel() {
		//Header view
		JPanel header = new JPanel();
		header.setBounds(Properties.multiplier * 0, Properties.multiplier * 0, Properties.multiplier * 359, Properties.multiplier * 30);
		header.setLayout(null);
		header.setOpaque(false);
		header.setBackground(new Color(0, 0, 0, 0));
		
		//Content view
		JPanel content = new JPanel();
		content.setBounds(Properties.multiplier * 0, Properties.multiplier * 30, Properties.multiplier * 359, 0);
		content.setLayout(null);
		content.setOpaque(false);
		content.setBackground(new Color(0, 0, 0, 0));
		
		//UI elements
		JLabel icon = new JLabel(new ImageIcon(getIcon().getScaledInstance(Properties.multiplier * 20, Properties.multiplier * 20, Image.SCALE_SMOOTH)));
		JLabel app = new JLabel(Properties.filterSpecialCharacters(this.app.toUpperCase(), Properties.font));
		JLabel date = new JLabel(Utilities.stringFromDate(getCalendar()), SwingConstants.RIGHT);
		JLabel title = new JLabel(Properties.filterSpecialCharacters(this.title, Properties.font));
		JLabel subtitle = new JLabel(Properties.filterSpecialCharacters(this.subtitle, Properties.font));
		JTextArea body = new JTextArea(Properties.filterSpecialCharacters(this.body, Properties.font));
		JLabel attachment = new JLabel(new ImageIcon(getAttachment().getScaledInstance(Properties.multiplier * 35, Properties.multiplier * 35, Image.SCALE_SMOOTH)));
		
		//Setting properties of the body element
		body.setWrapStyleWord(true);
		body.setLineWrap(true);
		body.setEditable(false);
		body.setBackground(new Color(0, 0, 0, 0));
		body.setOpaque(false);
		body.setHighlighter(null);
		body.setCursor(null);
		body.setFocusable(false);
		
		//Calculation font sizes
		float smallSize = Properties.getPointSizeForHeight(Properties.multiplier * 13, Properties.font);
		float normalSize = Properties.getPointSizeForHeight(Properties.multiplier * 14, Properties.font);
		
		//Setting font
		app.setFont(Properties.font.deriveFont(smallSize));
		date.setFont(Properties.font.deriveFont(smallSize));
		title.setFont(Properties.boldFont.deriveFont(normalSize));
		subtitle.setFont(Properties.boldFont.deriveFont(normalSize));
		body.setFont(Properties.font.deriveFont(normalSize));
		
		//Setting text colors
		app.setForeground(Color.GRAY);
		date.setForeground(Color.GRAY);
		
		//Adding all elements
		header.add(icon);
		header.add(app);
		header.add(date);
		if(!this.title.isEmpty()) content.add(title);
		if(!this.subtitle.isEmpty()) content.add(subtitle);
		if(!this.body.isEmpty()) content.add(body);
		if(!this.attachment.isEmpty()) content.add(attachment);
		
		//Setting element sizes
		icon.setBounds(Properties.multiplier * 10, Properties.multiplier * 10, Properties.multiplier * 20, Properties.multiplier * 20);
		app.setBounds(Properties.multiplier * 35, Properties.multiplier * 8, Properties.multiplier * 174, Properties.multiplier * 24);
		date.setBounds(Properties.multiplier * 209, Properties.multiplier * 8, Properties.multiplier * 135, Properties.multiplier * 24);
		
		title.setBounds(Properties.multiplier * 12, Properties.multiplier * 6, (!this.attachment.isEmpty() ? Properties.multiplier * 282 : Properties.multiplier * 332), (!this.title.isEmpty() ? Properties.multiplier * 16 : Properties.multiplier * 0));
		subtitle.setBounds(Properties.multiplier * 12, title.getY() + title.getHeight() + (!this.title.isEmpty() ? Properties.multiplier * 2 : Properties.multiplier * 0), (!this.attachment.isEmpty() ? Properties.multiplier * 282 : Properties.multiplier * 332), (!this.subtitle.isEmpty() ? Properties.multiplier * 16 : Properties.multiplier * 0));
		body.setBounds(Properties.multiplier * 12, subtitle.getY() + subtitle.getHeight() + (!this.subtitle.isEmpty() ? Properties.multiplier * 2 : Properties.multiplier * 0), (!this.attachment.isEmpty() ? Properties.multiplier * 282 : Properties.multiplier * 332), Properties.multiplier * 0);
		attachment.setBounds(Properties.multiplier * 309, Properties.multiplier * 6, Properties.multiplier * 35, (!this.attachment.isEmpty() ? Properties.multiplier * 35 : Properties.multiplier * 0));
		
		body.setSize(body.getWidth(), body.getFontMetrics(body.getFont()).getHeight() * Utilities.countLines(body));
		
		content.setSize(content.getWidth(), Properties.multiplier * 10 + Math.max(body.getY() + body.getHeight(), attachment.getY() + attachment.getHeight()));
		
		//Creating the panel
		@SuppressWarnings("serial")
		JPanel panel = new JPanel() {
			//Overriding the draw method to draw the rounded rectangle as background
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
				g.setColor(new Color(240, 240, 240, 224));
				g.fill(a);
			}
		};
		
		//Setting window properties
		panel.setSize(Properties.multiplier * 359, header.getHeight() + content.getHeight());
		panel.setLayout(null);
		panel.setBackground(new Color(0, 0, 0, 0));
		panel.setOpaque(false);
		
		//Add the header and content panel
		panel.add(header);
		panel.add(content);
		
		for(Component c : panel.getComponents()) {
			for(Component c2 : ((JPanel)c).getComponents()) {
				c2.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
						for(MouseListener ml : panel.getMouseListeners()) ml.mouseReleased(e);
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
		
		return panel;
	}
}
