package doc.mathobjects;

import java.awt.Color;

import doc.GridPoint;
import doc.Page;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.EnumeratedAttribute;
import doc.attributes.IntegerAttribute;

public class LineObject extends MathObject {
	
	public static final String ORIENTATION = "orientation", LEFT_TOP = "left top",
			RIGHT_TOP = "right top", HORIZONTAL = "horizontal", VERTICAL = "vertical";
	
	public static final String[] orientations = {LEFT_TOP, RIGHT_TOP, VERTICAL, HORIZONTAL};
	
	public static final GridPoint[] points = {new GridPoint(0,0), new GridPoint(1,1)};

	public LineObject(){
		
	}
	
	public LineObject(Page p){
		super(p);
	}
	
	public void addDefaultAttributes(){
//		addAttribute(new EnumeratedAttribute(ORIENTATION, LEFT_TOP, false, false, orientations));
		addAttribute(new BooleanAttribute(HORIZONTALLY_FLIPPED, false, false, false));
		addAttribute(new BooleanAttribute(VERTICALLY_FLIPPED, false, false, false));
		addAttribute(new IntegerAttribute(LINE_THICKNESS, 1, 1, 20, true, false));
		addAttribute(new ColorAttribute(LINE_COLOR, Color.BLACK, true, false));
		addAction(FLIP_HORIZONTALLY);
		addAction(FLIP_VERTICALLY);
	}
	
	public GridPoint[] getAdjustedVertices(){
		GridPoint[] points = this.points.clone();
		if ( isFlippedHorizontally() ){
			points = flipHorizontally(points);
		}
		if ( isFlippedVertically()){
			points = flipVertically(points);
		}
		return points;
	}
	
	public Color getLineColor(){
		return (Color) getAttributeWithName(LINE_COLOR).getValue();
	}
	
	public int getThickness(){
		return (Integer) getAttributeWithName(LINE_THICKNESS).getValue();
	}
	
	public String getOrientation(){
		return (String) getAttributeWithName(ORIENTATION).getValue();
	}
	
	public void setWidth(int width) {
		if ( width < 3 && width >= 0)
		{// snaps line to vertical
			width = 1;
		}
		getAttributeWithName(WIDTH).setValue(width);
	}
	
	public void setHeight(int height) {
		if ( height < 3 && height >= 0)
		{// snaps line to horizontal
			height = 1;
		}
		getAttributeWithName(HEIGHT).setValue(height);
	}
	
	@Override
	public String getType() {
		return LINE_OBJECT;
	}

	@Override
	public MathObject newInstance() {
		return new LineObject();
	}

}
