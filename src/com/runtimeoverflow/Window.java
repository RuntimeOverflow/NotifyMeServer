package com.runtimeoverflow;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Window extends JFrame {
	private static Window instance;
	JPanel scene = null;
	
	//Private constructor
	private Window() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);
		setLayout(null);
		setName("");
		setTitle(getName());
	}
	
	//Shortcut for getWindow()
	public static Window get() {
		return getWindow();
	}
	
	//This class is a singleton and this is like the sharedInstance function
	public static Window getWindow() {
		if (Window.instance == null) {
			Window.instance = new Window();
		}
		
		return Window.instance;
	}
	
	//Shortcut for setScene()
	public void set(JPanel scene){
		setScene(scene);
	}
	
	//Setting the main view and adjusting the size of the window
	public void setScene(JPanel scene) {
		if(scene.getName() != null && !scene.getName().isEmpty()) setTitle(scene.getName());
		
		//if(this.scene != null) remove(this.scene);
		this.scene = scene;
		
		scene.setLayout(null);
		scene.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				scene.requestFocus();
			}
		});
		
		scene.setPreferredSize(scene.getSize());
		setContentPane(scene);
		pack();
		
		scene.requestFocus();
	}
	
	public JPanel getScene() {
		return scene;
	}
}
