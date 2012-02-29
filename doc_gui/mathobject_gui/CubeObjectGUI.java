package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import doc.GridPoint;
import doc.mathobjects.CubeObject;

public class CubeObjectGUI extends MathObjectGUI<CubeObject>{

	public void drawMathObject(CubeObject object, Graphics g, Point pageOrigin, float zoomLevel){
		g.setColor(Color.BLACK);
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int thickness = (int) (object.getThickness() * zoomLevel);
		
		Graphics2D g2d = (Graphics2D)g;
		Color fillColor = object.getColor();
		Polygon fillShape;
		
		GridPoint[] outsidePoints = new GridPoint[6];
		int index = 0;
		for (GridPoint p : CubeObject.getOutsidePoints()){
			outsidePoints[index] = new GridPoint(p.getx(), p.gety());
			index++;
		}
		GridPoint innerPt = new GridPoint( CubeObject.getInnerPoint().getx(),
				CubeObject.getInnerPoint().gety());
		GridPoint side1Pt = new GridPoint( CubeObject.getSide1Pt().getx(),
				CubeObject.getSide1Pt().gety());
		GridPoint side2Pt = new GridPoint( CubeObject.getSide2Pt().getx(),
				CubeObject.getSide2Pt().gety());
		GridPoint cornerPt = new GridPoint( CubeObject.getCornerPt().getx(),
				CubeObject.getCornerPt().gety());
		
		if (object.isFlippedVertically()){
			for (GridPoint p : outsidePoints){
				flipPointVertically(p);
			}
			flipPointVertically(innerPt);
			flipPointVertically(cornerPt);
			flipPointVertically(side1Pt);
			flipPointVertically(side2Pt);
		}
		
		if (object.isFlippedHorizontally()){
			for (GridPoint p : outsidePoints){
				flipPointHorizontally(p);
			}
			flipPointHorizontally(innerPt);
			flipPointHorizontally(cornerPt);
			flipPointHorizontally(side1Pt);
			flipPointHorizontally(side2Pt);
		}
		
		if (fillColor != null){
			fillShape = new Polygon();
			fillShape.addPoint( (int) (outsidePoints[0].getx() * width ) + xOrigin,
					 (int) (outsidePoints[0].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (outsidePoints[1].getx() * width ) + xOrigin,
					 (int) (outsidePoints[1].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (outsidePoints[2].getx() * width ) + xOrigin,
					 (int) (outsidePoints[2].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (innerPt.getx() * width ) + xOrigin,
					 (int) (innerPt.gety() * height ) + yOrigin);
			g2d.setColor(fillColor);
			g2d.fillPolygon(fillShape);
			
			fillShape = new Polygon();
			fillShape.addPoint( (int) (outsidePoints[2].getx() * width ) + xOrigin,
					 (int) (outsidePoints[2].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (outsidePoints[3].getx() * width ) + xOrigin,
					 (int) (outsidePoints[3].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (outsidePoints[4].getx() * width ) + xOrigin,
					 (int) (outsidePoints[4].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (innerPt.getx() * width ) + xOrigin,
					 (int) (innerPt.gety() * height ) + yOrigin);
			g2d.setColor(MathObjectGUI.brightenColor(fillColor) );
			g2d.fillPolygon(fillShape);
			
			fillShape = new Polygon();
			fillShape.addPoint( (int) (outsidePoints[4].getx() * width ) + xOrigin,
					 (int) (outsidePoints[4].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (outsidePoints[5].getx() * width ) + xOrigin,
					 (int) (outsidePoints[5].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (outsidePoints[0].getx() * width ) + xOrigin,
					 (int) (outsidePoints[0].gety() * height ) + yOrigin);
			fillShape.addPoint( (int) (innerPt.getx() * width ) + xOrigin,
					 (int) (innerPt.gety() * height ) + yOrigin);
			g2d.setColor(MathObjectGUI.brightenColor(fillColor) );
			g2d.fillPolygon(fillShape);
		}
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER) );
		g2d.setColor(Color.BLACK);
		
		Polygon p = new Polygon();
		for (int i = 0; i < outsidePoints.length; i++){
			p.addPoint((int) (outsidePoints[i].getx() * width) + xOrigin,
					(int) (outsidePoints[i].gety() * height) + yOrigin);
		}
		
		g.drawLine((int) (cornerPt.getx() * width) + xOrigin,
				(int) (cornerPt.gety() * height) + yOrigin ,
				(int) (innerPt.getx() * width) + xOrigin,
				(int) (innerPt.gety() * height) + yOrigin);
		g.drawLine((int) (side1Pt.getx() * width) + xOrigin ,
				(int) (side1Pt.gety() * height) + yOrigin,
				(int) (innerPt.getx() * width) + xOrigin,
				(int) (innerPt.gety() * height) + yOrigin);
		g.drawLine((int) (side2Pt.getx() * width) + xOrigin,
				(int) (side2Pt.gety() * height) + yOrigin ,
				(int) (innerPt.getx() * width) + xOrigin,
				(int) (innerPt.gety() * height) + yOrigin);
		
		g2d.drawPolygon(p);
		
		g2d.setStroke(new BasicStroke(1));
	}
	
	private void flipPointVertically(GridPoint p){
		if (p.gety() < .5){
			p.sety(p.gety() + 2 * (.5 - p.gety()));
		}
		else if (p.gety() > .5){
			p.sety(p.gety() + 2 * (.5 - p.gety()));
		}
		else{
			//y is .5, should not be shifted
		}
	}
	
	private void flipPointHorizontally(GridPoint p){
		if (p.getx() < .5){
			p.setx(p.getx() + 2 * (.5 - p.getx()));
		}
		else if (p.getx() > .5){
			p.setx(p.getx() + 2 * (.5 - p.getx()));
		}
		else{
			//x is .5, should not be shifted
		}
	}
}
