package edu.guilford.picturegizmo;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageLayeredPane extends JLayeredPane {

	private BufferedImage image;
	private ImagePanel imagePanel;
	private DrawGlassPane glass;
	private JFrame swatch;
	private boolean drawNewLine;

	public ImageLayeredPane() {
		// TODO Auto-generated constructor stub
	}

	public ImageLayeredPane(BufferedImage image, JFrame swatch) 
	{
		this.image = image;
		this.swatch = swatch;
		imagePanel = new ImagePanel(image, swatch);
		this.setPreferredSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));
		add(imagePanel, JLayeredPane.DEFAULT_LAYER);
		imagePanel.repaint();
		drawNewLine = false;

		glass = new DrawGlassPane();
		glass.setOpaque(false);
		glass.setVisible(true);
		glass.setPreferredSize(imagePanel.getSize());
		glass.setSize(imagePanel.getSize());


		add(glass, JLayeredPane.PALETTE_LAYER);
		glass.repaint();

		MouseHandler mHandler = new MouseHandler();
		addMouseListener(mHandler);
		addMouseMotionListener(mHandler);
	}

	public ImagePanel getImagePanel() {
		return imagePanel;
	}

	public void setImagePanel(ImagePanel imagePanel) {
		this.imagePanel = imagePanel;
	}

	public DrawGlassPane getGlass() {
		return glass;
	}

	public void setGlass(DrawGlassPane glass) {
		this.glass = glass;
	}

	public boolean isDrawNewLine() {
		return drawNewLine;
	}

	public void setDrawNewLine(boolean drawNewLine) {
		this.drawNewLine = drawNewLine;
	}

	private class MouseHandler implements MouseListener, MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if (drawNewLine)
			{
				glass.mouseDragged(e);
			}
			else
			{	
				imagePanel.mouseDragged(e);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			if (drawNewLine)
			{
				glass.mouseMoved(e);
			}
			else
			{
				imagePanel.mouseMoved(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			if (drawNewLine)
			{
				glass.mouseClicked(e);
			}
			else
			{
				imagePanel.mouseClicked(e);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			if (drawNewLine)
			{
				glass.mousePressed(e);
			}
			else
			{
				imagePanel.mousePressed(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			if (drawNewLine)
			{
				glass.mouseReleased(e);
			}
			else
			{
				imagePanel.mouseReleased(e);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			if (drawNewLine)
			{
				glass.mouseEntered(e);
			}
			else
			{
				imagePanel.mouseEntered(e);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			if (drawNewLine)
			{
				glass.mouseExited(e);
			}
			else
			{
				imagePanel.mouseExited(e);
			}
		}

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


}
