package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import doc.GridPoint;
import doc.mathobjects.LineObject;
import doc.mathobjects.MathObject;
import doc.mathobjects.PolygonObject;

public class LineObjectGUI extends MathObjectGUI<LineObject>{

	public void drawMathObject(LineObject object, Graphics g, Point pageOrigin, float zoomLevel){
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int thickness = (int) (object.getThickness() * zoomLevel);
		
		Graphics2D g2d = (Graphics2D)g; 
		
		g2d.setStroke(new BasicStroke(thickness));
		
		GridPoint[] points = object.getAdjustedVertices();
		
		for (int i = 0; i < points.length; i++){
			points[i] = new GridPoint((int) (points[i].getx() * width) + xOrigin,
					(int) (points[i].gety() * height) + yOrigin);
		}
		
		if ( Math.abs(points[0].getx() - points[1].getx()) < 5){
			points[0].setx(points[1].getx());
		}
		
		else if ( Math.abs(points[0].gety() - points[1].gety()) < 5){
			points[0].sety(points[1].gety());
		}
		
		if ( object.getLineColor() == null){
			g.setColor(Color.BLACK);
		}
		else{
			g.setColor(object.getLineColor());
		}
		g2d.drawLine( (int) points[0].getx(), (int) points[0].gety(),
				(int) points[1].getx(), (int) points[1].gety());
		
		g2d.setStroke(new BasicStroke(1));
	}
}
