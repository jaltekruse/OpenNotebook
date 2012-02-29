package doc.mathobjects;

import java.awt.Color;

import doc.GridPoint;
import doc.attributes.AttributeException;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.IntegerAttribute;

public class ConeObject extends MathObject {
	
	private GridPoint[] trianglePts = { new GridPoint(0, .865), new GridPoint(.5, 0), new GridPoint(1, .865)};
	
	private GridPoint insideEdgeOfDisk = new GridPoint(.5, .25);
	
	private double heightHalfDisk = .25;
	
	private GridPoint pointBehindCone = new GridPoint(.5, .75);
	
	public ConeObject(MathObjectContainer p){
		super(p);
		addAction(FLIP_VERTICALLY);
	}
	
	public ConeObject(){
		addAction(FLIP_VERTICALLY);
	}
	
	@Override
	public void addDefaultAttributes() {
		addAttribute(new IntegerAttribute(LINE_THICKNESS, 1, 1, 20));
		addAttribute(new ColorAttribute(FILL_COLOR, null));
		addAttribute(new BooleanAttribute(VERTICALLY_FLIPPED, false, false));
	}

	@Override
	public String getType() {
		return CONE_OBJECT;
	}
	
	@Override
	public void performSpecialObjectAction(String s){}

	public GridPoint[] getTrianglePts() {
		return trianglePts;
	}
	
	public int getThickness(){
		return ((IntegerAttribute)getAttributeWithName(LINE_THICKNESS)).getValue();
	}
	
	public Color getColor(){
		return ((ColorAttribute)getAttributeWithName(FILL_COLOR)).getValue();
	}

	public void setInsideEdgeOfDisk(GridPoint insideEdgeOfDisk) {
		this.insideEdgeOfDisk = insideEdgeOfDisk;
	}

	public GridPoint getInsideEdgeOfDisk() {
		return insideEdgeOfDisk;
	}

	public void setPointBehindCylinder(GridPoint pointBehindCylinder) {
		this.pointBehindCone = pointBehindCylinder;
	}

	public GridPoint getPointBehindCone() {
		return pointBehindCone;
	}

	public void setHeightHalfDisk(double heightHalfDisk) {
		this.heightHalfDisk = heightHalfDisk;
	}

	public double getHalfDiskHeight() {
		return heightHalfDisk;
	}

	@Override
	public MathObject newInstance() {
		return new ConeObject();
	}

}
