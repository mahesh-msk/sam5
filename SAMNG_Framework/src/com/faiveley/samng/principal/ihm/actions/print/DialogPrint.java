package com.faiveley.samng.principal.ihm.actions.print;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DialogPrint {

	public static final String messageTitre=Messages.getString("TitreImpression");
	public static final String impression=Messages.getString("ImpressionVueTableau.1");
	public static final String voulezvousimprimer=Messages.getString("ImpressionVueTableau.2");
	public static final String totalite=Messages.getString("ImpressionVueTableau.7");
	public static final String zoneSelection=Messages.getString("ImpressionVueTableau.8");
	public static final String imprimer=Messages.getString("ImpressionVueTableau.9");
	public static final String annuler=Messages.getString("ImpressionVueTableau.10");

	public DialogPrint() {
		
	}

	public String res="";
	
	public void afficher(boolean plusDuneLigneSelectionnee){
		Display display = Display.getCurrent();
		final Shell shell = new Shell (display);
		shell.setLayout (new RowLayout (SWT.VERTICAL));
		int sizeX=350;
		int sizeY=150;
		shell.setSize(sizeX,sizeY);
		Rectangle rect=Display.getCurrent().getBounds();
		int posX=rect.width/2-sizeX/2;
		int posY=rect.height/2-sizeY/2;
		shell.setLocation(posX,posY);
		shell.setImage(com.faiveley.samng.principal.ihm.Activator.getDefault().
				getImage("/icons/toolBar/vues_commun_imprimer.png"));
		shell.setText(voulezvousimprimer);
		
		final Button tot = new Button (shell, SWT.RADIO);
		tot.setText(totalite);
		tot.setSelection(!plusDuneLigneSelectionnee);
		
		final Button sel = new Button (shell, SWT.RADIO);
		sel.setText(zoneSelection);
		sel.setSelection(plusDuneLigneSelectionnee);

		Composite compB=new Composite(shell, SWT.NORMAL);
		compB.setLayout(new RowLayout (SWT.HORIZONTAL));
		
		Button buttonImprimer = new Button (compB, SWT.PUSH);
		buttonImprimer.setText (imprimer);
		buttonImprimer.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				if (sel.getSelection()) {
					res="YES";
				}else{
					res="NO";
				}
				shell.close();
			}
		});
		
		Button buttonAnnuler = new Button (compB, SWT.NONE);
		buttonAnnuler.setText (annuler);
		buttonAnnuler.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				shell.close();
			}
		});
//		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
	}
}

