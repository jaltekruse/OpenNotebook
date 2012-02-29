/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.mathobjects;

import doc.attributes.DoubleAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;


public class NumberLineObject extends MathObject {
	
	public static final String MIN = "min", MAX = "max", STEP = "step", FONT_SIZE = "font size";
	public NumberLineObject(MathObjectContainer p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
		setVerticallyResizable(false);

	}
	
	public NumberLineObject(MathObjectContainer p){
		super(p);
		setVerticallyResizable(false);
	}

	public NumberLineObject() {
		setVerticallyResizable(false);
	}

	@Override
	public void addDefaultAttributes() {
		// TODO Auto-generated method stub
		addAttribute(new DoubleAttribute(MIN, -10000, 10000));
		addAttribute(new DoubleAttribute(MAX, -10000, 10000));
		addAttribute(new DoubleAttribute(STEP, 0, 5000));
		addAttribute(new IntegerAttribute(FONT_SIZE, 1, 20));
		getAttributeWithName(MIN).setValue(-5.0);
		getAttributeWithName(MAX).setValue(5.0);
		getAttributeWithName(STEP).setValue(1.0);
		getAttributeWithName(FONT_SIZE).setValue(8);
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return NUMBER_LINE;
	}

	@Override
	public MathObject newInstance() {
		return new NumberLineObject();
	}
}
