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
		int xOrigin = (int) (pageOrigin.getX() + pObject.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + pObject.getyPos() * zoomLevel);
		int width = (int) (mObj.getWidth() * zoomLevel);
		int height = (int) (mObj.getHeight() * zoomLevel);
		GridPoint[] pts = pObject.getAdjustedVertices();
		int[] xVals = new int[pts.length];
		int[] yVals = new int[pts.length];
		int i = 0;
		for (GridPoint pt : pts) {
			xVals[i] = (int) (pt.getx() * width) + xOrigin;
			yVals[i] = (int) (pt.gety() * height) + yOrigin;
			i++;
		}
		return new Polygon(xVals, yVals, pts.length);
	}

	public void drawMathObject(PolygonObject object, Graphics g, Point pageOrigin, float zoomLevel){
		g.setColor(Color.BLACK);
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int thickness = (int) (object.getThickness() * zoomLevel);
		
		Graphics2D g2d = (Graphics2D)g; 
		
		g2d.setStroke(new BasicStroke(thickness));
		
		Polygon p = new Polygon();
		
		GridPoint[] points = object.getAdjustedVertices();
		for (int i = 0; i < points.length; i++){
			p.addPoint((int) (points[i].getx() * width) + xOrigin,
					(int) (points[i].gety() * height) + yOrigin);
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
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int thickness = (int) (object.getThickness() * zoomLevel);


		GridPoint[] pts = object.getAdjustedVertices();
		for (int i = 0; i < pts.length; i++){
			g.setColor(Color.YELLOW);
			g.fillOval((int) (pts[i].getx() * width) + xOrigin - dotRadius,
					(int) (pts[i].gety() * height) + yOrigin - dotRadius
					, 2 * dotRadius, 2 * dotRadius);
			g.setColor(Color.BLACK);
			g.drawOval((int) (pts[i].getx() * width) + xOrigin - dotRadius,
					(int) (pts[i].gety() * height) + yOrigin - dotRadius
					, 2 * dotRadius, 2 * dotRadius);
		}
		}
	}
}
