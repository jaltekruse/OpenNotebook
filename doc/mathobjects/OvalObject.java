/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.mathobjects;

import java.awt.Color;

import doc.attributes.ColorAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class OvalObject extends MathObject {
	
	public OvalObject(MathObjectContainer p, int x, int y, int width, int height, int thickness) {
		super(p,x, y, width, height);
		getAttributeWithName(PolygonObject.LINE_THICKNESS).setValue(thickness);
	}
	
	public OvalObject(MathObjectContainer p) {
		super(p);
	}

	public OvalObject() {}

	public void setThickness(int t) {
		getAttributeWithName(PolygonObject.LINE_THICKNESS).setValue(t);
	}

	public int getThickness() {
		return ((IntegerAttribute)getAttributeWithName(PolygonObject.LINE_THICKNESS)).getValue();
	}

	@Override
	public void addDefaultAttributes() {
		addAttribute(new IntegerAttribute(PolygonObject.LINE_THICKNESS, 1, 1, 20));
		addAttribute(new ColorAttribute(PolygonObject.FILL_COLOR));
	}
	
	public Color getColor(){
		return ((ColorAttribute)getAttributeWithName(PolygonObject.FILL_COLOR)).getValue();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return OVAL_OBJ;
	}

	@Override
	public MathObject newInstance() {
		return new OvalObject();
	}

}
