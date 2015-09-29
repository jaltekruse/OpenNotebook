/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import math_rendering.DivisionGraphic.Style;

import tree.BinExpression;
import tree.Fraction;
import tree.Operator;
import tree.Expression;

public class ExponentGraphic extends BinExpressionGraphic {

	public static enum Style{
		CARET, SUPERSCRIPT
	}
	
	private int spaceBetweenBaseAndSuper;
	private int extraShiftUp;
	private Style style;

	public ExponentGraphic(BinExpression b, CompleteExpressionGraphic gr) {
		super(b, gr);
		style = Style.SUPERSCRIPT;
		// TODO Auto-generated constructor stub
	}
	
	public void draw(){
		//no symbol to draw
		if (isSelected()){
			super.getCompExGraphic().getGraphics().setColor(getSelectedColor());
			super.getCompExGraphic().getGraphics().fillRect(getX1(), getY1(), getX2() - getX1(), getY2() - getY1());
			super.getCompExGraphic().getGraphics().setColor(Color.black);
		}
	}
	
	public void drawCursor(){
		String numberString = getValue().toString();
		
		int xPos = findCursorXPos();
		
		super.getCompExGraphic().getGraphics().setColor(Color.BLACK);
		super.getCompExGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1()+ 5);
		
	}
	
	public void setCursorPos(int xPixelPos){
		//cursor does not exist in this graphic, send to upper child
		super.getLeftGraphic().getMostInnerSouth().setCursorPos(xPixelPos);
	}
	
	public void moveCursorWest(){
		int cursorPos = super.getCompExGraphic().getCursor().getPos();
		if (cursorPos == 1) {
			getLeftGraphic().sendCursorInFromEast((getY2() - getY1())/2, this);
		}
		if (cursorPos == 2) {
			getRightGraphic().sendCursorInFromEast((getY2() - getY1())/2, this);
		}
		
	}
	
	public void moveCursorEast(){
		int cursorPos = super.getCompExGraphic().getCursor().getPos();
		if (cursorPos == 0) {
			getLeftGraphic().sendCursorInFromWest((getY2() - getY1())/2, this);
			return;
		}
		else if (cursorPos == 1) {
			getRightGraphic().sendCursorInFromWest((getY2() - getY1())/2, this);
			return;
		}
	}
	
	public void moveCursorNorth(){}
	
	private int findCursorXPos() {
		// TODO Auto-generated method stub
		return getX1() + super.getCompExGraphic().getCursor().getPos() * (getX2() - getX1()); 
	}

	public void moveCursorSouth(){}
	
	public void sendCursorInFromEast(int yPos, ValueGraphic vg)
	{
		if (getLeftGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getWest().sendCursorInFromEast(yPos, vg);
			return;
		}
		else if (getRightGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getCompExGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerWest());
			getCompExGraphic().getCursor().setPos(getLeftGraphic().getMaxCursorPos());
		}
		else
		{//the cursor was outside of this expression, moving in
			getCompExGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerWest());
			getCompExGraphic().getCursor().setPos(getLeftGraphic().getMaxCursorPos());
		}
	}
	
	public void sendCursorInFromWest(int yPos, ValueGraphic vg)
	{
		if (getLeftGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getCompExGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerWest());
			getCompExGraphic().getCursor().setPos(0);
		}
		else if (getRightGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the right child of this exponent
			System.out.println("send west from rightChild");
			getCompExGraphic().getCursor().setValueGraphic(getEast());
			getCompExGraphic().getCursor().setPos(0);
			return;
		}
		else
		{//the cursor was outside of this expression, moving in
			getLeftGraphic().sendCursorInFromWest(yPos, vg);
		}
	}
	
	public void sendCursorInFromNorth(int xPos, ValueGraphic vg){
		if (getRightGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getCompExGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerEast());
			getCompExGraphic().getCursor().setPos(getLeftGraphic().getMostInnerEast().getMaxCursorPos());
		}
		else if (getLeftGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			if (getSouth() == null)
			{
				System.out.println("nothing to north");
				return;
			}
			else
			{
				getSouth().getMostInnerNorth().sendCursorInFromSouth(findCursorXPos(), this);
				return;
			}
		}
		else
		{//the cursor was outside of this expression, moving in
			getRightGraphic().setCursorPos(xPos);
		}
	}
	
	public void sendCursorInFromSouth(int xPos, ValueGraphic vg){
		if (getLeftGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getCompExGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerWest());
			getCompExGraphic().getCursor().setPos(0);
		}
		if (getRightGraphic().containedBelow(getCompExGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getCompExGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			if (getNorth() == null)
			{
				System.out.println("nothing to north");
				return;
			}
			else
			{
				getNorth().getMostInnerSouth().sendCursorInFromSouth(findCursorXPos(), this);
				return;
			}
		}
		else
		{//the cursor was outside of this expression, moving in
			getLeftGraphic().setCursorPos(xPos);
		}
	}
	
	/**
	 * The cursor can either be before or after the bar.
	 */
	public int getMaxCursorPos(){
		return 1;
	}

	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {
		// TODO Auto-generated method stub

		g.setFont(f);
		
		spaceBetweenBaseAndSuper = (int) (4 * getCompExGraphic().DOC_ZOOM_LEVEL);
		extraShiftUp = (int) (2 * getCompExGraphic().DOC_ZOOM_LEVEL);
		
		
		((BinExpression)super.getValue()).getOp().getSymbol();
		Expression tempLeft = ((BinExpression)super.getValue()).getLeftChild();
		Expression tempRight = ((BinExpression)super.getValue()).getRightChild();
		ValueGraphic leftValGraphic = null;
		ValueGraphic rightValGraphic = null; 
		int[] rightSize = {0,0};
		int[] leftSize = {0, 0};
		int[] totalSize = {0, 0};
		
		if (false){
//		if (style == Style.CARET)
//		{
//			BinExpressionGraphic ex = new BinExpressionGraphic(((BinExpression)getValue()), super.getCompExGraphic());
//			return ex.requestSize(g, f, x1, y1);
		}
		else if (style == Style.SUPERSCRIPT){

			leftValGraphic = makeValueGraphic(tempLeft);
			
			leftSize = leftValGraphic.requestSize(g, f, x1, y1);
			super.getCompExGraphic().getComponents().add(leftValGraphic);
			
			rightValGraphic = makeValueGraphic(tempRight);
			
			rightSize = rightValGraphic.requestSize(g, getCompExGraphic().getSmallFont(), x1, y1);
			super.getCompExGraphic().getComponents().add(rightValGraphic);
			
			//set the west and east fields for inside and outside of the expression
			setMostInnerEast(this);
			leftValGraphic.getMostInnerEast().setEast(this);
			setMostInnerWest(this);
			setMostInnerSouth(this);
			
			//the request to move to the east of the exponent will need to go down first
			//before being sent into the element east of this ExponentGraphic
			//usual moveEast: 45|+67, cursor at end of 45 -> 45+|67 cursor at end of +
			//skips the 0 position of the next element
			//In this case, the cursor clearly moves down from the superscript
			//moveEast: 4^5|+67, cursor at end of 5 in superscript -> 4^5|+/67 cursor at start of +
			
			rightValGraphic.getMostInnerWest().setWest(this);
			leftValGraphic.setNorth(this);
			rightValGraphic.setSouth(this);
			rightValGraphic.setWest(this);
			rightValGraphic.setEast(this);
			leftValGraphic.setWest(this);
			leftValGraphic.setEast(this);
			setMostInnerNorth(this);
			
//			//set the west and east fields for inside and outside of the expression
//			setMostInnerWest(leftValGraphic.getMostInnerWest());
//			leftValGraphic.getMostInnerEast().setEast(rightValGraphic.getMostInnerWest());
//			leftValGraphic.getMostInnerNorth().setNorth(rightValGraphic.getMostInnerSouth());
//			this.setWest(leftValGraphic.getMostInnerEast());
//			this.setSouth(leftValGraphic.getMostInnerNorth());
//			this.setMostInnerWest(leftValGraphic.getMostInnerWest());
//			this.setMostInnerSouth(leftValGraphic.getMostInnerSouth());
//			
//			setMostInnerNorth(rightValGraphic.getMostInnerNorth());
//			setMostInnerSouth(leftValGraphic.getMostInnerSouth());
//			
//			setMostInnerEast(rightValGraphic.getMostInnerEast());
//			rightValGraphic.getMostInnerSouth().setSouth(leftValGraphic.getMostInnerEast());
//			rightValGraphic.getMostInnerWest().setWest(leftValGraphic.getMostInnerEast());
//			this.setNorth(rightValGraphic.getMostInnerSouth());
//			this.setEast(rightValGraphic.getMostInnerWest());
//			this.setMostInnerEast(rightValGraphic.getMostInnerEast());
//			this.setMostInnerNorth(rightValGraphic.getMostInnerNorth());
			
			setLeftGraphic(leftValGraphic);
			setRightGraphic(rightValGraphic);
			
			System.out.println("exponent children:" + getComponents());
			System.out.println(this);
			
			rightValGraphic.shiftToX1(x1 + leftSize[0] + spaceBetweenBaseAndSuper);
			
			int shiftDownLeft = 0;
			shiftDownLeft = rightValGraphic.getUpperHeight() + extraShiftUp;
			if (leftValGraphic instanceof ExponentGraphic)
			{
				if ( ((ExponentGraphic) leftValGraphic).getRightGraphic().getHeight() / 2.0 <
						rightValGraphic.getLowerHeight() )
				{
					shiftDownLeft = rightValGraphic.getHeight() + extraShiftUp - (int) 
							Math.round(((ExponentGraphic)leftValGraphic).getRightGraphic().getHeight()/2.0);
				}
			}
			else
			{
				if ( leftValGraphic.getHeight() / 2.0 < rightValGraphic.getLowerHeight() )
				{
					shiftDownLeft = rightValGraphic.getHeight() + extraShiftUp - (int) 
							Math.round(leftValGraphic.getHeight()/2.0);
				}
			}
			leftValGraphic.shiftToY1(y1 + shiftDownLeft);
			
			setUpperHeight( shiftDownLeft + leftValGraphic.getUpperHeight() );
			setLowerHeight( leftValGraphic.getLowerHeight() );
			totalSize[0] = leftSize[0] + rightSize[0] + spaceBetweenBaseAndSuper;
			totalSize[1] = leftSize[1] + shiftDownLeft;
			super.setX1(x1);
			super.setY1(y1);
			super.setX2(x1 + totalSize[0]);
			super.setY2(y1 + totalSize[1]);
			return totalSize;
		}
		
		return null;
	}
}
