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
		g.setColor(Color.BLACK);
		ScaledSizeAndPosition sap = getSizeAndPositionWithLineThickness(object, pageOrigin,
				zoomLevel, object.getThickness());

		Graphics2D g2d = (Graphics2D)g; 
		g2d.setStroke(new BasicStroke(sap.getLineThickness()));
		
		if (object.getColor() != null){
			g2d.setColor(object.getColor());
			g2d.fill(new Ellipse2D.Double(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight()));
		}
		g2d.setColor(Color.BLACK);
		g2d.draw(new Ellipse2D.Double(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight()));
		
		//reset graphics object to draw without additional thickness
		g2d.setStroke(new BasicStroke());
	}

}
