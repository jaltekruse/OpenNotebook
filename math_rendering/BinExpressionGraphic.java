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
import java.awt.RenderingHints;
import java.util.Vector;

import tree.BinExpression;
import tree.Operator;
import tree.Expression;
import tree.Fraction;

public class BinExpressionGraphic extends ExpressionGraphic{

	private int space;
	private ValueGraphic leftChild, rightChild;
	
	public BinExpressionGraphic(BinExpression b, CompleteExpressionGraphic gr) {
		super(b, gr);
	}

	@Override
	public void draw() {
		
		if (isSelected()){
			super.getCompExGraphic().getGraphics().setColor(getSelectedColor());
			super.getCompExGraphic().getGraphics().fillRect(symbolX1, symbolY1 - 2,
					symbolX2 - symbolX1, symbolY2 - symbolY1 + 4);
			super.getCompExGraphic().getGraphics().setColor(Color.black);
		}
		getCompExGraphic().getGraphics().setFont(getFont());
		getCompExGraphic().getGraphics().drawString(getValue().getOp().getSymbol(),
				symbolX1 + space, symbolY2);
	}
	
	private int findCursorXPos() {
		String opString = getValue().getOp().getSymbol();
		return  symbolX1 + super.getCompExGraphic().getGraphics().getFontMetrics().stringWidth(
				opString.substring(0, super.getCompExGraphic().getCursor().getPos()));
	}
	
	public void drawCursor(){
		String opString = getValue().getOp().getSymbol();
		
		int xPos = findCursorXPos();
		
		if ( super.getCompExGraphic().getCursor().getPos() == getMaxCursorPos()){
			xPos += 2 * space;
		}
		super.getCompExGraphic().getGraphics().setColor(Color.BLACK);
		super.getCompExGraphic().getGraphics().fillRect(xPos, super.symbolY1 - 3, 2, super.symbolY2 - super.symbolY1 + 5);
		
	}
	
	public int getMaxCursorPos(){
		return getValue().getOp().getSymbol().length();
	}
	
	public void setCursorPos(int xPixelPos){
		
		String numberString = getValue().getOp().getSymbol();
		
		if (xPixelPos < super.symbolX1){
			getLeftGraphic().setCursorPos(xPixelPos);
			return;
		}
			
		else if (xPixelPos > super.symbolX2){
			getRightGraphic().setCursorPos(xPixelPos);
			return;
		}
		
		int startX, endX, xWidth;
		
		startX = super.getCompExGraphic().getGraphics().getFontMetrics().stringWidth(
				numberString.substring(0, 0)) + symbolX1 - space;
		endX = super.getCompExGraphic().getGraphics().getFontMetrics().stringWidth(
				numberString.substring(0, 1)) + symbolX1 + space;
		xWidth = endX - startX;
		
		if (startX < xPixelPos && endX > xPixelPos)
		{//if the x position is inside of a character, check if it is on the first or second
			//half of the character and set the cursor accordingly
			if (endX - xPixelPos > xWidth/2){
				super.getCompExGraphic().getCursor().setPos( 0 );
			}
			else{
				super.getCompExGraphic().getCursor().setPos( 1 );
			}
			super.getCompExGraphic().getCursor().setValueGraphic(this);
			return;
		}
	}
	
	public void moveCursorWest(){
		if (super.getCompExGraphic().getCursor().getPos() > 0){
			super.getCompExGraphic().getCursor().setPos( super.getCompExGraphic().getCursor().getPos() - 1); 
		}
		else{
			if (getWest() == null)
			{
				return;
			}
			else
			{
				getWest().sendCursorInFromEast((getY2() - getY1())/2, this);
				return;
			}
		}
	}
	
	public void moveCursorEast(){
		if (super.getCompExGraphic().getCursor().getPos() < getMaxCursorPos()){
			super.getCompExGraphic().getCursor().setPos( super.getCompExGraphic().getCursor().getPos() + 1); 
		}
		else{
			if (getEast() == null)
			{
				return;
			}
			else
			{
				getEast().sendCursorInFromWest((getY2() - getY1())/2, this);
				return;
			}
		}
	}
	
	public void moveCursorNorth(){
		if (getNorth() == null)
		{
			return;
		}
		else
		{
			getNorth().sendCursorInFromSouth(findCursorXPos(), this);
			return;
		}
	}
	
	public void moveCursorSouth(){
		if (getSouth() == null)
		{
			return;
		}
		else
		{
			getSouth().sendCursorInFromNorth(findCursorXPos(), this);
			return;
		}
	}
	
	public void sendCursorInFromEast(int yPos, ValueGraphic vg){
		super.getCompExGraphic().getCursor().setValueGraphic(this);
		super.getCompExGraphic().getCursor().setPos(getMaxCursorPos() - 1);
	}
	
	public void sendCursorInFromWest(int yPos, ValueGraphic vg){
		super.getCompExGraphic().getCursor().setValueGraphic(this);
		super.getCompExGraphic().getCursor().setPos(1);
	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {
		// TODO Auto-generated method stub
		
		g.setFont(f);
		setFont(f);
		
		space = (int) (4 * getCompExGraphic().DOC_ZOOM_LEVEL);
		
		Expression tempLeft = ((BinExpression)super.getValue()).getLeftChild();
		Expression tempRight = ((BinExpression)super.getValue()).getRightChild();
		ValueGraphic leftValGraphic = null;
		ValueGraphic rightValGraphic = null; 
		int[] rightSize = {0,0};
		int[] leftSize = {0, 0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		leftValGraphic = makeValueGraphic(tempLeft);
		
		super.getCompExGraphic().getComponents().add(leftValGraphic);
		leftSize = leftValGraphic.requestSize(g, f, x1, y1);
		
		rightValGraphic = makeValueGraphic(tempRight);
		
		rightSize = rightValGraphic.requestSize(g, f, x1, y1);
		super.getCompExGraphic().getComponents().add(rightValGraphic);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(leftValGraphic.getMostInnerWest());
		leftValGraphic.getMostInnerEast().setEast(this);
		this.setWest(leftValGraphic.getMostInnerEast());
		setMostInnerNorth(this);
		setMostInnerSouth(this);
		setMostInnerEast(rightValGraphic.getMostInnerEast());
		rightValGraphic.getMostInnerWest().setWest(this);
		this.setEast(rightValGraphic.getMostInnerWest());
		
		symbolSize[0] = getCompExGraphic().getStringWidth(getValue().getOp().getSymbol(), f) + 2 * space;
		symbolSize[1] = getCompExGraphic().getFontHeight(f);
		rightValGraphic.shiftToX1(leftSize[0] + symbolSize[0] + x1);
		
		int height = 0;
		
		if (leftValGraphic.getUpperHeight() > rightValGraphic.getUpperHeight()){
			height = leftValGraphic.getUpperHeight();
			symbolY1 = leftValGraphic.getUpperHeight() + y1 - (int) (Math.round((symbolSize[1]/2.0)));
			symbolY2 = symbolY1 + symbolSize[1];
			setUpperHeight(leftValGraphic.getUpperHeight());
			rightValGraphic.shiftToY1(y1 - rightValGraphic.getUpperHeight() + leftValGraphic.getUpperHeight());
		}
		else
		{
			height = rightValGraphic.getUpperHeight();
			symbolY1 = rightValGraphic.getUpperHeight() + y1 - (int) (Math.round((symbolSize[1]/2.0)));
			symbolY2 = symbolY1 + symbolSize[1];
			setUpperHeight(rightValGraphic.getUpperHeight());
			leftValGraphic.shiftToY1(y1 - leftValGraphic.getUpperHeight() + rightValGraphic.getUpperHeight());
		}
		if (leftValGraphic.getLowerHeight() > rightValGraphic.getLowerHeight()){
			height += leftValGraphic.getLowerHeight();
			setLowerHeight(leftValGraphic.getLowerHeight());
		}
		else
		{
			height += rightValGraphic.getLowerHeight();
			setLowerHeight(rightValGraphic.getLowerHeight());
		}
		
		symbolX1 = x1 + leftSize[0];
		symbolX2 = x1 + leftSize[0] + symbolSize[0];
		
		setLeftGraphic(leftValGraphic);
		setRightGraphic(rightValGraphic);
		
		totalSize[0] = symbolX2 + rightSize[0] - x1;
		totalSize[1] = height;
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + totalSize[0]);
		super.setY2(y1 + totalSize[1]);
		return totalSize;
	}

	@Override
	public int[] requestSize(Graphics g, Font f) {
		return null;
	}
	
	protected void setLeftGraphic(ValueGraphic vg){
		leftChild = vg;
	}
	
	public ValueGraphic getLeftGraphic(){
		return leftChild;
	}

	protected void setRightGraphic(ValueGraphic vg){
		rightChild = vg;
	}
	
	public ValueGraphic getRightGraphic(){
		return rightChild;
	}
	
	@Override
	public Vector<ValueGraphic> getComponents() {
		// TODO Auto-generated method stub
		Vector<ValueGraphic> children = new Vector<ValueGraphic>();
		children.add(leftChild);
		children.add(rightChild);
		return children;
	}
}
