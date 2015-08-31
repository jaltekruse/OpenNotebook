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
import java.awt.Polygon;

import doc.GridPoint;
import doc.mathobjects.MathObject;
import doc.mathobjects.PolygonObject;

public class PolygonObjectGUI extends MathObjectGUI<PolygonObject> {
	
	private static final int dotRadius = 3;

	public Polygon getCollisionAndSelectionPolygon(MathObject mObj, Point pageOrigin, float zoomLevel) {
		PolygonObject pObject = (PolygonObject) mObj;

		ScaledSizeAndPosition sap = getSizeAndPosition(mObj, pageOrigin,
				zoomLevel);
		GridPoint[] pts = pObject.getAdjustedVertices();
		int[] xVals = new int[pts.length];
		int[] yVals = new int[pts.length];
		int i = 0;
		for (GridPoint pt : pts) {
			xVals[i] = (int) (pt.getx() * sap.getWidth()) + sap.getxOrigin();
			yVals[i] = (int) (pt.gety() * sap.getHeight()) + sap.getyOrigin();
			i++;
		}
		return new Polygon(xVals, yVals, pts.length);
	}

	public void drawMathObject(PolygonObject object, Graphics g, Point pageOrigin, float zoomLevel){
		g.setColor(Color.BLACK);
		ScaledSizeAndPosition sap = getSizeAndPositionWithLineThickness(object, pageOrigin,
				zoomLevel, object.getThickness());

		Graphics2D g2d = (Graphics2D)g; 
		
		g2d.setStroke(new BasicStroke(sap.getLineThickness()));
		
		Polygon p = new Polygon();
		
		GridPoint[] points = object.getAdjustedVertices();
		for (int i = 0; i < points.length; i++){
			p.addPoint((int) (points[i].getx() * sap.getWidth()) + sap.getxOrigin(),
					(int) (points[i].gety() * sap.getHeight()) + sap.getyOrigin());
		}
		
		if (object.getColor() != null){
			g2d.setColor(object.getColor());
			g2d.fillPolygon(p);
			g2d.setColor(Color.BLACK);
		}
		
		g2d.drawPolygon(p);
		
		g2d.setStroke(new BasicStroke(1));
	}
	
	public void drawInteractiveComponents(PolygonObject object, Graphics g, Point pageOrigin, float zoomLevel){
		// This is currently unused, this is an alternative to commenting out the code that makes sure it keeps compiling
		if (false) {
		ScaledSizeAndPosition sap = getSizeAndPosition(object, pageOrigin,
				zoomLevel);
		GridPoint[] pts = object.getAdjustedVertices();
		for (int i = 0; i < pts.length; i++){
			g.setColor(Color.YELLOW);
			g.fillOval((int) (pts[i].getx() * sap.getWidth()) + sap.getxOrigin() - dotRadius,
					(int) (pts[i].gety() * sap.getWidth()) + sap.getyOrigin() - dotRadius
					, 2 * dotRadius, 2 * dotRadius);
			g.setColor(Color.BLACK);
			g.drawOval((int) (pts[i].getx() * sap.getWidth()) + sap.getxOrigin() - dotRadius,
					(int) (pts[i].gety() * sap.getHeight()) + sap.getyOrigin() - dotRadius
					, 2 * dotRadius, 2 * dotRadius);
		}
		}
	}
}
