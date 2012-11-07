/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.mathobjects;

import doc.attributes.AttributeException;
import doc.attributes.BooleanAttribute;
import doc.attributes.EnumeratedAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;

public class TextObject extends MathObject {
	
	public static final String TEXT = "text",  FONT_SIZE = "font size", SHOW_BOX = "show border";
	
	public static final String LEFT = "Left", RIGHT = "Right", CENTER = "Center", ALIGNMENT = "alignment";
	
	public static final String[] alignments = {LEFT, RIGHT, CENTER};
	
	public TextObject(MathObjectContainer p, int x, int y, int width, int height, int fontSize, String s){
		super(p, x, y, width, height);
		getAttributeWithName(FONT_SIZE).setValue(fontSize);
		getAttributeWithName(TEXT).setValue(s);
		getAttributeWithName(SHOW_BOX).setValue(false);
	}
	
	public TextObject(MathObjectContainer p) {
		super(p);
	}

	public TextObject() {}

	@Override
	public void addDefaultAttributes() {
		addAttribute(new StringAttribute(TEXT, ""));
		addAttribute(new IntegerAttribute(FONT_SIZE, 12, 1, 50));
		addAttribute(new BooleanAttribute(SHOW_BOX, false));
		addAttribute(new EnumeratedAttribute(ALIGNMENT, LEFT, alignments));
		
		addAction(MathObject.MAKE_INTO_PROBLEM);
	}

	public void setFontSize(int fontSize) throws AttributeException {
		setAttributeValue(FONT_SIZE, fontSize);
	}
	
	public String getAlignment(){
		return (String) getAttributeWithName(ALIGNMENT).getValue();
	}

	public int getFontSize() {
		return ((IntegerAttribute)getAttributeWithName(FONT_SIZE)).getValue();
	}

	@Override
	public String getType() {
		return TEXT_OBJ;
	}
	
	public String getText(){
		return ((StringAttribute)getAttributeWithName(TEXT)).getValue();
	}
	
	public void setText(String s) throws AttributeException{
		setAttributeValue(TEXT, s);
	}

	@Override
	public MathObject newInstance() {
		return new TextObject();
	}

}
