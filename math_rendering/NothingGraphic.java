/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;

import expression.EmptyValue;

public class NothingGraphic extends NodeGraphic<EmptyValue> {

	public NothingGraphic(EmptyValue v, RootNodeGraphic compExGraphic) {
		super(v, compExGraphic);
		setMostInnerWest(this);
		setMostInnerEast(this);
		setMostInnerNorth(this);
		setMostInnerSouth(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		getRootNodeGraphic().getGraphics().setFont(getFont());
		super.getRootNodeGraphic().getGraphics().setStroke(new BasicStroke(
				(int) (1 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL)));
		getRootNodeGraphic().getGraphics().drawRect(getX1(), getY1(), getX2() - getX1(), getY2() - getY1());
		super.getRootNodeGraphic().getGraphics().setStroke(new BasicStroke());
	}
	
	public void drawCursor(int pos){
		
	}

	@Override
	public int[] requestSize(Graphics g, Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1)
			throws Exception {
		// TODO Auto-generated method stub
		g.setFont(f);
		setFont(f);
		
		
		//random character that happens to be the size needed
		String s = "8";
		
		int[] size = new int[2];
		size[0] = getRootNodeGraphic().getStringWidth(s, f);
		size[1] = getRootNodeGraphic().getFontHeight(f);
		setUpperHeight((int) Math.round(size[1]/2.0));
		setLowerHeight(getUpperHeight());
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + size[0]);
		super.setY2(y1 + size[1]);
		return size;
	}

	@Override
	public Vector getComponents() {
		// TODO Auto-generated method stub
		return new Vector<NodeGraphic>();
	}

}
