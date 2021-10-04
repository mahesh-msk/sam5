package com.faiveley.samng.principal.ihm.vues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.eclipse.swt.graphics.Rectangle;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.progbar.VueWaitBarExplorer;



public class VueWaitBar {
	protected Rectangle rect;
	protected boolean isFinished = true;
	protected boolean isModal = true;
	
	public boolean isEscaped = false;
	
	
	public int courant = 0;
	public int fin = 99;
	
	private Thread t = null;
	
	private static VueWaitBar instance = new VueWaitBar();
	
	protected VueWaitBar() {
		// 
	}
	
	public static VueWaitBar getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return VueWaitBarExplorer.getInstance();
		}
		return instance;
	}
	
	/** Suppression de l'instance */
	public void clear(){
		t=null;
		isFinished = true;
		isModal = true;
		courant = 0;
		fin = 99;
		isEscaped = false;
		rect=null;
	}
	
	/**
	 * Starts the progress bar
	 */
	public void start() {
		start(true);
	}
	
	public synchronized void start(boolean modal) {
		if (!isWorking()) {
			this.isFinished = false;
			this.isModal = modal;
			this.isEscaped = false;
			
			this.t = new Thread(new MyRunnable());
			this.t.start();
		}
	}
	
	/**
	 * Stops the progress bar
	 */
	public synchronized void stop() {
		this.isFinished = true;
		if (this.t!=null) {
			this.t.interrupt();
		}
	}
	
	/**
	 * If it's working returns true
	 * @return
	 */
	public boolean isWorking() {
		return !this.isFinished;
	}
	
	/**
	 * @author meggy
	 *
	 */
	public class MyRunnable implements Runnable {
		
		private JLabel jLabel;
		private JPanel jContentPane = null;
		JFrame f = new JFrame();


		public void run() {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jLabel = new JLabel();
			jContentPane.add(jLabel, BorderLayout.CENTER);
			f.setContentPane(jContentPane);
			
			jLabel.setText(Messages.getString("WaitBar.0"));
			jLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jContentPane.setBorder(BorderFactory.createLineBorder (new Color(155,147,150), 1));
			
			f.setUndecorated(true);

			int x = 300, y = 40;
			f.setPreferredSize(new Dimension(x, y));
	        jContentPane.setBackground(new Color(255,255,255));
	        f.setLocation(
	        		VueWaitBar.this.rect.x + VueWaitBar.this.rect.width / 2 - x /2, 
	        		VueWaitBar.this.rect.y + VueWaitBar.this.rect.height / 2 - y /2);


	        f.pack();
	        f.setVisible(true);
	        f.setAlwaysOnTop(true);
	        
	        
			while (!VueWaitBar.this.isFinished) {
		        try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
				}
			}
		  f.setVisible(false);	
		}


	}

	/**
	 * @return the rect
	 */
	public Rectangle getRect() {
		return this.rect;
	}

	/**
	 * @param rect the rect to set
	 */
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
}
