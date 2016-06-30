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
		return OVAL_OBJ;
	}

	@Override
	public MathObject newInstance() {
		return new OvalObject();
	}

}
