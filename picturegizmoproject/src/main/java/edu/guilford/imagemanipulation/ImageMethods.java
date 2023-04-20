package edu.guilford.imagemanipulation;

/*
 * Chromakey code adapted by Rob Whitnell (rwhitnel@guilford.edu)
 * from the Java Image Processing Cookbook by Rafael Santos (rafael.santos@lac.inpe.br)
 * (http://www.lac.inpe.br/JIPCookbook/index.jsp)
 * Except for the code copyrighted by others, 
 * it is distributed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/).
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageMethods {
	
	// convolution matrices from http://setosa.io/ev/image-kernels/

	private static double blurMatrix[][] = {
			{0.0625, 0.125, 0.0625},
			{0.125, 0.25, 0.125},
			{0.0625,0.125,0.0625}
	};

	private static double embossMatrix[][] = {
			{-2, -1, 0},
			{-1, 1, 1},
			{0, 1, 2}
	};
	
	private static double sharpenMatrix[][] = {
		{0, -1, 0},
		{-1, 5, -1},
		{0, -1, 0}
	};
	
	private static double edgeDetectMatrix[][] = {
			{-1, -1, -1},
			{-1, 8, -1},
			{-1, -1, -1}
		};
	
	
	public static BufferedImage layerImages(BufferedImage overlay, BufferedImage background, 
			int shiftX, int shiftY, int redClear, int greenClear, int blueClear, double tolerance)
	{
		BufferedImage saveImage; 
		saveImage = clone(background);
		WritableRaster raster = background.getRaster();
		int width = overlay.getWidth();
		int height = overlay.getHeight();
		int backWidth = background.getWidth();
		int backHeight = background.getHeight();
		int red, green, blue;

		// Slow method: scan all input (layer) image pixels, plotting only those which are not 
		// the reference color
		for (int w = 0; w < width; w = w + 1) 
		{
			for (int h = 0; h < height; h = h + 1)
			{

				int newX = w + shiftX;
				int newY = h + shiftY;

				if (newX >= 0 && newX < backWidth && newY >=0 && newY < backHeight)
				{
					int[] pixelValues = new int[4];
					getPixelColors(overlay, w, h, pixelValues);
					//					lPixel = overlay.getRGB(w,h);
					red   = pixelValues[0]; // Red level
					green = pixelValues[1];  // Green level
					blue  = pixelValues[2];       // Blue level

					//If the pixel color isn't close enough to the specified color of the overlay,
					//draw it on the background
					if (colorDistance(red, green, blue, redClear, greenClear, blueClear) > tolerance)
					{
						raster.setPixel(newX, newY,	new int[]{red, green, blue, 255});   
					}
				}
			} 
		} 
		return saveImage;
	}

	public static void getPixelColors(BufferedImage image, int x, int y, int[] colorValues)
	{
		int p = image.getRGB(x,y);
		colorValues[0]   = (int)((p&0x00FF0000)>>>16); // Red level
		colorValues[1] = (int)((p&0x0000FF00)>>>8);  // Green level
		colorValues[2]  = (int) (p&0x000000FF);       // Blue level
		colorValues[3] = (int)((p&0xFF000000)>>>24); //bug fixed

	}
	
	public static void setPixelColor(BufferedImage image, int x, int y, Color color)
	{
		image.setRGB(x, y, color.getRGB());
	}

	public static Color getPixelColor(BufferedImage image, int x, int y)
	{
		int color = image.getRGB(x, y);
		ColorModel model = image.getColorModel();
		return new Color(color, model.hasAlpha());
	}

	public static BufferedImage averageImages(BufferedImage overlay, BufferedImage background, 
			int shiftX, int shiftY)
	{
		BufferedImage saveImage; 
		saveImage = clone(background);
		WritableRaster raster = background.getRaster();
		int width = overlay.getWidth();
		int height = overlay.getHeight();
		int backWidth = background.getWidth();
		int backHeight = background.getHeight();
		int layerRed, layerGreen, layerBlue;
		int backgroundRed, backgroundGreen, backgroundBlue;
		int newRed, newGreen, newBlue;

		// Slow method: scan all input (layer) image pixels, plotting only those which are not 
		// the reference color
		for (int w = 0; w < width; w = w + 1) 
		{
			for (int h = 0; h < height; h = h + 1)
			{
				int[] pixelValues = new int[4];
				getPixelColors(overlay, w, h, pixelValues);
				//				lPixel = overlay.getRGB(w,h);
				layerRed   = pixelValues[0]; // Red level
				layerGreen = pixelValues[1];  // Green level
				layerBlue  = pixelValues[2];       // Blue level


				//If the pixel color isn't close enough to the specified color of the overlay,
				//draw it on the background

				int newX = w + shiftX;
				int newY = h + shiftY;
				if (newX >= 0 && newX < backWidth && newY >=0 && newY < backHeight)
				{
					getPixelColors(background, newX, newY, pixelValues);
					backgroundRed   = pixelValues[0]; // Red level
					backgroundGreen = pixelValues[1];  // Green level
					backgroundBlue  = pixelValues[2];       // Blue level

					newRed = (layerRed + backgroundRed) / 2;
					newGreen = (layerGreen + backgroundGreen) / 2;
					newBlue = (layerBlue + backgroundBlue) / 2;
					raster.setPixel(w + shiftX, h + shiftY, 
							new int[]{newRed, newGreen, newBlue, 255}); 
				}
			} 
		} 
		return saveImage;
	}


	public static void addAlpha(BufferedImage img)
	{
		WritableRaster raster = img.getRaster();
		int width = img.getWidth();
		int height = img.getHeight();
		for (int w = 0; w < width; w++) 
		{
			for (int h = 0; h < height; h++)
			{
				raster.setSample(w, h, 3, 255);
			}
		}
	}

	public static void showChannel(BufferedImage img, int channel)
	{
		int[] pixelValues = new int[4];
		if (channel < 0 || channel > 2) return;
		WritableRaster raster = img.getRaster();
		int width = img.getWidth();
		int height = img.getHeight();
		for (int w = 0; w < width; w++) 
		{
			for (int h = 0; h < height; h++)
			{
				getPixelColors(img, w, h, pixelValues);
				int value = pixelValues[channel];
				raster.setPixel(w, h, new int[]{value, value, value, pixelValues[3]});
				//				lPixel = overlay.getRGB(w,h);
				//				for (int i = 0; i < 3; i++)
				//				{
				//					if (i != channel)
				//					{
				//						raster.setSample(w, h, i, 0);
				//					}
				//				}
			}
		}

	}

	public static void grayScale(BufferedImage img, Rectangle region)
	{
		// Using values from https://en.wikipedia.org/wiki/Grayscale
		WritableRaster raster = img.getRaster();
//		int width = img.getWidth();
//		int height = img.getHeight();
		for (int w = region.x; w < region.x + region.width; w++) 
		{
			for (int h = region.y; h < region.y + region.height; h++)
			{
				Color temp = getPixelColor(img, w, h);
				int gray = (int)(0.2126 * temp.getRed() + 0.7152 * temp.getGreen() + 0.0722 * temp.getBlue());
				raster.setPixel(w, h, new int[]{gray, gray, gray, temp.getAlpha()});
			}
		}
	}

	public static void averageValue(BufferedImage img, Rectangle region)
	{
		// Using values from https://en.wikipedia.org/wiki/Grayscale
		WritableRaster raster = img.getRaster();
//		int width = img.getWidth();
//		int height = img.getHeight();
		int redAverage = 0;
		int greenAverage = 0;
		int blueAverage = 0;
		for (int w = region.x; w < region.x + region.width; w++) 
		{
			for (int h = region.y; h < region.y + region.height; h++)
			{
				Color temp = getPixelColor(img, w, h);
				redAverage = redAverage + temp.getRed();
				greenAverage = greenAverage + temp.getGreen();
				blueAverage = blueAverage + temp.getBlue();
//				raster.setPixel(w, h, new int[]{gray, gray, gray, temp.getAlpha()});
			}
		}
		
		redAverage = redAverage / (region.width * region.height);
		greenAverage = greenAverage / (region.width * region.height);
		blueAverage = blueAverage / (region.width * region.height);
		
		System.out.println(new Color(redAverage, greenAverage, blueAverage));
		for (int w = region.x; w < region.x + region.width; w++) 
		{
			for (int h = region.y; h < region.y + region.height; h++)
			{
				Color temp = getPixelColor(img, w, h);
				int redNew = Math.abs(temp.getRed() - redAverage);
				int greenNew = Math.abs(temp.getGreen() - greenAverage);
				int blueNew = Math.abs(temp.getBlue() - blueAverage);
				raster.setPixel(w, h, new int[]{redNew, greenNew, blueNew, temp.getAlpha()});
			}
		}
	}

	
	public static void clearImage(BufferedImage img)
	{
		clearImage(img, new Color(0, 0, 0));
	}

	public static void brightenImage(BufferedImage img, Rectangle region)
	{
		brightenImage(img, 1.1, region);
	}

	public static void darkenImage(BufferedImage img, Rectangle region)
	{
		brightenImage(img, 0.9, region);
	}

	public static void brightenImage(BufferedImage img, double amount, Rectangle region)
	{
		WritableRaster raster = img.getRaster();
//		int width = img.getWidth();
//		int height = img.getHeight();
		for (int w = region.x; w < region.x + region.width; w++) 
		{
			for (int h = region.y; h < region.y + region.height; h++)
			{
				Color temp = getPixelColor(img, w, h);
				int red = Math.max(0, Math.min(255, (int)(temp.getRed() * amount)));
				int green = Math.max(0, Math.min(255, (int)(temp.getGreen() * amount)));
				int blue = Math.max(0, Math.min(255, (int)(temp.getBlue() * amount)));
				raster.setPixel(w, h, new int[]{red, green, blue, temp.getAlpha()});
			}
		}

	}


	public static void clearImage(BufferedImage img, Color clearColor)
	{
		WritableRaster raster = img.getRaster();
		int width = img.getWidth();
		int height = img.getHeight();
		int red = clearColor.getRed();
		int green = clearColor.getGreen();
		int blue = clearColor.getBlue();
		for (int w = 0; w < width; w++) 
		{
			for (int h = 0; h < height; h++)
			{
				raster.setPixel(w, h, new int[]{red, green, blue, 255});
			}
		}

	}

	public static double colorDistance(Color color1, Color color2)
	{

		return colorDistance(color1.getRed(), color1.getGreen(), color1.getBlue(),
				color2.getRed(), color2.getGreen(), color2.getBlue());
	}

	public static double colorDistance(int red1, int green1, int blue1, 
			int red2, int green2, int blue2)
	{
		double distance;

		int redDist = red1 - red2;
		int greenDist = green1 - green2;
		int blueDist = blue1 - blue2;

		distance = Math.sqrt(redDist * redDist + greenDist * greenDist + blueDist * blueDist);

		return distance;
	}

	public static void blurImage(BufferedImage img, Rectangle region)
	{
		convolveImage(img, blurMatrix, region);
	}

	public static void embossImage(BufferedImage img, Rectangle region)
	{
		convolveImage(img, embossMatrix, region);
	}
	
	public static void sharpenImage(BufferedImage img, Rectangle region)
	{
		convolveImage(img, sharpenMatrix, region);
	}

	public static void edgeDetectImage(BufferedImage img, Rectangle region)
	{
		convolveImage(img, edgeDetectMatrix, region);
	}
	public static void convolveImage(BufferedImage img, double[][] kernel, Rectangle region)
	{
		
		double kernelSum  = 0;
		int endX = region.x + region.width - 1;
		int endY = region.y + region.height - 1;
		int ksize = kernel[0].length;
		WritableRaster raster = img.getRaster();
		BufferedImage convImg = new BufferedImage(img.getWidth() + (ksize + 1) / 2, img.getHeight() + (ksize + 1) / 2, BufferedImage.TYPE_4BYTE_ABGR);
		WritableRaster convRaster = convImg.getRaster();
		convImg.getGraphics().drawImage(img, 1, 1, null);

		for (int i = 0; i < convImg.getWidth(); i++)
		{
			convRaster.setPixel(i, 0, new int[]{0, 0, 0, 255});
		}

		for (int i = 0; i < convImg.getHeight(); i++)
		{
			convRaster.setPixel(0, i, new int[]{0, 0, 0, 255});
		}
		for (int kw = 0; kw < ksize; kw++)
		{
			for (int kh = 0; kh < ksize; kh++)
			{
				kernelSum += kernel[kw][kh];
			}
		}
		if (Math.abs(kernelSum) > 0.01)
		{
			for (int kw = 0; kw < ksize; kw++)
			{
				for (int kh = 0; kh < ksize; kh++)
				{
					kernel[kw][kh] /= kernelSum;
				}
			}
		}

		for (int w = region.x + 1; w < endX; w++)
		{
			for (int h = region.y + 1; h < endY; h++)
			{
				double newRed = 0;
				double newGreen = 0;
				double newBlue = 0;
				int alpha = getPixelColor(convImg, w, h).getAlpha();
				for (int kw = 0; kw < ksize; kw++)
				{
					for (int kh = 0; kh < ksize; kh++)
					{
						Color thisColor = getPixelColor(convImg, w - 1 + kw, h - 1 + kh);
						newRed = newRed + kernel[kw][kh] * thisColor.getRed();
						newGreen = newGreen + kernel[kw][kh] * thisColor.getGreen();
						newBlue = newBlue + kernel[kw][kh] * thisColor.getBlue();

					}
				}
				int red = Math.max(0, Math.min((int)newRed, 255));
				int green = Math.max(0, Math.min((int)newGreen, 255));
				int blue = Math.max(0, Math.min((int)newBlue, 255));
				raster.setPixel(w - 1, h - 1, new int[]{red, green, blue, alpha});
			}
		}

	}

	public static JFrame displayColor(double red, double green, double blue, int width, int height,
			int shiftXLocation, int shiftYLocation, String title)
	{
		return displayColor(new Color((int)red, (int)green, (int)blue), width, height, shiftXLocation,
				shiftYLocation, title);
		
	}
	
	public static JFrame displayColor(int red, int green, int blue, int width, int height,
			int shiftXLocation, int shiftYLocation, String title)
	{
		return displayColor(new Color(red, green, blue), width, height, shiftXLocation, shiftYLocation,
				title);
	}


	public static JFrame displayColor(Color swatch, int width, int height,
			int shiftXLocation, int shiftYLocation, String title)
	{
		JFrame frame = new JFrame(title);
		frame.getContentPane().setBackground(swatch);
		frame.setPreferredSize(new Dimension(width,  height));
		frame.pack();
		frame.setLocation(shiftXLocation, shiftYLocation);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;

	}


	public static final BufferedImage clone(BufferedImage image) {
		BufferedImage clone = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		Graphics2D g2d = clone.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return clone;
	}

	public static void copyImage(BufferedImage fromImage, BufferedImage toImage) {
		Graphics2D g2d = toImage.createGraphics();
		g2d.drawImage(fromImage, 0, 0, null);
		g2d.dispose();
	}

	public static void writeImage(BufferedImage image, String format, String fileName) throws IOException {
		ImageIO.write(image, format, new File(fileName));
	}

	public static BufferedImage readImage(String fileName) throws IOException
	{
		return ImageIO.read(new File(fileName));
	}

	public static JFrame displayImage(BufferedImage image, 
			int shiftXLocation, int shiftYLocation, String title)
	{
		JFrame frame = new JFrame(title);    
		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		frame.pack();
		frame.setLocation(shiftXLocation, shiftYLocation);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;

	}

	public static double[][] getBlurMatrix() {
		return blurMatrix;
	}

	public static double[][] getEmbossMatrix() {
		return embossMatrix;
	}

	public static double[][] getSharpenMatrix() {
		return sharpenMatrix;
	}

	public static double[][] getEdgeDetectMatrix() {
		return edgeDetectMatrix;
	}


}
