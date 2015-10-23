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
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class TrapezoidObject extends PolygonObject {

	private static final GridPoint[] vertices = {new GridPoint(.25, 0),
		new GridPoint(.75, 0), new GridPoint(1, 1),new GridPoint(0, 1)};
	
	public TrapezoidObject(MathObjectContainer p, int x, int y, int w, int h, int t) {
		super(p, x, y, w, h, t);
		addAction(PolygonObject.FLIP_VERTICALLY);
	}
	
	public TrapezoidObject(MathObjectContainer p){
		super(p);
		addAction(PolygonObject.FLIP_VERTICALLY);
	}

	public TrapezoidObject() {
		addAction(PolygonObject.FLIP_VERTICALLY);
	}
	
	@Override
	public void addDefaultAttributes() {
		
	}
	
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return TRAPEZOID_OBJ;
	}

	@Override
	public GridPoint[] getVertices() {
		// TODO Auto-generated method stub
		return vertices;
	}

	@Override
	public MathObject newInstance() {
		return new TrapezoidObject();
	}

}
