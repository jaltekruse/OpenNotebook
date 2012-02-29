/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
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
		// TODO Auto-generated method stub
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
