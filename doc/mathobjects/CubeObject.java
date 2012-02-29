package doc.mathobjects;

import java.awt.Color;

import doc.GridPoint;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class CubeObject extends MathObject{
	
	private static GridPoint side1Pt = new GridPoint(0, .25),
			cornerPt = new GridPoint(1,0), side2Pt = new GridPoint(.75, 1);
	private static GridPoint[] outsidePoints = { side1Pt, new GridPoint(0,1),
		side2Pt, new GridPoint(1, .75), cornerPt, new GridPoint(.25, 0)};
	
	private static GridPoint innerPoint = new GridPoint(.75, .25);

	
	public CubeObject(MathObjectContainer p, int x, int y, int w, int h, int t) {
		super(p, x, y, w, h);
		getAttributeWithName(LINE_THICKNESS).setValue(t);
		addAction(FLIP_VERTICALLY);
		addAction(FLIP_HORIZONTALLY);
	}
	
	public CubeObject(MathObjectContainer p){
		super(p);
		addAction(FLIP_VERTICALLY);
		addAction(FLIP_HORIZONTALLY);
	}

	public CubeObject() {
		addAction(FLIP_VERTICALLY);
		addAction(FLIP_HORIZONTALLY);
	}
	
	@Override
	public void performSpecialObjectAction(String s){}
	
	@Override
	public void addDefaultAttributes() {
		addAttribute(new IntegerAttribute(LINE_THICKNESS, 1, 1, 20));
		addAttribute(new ColorAttribute(FILL_COLOR, null));
		addAttribute(new BooleanAttribute(HORIZONTALLY_FLIPPED, false, false));
		addAttribute(new BooleanAttribute(VERTICALLY_FLIPPED, false, false));
	}
	
	public int getThickness(){
		return ((IntegerAttribute)getAttributeWithName(LINE_THICKNESS)).getValue();
	}
	
	public Color getColor(){
		return ((ColorAttribute)getAttributeWithName(FILL_COLOR)).getValue();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return CUBE_OBJECT;
	}

	public static GridPoint getInnerPoint() {
		return innerPoint;
	}


	public static GridPoint[] getOutsidePoints() {
		return outsidePoints;
	}

	public static GridPoint getSide1Pt() {
		return side1Pt;
	}

	public static GridPoint getCornerPt() {
		return cornerPt;
	}

	public static GridPoint getSide2Pt() {
		return side2Pt;
	}

	@Override
	public MathObject newInstance() {
		return new CubeObject();
	}
}
