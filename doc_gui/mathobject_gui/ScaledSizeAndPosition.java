/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately.
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
