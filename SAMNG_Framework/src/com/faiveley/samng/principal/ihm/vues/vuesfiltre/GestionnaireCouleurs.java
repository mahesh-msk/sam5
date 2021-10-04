package com.faiveley.samng.principal.ihm.vues.vuesfiltre;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.faiveley.samng.principal.sm.parseurs.ParseurCouleurs;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class GestionnaireCouleurs {
	private static final String COLOR_TIME_BREAKS_NAME = "COLOR_TIME_BREAKS";
	private static final String COLOR_DISTANCE_BREAKS_NAME = "COLOR_DISTANCE_BREAKS";
	private static final String GRAPHICS_COLOR_NAME_PREFIX = "COLOR_";
	
	private static PaletteData palette = new PaletteData(0xFF , 0xFF00 , 0xFF0000);
	private static ParseurCouleurs parseurCouleur;
	private static String cheminFichierCouleurs = RepertoiresAdresses.graphic_colorsXML;
	private static List<RGB> defaultRgbColors = new ArrayList<RGB>(16);
	private static RGB RGB_COLOR_TIME_BREAKS;
	private static RGB RGB_COLOR_DISTANCE_BREAKS;
	
	private static List<RGB> RGB_COURBES_COLORS = new ArrayList<RGB>(16);
	static {
		try {
			parseurCouleur = ParseurCouleurs.getInstance();
			parseurCouleur.parseRessource(cheminFichierCouleurs,false,0,-1);
			Map<String, RGB> loadedColors = parseurCouleur.chargerCouleurs();
			if(loadedColors.containsKey(COLOR_TIME_BREAKS_NAME)) {
				RGB_COLOR_TIME_BREAKS = loadedColors.get(COLOR_TIME_BREAKS_NAME);
			}else{
				RGB_COLOR_TIME_BREAKS = new RGB(0,0,0);
			}

			if(loadedColors.containsKey(COLOR_DISTANCE_BREAKS_NAME)) {
				RGB_COLOR_DISTANCE_BREAKS = loadedColors.get(COLOR_DISTANCE_BREAKS_NAME);
			}
			else{
				RGB_COLOR_DISTANCE_BREAKS = new RGB(200,100,200);
			}
			
			RGB rgbColor;
			for(int i = 1; i<=16; i++) {
				rgbColor = loadedColors.get(GRAPHICS_COLOR_NAME_PREFIX + i);
				if(rgbColor == null)
					rgbColor = defaultRgbColors.get(i);
				RGB_COURBES_COLORS.add(rgbColor);
			}
		} catch (Exception e) {
			defaultRgbColors.add(new RGB(0, 0, 255));
			defaultRgbColors.add(new RGB(0, 255, 255));
			defaultRgbColors.add(new RGB(150, 0, 0));
			defaultRgbColors.add(new RGB(255, 0, 0));
			defaultRgbColors.add(new RGB(0, 128, 0));
			defaultRgbColors.add(new RGB(0, 255, 0)); 
			defaultRgbColors.add(new RGB(170, 80, 255));
			defaultRgbColors.add(new RGB(255, 0, 255)); 
			defaultRgbColors.add(new RGB(240, 110, 0)); 
			defaultRgbColors.add(new RGB(255, 255, 0));
			defaultRgbColors.add(new RGB(0, 0, 150));
			defaultRgbColors.add(new RGB(255, 175, 175));
			defaultRgbColors.add(new RGB(255, 200, 0));
			defaultRgbColors.add(new RGB(0, 0, 0)); 
			defaultRgbColors.add(new RGB(90, 90, 90));
			defaultRgbColors.add(new RGB(230, 230, 230));
			RGB_COLOR_TIME_BREAKS = new RGB(0,0,0);
			RGB_COLOR_DISTANCE_BREAKS = new RGB(200,100,200);
			RGB rgbColor;
			for(int i = 1; i<=16; i++) {
					rgbColor = defaultRgbColors.get(i-1);
					RGB_COURBES_COLORS.add(rgbColor);
			}
//			MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR);
//		    messageBox.setMessage(Messages.getString("PaletteCouleur.0"));
//		    messageBox.open();
		}
	};
	
    public static Image createImage(Display display, RGB rgbLine, int imgHeight, int imgWidth, int lineHeight) {
        ImageData imageData = new ImageData(imgWidth,imgHeight,24,palette); 
        int linePixelColor = rgbLine.blue << 16 | rgbLine.green << 8 | rgbLine.red;
        for (int x=0; x<imgWidth; x++) {
            for(int y=0; y<imgHeight; y++){
                if(y > (imgHeight - lineHeight)/2 && y < (imgHeight - lineHeight)/2 + lineHeight){
                 imageData.setPixel(x, y, linePixelColor);   // Set the center to the line color
                } else {
                    imageData.setPixel(x,y,0xFFFFFF);   // and everything else to white
             }
            }
        };
        return new Image(display,imageData); 
    }
    
    public static int getIntValue(RGB rgbValue) {
    	//: see which is the right order
    	return rgbValue.blue << 16 | rgbValue.green << 8 | rgbValue.red;
    }
    
    public static int getIntValue(Image image, int height) {
    	ImageData imgData = image.getImageData();
    	int x = 1;
    	int y = height/2;
    	return imgData.getPixel(x, y);
    }
    
    public static RGB getRgbForHexValue(String hexValue) {
    	int color;
    	RGB colorRgb;
		try {
			color = Integer.parseInt(hexValue, 16);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return new RGB(0, 0, 0);	//return a black color
		}
		
		colorRgb = new RGB(color & 0xFF, (color >> 8) & 0xFF, (color >> 16) & 0xFF);
		return colorRgb;
    }
    
    public static RGB getTimeBreaksColor() {
    	return RGB_COLOR_TIME_BREAKS;
    }
    
    public static RGB getDistanceBreaksColor() {
    	return RGB_COLOR_DISTANCE_BREAKS;
    }
    
    public static RGB[] getVariablesColors() {
    	return RGB_COURBES_COLORS.toArray(new RGB[RGB_COURBES_COLORS.size()]);
    }

}
