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
