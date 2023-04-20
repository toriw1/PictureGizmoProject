package edu.guilford.picturegizmo;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import edu.guilford.imagemanipulation.ImageMethods;

public class GizmoMain {
	static BufferedImage modified, original;
	static JFrame originalImage;
	static JFrame modifiedImage;
	static ImageLayeredPane originalPanel;
	static ImageLayeredPane modifiedPanel;
	static JFrame colorSwatch;
	static JFrame controlFrame;
	static ControlPanel controlPanel;
	static Color swatch;

	public static void main(String[] args) throws URISyntaxException {
		// TODO Auto-generated method stub
		// Read the input image
		try {
			original = ImageIO.read(new File(GizmoMain.class.getResource("/guilfordfarm.png").toURI()));
			ImageMethods.addAlpha(original);
		}

		catch (IOException e)
		{
			System.out.println(e);
		}
		// Display the image

		swatch = new Color(200, 100, 200);
		colorSwatch = ImageMethods.displayColor(swatch, 100, 100, 2*original.getWidth() + 20, 0, "Current Color");
		originalPanel = new ImageLayeredPane(original, colorSwatch);
		modified = ImageMethods.clone(original);
		ImageMethods.addAlpha(modified);
		modifiedPanel = new ImageLayeredPane(modified, colorSwatch);
		originalImage = displayImage(originalPanel, 0, 0, "Original Image");
		modifiedImage = displayImage(modifiedPanel, original.getWidth() + 10, 0, "Modified Image");
		controlPanel = new ControlPanel(modifiedPanel, originalPanel, colorSwatch);
		controlFrame = new JFrame();
		controlFrame.getContentPane().add(controlPanel);
		controlFrame.setLocation(2*original.getWidth() + 20, 150);
		controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		controlFrame.pack();
		controlFrame.setVisible(true);

		// Create a transparent pane for drawing over the image
//		DrawGlassPane glassPane = new DrawGlassPane();
//		modifiedImage.setGlassPane(glassPane);
//		glassPane.setVisible(true);
//
		DrawMenuBar modifiedMenuBar = new DrawMenuBar(modifiedPanel);
		modifiedImage.setJMenuBar(modifiedMenuBar);
		modifiedImage.pack();
	


	}
	public static JFrame displayImage(ImageLayeredPane image, 
			int shiftXLocation, int shiftYLocation, String title)
	{
		JFrame frame = new JFrame(title);    
		frame.getContentPane().add(image);
//		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		frame.pack();
		frame.setLocation(shiftXLocation, shiftYLocation);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;

	}

	public static JFrame displayImage(JPanel panel, 
			int shiftXLocation, int shiftYLocation, String title)
	{
		JFrame frame = new JFrame(title);    
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(shiftXLocation, shiftYLocation);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;

	}

}
