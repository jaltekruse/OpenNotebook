/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import tree.UnaryExpression;
import tree.Expression;

public class ParenGraphic extends UnaryExpressionGraphic {

	private int space;
	private int overhang;
	private int widthParens;
	
	public ParenGraphic(UnaryExpression v, CompleteExpressionGraphic compExGraphic) {
		super(v, compExGraphic);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		if (isSelected()){
			super.getCompExGraphic().getGraphics().setColor(getSelectedColor());
			super.getCompExGraphic().getGraphics().fillRect(symbolX1, symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
			super.getCompExGraphic().getGraphics().setColor(Color.black);
		}
		
		super.getCompExGraphic().getGraphics().setStroke(new BasicStroke(
				(int) (1 * super.getCompExGraphic().DOC_ZOOM_LEVEL)));
		super.getCompExGraphic().getGraphics().drawArc(getX1(), getY1(),
				widthParens * 2, getY2() - getY1(), 90, 180);
		super.getCompExGraphic().getGraphics().drawArc(getX2() - widthParens * 2,
				getY1(), widthParens * 2, getY2() - getY1(), 270, 180);
		super.getCompExGraphic().getGraphics().setStroke(new BasicStroke());
	}

	public void drawCursor(){
		int cursorPos = super.getCompExGraphic().getCursor().getPos();
		
		super.getCompExGraphic().getGraphics().setColor(Color.BLACK);
		super.getCompExGraphic().getGraphics().fillRect(findCursorXPos(), getY1() - 3, 2,
				getY2() - getY1()+ 5);
		return;
		
	}
	
	public int findCursorXPos(){
		super.getCompExGraphic().getGraphics().setFont(getFont());
		int cursorPos = super.getCompExGraphic().getCursor().getPos();
		
		if (cursorPos == 0){
			return getX1();
		}
		else if (cursorPos == 1){
			return getX1() + widthParens + space/2;
		}
		else if (cursorPos == 2){
			return getX2() - widthParens - space/2;
		}
		else if (cursorPos == 3){
			return getX2();
		}
		
		//should replace this with exception thrown
		return Integer.MAX_VALUE;
			
	}
	
	public int getMaxCursorPos(){
		return 3;
	}
	
	public void setCursorPos(int xPixelPos){
		
		String valueString = getValue().getOp().getSymbol();

		if (xPixelPos < getX1() + widthParens + space){
			if (xPixelPos < getX1() + widthParens/2){
				super.getCompExGraphic().getCursor().setPos(0);
				super.getCompExGraphic().getCursor().setValueGraphic(this);
				return;
			}
			else{
				super.getCompExGraphic().getCursor().setPos(1);
				super.getCompExGraphic().getCursor().setValueGraphic(this);
				return;
			}
		}
		else{
			if (xPixelPos > getX2() - widthParens - space){
				if (xPixelPos < getX2() - widthParens/2){
					super.getCompExGraphic().getCursor().setPos(2);
					super.getCompExGraphic().getCursor().setValueGraphic(this);
					return;
				}
				else{
					super.getCompExGraphic().getCursor().setPos(3);
					super.getCompExGraphic().getCursor().setValueGraphic(this);
					return;
				}
			}
			else{
				getChildGraphic().setCursorPos(xPixelPos);
				return;
			}
		}
	}
	
	public void moveCursorWest(){
		int cursorPos = getCompExGraphic().getCursor().getPos();
		if (cursorPos == 3 || cursorPos == 1){
			super.getCompExGraphic().getCursor().setPos( cursorPos - 1);
			return;
		}
		else if (cursorPos == 0){
			if (getWest() != null){
				getWest().sendCursorInFromEast((getY2() - getY1())/2, this);
				return;
			}
		}
		else{
			getChildGraphic().getMostInnerEast().sendCursorInFromEast((getY2() - getY1())/2, this);
			return;
		}
	}
	
	public void moveCursorEast(){
		int cursorPos = getCompExGraphic().getCursor().getPos();
		if (cursorPos == 2 || cursorPos == 0){
			super.getCompExGraphic().getCursor().setPos( cursorPos + 1); 
		}
		else if (cursorPos == 3){
			if (getEast() != null){
				getEast().sendCursorInFromWest((getY2() - getY1())/2, this);
				return;
			}
		}
		else{
			getChildGraphic().getMostInnerWest().sendCursorInFromWest((getY2() - getY1())/2, this);
			return;
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
		if (super.containedBelow(vg)){
			super.getCompExGraphic().getCursor().setValueGraphic(this);
			super.getCompExGraphic().getCursor().setPos(0);
		}
		else{
			super.getCompExGraphic().getCursor().setValueGraphic(getChildGraphic().getMostInnerEast());
			super.getCompExGraphic().getCursor().setPos(getChildGraphic().getMostInnerEast().getMaxCursorPos());
		}
	}
	
	public void sendCursorInFromWest(int yPos, ValueGraphic vg){
		if (super.containedBelow(vg)){
			super.getCompExGraphic().getCursor().setValueGraphic(this);
			super.getCompExGraphic().getCursor().setPos(getMaxCursorPos());
		}
		else{
			super.getCompExGraphic().getCursor().setValueGraphic(getChildGraphic().getMostInnerWest());
			super.getCompExGraphic().getCursor().setPos(0);
		}
	}
	
	public void sendCursorInFromNorth(int xPos, ValueGraphic vg){
		setCursorPos(xPos);
	}
	
	public void sendCursorInFromSouth(int xPos, ValueGraphic vg){
		setCursorPos(xPos);
	}
	
	@Override
	public int[] requestSize(Graphics g, Font f) {
		return null;
	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1)
			throws Exception {
		//g is used to decide the size of the text to display for this element
		g.setFont(f);
		
		//to draw this element later, the font must be the same, so its stored in this object
		setFont(f);
		
		space = (int) (2 * super.getCompExGraphic().DOC_ZOOM_LEVEL);
		overhang = (int) (3 * super.getCompExGraphic().DOC_ZOOM_LEVEL);
		widthParens = 3;
		
		// The call to getChild() skips the first paren inside of the operator, the parens are needed to have
		// an expression inside of a UnaryOp, but they are not usually displayed
		// if a user wants to show parens, the can use  two pairs of parens: sqrt((5/6))
		Expression tempChild = ((UnaryExpression)super.getValue()).getChild();
		ValueGraphic childValGraphic = null;
		
		
		int[] childSize = {0,0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		childValGraphic = makeValueGraphic(tempChild);
		childSize = childValGraphic.requestSize(g, f, x1 + widthParens + space, y1 + overhang);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(this);
		childValGraphic.getMostInnerEast().setEast(this);
		
		setMostInnerEast(this);
		childValGraphic.getMostInnerWest().setWest(this);
		
		setChildGraphic(childValGraphic);
		super.getCompExGraphic().getComponents().add(childValGraphic);
		
		widthParens += (int) Math.round(childSize[1]/14.0);
		childValGraphic.shiftToX1(x1 + widthParens + space);
		
		symbolSize[0] = childSize[0] + space * 2 + widthParens * 2;
		symbolSize[1] = childSize[1] + overhang * 2;
		
		symbolY1 = y1;
		symbolY2 = symbolY1 + symbolSize[1];
		symbolX1 = x1;
		symbolX2 = x1 + symbolSize[0];
		
		setUpperHeight((symbolY2 - symbolY1)/2);
		setLowerHeight(getUpperHeight());
		
		totalSize[0] = symbolSize[0];
		totalSize[1] = symbolSize[1];
		
		//set the outer bounds of this element
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + totalSize[0]);
		super.setY2(y1 + totalSize[1]);
		
		return totalSize;
	}

}
