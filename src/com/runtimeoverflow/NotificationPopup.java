package com.runtimeoverflow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.PlainDocument;

import com.runtimeoverflow.Objects.Notification;
import com.runtimeoverflow.Utilities.Properties;

@SuppressWarnings("serial")
public class NotificationPopup extends JFrame {
	private static int cornerRadius = 13;
	public int targetY = -1; //this variable sets the target position, to which it will animate to
	
	//Constructor
	public NotificationPopup(Notification notification) {
		super();
		cornerRadius *= Properties.multiplier;
		
		//Set background invisible
		getContentPane().setBackground(new Color(0, 0, 0, 0));
		getRootPane().setBackground(new Color(0, 0, 0, 0));
		
		JPanel panel = createPanel(notification);
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
				if(isVisible() && getOpacity() == 1f) {
					Notification.removeNotification(self);
				}
				
				((Timer)e.getSource()).stop();
			}
		});
		t.setInitialDelay(10000);
		t.start();
		
		//Creating the click listener, which will hide the notification on right click
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					if(isVisible() && getOpacity() == 1f) {
						t.stop();
						Notification.removeNotification(self);
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
	}
	
	public static JPanel createPanel(Notification notification) {
		cornerRadius = 13 * Properties.multiplier;
		
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
		JLabel icon = new JLabel(new ImageIcon(notification.getIcon().getScaledInstance(Properties.multiplier * 20, Properties.multiplier * 20, Image.SCALE_SMOOTH)));
		JLabel app = new JLabel(Properties.filterSpecialCharacters(notification.app.toUpperCase(), Properties.font));
		JLabel date = new JLabel(notification.stringFromDate(), SwingConstants.RIGHT);
		JLabel title = new JLabel(Properties.filterSpecialCharacters(notification.title, Properties.font));
		JLabel subtitle = new JLabel(Properties.filterSpecialCharacters(notification.subtitle, Properties.font));
		JTextArea body = new JTextArea(Properties.filterSpecialCharacters(notification.body, Properties.font));
		JLabel attachment = new JLabel(new ImageIcon(notification.getAttachment().getScaledInstance(Properties.multiplier * 35, Properties.multiplier * 35, Image.SCALE_SMOOTH)));
		
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
		if(!notification.title.isEmpty()) content.add(title);
		if(!notification.subtitle.isEmpty()) content.add(subtitle);
		if(!notification.body.isEmpty()) content.add(body);
		if(!notification.attachment.isEmpty()) content.add(attachment);
		
		//Setting element sizes
		icon.setBounds(Properties.multiplier * 10, Properties.multiplier * 10, Properties.multiplier * 20, Properties.multiplier * 20);
		app.setBounds(Properties.multiplier * 35, Properties.multiplier * 8, Properties.multiplier * 174, Properties.multiplier * 24);
		date.setBounds(Properties.multiplier * 209, Properties.multiplier * 8, Properties.multiplier * 135, Properties.multiplier * 24);
		
		title.setBounds(Properties.multiplier * 12, Properties.multiplier * 6, (!notification.attachment.isEmpty() ? Properties.multiplier * 282 : Properties.multiplier * 332), (!notification.title.isEmpty() ? Properties.multiplier * 16 : Properties.multiplier * 0));
		subtitle.setBounds(Properties.multiplier * 12, title.getY() + title.getHeight() + (!notification.title.isEmpty() ? Properties.multiplier * 2 : Properties.multiplier * 0), (!notification.attachment.isEmpty() ? Properties.multiplier * 282 : Properties.multiplier * 332), (!notification.subtitle.isEmpty() ? Properties.multiplier * 16 : Properties.multiplier * 0));
		body.setBounds(Properties.multiplier * 12, subtitle.getY() + subtitle.getHeight() + (!notification.subtitle.isEmpty() ? Properties.multiplier * 2 : Properties.multiplier * 0), (!notification.attachment.isEmpty() ? Properties.multiplier * 282 : Properties.multiplier * 332), Properties.multiplier * 0);
		attachment.setBounds(Properties.multiplier * 309, Properties.multiplier * 6, Properties.multiplier * 35, (!notification.attachment.isEmpty() ? Properties.multiplier * 35 : Properties.multiplier * 0));
		
		body.setSize(body.getWidth(), body.getFontMetrics(body.getFont()).getHeight() * countLines(body));
		
		System.out.println(countLines(body) + " <> " + countLines2(body));
		
		content.setSize(content.getWidth(), Properties.multiplier * 10 + Math.max(body.getY() + body.getHeight(), attachment.getY() + attachment.getHeight()));
		
		//Creating the panel
		JPanel panel = new JPanel() {
			//Overriding the draw method to draw the rounded rectangle as background
			@Override
			protected void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				Graphics2D g = (Graphics2D) g2;
				
				//Enabling anti aliasing
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
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
		
		panel.add(header);
		panel.add(content);
		
		return panel;
	}
	
	//Utility method for calculation the amount of rows for the body
	private static int countLines(JTextArea textArea) {
		PlainDocument doc = (PlainDocument)textArea.getDocument();
		
		int count = 0;
		for (int i = 0; i < textArea.getLineCount(); i++) {
			try {
				int start = textArea.getLineStartOffset(i);
				int length = textArea.getLineEndOffset(i) - start;
				
				count += Math.max(1, Math.ceil(textArea.getFontMetrics(textArea.getFont()).stringWidth(doc.getText(start, length)) / (double)textArea.getWidth()));
			} catch (javax.swing.text.BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		return count;
	}
	
	private static int countLines2(JTextArea textArea) {
	    AttributedString text = new AttributedString(textArea.getText());
	    text.addAttribute(TextAttribute.FONT, textArea.getFont());
	    FontRenderContext frc = textArea.getFontMetrics(textArea.getFont()).getFontRenderContext();
	    AttributedCharacterIterator charIt = text.getIterator();
	    LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
	    Insets textAreaInsets = textArea.getInsets();
	    float formatWidth = textArea.getWidth() - textAreaInsets.left - textAreaInsets.right;
	    lineMeasurer.setPosition(charIt.getBeginIndex());

	    int noLines = 0;
	    while (lineMeasurer.getPosition() < charIt.getEndIndex())
	    {
	        lineMeasurer.nextLayout(formatWidth);
	        noLines++;
	    }

	    return noLines;
	}
	
	//Simple override to adjust the targetY when changing the position
	@Override
	public void setLocation(int x, int y) {
		if(targetY == getY() || targetY == -1) {
			targetY = y;
		}
		
		super.setLocation(x, y);
	}
}