/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.mathobject_gui;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import doc.attributes.DoubleAttribute;
import doc.mathobjects.NumberLineObject;
import doc_gui.graph.NumberLine;

public class NumberLineObjectGUI extends MathObjectGUI<NumberLineObject> {

	private NumberLine numLine;
	
	public NumberLineObjectGUI(){
		numLine = new NumberLine();
	}
	
	public void drawMathObject(NumberLineObject object, Graphics g, Point pageOrigin,
			float zoomLevel) {

		g.setColor(Color.BLACK);
		ScaledSizeAndPosition sap = getSizeAndPosition(object, pageOrigin,
				zoomLevel);

		numLine.repaint(g, sap.getWidth(), sap.getHeight(), zoomLevel, sap.getxOrigin(), sap.getyOrigin(), object);
		
	}
}
