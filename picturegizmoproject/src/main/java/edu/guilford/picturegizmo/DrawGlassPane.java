package edu.guilford.picturegizmo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class DrawGlassPane extends JPanel {

	private Point startPoint, endPoint;
	private ArrayList<Line2D> drawLines;
	private Color currentColor;


	public DrawGlassPane() {
		// TODO Auto-generated constructor stub
		drawLines = new ArrayList<Line2D>();
		currentColor = Color.BLACK;

	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);


		//		DrawListener dListener = new DrawListener();
		//		addMouseListener(dListener);
		//		addMouseMotionListener(dListener);


		if (startPoint != null)
		{
			g.setColor(Color.gray);
			g.drawLine((int)startPoint.getX(), (int)startPoint.getY(), 
					(int)endPoint.getX(), (int)endPoint.getY());
		}
		if (drawLines.size() > 0)
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(currentColor);
			g2d.setStroke(new BasicStroke(4));
			for (Line2D line : drawLines)
			{
				g2d.drawLine((int)line.getX1(), (int)line.getY1(), 
						(int)line.getX2(), (int)line.getY2());
				
			}
		}

	}


	//	private class DrawListener implements MouseListener, MouseMotionListener
	//	{

	public Color getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(Color currentColor) {
		this.currentColor = currentColor;
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		endPoint = e.getPoint();
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		startPoint = e.getPoint();

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		drawLines.add(new Line2D.Double(startPoint, endPoint));
//		System.out.println(drawLines.size());
		startPoint = endPoint = null;
		repaint();
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	//	}

}
