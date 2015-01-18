/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

public class ExpressionGraphicCursor {
	
	private ValueGraphic currValue;
	private int positionWithinValue;
	
	public ExpressionGraphicCursor(ValueGraphic vg, int pos){
		currValue = vg;
		positionWithinValue = pos;
	}
	
	public void setCurrValue(ValueGraphic currValue) {
		this.currValue = currValue;
	}
	public ValueGraphic getCurrValue() {
		return currValue;
	}

	public void setPositionWithinValue(int positionWithinValue) {
		this.positionWithinValue = positionWithinValue;
	}

	public int getPositionWithinValue() {
		return positionWithinValue;
	}

}
