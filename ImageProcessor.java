import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.*;
import java.util.*;

public class ImageProcessor {
	
	// Create a clone of a buffered image
	// (The BufferedImage class describes an Image with an accessible buffer of image data.)
	public static BufferedImage copy(BufferedImage img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return bi;
	}

	// Create a clone of a buffered image
	// (Another implementation)
/*
	public static BufferedImage copy(BufferedImage img) {
		 ColorModel cm = img.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = img.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
*/
	
	// Convert an input color image to grayscale image
	public static BufferedImage convertToGrayScale(BufferedImage src) {
		// Make a copy of the source image as the target image
		BufferedImage target = copy(src);
		int width = target.getWidth();
		int height = target.getHeight();
		
		// Scan through each row of the image
		for (int j = 0; j < height; j++) {
			// Scan through each column of the image
			for (int i = 0; i < width; i++) {
				// Get an integer pixel in the default RGB color model
				int pixel = target.getRGB(i, j);
				// Convert the single integer pixel value to RGB color
				Color oldColor = new Color(pixel);

				int red = oldColor.getRed(); 	// get red value
				int green = oldColor.getGreen();	// get green value
				int blue = oldColor.getBlue(); 	// get blue value

				// Convert RGB to grayscale using formula
				// gray = 0.299 * R + 0.587 * G + 0.114 * B
				double grayVal = 0.299 * red + 0.587 * green + 0.114 * blue;

				// Assign each channel of RGB with the same value
				Color newColor = new Color((int) grayVal, (int) grayVal, (int) grayVal);

				// Get back the integer representation of RGB color and assign it back to the original position
				target.setRGB(i, j, newColor.getRGB());
			}
		}
		// return the resulting image in BufferedImage type
		return target;
	}

	// Invert the color of an input image
	public static BufferedImage invertColor(BufferedImage src) {
		BufferedImage target = copy(src);
		int width = target.getWidth();
		int height = target.getHeight();
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int pixel = target.getRGB(i, j);
				Color oldColor = new Color(pixel);

				int red = oldColor.getRed();
				int green = oldColor.getGreen();
				int blue = oldColor.getBlue();
				
				// invert the color of each channel
				Color newColor = new Color(255 - red, 255 - green, 255 - blue);
				
				target.setRGB(i, j, newColor.getRGB());
			}
		}
		return target;
	}

	// Adjust the brightness of an input image
	public static BufferedImage adjustBrightness(BufferedImage src, int amount) {
		BufferedImage target = copy(src);
		int width = target.getWidth();
		int height = target.getHeight();
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int pixel = target.getRGB(i, j);
				Color oldColor = new Color(pixel);

				int red = oldColor.getRed();
				int green = oldColor.getGreen();
				int blue = oldColor.getBlue();

				int newRed = (red + amount > 255) ? 255 : red + amount;
				int newGreen = (green + amount > 255) ? 255 : green + amount;
				int newBlue = (blue + amount > 255) ? 255 : blue + amount;

				newRed = (newRed < 0) ? 0 : newRed;
				newGreen = (newGreen < 0) ? 0 : newGreen;
				newBlue = (newBlue < 0) ? 0 : newBlue;

				Color newColor = new Color(newRed, newGreen, newBlue);

				target.setRGB(i, j, newColor.getRGB());
			}
		}
		return target;
	}

	// Apply a blur effect to an input image by random pixel movement
	public static BufferedImage blur(BufferedImage src, int offset) {
		
		// TODO: add your implementation
		 int width  = src.getWidth();
	     int height = src.getHeight();
	     BufferedImage target = new BufferedImage(width,height,src.getType());
	     Random randomGen = new Random();
	     int vl,vl2;
	        for (int ii = 0; ii < width; ii++) {
	            for (int jj = 0; jj < height; jj++) {
	            	vl  = randomGen.nextInt(offset);
		        	vl2 = randomGen.nextInt(offset);
	            	int i = (int) ii+(vl-offset/2);
	                int j = (int) jj+(vl2-offset/2);
	               
	                //Normal
	                if (i >= 0 && i < width && j >= 0 && j < height) {
	                	target.setRGB(ii,jj, src.getRGB(i,j));
	                	target.setRGB(i, j, src.getRGB(ii, jj));
	                }
	                //Out of Left bound
	                else if (i <0  && i < width && j >= 0 && j < height) {
	                	target.setRGB(ii, jj, src.getRGB(ii+1, j));
	                }
	                //Out of Top bound
	                else if (i >= 0 && i < width && j < 0 && j < height) {
	                	target.setRGB(ii, jj, src.getRGB(i, jj+1));
	                }
	                //Out of Right bound
	                else if (i >= 0 && i >= width && j >= 0 && j < height) {
	                	target.setRGB(ii, jj, src.getRGB((ii-1), jj));
	                	target.setRGB((ii-1),jj, src.getRGB(ii, jj));
	                }
	                //Out of Bottom bound
	                else if (i >= 0 && i < width && j >= 0 && j >= height) {
	                	target.setRGB(ii, jj, src.getRGB(ii, (jj-1)));
	                	target.setRGB(ii, (jj-1), src.getRGB(ii, jj));
	                }
	                //Out of Right Top
	                else if (i >= 0 && i >= width && j < 0 && j < height) {
	                	target.setRGB(ii, jj, src.getRGB((ii), (jj)));
	                }
	                //Out of Right bottom
	                else if (i >= 0 && i >= width && j >= 0 && j >= height) {
	                	target.setRGB(ii, jj, src.getRGB((ii), (jj)));
	                }
	                //Out of Left Top
	                else if (i < 0 && i < width && j < 0 && j < height) {
	                	target.setRGB(ii, jj, src.getRGB((ii), (jj)));
	                }
	                //Out of Left bottom
	                else if (i < 0 && i < width && j >= 0 && j >= height) {
	                	target.setRGB(ii, jj, src.getRGB(ii, jj));
	                }
	            }
	        }
	       
		return target; // temporary for passing compilation (remove it after added your code)
	}

	// Scale (resize) an image
	public static BufferedImage scale(BufferedImage src, int tWidth, int tHeight) {

		// TODO: add your implementation
		//create same size of image same as source
		BufferedImage target = new BufferedImage(tWidth,tHeight,src.getType());
		//create a graphics drawer of target
		Graphics2D g2d = target.createGraphics();
		//draw the same thing as source but with settled [width] and [height]
	    g2d.drawImage(src, 0, 0, tWidth, tHeight, null);
	    //dispose drawer
	    g2d.dispose();
	    return target;
	 
	}
	
	// Rotate an image by angle degrees clockwise
	public static BufferedImage rotate(BufferedImage src, double angle) {

		// TODO: add your implementation
		int w = src.getWidth();
		int h = src.getHeight();
		double rad = Math.toRadians(angle);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		double midX = (w-1)*0.5;
		double midY = (h-1)*0.5;
		BufferedImage target = new BufferedImage(w,h,src.getType());
		for (int x = 0; x<w; x++) {
			for (int y = 0; y<h; y++) {
				int newX = (int) ( (x-midY) * sin + (y-midY) * cos + midY);
				int newY = (int) ( (x-midX) * cos - (y-midY) * sin + midX);
				if (newX>=0 && newX<w && newY>=0 && newY<h) {
					target.setRGB(y, x, src.getRGB(newX, newY));
				}
			}
		}
		
		return target;
		 // temporary for passing compilation (remove it after added your code)
	}
	
	// Apply a swirl effect to an input image
	public static BufferedImage swirl(BufferedImage src, double degree) {
		
		// TODO: add your implementation
        int width  = src.getWidth();
        int height = src.getHeight();
        double i0 = (width-1)*0.5;
        double j0 = (height-1)*0.5;
        // swirl
        BufferedImage target = new BufferedImage(width,height,src.getType());
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double di = i - i0;
                double dj = j - j0;
                double theta = Math.atan2(dj,di);
                double r = Math.sqrt(di*di + dj*dj);
                int ii = (int) ( i0 + r * Math.cos(theta + degree * r));
                int jj = (int) ( j0 + r * Math.sin(theta + degree * r));
               
                // plot pixel (i, j) the same color as (ii, jj) if it's in bounds
                if (ii >= 0 && ii < width && jj >= 0 && jj < height) {
                    target.setRGB(i, j, src.getRGB(ii, jj));}
              //Out of Left bound
                else {         
               	target.setRGB(i, j, src.getRGB(i,j)); 
               }
            }
        }
      
		return target;
	}

	// Apply an effect to preserve partial colors of an image 
	public static BufferedImage preserveColor(BufferedImage src, boolean[][] mask, int colorVal, 
			int rgValue, int gbValue, int brValue) {
		
		// TODO: add your implementation
		BufferedImage target = copy(src);
		int width = target.getWidth();
		int height = target.getHeight();
		
		// Scan through each row of the image
		for (int j = 0; j < height; j++) {
			// Scan through each column of the image
			for (int i = 0; i < width; i++) {
				// Get an integer pixel in the default RGB color model
				int pixel = target.getRGB(i, j);
				// Convert the single integer pixel value to RGB color
				Color oldColor = new Color(pixel);

				int red = oldColor.getRed(); 	// get red value
				int green = oldColor.getGreen();	// get green value
				int blue = oldColor.getBlue(); 	// get blue value
				
				Color c = new Color(colorVal);
				int redP = c.getRed();
				int greenP = c.getGreen();
				int blueP = c.getBlue();
				
				int diffRG = redP-greenP;
				int diffGB = greenP-blueP;
				int diffBR = blueP-redP;
				int RGlow = diffRG - rgValue;
				int RGhigh = diffRG + rgValue;
				int GBlow = diffGB - gbValue;
				int GBhigh = diffGB + gbValue;
				int BRlow = diffBR - brValue;
				int BRhigh = diffBR + brValue;
				
				if (((red-green)>RGlow) && ((red-green)<RGhigh) && ((green-blue)>GBlow) && ((green-blue)<GBhigh) && ((blue-red)>BRlow) && ((blue-red)<BRhigh))
				{
				mask[i][j]=true;
				}
				
				if (mask[i][j]==true){
				target.setRGB(i, j, oldColor.getRGB());
				}
				else{
					double grayVal = 0.299 * red + 0.587 * green + 0.114 * blue;
					Color newColor = new Color((int) grayVal, (int) grayVal, (int) grayVal);
					target.setRGB(i, j, newColor.getRGB());
				}
			}
		}
		// return the resulting image in BufferedImage type
		return target; // temporary for passing compilation (remove it after added your code)
	}

	// Perform edge detection for an input image
	public static BufferedImage detectEdges(BufferedImage src) {
		
		// TODO: add your implementation		
		  BufferedImage target = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
		  
		  int[][] xfilter = {
			 {-1,0,1},
			 {-2,0,2},
			 {-1,0,1},
		  };
		  int[][] yfilter = {
					 {-1,-2,-1},
					 {0,0,0},
					 {1,2,1},
				  };
		  for(int y = 1; y < src.getHeight()-2; y++){
		    for(int x = 1; x < src.getWidth()-2; x++){
		      int pixel_x = ((xfilter[0][0]*src.getRGB(x-1, y-1)) + (xfilter[0][1]*src.getRGB(x, y-1)) + (xfilter[0][2]*src.getRGB(x+1, y-1)) +
		    		  		 (xfilter[1][0]*src.getRGB(x-1, y)) + (xfilter[1][1]*src.getRGB(x, y)) + (xfilter[1][2]*src.getRGB(x+1, y)) +
		    		  		 (xfilter[2][0]*src.getRGB(x-1, y+1)) + (xfilter[2][1]*src.getRGB(x, y+1)) + (xfilter[2][2]*src.getRGB(x+1, y+1)));
		    		 
		      int pixel_y = ((yfilter[0][0]*src.getRGB(x-1, y-1)) + (yfilter[0][1]*src.getRGB(x, y-1)) + (yfilter[0][2]*src.getRGB(x+1, y-1)) +
	    		  			 (yfilter[1][0]*src.getRGB(x-1, y)) + (yfilter[1][1]*src.getRGB(x, y)) + (yfilter[1][2]*src.getRGB(x+1, y)) +
	    		  			 (yfilter[2][0]*src.getRGB(x-1, y+1)) + (yfilter[2][1]*src.getRGB(x, y+1)) + (yfilter[2][2]*src.getRGB(x+1, y+1)));
	    		 
		      
		      if ( Math.sqrt(pixel_x*pixel_x)+Math.ceil(pixel_y*pixel_y) > 100000
		    		  ){			
		    	  Color black = new Color(0,0,0);
		        target.setRGB(x,y,black.getRGB());
		      }
		      else {
		    	  Color white = new Color(1.0f,1.0f,1.0f);
				  target.setRGB(x,y,white.getRGB());
		      }
		    }
		  }
				
        return target;
	}
}
	