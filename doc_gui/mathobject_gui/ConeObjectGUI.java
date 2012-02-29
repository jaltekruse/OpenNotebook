package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import doc.GridPoint;
import doc.mathobjects.ConeObject;

public class ConeObjectGUI {

	public void drawMathObject(ConeObject object, Graphics g, Point pageOrigin, float zoomLevel){
		g.setColor(Color.BLACK);
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int thickness = (int) (object.getThickness() * zoomLevel);
		
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
			fillShape.addPoint( (int) (translated[0].getx() * width ) + xOrigin,
					 (int) (translated[0].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (translated[1].getx() * width ) + xOrigin,
					 (int) (translated[1].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (translated[2].getx() * width ) + xOrigin,
					 (int) (translated[2].gety() * height ) + yOrigin);
			g2d.setColor(fillColor);
			g2d.fillPolygon(fillShape);
			
			
			if (object.isFlippedVertically()){
				g2d.setColor(MathObjectGUI.brightenColor(fillColor));;
				g2d.fillOval(xOrigin,  yOrigin, width,
						(int) (object.getInsideEdgeOfDisk().gety() * height));
			}
			else{
				g2d.setColor(MathObjectGUI.brightenColor(fillColor));
				g2d.fillOval(xOrigin,  (int) (object.getPointBehindCone().gety() * height) + yOrigin ,
						width, (int) (object.getHalfDiskHeight() * height));
			}
	
		}
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER) );
		g2d.setColor(Color.BLACK);
		
		g.drawLine((int) (translated[0].getx() * width) + xOrigin,
				(int) (translated[0].gety() * height) + yOrigin ,
				(int) (translated[1].getx() * width) + xOrigin,
				(int) (translated[1].gety() * height) + yOrigin );
		g.drawLine((int) (translated[1].getx() * width) + xOrigin,
				(int) (translated[1].gety() * height) + yOrigin ,
				(int) (translated[2].getx() * width) + xOrigin,
				(int) (translated[2].gety() * height) + yOrigin );
		if (object.isFlippedVertically()){
			g.drawOval(xOrigin,  yOrigin, width,
					(int) (object.getHalfDiskHeight() * height));
		}
		else{
			g.drawOval(xOrigin,  (int) (object.getPointBehindCone().gety() * height) + yOrigin , width,
					(int) (object.getHalfDiskHeight() * height));
		}
		
		g2d.setStroke(new BasicStroke(1));
	}
}
