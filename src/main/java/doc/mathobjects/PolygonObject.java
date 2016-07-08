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

import doc.GridPoint;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.IntegerAttribute;

public abstract class PolygonObject extends MathObject {

	public PolygonObject(MathObjectContainer p, int x, int y, int w, int h, int t) {
		super(p, x, y, w, h);
		addPolygonAttributes();
		getAttributeWithName(LINE_THICKNESS).setValue(t);
	}

	public PolygonObject(MathObjectContainer p){
		super(p);
		addPolygonAttributes();
	}

	public PolygonObject(){
		addPolygonAttributes();
	}
	
	private void addPolygonAttributes(){
		addAttribute(new IntegerAttribute(LINE_THICKNESS, 1, 1, 20));
		addAttribute(new ColorAttribute(FILL_COLOR));
		addAttribute(new BooleanAttribute(HORIZONTALLY_FLIPPED, false, false));
		getAttributeWithName(HORIZONTALLY_FLIPPED).setValue(false);
		addAttribute(new BooleanAttribute(VERTICALLY_FLIPPED, false, false));
		getAttributeWithName(VERTICALLY_FLIPPED).setValue(false);
	}

	public Color getColor(){
		return ((ColorAttribute)getAttributeWithName(FILL_COLOR)).getValue();
	}

	@Override
	public void performSpecialObjectAction(String s){}
	
	public GridPoint[] getAdjustedVertices(){
		GridPoint[] points = getVertices();
		if ( isFlippedHorizontally() ){
			points = flipHorizontally(points);
		}
		if ( isFlippedVertically()){
			points = flipVertically(points);
		}
		return points;
	}

	protected abstract GridPoint[] getVertices();

	public int countVertices(){
		return getVertices().length;	
	}

	public void setThickness(int t) {
		getAttributeWithName(LINE_THICKNESS).setValue(t);
	}

	public int getThickness() {
		return ((IntegerAttribute)getAttributeWithName(LINE_THICKNESS)).getValue();
	}

}
