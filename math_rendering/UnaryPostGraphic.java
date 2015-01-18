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
import java.awt.Graphics;

import tree.UnaryExpression;
import tree.Expression;

public class UnaryPostGraphic extends UnaryExpressionGraphic {

private int space;
	
	public UnaryPostGraphic(UnaryExpression v,
			CompleteExpressionGraphic compExGraphic) {
		super(v, compExGraphic);
		setMostInnerNorth(this);
		setMostInnerSouth(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		if (isSelected()){
			getCompExGraphic().getGraphics().setColor(getSelectedColor());
			getCompExGraphic().getGraphics().fillRect(symbolX1, symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
			getCompExGraphic().getGraphics().setColor(Color.black);
		}
		getCompExGraphic().getGraphics().setFont(getFont());
		getCompExGraphic().getGraphics().drawString(getValue().getOp().getSymbol(),
				symbolX1, symbolY2);
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
		g.setFont(f);
		setFont(f);
		
		space = (int) (2 * super.getCompExGraphic().DOC_ZOOM_LEVEL);
		
		Expression tempChild = ((UnaryExpression)super.getValue()).getChild();
		ValueGraphic childValGraphic = null;
		int[] childSize = {0,0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		childValGraphic = makeValueGraphic(tempChild);
		
		setChildGraphic(childValGraphic);
		super.getCompExGraphic().getComponents().add(childValGraphic);
		
		childSize = childValGraphic.requestSize(g, f, x1 + symbolSize[0], y1);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(childValGraphic);
		setWest(childValGraphic.getMostInnerEast());
		childValGraphic.getMostInnerEast().setEast(this);
		setMostInnerEast(this);
		
		symbolSize[0] = super.getCompExGraphic().getStringWidth(value.getOp().getSymbol(), f);
		symbolSize[1] = super.getCompExGraphic().getFontHeight(f);
		
		symbolY1 = y1 + childValGraphic.getUpperHeight() - (int) Math.round(symbolSize[1]/2.0);
		symbolY2 = symbolY1 + symbolSize[1];
		symbolX1 = x1 + space + childSize[0];
		symbolX2 = x1 + symbolSize[0];
		
		setUpperHeight(childValGraphic.getUpperHeight());
		setLowerHeight(childValGraphic.getLowerHeight());
		
		totalSize[0] = symbolSize[0] + childSize[0];
		totalSize[1] = childSize[1];
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + totalSize[0]);
		super.setY2(y1 + totalSize[1]);
		// TODO Auto-generated method stub
		return totalSize;
	}

	public void drawCursor(){
		String opString = getValue().getOp().getSymbol();
		
		int xPos = symbolX1 + super.getCompExGraphic().getGraphics().getFontMetrics().stringWidth(
				opString.substring(0, super.getCompExGraphic().getCursor().getPos()));
		
		if ( super.getCompExGraphic().getCursor().getPos() == getMaxCursorPos()){
			xPos += space;
		}
		super.getCompExGraphic().getGraphics().setColor(Color.BLACK);
		super.getCompExGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1() + 5);
		
	}
	
	public int getMaxCursorPos(){
		return getValue().getOp().getSymbol().length();
	}
	
	public void setCursorPos(int xPixelPos){
		
		String numberString = getValue().getOp().getSymbol();
		
		if (xPixelPos < super.symbolX1){
			getChildGraphic().setCursorPos(xPixelPos);
			return;
		}
			
		else if (xPixelPos > super.symbolX2){
			super.getCompExGraphic().getCursor().setPos(numberString.length());
			super.getCompExGraphic().getCursor().setValueGraphic(this);
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
	
	public void sendCursorInFromEast(int yPos, ValueGraphic vg){
		super.getCompExGraphic().getCursor().setValueGraphic(this);
		super.getCompExGraphic().getCursor().setPos(getMaxCursorPos() - 1);
	}
	
	public void sendCursorInFromWest(int yPos, ValueGraphic vg){
		super.getCompExGraphic().getCursor().setValueGraphic(this);
		super.getCompExGraphic().getCursor().setPos(1);
	}
	
	public void sendCursorInFromNorth(int xPos, ValueGraphic vg){
		setCursorPos(xPos);
	}
	
	public void sendCursorInFromSouth(int xPos, ValueGraphic vg){
		setCursorPos(xPos);
	}
	
}
