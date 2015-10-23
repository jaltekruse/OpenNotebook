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
package doc_gui.mathobject_gui;

import doc.mathobjects.MathObject;

import java.awt.*;

public class ScaledSizeAndPosition {
	private int xOrigin;
	private int yOrigin;
	private int width;
	private int height;
	private float fontSize;
	private float lineThickness;

	public ScaledSizeAndPosition populateValues(MathObject object, Point pageOrigin,
			float zoomLevel) {
		xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		width =  (int) (object.getWidth() * zoomLevel);
		height = (int) (object.getHeight() * zoomLevel);
		return this;
	}

	public ScaledSizeAndPosition populateValuesWithFontSize(MathObject object, Point pageOrigin,
																							float zoomLevel, int fontSize) {
		populateValues(object, pageOrigin, zoomLevel);
		this.fontSize = (int) (fontSize * zoomLevel);
		return this;
	}

	public ScaledSizeAndPosition populateValuesWithLineThickness(MathObject object, Point pageOrigin,
																													float zoomLevel, int lineThickness) {
		populateValues(object, pageOrigin, zoomLevel);
		this.lineThickness = (int) (lineThickness * zoomLevel);
		return this;
	}

	public ScaledSizeAndPosition populateAllValues(MathObject object, Point pageOrigin,
																								 float zoomLevel, int fontSize, int lineThickness) {
		// some of the work of these two methods is redundant, but it saves duplicating
		// any of the calculations here
		populateValuesWithFontSize(object, pageOrigin, zoomLevel, fontSize);
		populateValuesWithLineThickness(object, pageOrigin, zoomLevel, lineThickness);
		return this;
	}


	public float getFontSize() {
		return fontSize;
	}

	public float getLineThickness() {
		return lineThickness;
	}

	public int getxOrigin() {
		return xOrigin;
	}

	public int getyOrigin() {
		return yOrigin;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
