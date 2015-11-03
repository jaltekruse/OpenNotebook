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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import expression.Expression;
import expression.Node;
import expression.NodeException;

public class ImpliedMultGraphic extends BinExpressionGraphic {

private int space;
	
	public ImpliedMultGraphic(Expression e, RootNodeGraphic gr) {
		super(e, gr);
	}

	@Override
	public void draw() {
		
		if (isSelected()){
			super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
			super.getRootNodeGraphic().getGraphics().fillRect(symbolX1, symbolY1 - 2,
					symbolX2 - symbolX1, symbolY2 - symbolY1 + 4);
			super.getRootNodeGraphic().getGraphics().setColor(Color.black);
		}
	}

    protected int findCursorXPos(){
        int xPos = symbolX1;

		if ( super.getRootNodeGraphic().getCursor().getPos() == getMaxCursorPos()){
			xPos += space;
		}
        return xPos;
    }

	@Override
	public void drawCursor(){
		int xPos = findCursorXPos();

		super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		super.getRootNodeGraphic().getGraphics().fillRect(xPos, super.symbolY1 - 3, 2, super.symbolY2 - super.symbolY1 + 5);

	}
	
	@Override
	public int getMaxCursorPos(){
		return NO_VALID_CURSOR_POS;
	}
	
	@Override
	public void setCursorPos(int xPixelPos) throws NodeException {
		
		String numberString = getValue().getOperator().getSymbol();
		
		if (xPixelPos <= super.symbolX1){
			getLeftGraphic().setCursorPos(xPixelPos);
			return;
		}
			
		else if (xPixelPos > super.symbolX2){
			getRightGraphic().setCursorPos(xPixelPos);
			return;
		}
		
//		int startX, endX, xWidth;
//
//		startX = super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
//				numberString.substring(0, 0)) + symbolX1 - space;
//		endX = super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
//				numberString.substring(0, 1)) + symbolX1 + space;
//		xWidth = endX - startX;


        int startX = symbolX1, endX = symbolX1 + space, xWidth = space;
		
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
	public void moveCursorWest() throws NodeException {
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
	
	@Override
	public void moveCursorEast(){
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
	
//	@Override
//	public void sendCursorInFromEast(int yPos, NodeGraphic vg) throws NodeException {
//		super.getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerEast());
//		super.getRootNodeGraphic().getCursor().setPos(getLeftGraphic().getMostInnerEast().getMaxCursorPos() - 1);
//	}
//
//	@Override
//	public void sendCursorInFromWest(int yPos, NodeGraphic vg){
//		super.getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerWest());
//		super.getRootNodeGraphic().getCursor().getValueGraphic().sendCursorInFromWest(yPos, vg);
//	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {

		g.setFont(f);
		setFont(f);
		
		space = (int) Math.round(2 * super.getRootNodeGraphic().getFontSizeAdjustment());
		if ( space == 0){
			space = 2;
		}
		Node tempLeft = (super.getValue()).getChild(0);
		Node tempRight = (super.getValue()).getChild(1);
		NodeGraphic leftValGraphic = null;
		NodeGraphic rightValGraphic = null; 
		int[] rightSize = {0,0};
		int[] leftSize = {0, 0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		leftValGraphic = makeNodeGraphic(tempLeft);
		
		super.getRootNodeGraphic().getComponents().add(leftValGraphic);
		leftSize = leftValGraphic.requestSize(g, f, x1, y1);
		
		rightValGraphic = makeNodeGraphic(tempRight);
		
		rightSize = rightValGraphic.requestSize(g, f, x1, y1);
		super.getRootNodeGraphic().getComponents().add(rightValGraphic);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(leftValGraphic.getMostInnerWest());
		leftValGraphic.getMostInnerEast().setEast(this);
		this.setWest(leftValGraphic.getMostInnerEast());
		setMostInnerNorth(this);
		setMostInnerSouth(this);
		setMostInnerEast(rightValGraphic.getMostInnerEast());
		rightValGraphic.getMostInnerWest().setWest(this);
		this.setEast(rightValGraphic.getMostInnerWest());
		
		symbolSize[0] = space;
		symbolSize[1] = getRootNodeGraphic().getFontHeight(f);
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
	
	@Override
	public NodeGraphic getLeftGraphic(){
		return super.getComponents().get(0);
	}
	
	@Override
	public NodeGraphic getRightGraphic(){
		return super.getComponents().get(1);
	}
}
