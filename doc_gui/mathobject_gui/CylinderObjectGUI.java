package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import doc.mathobjects.CylinderObject;

public class CylinderObjectGUI extends MathObjectGUI<CylinderObject> {

	public void drawMathObject(CylinderObject object, Graphics g, Point pageOrigin, float zoomLevel){
		g.setColor(Color.BLACK);
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int thickness = (int) (object.getThickness() * zoomLevel);
		
		Graphics2D g2d = (Graphics2D)g;
		Color fillColor = object.getColor();
		Polygon fillShape;
		
		if (fillColor != null){
			fillShape = new Polygon();
			fillShape.addPoint( (int) (object.getSide1pts()[0].getx() * width ) + xOrigin,
					 (int) (object.getSide1pts()[0].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (object.getSide1pts()[1].getx() * width ) + xOrigin,
					 (int) (object.getSide1pts()[1].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (object.getSide2pts()[1].getx() * width ) + xOrigin,
					 (int) (object.getSide2pts()[1].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (object.getSide2pts()[0].getx() * width ) + xOrigin,
					 (int) (object.getSide2pts()[0].gety() * height ) + yOrigin);
			g2d.setColor(fillColor);
			g2d.fillPolygon(fillShape);
			
			
			if (object.isFlippedVertically()){
				g2d.fillOval(xOrigin,  yOrigin, width,
						(int) (object.getInsideEdgeOfDisk().gety() * height));
				g2d.setColor(MathObjectGUI.brightenColor(fillColor));
				g2d.fillOval(xOrigin,  (int) (object.getPointBehindCylinder().gety() * height) + yOrigin ,
						width, (int) (object.getHalfDiskHeight() * height));
			}
			else{
				g2d.fillOval(xOrigin,  (int) (object.getPointBehindCylinder().gety() * height) + yOrigin ,
						width, (int) (object.getHalfDiskHeight() * height));
				g2d.setColor(MathObjectGUI.brightenColor(fillColor));
				g2d.fillOval(xOrigin,  yOrigin, width,
						(int) (object.getInsideEdgeOfDisk().gety() * height));
				
			}
	
		}
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER) );
		g2d.setColor(Color.BLACK);
		
		g.drawLine((int) (object.getSide1pts()[0].getx() * width) + xOrigin,
				(int) (object.getSide1pts()[0].gety() * height) + yOrigin ,
				(int) (object.getSide1pts()[1].getx() * width) + xOrigin,
				(int) (object.getSide1pts()[1].gety() * height) + yOrigin );
		g.drawLine((int) (object.getSide2pts()[0].getx() * width) + xOrigin,
				(int) (object.getSide2pts()[0].gety() * height) + yOrigin ,
				(int) (object.getSide2pts()[1].getx() * width) + xOrigin,
				(int) (object.getSide2pts()[1].gety() * height) + yOrigin );
		if (object.isFlippedVertically()){
			g.drawArc(xOrigin, yOrigin , width,
					(int) (object.getHalfDiskHeight() * height),
					0, 180);
			g.drawOval(xOrigin,  (int) (object.getPointBehindCylinder().gety() * height) + yOrigin , width,
					(int) (object.getHalfDiskHeight() * height));
		}
		else{
			g.drawOval(xOrigin,  yOrigin, width,
					(int) (object.getHalfDiskHeight() * height));
			g.drawArc(xOrigin,  (int) (object.getPointBehindCylinder().gety() * height) + yOrigin , width,
					(int) (object.getHalfDiskHeight() * height),
					180, 180);
		}
		
		g2d.setStroke(new BasicStroke(1));
	}
}
