/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import doc.mathobjects.OvalObject;

public class OvalObjectGUI extends MathObjectGUI<OvalObject> {

	public void drawMathObject(OvalObject object, Graphics g, Point pageOrigin,
			float zoomLevel) {
//		System.out.println("draw oval");
		g.setColor(Color.BLACK);
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int thickness = (int) (object.getThickness() * zoomLevel);
		
		Graphics2D g2d = (Graphics2D)g; 
		g2d.setStroke(new BasicStroke(thickness));
		
		if (object.getColor() != null){
			g2d.setColor(object.getColor());
			g2d.fill(new Ellipse2D.Double(xOrigin, yOrigin, width, height));
		}
		g2d.setColor(Color.BLACK);
		g2d.draw(new Ellipse2D.Double(xOrigin, yOrigin, width, height));
		
		//reset graphics object to draw without additional thickness
		g2d.setStroke(new BasicStroke());
		
//		g.setColor(Color.BLUE);
//		g.drawRect(xOrigin, yOrigin, width, height);
	}

}
