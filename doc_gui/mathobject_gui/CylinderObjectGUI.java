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
		ScaledSizeAndPosition sap = getSizeAndPositionWithLineThickness(object, pageOrigin,
				zoomLevel, object.getThickness());
		
		Graphics2D g2d = (Graphics2D)g;
		Color fillColor = object.getColor();
		Polygon fillShape;
		
		if (fillColor != null){
			fillShape = new Polygon();
			fillShape.addPoint( (int) (object.getSide1pts()[0].getx() * sap.getWidth() ) + sap.getxOrigin(),
					 (int) (object.getSide1pts()[0].gety() * sap.getHeight() ) + sap.getyOrigin());
			fillShape.addPoint( (int) (object.getSide1pts()[1].getx() * sap.getWidth() ) + sap.getxOrigin(),
					 (int) (object.getSide1pts()[1].gety() * sap.getHeight() ) + sap.getyOrigin());
			fillShape.addPoint( (int) (object.getSide2pts()[1].getx() * sap.getWidth() ) + sap.getxOrigin(),
					 (int) (object.getSide2pts()[1].gety() * sap.getHeight() ) + sap.getyOrigin());
			fillShape.addPoint( (int) (object.getSide2pts()[0].getx() * sap.getWidth() ) + sap.getxOrigin(),
					 (int) (object.getSide2pts()[0].gety() * sap.getHeight() ) + sap.getyOrigin());
			g2d.setColor(fillColor);
			g2d.fillPolygon(fillShape);
			
			
			if (object.isFlippedVertically()){
				g2d.fillOval(sap.getxOrigin(),  sap.getyOrigin(), sap.getWidth(),
						(int) (object.getInsideEdgeOfDisk().gety() * sap.getHeight()));
				g2d.setColor(MathObjectGUI.brightenColor(fillColor));
				g2d.fillOval(sap.getxOrigin(),  (int) (object.getPointBehindCylinder().gety() * sap.getHeight()) + sap.getyOrigin() ,
						sap.getWidth(), (int) (object.getHalfDiskHeight() * sap.getHeight()));
			}
			else{
				g2d.fillOval(sap.getxOrigin(),  (int) (object.getPointBehindCylinder().gety() * sap.getHeight()) + sap.getyOrigin() ,
						sap.getWidth(), (int) (object.getHalfDiskHeight() * sap.getHeight()));
				g2d.setColor(MathObjectGUI.brightenColor(fillColor));
				g2d.fillOval(sap.getxOrigin(),  sap.getyOrigin(), sap.getWidth(),
						(int) (object.getInsideEdgeOfDisk().gety() * sap.getHeight()));
				
			}
	
		}
		g2d.setStroke(new BasicStroke(sap.getLineThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER) );
		g2d.setColor(Color.BLACK);
		
		g.drawLine((int) (object.getSide1pts()[0].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (object.getSide1pts()[0].gety() * sap.getHeight()) + sap.getyOrigin() ,
				(int) (object.getSide1pts()[1].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (object.getSide1pts()[1].gety() * sap.getHeight()) + sap.getyOrigin() );
		g.drawLine((int) (object.getSide2pts()[0].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (object.getSide2pts()[0].gety() * sap.getHeight()) + sap.getyOrigin() ,
				(int) (object.getSide2pts()[1].getx() * sap.getWidth()) + sap.getxOrigin(),
				(int) (object.getSide2pts()[1].gety() * sap.getHeight()) + sap.getyOrigin() );
		if (object.isFlippedVertically()){
			g.drawArc(sap.getxOrigin(), sap.getyOrigin() , sap.getWidth(),
					(int) (object.getHalfDiskHeight() * sap.getHeight()),
					0, 180);
			g.drawOval(sap.getxOrigin(),  (int) (object.getPointBehindCylinder().gety() * sap.getHeight()) + sap.getyOrigin() , sap.getWidth(),
					(int) (object.getHalfDiskHeight() * sap.getHeight()));
		}
		else{
			g.drawOval(sap.getxOrigin(),  sap.getyOrigin(), sap.getWidth(),
					(int) (object.getHalfDiskHeight() * sap.getHeight()));
			g.drawArc(sap.getxOrigin(),  (int) (object.getPointBehindCylinder().gety() * sap.getHeight()) + sap.getyOrigin() , sap.getWidth(),
					(int) (object.getHalfDiskHeight() * sap.getHeight()),
					180, 180);
		}
		
		g2d.setStroke(new BasicStroke(1));
	}
}
