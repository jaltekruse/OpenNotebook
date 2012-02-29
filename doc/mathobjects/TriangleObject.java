/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.mathobjects;

import doc.GridPoint;
import doc.attributes.EnumeratedAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class TriangleObject extends PolygonObject {
	
	public static final String MAKE_RIGHT_TRIANGLE = "make right triangle",
			MAKE_ISOSCELES_TRIANGLE = "make isosceles triangle";
	public static final String ISOSCELES = "isosoceles", RIGHT = "right",
			TRIANGLE_TYPE = "triangle type"; 
	private String[] triangleTypes = { ISOSCELES, RIGHT };
	
	private static final GridPoint[] rightVertices = {
		new GridPoint(0, 0), new GridPoint(0, 1), new GridPoint(1, 1)
	};
	
	private static final GridPoint[] isosocelesVertices = {
		new GridPoint(.5, 0), new GridPoint(0, 1), new GridPoint(1, 1)
	};
		
	public TriangleObject(MathObjectContainer p){
		super(p);
		addTriangleActions();
		addTriangleAttributes();
	}
	
	public TriangleObject(MathObjectContainer p, int x, int y, int w, int h, int thickness) {
		super(p, x, y, w, h, thickness);
		addTriangleActions();
		addTriangleAttributes();
	}

	public TriangleObject() {
		addTriangleActions();
		addTriangleAttributes();
	}
	
	private void addTriangleActions(){
		addAction(PolygonObject.FLIP_VERTICALLY);
		addAction(PolygonObject.FLIP_HORIZONTALLY);
		addAction(MAKE_RIGHT_TRIANGLE);
		addAction(MAKE_ISOSCELES_TRIANGLE);
	}
	
	private void addTriangleAttributes(){
		addAttribute(new EnumeratedAttribute(TRIANGLE_TYPE, ISOSCELES, false, triangleTypes));
	}
	
	@Override
	protected void addDefaultAttributes() {
		
	}
	
	@Override
	public void performSpecialObjectAction(String s){
		if(s.equals(MAKE_RIGHT_TRIANGLE)){
			getAttributeWithName(TRIANGLE_TYPE).setValue(RIGHT);
		}
		else if(s.equals(MAKE_ISOSCELES_TRIANGLE)){
			getAttributeWithName(TRIANGLE_TYPE).setValue(ISOSCELES);
		}
	}
	
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return TRIANGLE_OBJ;
	}

	@Override
	public GridPoint[] getVertices() {
		// TODO Auto-generated method stub
		if ( getAttributeWithName(TRIANGLE_TYPE).getValue().equals(ISOSCELES)){
			return isosocelesVertices;
		}
		else if (getAttributeWithName(TRIANGLE_TYPE).getValue().equals(RIGHT)){
			return rightVertices;
		}
		else{
			return null;
		}
	}

	@Override
	public MathObject newInstance() {
		return new TriangleObject();
	}
	
	
}
