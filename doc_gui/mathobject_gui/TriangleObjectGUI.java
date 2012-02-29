/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.mathobject_gui;

import java.awt.Graphics;
import java.awt.Point;

import doc.mathobjects.TriangleObject;

public class TriangleObjectGUI extends MathObjectGUI<TriangleObject> {

	@Override
	public void drawMathObject(TriangleObject object, Graphics g,
			Point pageOrigin, float zoomLevel) {
		// TODO Auto-generated method stub
		
	}
	
	// add custom drawing method if generic PolygonObjectGUI
	// does not provide enough functionality, need to also modify PageGUI
	// for the new method to be used
}
