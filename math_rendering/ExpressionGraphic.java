/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

import java.awt.Font;
import java.awt.Graphics;

import tree.Expression;
import tree.Expression;

public abstract class ExpressionGraphic extends ValueGraphic<Expression> {

	protected int symbolX1, symbolX2, symbolY1, symbolY2;
	
	public ExpressionGraphic(Expression v, CompleteExpressionGraphic compExGraphic) {
		super(v, compExGraphic);
		if ( v.getCursorPos() >= 0) {
			this.getCompExGraphic().getCursor().setValueGraphic(this);
			this.getCompExGraphic().getCursor().setPos(v.getCursorPos());
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub

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
		return null;
	}

	void shiftToX1(int x1) {
		int xChange = x1 - getX1();
		for (ValueGraphic vg : getComponents()){
			vg.shiftToX1(vg.getX1() + xChange);
		}
		setX2(getX2() + xChange);
		symbolX1 += xChange;
		symbolX2 += xChange;
		setX1(x1);
	}

	void shiftToY1(int y1) {
		int yChange = y1 - getY1();
		for (ValueGraphic vg : getComponents()){
			vg.shiftToY1(vg.getY1() + yChange);
		}
		setY2(getY2() + yChange);
		symbolY1 += yChange;
		symbolY2 += yChange;
		setY1(y1);
	}
}
