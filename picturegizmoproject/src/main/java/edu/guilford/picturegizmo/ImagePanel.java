package edu.guilford.picturegizmo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;



public class ImagePanel extends JPanel {

	private BufferedImage display;
	private JFrame swatch;
	private boolean colorShow;
	private boolean expandShow;
	private boolean regionSelect;
	private BufferedImage pixelSample;
	private boolean resetSample;
	private BufferedImage expandSample;
	private int expandSize, sampleSize, expandMultiple;
	private Point mousePos;
	private boolean mouseRelease;
	private Point iRegion, fRegion;
	private int startX, startY, regionWidth, regionHeight;
	private Rectangle selectedRegion;


	public ImagePanel(BufferedImage display, JFrame swatch) {
		this.swatch = swatch;
		this.display = display;
		int width = display.getWidth();
		int height = display.getHeight();
		setSize(new Dimension(width, height));
		expandSize = 13;
		expandMultiple = 10;
		sampleSize = expandSize * expandMultiple;
		//		MouseInteractionListener mListener = new MouseInteractionListener();
		//		addMouseListener(mListener);
		//		addMouseMotionListener(mListener);
		expandSample = new BufferedImage(sampleSize, sampleSize, BufferedImage.TYPE_4BYTE_ABGR);
		colorShow = false;
		expandShow = false;
		pixelSample = null;
		resetSample = true;
		mouseRelease = true;
		startX = startY = 0;
		regionWidth = display.getWidth();
		regionHeight = display.getHeight();
		selectedRegion = new Rectangle(startX, startY, regionWidth, regionHeight);

	}




	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = display.getWidth();
		int height = display.getHeight();
		g.drawImage(display, 0, 0, width, height, null);
		//		g.setColor(new Color(255, 255, 0, 150));
		//		g.fillOval(100, 100, 200, 200);
		if (resetSample)
		{
			pixelSample = null;
		}
		if (expandShow && !mouseRelease)
		{
			g.drawImage(expandSample, mousePos.x + (expandSize - 1) / 2, 
					mousePos.y - sampleSize - (expandSize - 1) / 2,
					sampleSize, sampleSize, null);
			g.setColor(Color.yellow);
			g.drawRect(mousePos.x - (expandSize - 1) / 2, mousePos.y - (expandSize - 1) / 2, 
					expandSize, expandSize);
		}
		if (regionSelect)
		{
			g.setColor(Color.yellow);
			g.drawRect(startX, startY, regionWidth, regionHeight);
		}
	}




	public BufferedImage getDisplay() {
		return display;
	}

	public void setDisplay(BufferedImage display) {
		this.display = display;
	}

	public boolean isColorShow() {
		return colorShow;
	}

	public void setColorShow(boolean colorShow) {
		this.colorShow = colorShow;
		if (!colorShow)
		{
			pixelSample = null;
		}
	}

	public boolean isExpandShow() {
		return expandShow;
	}




	public void setExpandShow(boolean expandShow) {
		this.expandShow = expandShow;
	}




	public boolean isRegionSelect() {
		return regionSelect;
	}




	public void setRegionSelect(boolean regionSelect) {
		this.regionSelect = regionSelect;
	}


	public void resetSelection()
	{
		startX = startY = 0;
		regionWidth = display.getWidth();
		regionHeight = display.getHeight();
		iRegion = new Point(startX, startY);
		fRegion = new Point(startX + regionWidth, startY + regionHeight);
		selectedRegion = new Rectangle(startX, startY, regionWidth, regionHeight);
		repaint();
	}

	public Rectangle getSelectedRegion() {
		return selectedRegion;
	}




	// From http://stackoverflow.com/questions/6575578/convert-a-graphics2d-to-an-image-or-bufferedimage?rq=1
	// and http://stackoverflow.com/questions/5853879/swing-obtain-image-of-jframe/5853992#5853992
	public void saveImageActionPerformed() {
		JFileChooser filechooser = new JFileChooser();
		//		BufferedImage bImage = (BufferedImage)createImage(getWidth(), getHeight());
		//		Container c = getContentPane();
		BufferedImage bImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		printAll(bImage.getGraphics());
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"PNG images", "png");
		filechooser.setFileFilter(filter);
		int result = filechooser.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File saveFile = filechooser.getSelectedFile();
			try {
				ImageIO.write(bImage, "png", saveFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	//	private class MouseInteractionListener implements MouseListener, MouseMotionListener
	//	{

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if (expandShow)
		{
			mouseRelease = false;
			int span = (expandSize - 1) / 2;
			mousePos = new Point(e.getX(), e.getY());
			if ((mousePos.x - span < 0) || mousePos.x + span >= getWidth() || mousePos.y - span < 0 || mousePos.y + span >= getHeight())
			{
				return;
			}
			WritableRaster raster = expandSample.getRaster();
			for (int i = mousePos.x - span; i <= mousePos.x + span; i++)
			{
				for (int j = mousePos.y - span; j <= mousePos.y + span; j++)					
				{
					Color tempColor = new Color(display.getRGB(i, j));
					int red = tempColor.getRed();
					int green = tempColor.getGreen();
					int blue = tempColor.getBlue();
					int alpha = tempColor.getAlpha();
					int startX = ((i - mousePos.x) + span) * expandMultiple;
					int startY = ((j - mousePos.y) + span) * expandMultiple;
					//						System.out.println(startX + " " + startY);
					for (int oi = startX; oi < startX + expandMultiple; oi++)
					{
						for (int oj = startY; oj < startY + expandMultiple; oj++)
						{
							raster.setPixel(oi, oj, new int[]{red, green, blue, alpha});
						}
					}
				}
			}
			repaint();
		}
		else if (regionSelect)
		{
			//				System.out.println("grab final");
			fRegion = e.getPoint();
			//				System.out.println(fRegion);
			startX = 0; startY = 0;
			regionWidth = Math.abs(Math.min(display.getWidth() - 1, Math.max(0, fRegion.x)) - iRegion.x);
			regionHeight = Math.abs(Math.min(display.getHeight() - 1, Math.max(0, fRegion.y)) - iRegion.y);
			if (iRegion.x > fRegion.x)
			{
				startX = fRegion.x;
				startY = iRegion.y > fRegion.y ? fRegion.y: iRegion.y;
			}
			else
			{
				startX = iRegion.x;
				startY = iRegion.y > fRegion.y ? fRegion.y: iRegion.y;
			}
			startX = Math.max(0, startX);
			startY = Math.max(0, startY);
			selectedRegion = new Rectangle(startX, startY, regionWidth, regionHeight);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

		if (isColorShow()){
			if (pixelSample == null)
			{
				pixelSample = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
				resetSample = false;
				printAll(pixelSample.getGraphics());
				resetSample = true;
			}
			int x = e.getX();
			int y = e.getY();
			Color tempColor = new Color(pixelSample.getRGB(x,  y));
			swatch.getContentPane().setBackground(tempColor);
		}	
	}

	public void mouseClicked(MouseEvent e) {
		//			if (isColorShow()){
		//				if (pixelSample == null)
		//				{
		//					pixelSample = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		//					resetSample = false;
		//					printAll(pixelSample.getGraphics());
		//					resetSample = true;
		//				}
		//				int x = e.getX();
		//				int y = e.getY();
		//				Color tempColor = new Color(pixelSample.getRGB(x,  y));
		//				swatch.getContentPane().setBackground(tempColor);
		//			}	
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if (regionSelect)
		{
			//				System.out.println(iRegion);
			iRegion = e.getPoint();
		}

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseRelease = true;
		repaint();

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}


}
