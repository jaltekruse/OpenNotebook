/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
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

	public Polygon getCollisionAndSelectionPolygon(PolygonObject pObject, Point pageOrigin, float zoomLevel) {

		ScaledSizeAndPosition sap = getSizeAndPosition(pObject, pageOrigin,
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
		drawMathObject(object, g, pageOrigin, zoomLevel);
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
