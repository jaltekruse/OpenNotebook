package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import doc.GridPoint;
import doc.mathobjects.ConeObject;

public class ConeObjectGUI extends MathObjectGUI<ConeObject> {

	public void drawMathObject(ConeObject object, Graphics g, Point pageOrigin, float zoomLevel){
		g.setColor(Color.BLACK);
		ScaledSizeAndPosition sap = getSizeAndPositionWithLineThickness(object, pageOrigin,
				zoomLevel, object.getThickness());
		
		Graphics2D g2d = (Graphics2D)g;
		Color fillColor = object.getColor();
		Polygon fillShape;
		
		GridPoint[] translated = new GridPoint[3];
		int i = 0;
		for (GridPoint p : object.getTrianglePts()){
			if ( object.isFlippedVertically()){
				translated[i] = object.flipPointVertically(p);
			}
			else{
				translated[i] = p.clone();
			}
			i++;
		}
		
		if (fillColor != null){
			fillShape = new Polygon();
			fillShape.addPoint( (int) (translated[0].getx() * sap.getWidth() ) + sap.getxOrigin(),
					 (int) (translated[0].gety() * sap.getHeight() ) + sap.getyOrigin());
			fillShape.addPoint( (int) (translated[1].getx() * sap.getWidth() ) + sap.getxOrigin(),
					 (int) (translated[1].gety() * sap.getHeight() ) + sap.getyOrigin());
			fillShape.addPoint( (int) (translated[2].getx() * sap.getWidth() ) + sap.getxOrigin(),
					 (int) (translated[2].gety() * sap.getHeight() ) + sap.getyOrigin());
			g2d.setColor(fillColor);
			g2d.fillPolygon(fillShape);

			g2d.setColor(MathObjectGUI.brightenColor(fillColor));
			if (object.isFlippedVertically()){
				g2d.fillOval(sap.getxOrigin(),  sap.getyOrigin(), sap.getWidth(),
						(int) (object.getInsideEdgeOfDisk().gety() * sap.getHeight()));
			}
			else{
				g2d.fillOval(sap.getxOrigin(),  (int) (object.getPointBehindCone().gety() * sap.getHeight()) + sap.getyOrigin() ,
						sap.getWidth(), (int) (object.getHalfDiskHeight() * sap.getHeight()));
			}
	
		}
		g2d.setStroke(new BasicStroke(sap.getLineThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER) );
		g2d.setColor(Color.BLACK);
		
		g.drawLine((int) (translated[0].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (translated[0].gety() * sap.getHeight()) + sap.getyOrigin() ,
				(int) (translated[1].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (translated[1].gety() * sap.getHeight()) + sap.getyOrigin() );
		g.drawLine((int) (translated[1].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (translated[1].gety() * sap.getHeight()) + sap.getyOrigin() ,
				(int) (translated[2].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (translated[2].gety() * sap.getHeight()) + sap.getyOrigin() );
		if (object.isFlippedVertically()){
			g.drawOval(sap.getxOrigin(),  sap.getyOrigin(), sap.getWidth(),
					(int) (object.getHalfDiskHeight() * sap.getHeight()));
		}
		else{
			g.drawOval(sap.getxOrigin(),  (int) (object.getPointBehindCone().gety() * sap.getHeight()) + sap.getyOrigin() , sap.getWidth(),
					(int) (object.getHalfDiskHeight() * sap.getHeight()));
		}
		
		g2d.setStroke(new BasicStroke(1));
	}
}
