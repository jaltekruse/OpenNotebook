/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
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
