/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
 */

package math_rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import expression.Expression;
import expression.Node;

public class NegationGraphic extends UnaryExpressionGraphic {

	private int space;
	
	public NegationGraphic(Expression v,
			RootNodeGraphic compExGraphic) {
		super(v, compExGraphic);
		setMostInnerNorth(this);
		setMostInnerSouth(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
	
		if (isSelected()){
			super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
			super.getRootNodeGraphic().getGraphics().fillRect(symbolX1, symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
			super.getRootNodeGraphic().getGraphics().setColor(Color.black);
		}
		
		getRootNodeGraphic().getGraphics().setFont(getFont());
		getRootNodeGraphic().getGraphics().drawString("-", symbolX1, symbolY2);
//		getRootNodeGraphic().getGraphics().setStroke(new BasicStroke(
//				(int) (1 * getRootNodeGraphic().DOC_ZOOM_LEVEL * getRootNodeGraphic().getFontSizeAdjustment())));
//		super.getRootNodeGraphic().getGraphics().drawLine(symbolX1 + space,
//				symbolY1 + (int) Math.round((symbolY2 - symbolY1) * .4),
//				symbolX2 - space, 
//				symbolY1 + (int) Math.round((symbolY2 - symbolY1) * .4));
//		super.getRootNodeGraphic().getGraphics().setStroke(new BasicStroke());
	}

	@Override
	public void drawCursor(){
		int xPos = symbolX1;
		
		if ( super.getRootNodeGraphic().getCursor().getPos() == getMaxCursorPos()){
			xPos = symbolX2;
		}
		super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		super.getRootNodeGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1() + 5);
		
	}
	
	@Override
	public int getMaxCursorPos(){
		return 1;
	}
	
	@Override
	public void setCursorPos(int xPixelPos){
		
		String numberString = getValue().getOperator().getSymbol();
		
		if (xPixelPos < getX1()){
			super.getRootNodeGraphic().getCursor().setPos(0);
			super.getRootNodeGraphic().getCursor().setValueGraphic(this);
			return;
		}
			
		else if (xPixelPos > getX2()){
			super.getRootNodeGraphic().getCursor().setPos(numberString.length());
			super.getRootNodeGraphic().getCursor().setValueGraphic(this);
			return;
		}
		
		int startX, endX, xWidth;
		
		startX = super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
				numberString.substring(0, 0)) + symbolX1 - space;
		endX = super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
				numberString.substring(0, 1)) + symbolX1 + space;
		xWidth = endX - startX;
		
		if (startX < xPixelPos && endX > xPixelPos)
		{//if the x position is inside of a character, check if it is on the first or second
			//half of the character and set the cursor accordingly
			if (endX - xPixelPos > xWidth/2){
				super.getRootNodeGraphic().getCursor().setPos( 0 );
			}
			else{
				super.getRootNodeGraphic().getCursor().setPos( 1 );
			}
			super.getRootNodeGraphic().getCursor().setValueGraphic(this);
			return;
		}
	}
	
	@Override
	public void moveCursorWest(){
		if (super.getRootNodeGraphic().getCursor().getPos() > 0){
			super.getRootNodeGraphic().getCursor().setPos( super.getRootNodeGraphic().getCursor().getPos() - 1); 
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
	
	@Override
	public void moveCursorEast(){
		if (super.getRootNodeGraphic().getCursor().getPos() < getMaxCursorPos()){
			super.getRootNodeGraphic().getCursor().setPos( super.getRootNodeGraphic().getCursor().getPos() + 1); 
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
	
	public void sendCursorInFromEast(int yPos){
		super.getRootNodeGraphic().getCursor().setValueGraphic(this);
		super.getRootNodeGraphic().getCursor().setPos(getMaxCursorPos() - 1);
	}
	
	public void sendCursorInFromWest(int yPos){
		super.getRootNodeGraphic().getCursor().setValueGraphic(this);
		super.getRootNodeGraphic().getCursor().setPos(1);
	}
	
	@Override
	public int[] requestSize(Graphics g, Font f) {
		return null;
	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1)
			throws Exception {
		g.setFont(f);
		setFont(f);
		
		space = (int) (1 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL);
		
		Node tempChild = ((Expression)getValue()).getChild(0);
		NodeGraphic childValGraphic = null;
		int[] childSize = {0,0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		
		symbolSize[0] = (int) Math.round(super.getRootNodeGraphic().getStringWidth("-", f)) + 2 * space;
		symbolSize[1] = super.getRootNodeGraphic().getFontHeight(f);
		childValGraphic = makeNodeGraphic(tempChild);
		
		setChildGraphic(childValGraphic);
		super.getRootNodeGraphic().getComponents().add(childValGraphic);
		
		childSize = childValGraphic.requestSize(g, f, x1 + symbolSize[0], y1);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(this);
		setEast(childValGraphic);
		childValGraphic.setWest(this);
		setMostInnerEast(childValGraphic);
		
		symbolY1 = y1 + childValGraphic.getUpperHeight() - (int) Math.round(symbolSize[1]/2.0);
		symbolY2 = symbolY1 + symbolSize[1];
		symbolX1 = x1;
		symbolX2 = x1 + symbolSize[0];
		
		setUpperHeight(childValGraphic.getUpperHeight());
		setLowerHeight(childValGraphic.getLowerHeight());
		
		totalSize[0] = symbolSize[0] + childSize[0];
		totalSize[1] = childSize[1];
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + totalSize[0]);
		super.setY2(y1 + totalSize[1]);
		return totalSize;
	}

}
