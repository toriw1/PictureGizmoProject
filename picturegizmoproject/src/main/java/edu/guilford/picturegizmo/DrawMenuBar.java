package edu.guilford.picturegizmo;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class DrawMenuBar extends JMenuBar {

	private JMenuItem exitItem, lineItem, colorItem;
	private ImageLayeredPane layeredPane;
	private ImageIcon drawIcon;
	private DrawMenuBar thisMenu;

	public DrawMenuBar(ImageLayeredPane layeredPane) {
		// TODO Auto-generated constructor stub
		this.layeredPane = layeredPane;
		JMenu fileMenu = new JMenu("File");
		add(fileMenu);
		exitItem = new JMenuItem("Exit");
		MenuListener mListener = new MenuListener();
		exitItem.addActionListener(mListener);
		fileMenu.add(exitItem);
		JMenu drawMenu = new JMenu("Draw");
		add(drawMenu);
		lineItem = new JMenuItem("Line");
		lineItem.addActionListener(mListener);;
		drawMenu.add(lineItem);
		drawIcon = new ImageIcon("Small-checkmark.png");
		colorItem = new JMenuItem("Color");
		colorItem.addActionListener(mListener);;
		drawMenu.add(colorItem);
		thisMenu = this;



	}

	private class MenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == exitItem)
			{
				System.exit(0);
			}
			else if (e.getSource() == lineItem)
			{
				layeredPane.setDrawNewLine(!layeredPane.isDrawNewLine());
				if (layeredPane.isDrawNewLine())
				{
					lineItem.setIcon(drawIcon);
				}
				else
				{
					lineItem.setIcon(null);
				}
			}
			else if (e.getSource() == colorItem)
			{
				Color newColor = JColorChooser.showDialog(
						thisMenu,
						"Choose Background Color",
						layeredPane.getGlass().getCurrentColor());
				if (newColor != null)
				{
					layeredPane.getGlass().setCurrentColor(newColor);
				}

			}

		}

	}

}
