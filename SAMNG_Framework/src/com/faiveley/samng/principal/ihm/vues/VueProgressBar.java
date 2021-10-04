package com.faiveley.samng.principal.ihm.vues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.eclipse.swt.graphics.Rectangle;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.progbar.VueProgressBarExplorer;



public class VueProgressBar {
	protected Rectangle rect = new Rectangle(0,0,0,0);
	protected boolean isFinished = true;
	protected boolean isModal = true;
	
	public boolean isEscaped = false;
	
	
	public int courant = 0;
	public int fin = 99;
	public String libelle="";
	
	public Thread t = null;
	
	private static VueProgressBar instance = new VueProgressBar();
	
	protected VueProgressBar() {
		// 
	}
	
	public static VueProgressBar getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return VueProgressBarExplorer.getInstance();
		}
		return instance;
	}
	
	/** Suppression de l'instance */
	public void clear(){
		//rect = new Rectangle(0,0,0,0);
		isFinished = true;
		isModal = true;
		isEscaped = false;
		courant = 0;
		fin = 99;
		libelle="";
		t=null;
		rect=null;
	}
	
	/**
	 * Starts the progress bar
	 */
	public void start() {
		start(true);
	}
	
	public void refresh(){
		if (!isWorking()) {
			this.isFinished = false;
			this.isModal = true;
			this.isEscaped = false;
			
			this.t = new Thread(new MyRunnable());
			this.t.setPriority(Thread.MAX_PRIORITY);
			this.t.start();
		}
	}
	
	public synchronized void start(boolean modal) {
		
		if (!isWorking()) {
			this.isFinished = false;
			this.isModal = modal;
			this.isEscaped = false;
			
			this.t = new Thread(new MyRunnable());
			this.t.setPriority(Thread.MAX_PRIORITY);
			this.t.start();
		}
	}
	
	/**
	 * Stops the progress bar
	 */
	public synchronized void stop() {
		this.isFinished = true;
		if (t!=null) {
			this.t.interrupt();
		}
		this.clear();
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
		
		private JProgressBar progbar;
		private JPanel jContentPane = null;
		JFrame f = new JFrame();
		JLabel lab = new JLabel();
		


		public void run() {
			
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			progbar = new JProgressBar();
			jContentPane.add(progbar, BorderLayout.CENTER);
			// Remplacer cette chaine de caractères
			lab.setText(libelle);
			lab.setHorizontalAlignment(SwingConstants.CENTER);
			jContentPane.add(lab, BorderLayout.NORTH);
			f.setContentPane(jContentPane);
			
	    	courant = 0;
	        progbar.setMinimum(0);
	        progbar.setMaximum(fin);

			f.setUndecorated(true);

			int x = 300, y = 40;
			f.setPreferredSize(new Dimension(x, y));
	        jContentPane.setBackground(new Color(155,147,150));
	        f.setLocation(
	        		VueProgressBar.this.rect.x + VueProgressBar.this.rect.width / 2 - x /2, 
	        		VueProgressBar.this.rect.y + VueProgressBar.this.rect.height / 2 - y /2);


	        f.pack();
	        f.setVisible(true);
//	        f.setAlwaysOnTop(true);
	        f.addKeyListener(new KeyListener(){
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
						VueProgressBar.getInstance().isEscaped = true;
					}
					
				}
	
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
	        });
	        
	        f.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					f.requestFocus();
				}

				public void focusLost(FocusEvent e) {
					f.toFront();
				}
			});	 
	        
	        
			while (!VueProgressBar.this.isFinished) {
		        progbar.setMinimum(0);
		        progbar.setMaximum(fin);
				progbar.setValue(courant);
		        try {
					Thread.sleep(200);
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

	public int getCourant() {
		return courant;
	}

	public void setCourant(int courant) {
		if (!(courant>=this.fin)) {
			this.courant = courant;
		}
		
	}
}
