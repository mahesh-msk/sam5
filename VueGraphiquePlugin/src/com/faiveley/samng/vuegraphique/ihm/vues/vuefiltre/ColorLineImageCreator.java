package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorLineImageCreator {
	private static PaletteData palette = new PaletteData(0xFF , 0xFF00 , 0xFF0000);
	
	public static final List<RGB> RGB_COLORS = new ArrayList<RGB>();
	
	static {
		RGB_COLORS.add(new RGB(0, 0, 255));
		RGB_COLORS.add(new RGB(0, 255, 255));
		RGB_COLORS.add(new RGB(150, 0, 0));
		RGB_COLORS.add(new RGB(255, 0, 0));
		RGB_COLORS.add(new RGB(0, 128, 0));
		RGB_COLORS.add(new RGB(0, 255, 0)); 
		RGB_COLORS.add(new RGB(170, 80, 255));
		RGB_COLORS.add(new RGB(255, 0, 255)); 
		RGB_COLORS.add(new RGB(240, 110, 0)); 
		RGB_COLORS.add(new RGB(255, 255, 0));
		RGB_COLORS.add(new RGB(0, 0, 150));
		RGB_COLORS.add(new RGB(255, 175, 175));
		RGB_COLORS.add(new RGB(255, 200, 0));
		RGB_COLORS.add(new RGB(0, 0, 0)); 
		RGB_COLORS.add(new RGB(90, 90, 90));
		RGB_COLORS.add(new RGB(230, 230, 230));
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
}
