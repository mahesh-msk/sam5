package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Legende;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;

public class LegendeComponent extends Canvas {
	private static String FONT_NAME = "Tahoma";
	private RGB rgb = new RGB(0, 255, 128);
	private AVariableComposant variable;
	private static RGB rgbTextColor = new RGB(102, 102, 153);
	private static Color textColor;
	private static Font textFont;

	public LegendeComponent(Composite parent, int style) {
		super(parent, style);
		if(textFont == null)
			textFont = new Font(Display.getCurrent(), FONT_NAME, 7, SWT.NORMAL);
		if(textColor == null)
			textColor= new Color(Display.getCurrent(), rgbTextColor.red, rgbTextColor.green, rgbTextColor.blue);
		
		this.addListener(SWT.Paint,new Listener(){ 
        	public void handleEvent(Event event) { 
        		DrawComponent(event);     		
        	}
        });

	}

	public void setColor(RGB color) {
		this.rgb = color;
	}
	
	public void setVariable(AVariableComposant var) {
		this.variable = var;
	}
	
	private void DrawComponent(Event event){
		Color color = new Color(event.gc.getDevice(), rgb.red, rgb.green, rgb.blue); 
		event.gc.setForeground(color);
		event.gc.setLineWidth(2);
		event.gc.drawLine(0, 5, 20, 5);
		String displayText = "";
		if(variable.getValeur() != null) {//tagValCor cette classe n'est pas utilisee dans cette version
			displayText = "[" + variable.toString() + "]";
		}
		displayText += getDescriptionForVariable();
		event.gc.setForeground(textColor);
		event.gc.setFont(textFont);
		
		event.gc.drawString(displayText, 25, 0);
	}
	
	private String getDescriptionForVariable() {
		//: NomUtilisateur should be used here
		return this.variable.getDescriptor().getM_AIdentificateurComposant().getNom();
	}
	

}
