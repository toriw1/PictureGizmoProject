package edu.guilford.picturegizmo;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.guilford.imagemanipulation.ImageMethods;

public class ControlPanel extends JPanel {

	private JButton resetButton, saveButton, saveAllButton;
	private JButton brightenButton, darkenButton, showChannelButton, grayScaleButton;
	private JButton blurButton, embossButton, sharpenButton, edgeDetectButton;
	private JButton openButton, resetSelection, averageButton;
	private ImagePanel mPanel, oPanel;
	private ImageLayeredPane layeredPane;
	private JFrame swatch;
	private JRadioButton showColor, expandColor, selectRegion;
	private JRadioButton rbRed, rbGreen, rbBlue;
	private JPanel kernelPanel;
	private JPanel buttonPanel;
	private JTextField[][] kernelValues;
	private JButton kernelButton;
	private double[][] customKernel;
	private ArrayList<JRadioButton> colorButtons;

	public ControlPanel(ImageLayeredPane mPanel, ImageLayeredPane oPanel, JFrame swatch)
	{
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(5,5));
		kernelPanel =  new JPanel();
		kernelPanel.setPreferredSize(new Dimension(200, 200));
		kernelPanel.setLayout(new GridLayout(3,3));
		this.mPanel = mPanel.getImagePanel();
		this.oPanel = oPanel.getImagePanel();
		layeredPane = mPanel;
		this.swatch = swatch;
		ButtonListener buListener = new ButtonListener();
		resetButton = new JButton("Reset image");
		buttonPanel.add(resetButton);
		resetButton.addActionListener(buListener);
		saveButton = new JButton("Save image");
		buttonPanel.add(saveButton);
		saveButton.addActionListener(buListener);
		saveAllButton = new JButton("Save all");
		buttonPanel.add(saveAllButton);
		saveAllButton.addActionListener(buListener);
		BrightenListener bListener = new BrightenListener();
		brightenButton = new JButton("Brighten image");
		buttonPanel.add(brightenButton);
		brightenButton.addActionListener(bListener);
		darkenButton = new JButton("Darken image");
		buttonPanel.add(darkenButton);
		darkenButton.addActionListener(bListener);
		CheckBoxListener cbListener = new CheckBoxListener();
		showColor = new JRadioButton("Show color value");
		buttonPanel.add(showColor);
		showColor.addItemListener(cbListener);
		expandColor = new JRadioButton("Expand colors");
		buttonPanel.add(expandColor);
		expandColor.addItemListener(cbListener);
		selectRegion = new JRadioButton("Select region");
		buttonPanel.add(selectRegion);
		selectRegion.addItemListener(cbListener);
		resetSelection = new JButton("Reset Selection");
		resetSelection.addActionListener(buListener);
		buttonPanel.add(resetSelection);
		blurButton = new JButton("Blur");
		blurButton.addActionListener(buListener);
		buttonPanel.add(blurButton);
		embossButton = new JButton("Emboss");
		embossButton.addActionListener(buListener);
		buttonPanel.add(embossButton);
		sharpenButton = new JButton("Sharpen");
		sharpenButton.addActionListener(buListener);
		buttonPanel.add(sharpenButton);
		edgeDetectButton = new JButton("Edge Detect");
		edgeDetectButton.addActionListener(buListener);
		buttonPanel.add(edgeDetectButton);
		grayScaleButton = new JButton("Grayscale");
		grayScaleButton.addActionListener(buListener);
		buttonPanel.add(grayScaleButton);
		averageButton = new JButton("Average");
		averageButton.addActionListener(buListener);
		buttonPanel.add(averageButton);
		showChannelButton = new JButton("Show Channel");
		showChannelButton.addActionListener(buListener);
		buttonPanel.add(showChannelButton);
		rbRed = new JRadioButton("Red");
		rbGreen = new JRadioButton("Green");
		rbBlue = new JRadioButton("Blue");
		ButtonGroup colorButtonGroup = new ButtonGroup();
		colorButtons = new ArrayList<JRadioButton>();
		colorButtons.add(rbRed);
		colorButtons.add(rbGreen);
		colorButtons.add(rbBlue);
		ButtonGroup utilityButtonGroup = new ButtonGroup();
		utilityButtonGroup.add(showColor);
		utilityButtonGroup.add(expandColor);
		utilityButtonGroup.add(selectRegion);
		showColor.setSelected(true);
		openButton = new JButton("Open");
		buttonPanel.add(openButton);
		openButton.addActionListener(new OpenListener());
		
		for (JRadioButton button : colorButtons)
		{
			buttonPanel.add(button);
			colorButtonGroup.add(button);
		}
	
		rbRed.setSelected(true);
		
		kernelButton = new JButton("Custom kernel");
		kernelButton.addActionListener(buListener);
		buttonPanel.add(kernelButton);
		kernelValues = new JTextField[3][3];
		customKernel = new double[3][3];
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				customKernel[i][j] = 1./9.;
				kernelValues[i][j] = new JTextField(customKernel[i][j]+"", 5);
				kernelPanel.add(kernelValues[i][j]);
			}
		}
		add(buttonPanel);
		add(kernelPanel);
		
		
	}
	
	private void fillKernelValues(double[][] kernel)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				kernelValues[i][j].setText(kernel[i][j] + "");
			}
		}
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource() == resetButton)
			{
				ImageMethods.copyImage(oPanel.getDisplay(), mPanel.getDisplay());
				mPanel.repaint();
			}
			else if (e.getSource() == saveAllButton)
			{
				layeredPane.saveImageActionPerformed();
			}
			else if (e.getSource() == saveButton)
			{
				mPanel.saveImageActionPerformed();
			}
			else if (e.getSource() == showChannelButton)
			{
				for (int i = 0; i < colorButtons.size(); i++)
				{
					if (colorButtons.get(i).isSelected())
					{
						ImageMethods.showChannel(mPanel.getDisplay(), i);
					}
				}
				mPanel.repaint();
			}
			else if (e.getSource() == grayScaleButton)
			{
				ImageMethods.grayScale(mPanel.getDisplay(), mPanel.getSelectedRegion());
				mPanel.repaint();
			}
			else if (e.getSource() == blurButton)
			{
				ImageMethods.blurImage(mPanel.getDisplay(), mPanel.getSelectedRegion());
				fillKernelValues(ImageMethods.getBlurMatrix());
				mPanel.repaint();
			}
			else if (e.getSource() == embossButton)
			{
				ImageMethods.embossImage(mPanel.getDisplay(), mPanel.getSelectedRegion());
				fillKernelValues(ImageMethods.getEmbossMatrix());
				mPanel.repaint();
			}
			else if (e.getSource() == sharpenButton)
			{
				ImageMethods.sharpenImage(mPanel.getDisplay(), mPanel.getSelectedRegion());
				fillKernelValues(ImageMethods.getSharpenMatrix());
				mPanel.repaint();
			}
			else if (e.getSource() == edgeDetectButton)
			{
				ImageMethods.edgeDetectImage(mPanel.getDisplay(), mPanel.getSelectedRegion());
				fillKernelValues(ImageMethods.getEdgeDetectMatrix());
				mPanel.repaint();
			}
			else if (e.getSource() == averageButton)
			{
				ImageMethods.averageValue(mPanel.getDisplay(), mPanel.getSelectedRegion());
				mPanel.repaint();
			}
			else if (e.getSource() == resetSelection)
			{
				mPanel.resetSelection();
				mPanel.repaint();
			}
			else if (e.getSource() == kernelButton)
			{
				System.out.println("convolve");
				for (int i = 0; i < 3; i++)
				{
					for (int j = 0; j < 3; j++)
					{
						customKernel[i][j] = Double.parseDouble(kernelValues[i][j].getText());
					}
				}
				ImageMethods.convolveImage(mPanel.getDisplay(), customKernel, mPanel.getSelectedRegion());
				mPanel.repaint();
			}
		}
	}
	private class BrightenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource() == brightenButton)
			{
				ImageMethods.brightenImage(mPanel.getDisplay(), mPanel.getSelectedRegion());
			}
			else if (e.getSource() == darkenButton)
			{
				ImageMethods.darkenImage(mPanel.getDisplay(), mPanel.getSelectedRegion());
			}
			mPanel.repaint();
		}
	}
	
	private class CheckBoxListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			JRadioButton source = (JRadioButton)e.getSource();
			if (source == showColor)
			{
				boolean value = source.isSelected();
				mPanel.setColorShow(value);
				oPanel.setColorShow(value);
			}
			else if (source == expandColor)
			{
				boolean value = source.isSelected();
				mPanel.setExpandShow(value);
				if (!value)
				{
					mPanel.repaint();
				}
			}
			else if (source == selectRegion)
			{
				boolean value = source.isSelected();
				mPanel.setRegionSelect(value);
			}
		}
		
	}
	
	private class OpenListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser fc = new JFileChooser();
			int result = fc.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File openFile = fc.getSelectedFile();
				try {
					BufferedImage display = ImageIO.read(openFile);
					oPanel.setDisplay(display);
					BufferedImage cloneDisplay = ImageMethods.clone(display);
					mPanel.setDisplay(cloneDisplay);
//					System.out.println(display.getWidth() + " " + display.getHeight());
					oPanel.setSize(new Dimension( display.getWidth(), display.getHeight()));
					mPanel.setSize(new Dimension( display.getWidth(), display.getHeight()));
					JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(oPanel);
					topFrame.setPreferredSize(new Dimension( display.getWidth(), display.getHeight()));
					topFrame.pack();
					topFrame = (JFrame) SwingUtilities.getWindowAncestor(mPanel);
					topFrame.setPreferredSize(new Dimension( display.getWidth(), display.getHeight()));
					topFrame.pack();
					//					oPanel = new ImagePanel(oDisplay, swatch);
					mPanel.resetSelection();
					oPanel.repaint();
					mPanel.repaint();
//					Window arr[] = Window.getWindows();
//					System.out.println(Arrays.toString(arr));
//					arr[1].pack();
				}
				catch (IOException exception)
				{
					System.out.println(exception);
				}

			}
		}
	}
	

}
