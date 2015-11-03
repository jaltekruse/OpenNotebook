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

/**
 * A basic MathObject that represents a rectangular space on the screen.
 * 
 * @author jason altekruse
 *
 */
public class RectangleObject extends PolygonObject {
	
	private static final GridPoint[] vertices = {new GridPoint(0, 0), new GridPoint(1, 0),
		new GridPoint(1, 1), new GridPoint(0, 1)};
	
	public RectangleObject(MathObjectContainer p, int x, int y, int w, int h, int t) {
		super(p, x, y, w, h, t);
	}
	
	public RectangleObject(MathObjectContainer p){
		super(p);
	}

	public RectangleObject() {}
	
	@Override
	public void addDefaultAttributes() {}

	@Override
	public String getType() {
		return RECTANGLE;
	}

	@Override
	public GridPoint[] getVertices() {
		return vertices;
	}

	@Override
	public MathObject newInstance() {
		return new RectangleObject();
	}
}
