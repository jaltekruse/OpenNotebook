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
import expression.NodeException;

public class DivisionGraphic extends BinExpressionGraphic {

	public static enum Style{
		SLASH, DIAGONAL, HORIZONTAL
	}
	//number of pixels the bar overhangs the widest child (numerator or denominator)
	private int sizeOverhang;
	
	//number of pixels left above and below the horizontal bar
	private int spaceAroundBar;
	private Style style;
	private int heightNumer, heightDenom;
	
	public DivisionGraphic(Expression b, RootNodeGraphic gr) {
		super(b, gr);
		style = Style.HORIZONTAL;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		if (style == Style.SLASH)
		{
			if (isSelected()){

			}
			super.getRootNodeGraphic().getGraphics().drawString(getValue().getOperator().getSymbol(),
					symbolX1, symbolY2);
		}
		else if (style == Style.HORIZONTAL){
			if (isSelected()){
				super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
				super.getRootNodeGraphic().getGraphics().fillRect(symbolX1, 
						symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
				super.getRootNodeGraphic().getGraphics().setColor(Color.black);
			}
			getRootNodeGraphic().getGraphics().setStroke(new BasicStroke(
					(int) (1 * getRootNodeGraphic().DOC_ZOOM_LEVEL * getRootNodeGraphic().getFontSizeAdjustment())));
			super.getRootNodeGraphic().getGraphics().drawLine(symbolX1, symbolY1 + spaceAroundBar + 1, symbolX2, 
					symbolY1 + spaceAroundBar + 1); 
			super.getRootNodeGraphic().getGraphics().setStroke(new BasicStroke());
		}
	}
	
	@Override
	public void drawCursor() throws NodeException {
		String numberString = getValue().toStringRepresentation();
		
		int xPos = findCursorXPos();
		
		super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		super.getRootNodeGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1()+ 5);
		
	}
	
	@Override
	public void setCursorPos(int xPixelPos){
		//cursor does not exist in this graphic, send to upper child
		super.getLeftGraphic().getMostInnerSouth().setCursorPos(xPixelPos);
	}
	
	@Override
	public void moveCursorWest(){
		if (super.getRootNodeGraphic().getCursor().getPos() == 1){
			super.getRootNodeGraphic().getCursor().setValueGraphic(
					getLeftGraphic().getMostInnerEast());
			super.getRootNodeGraphic().getCursor().setPos(
					getLeftGraphic().getMostInnerEast().getMaxCursorPos());
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
		if (super.getRootNodeGraphic().getCursor().getPos() == 0){
			super.getRootNodeGraphic().getCursor().setValueGraphic(
					getLeftGraphic().getMostInnerWest());
			super.getRootNodeGraphic().getCursor().setPos(0); 
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
	
	@Override
	public void moveCursorNorth(){
		if (getNorth() == null)
		{
//			System.out.println("nothing to north");
			return;
		}
		else
		{
			getNorth().getMostInnerSouth().sendCursorInFromSouth(findCursorXPos(), this);
			return;
		}
	}
	
	protected int findCursorXPos() {
		// TODO Auto-generated method stub
		return getX1() + super.getRootNodeGraphic().getCursor().getPos() * (getX2() - getX1()); 
	}

	@Override
	public void moveCursorSouth(){
		if (getSouth() == null)
		{
//			System.out.println("nothing to south");
			return;
		}
		else
		{
            // TODO - see if this revised line workds, before it was requesting the most inner north object of south
			getSouth().sendCursorInFromNorth(findCursorXPos(), this);
			return;
		}
	}
	
	@Override
	public void sendCursorInFromEast(int yPos, NodeGraphic vg)
	{
		if (super.hasDescendent(vg)){
//			System.out.println("move into division from east, containedbelow");
			super.getRootNodeGraphic().getCursor().setValueGraphic(this);
			super.getRootNodeGraphic().getCursor().setPos(0);
		}
		else{
			super.getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerEast());
			super.getRootNodeGraphic().getCursor().setPos(getLeftGraphic().getMostInnerEast().getMaxCursorPos());
		}
//		getLeftGraphic().sendCursorInFromEast(yPos);
	}
	
	@Override
	public void sendCursorInFromWest(int yPos, NodeGraphic vg)
	{
		if (super.hasDescendent(vg)){
//			System.out.println("move into division from west, containedbelow");
			super.getRootNodeGraphic().getCursor().setValueGraphic(this);
			super.getRootNodeGraphic().getCursor().setPos(getMaxCursorPos());
		}
		else{
			super.getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerWest());
			super.getRootNodeGraphic().getCursor().setPos(0);
		}
//		getLeftGraphic().sendCursorInFromWest(yPos);
	}
	
	@Override
	public void sendCursorInFromNorth(int xPos, NodeGraphic vg){
//		System.out.println("Division send in from north, xPos: " + xPos);
        if ( getLeftGraphic().hasDescendent(vg) || getLeftGraphic() == vg){
            getRightGraphic().getMostInnerNorth().sendCursorInFromNorth(xPos, this);
        }
        else{
		    getLeftGraphic().getMostInnerNorth().sendCursorInFromNorth(xPos, this);
        }
	}
	
	@Override
	public void sendCursorInFromSouth(int xPos, NodeGraphic vg){
//		System.out.println("division send from south");
        if ( getRightGraphic().hasDescendent(vg) || getRightGraphic() == vg){
            getLeftGraphic().getMostInnerSouth().sendCursorInFromSouth(xPos, this);
        }
        else{
            getRightGraphic().getMostInnerSouth().sendCursorInFromSouth(xPos, this);
        }
	}
	
	/**
	 * The cursor can either be before or after the bar.
	 */
	@Override
	public int getMaxCursorPos(){
		return 1;
	}
	
	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {
		// TODO Auto-generated method stub
		
		g.setFont(f);
		setFont(f);
		
		sizeOverhang = (int) (3 * getRootNodeGraphic().getFontSizeAdjustment());
		spaceAroundBar = (int) (5 * getRootNodeGraphic().getFontSizeAdjustment());
		
		Node tempLeft = (super.getValue()).getChild(0);
		Node tempRight = (super.getValue()).getChild(1);
		NodeGraphic leftValGraphic = null;
		NodeGraphic rightValGraphic = null; 
		int[] rightSize = {0,0};
		int[] leftSize = {0, 0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		if (style == Style.SLASH)
		{
			
			leftValGraphic = makeNodeGraphic(tempLeft);
			
			super.getRootNodeGraphic().getComponents().add(leftValGraphic);
			leftSize = leftValGraphic.requestSize(g, f, x1, y1);
			symbolSize[0] = getRootNodeGraphic().getStringWidth(getValue().getOperator().getSymbol(), f);
			symbolSize[1] = getRootNodeGraphic().getFontHeight(f);
			symbolX1 = x1 + leftSize[0];
			symbolY1 = y1 + ((int)Math.round(leftSize[1]/2.0) - (int) (Math.round(symbolSize[1])/2.0));
			symbolX2 = x1 + leftSize[0] + symbolSize[0];
			symbolY2 = symbolSize[1] + symbolY1;
			
			rightValGraphic = makeNodeGraphic(tempRight);
			
			rightSize = rightValGraphic.requestSize(g, f, symbolX2, y1);
			super.getRootNodeGraphic().getComponents().add(rightValGraphic);
			
			//set the west and east fields for inside an outside of the expression
			setMostInnerWest(leftValGraphic.getMostInnerWest());
			leftValGraphic.getMostInnerEast().setEast(this);
			this.setWest(leftValGraphic.getMostInnerEast());
			
			setMostInnerEast(rightValGraphic.getMostInnerEast());
			rightValGraphic.getMostInnerWest().setWest(this);
			this.setEast(rightValGraphic.getMostInnerWest());
			
			setLeftGraphic(leftValGraphic);
			setRightGraphic(rightValGraphic);
			
			totalSize[0] = symbolX2 + rightSize[0] - x1;
			totalSize[1] = symbolY2 - y1;
			setUpperHeight((int) Math.round((totalSize[1]/2.0)));
			setLowerHeight(getUpperHeight());
			super.setX1(x1);
			super.setY1(y1);
			super.setX2(x1 + totalSize[0]);
			super.setY2(y1 + totalSize[1]);
			return totalSize;
		}
		else if (style == Style.HORIZONTAL){
			
			// prevent parenthesis from displaying if not used twice to show they are explicitly wanted
			tempLeft.setDisplayParentheses(false);
			leftValGraphic = makeNodeGraphic(tempLeft);
			
			leftSize = leftValGraphic.requestSize(g, f, x1, y1);
			super.getRootNodeGraphic().getComponents().add(leftValGraphic);
			
			//other if statements for checking the left, decimal, imaginary, other val types
			
			// prevent parenthesis from displaying if not used twice to show they are explicitly wanted
			tempRight.setDisplayParentheses(false);
			rightValGraphic = makeNodeGraphic(tempRight);
			
			rightSize = rightValGraphic.requestSize(g, f, symbolX2, y1);
			super.getRootNodeGraphic().getComponents().add(rightValGraphic);
			setHeightNumer(leftSize[1]);
			setHeightDenom(rightSize[1]);
			
			//set the west and east fields for inside an outside of the expression
			setMostInnerWest(this);
			setMostInnerNorth(leftValGraphic.getMostInnerNorth());
			leftValGraphic.getMostInnerEast().setEast(this);
			leftValGraphic.getMostInnerWest().setWest(this);
			leftValGraphic.getMostInnerSouth().setSouth(this);
//			this.setWest(leftValGraphic.getMostInnerEast());
			
			setMostInnerEast(this);
			//setSouth(rightValGraphic.getMostInnerNorth());
			setMostInnerSouth(rightValGraphic.getMostInnerSouth());
			rightValGraphic.getMostInnerNorth().setNorth(this);
			rightValGraphic.getMostInnerWest().setWest(this);
			rightValGraphic.getMostInnerEast().setEast(this);
//			this.setEast(rightValGraphic.getMostInnerWest());
            south = rightValGraphic;
            north = leftValGraphic;
			
			symbolX1 = x1;
			
			if (leftSize[0] > rightSize[0]){
				symbolX2 = x1 + leftSize[0] + 2 * sizeOverhang;
			}
			else
			{
				symbolX2 = x1 + rightSize[0] + 2 * sizeOverhang;
			}
			
			leftValGraphic.shiftToX1((int)(Math.round(((symbolX2 - symbolX1) - leftSize[0]))/2.0) + x1);
			//leftValGraphic.shiftToX2(leftValGraphic.getX1() + leftSize[0]);
			
			symbolY1 = leftSize[1] + y1;
			symbolY2 = symbolY1 + 1 + 2 * spaceAroundBar;
			
			setLeftGraphic(leftValGraphic);
			setRightGraphic(rightValGraphic);

			rightValGraphic.shiftToX1((int)(Math.round(((symbolX2 - symbolX1) - rightSize[0]))/2.0) + x1);
			//rightValGraphic.shiftToX2(rightValGraphic.getX1() + rightSize[0]);
			rightValGraphic.shiftToY1(symbolY2);
			//rightValGraphic.shiftToY2(rightValGraphic.getY1() + rightSize[1]);
			
			setUpperHeight(leftSize[1] + spaceAroundBar);
			setLowerHeight(rightSize[1] + spaceAroundBar);
			totalSize[0] = symbolX2 - x1;
			totalSize[1] = symbolY2 + rightSize[1] - y1;
			super.setX1(x1);
			super.setY1(y1);
			super.setX2(x1 + totalSize[0]);
			super.setY2(y1 + totalSize[1]);
			return totalSize;
		}
		
		return null;
	}

	public void setHeightNumer(int heightNumer) {
		this.heightNumer = heightNumer;
	}

	public int getHeightNumer() {
		//adds 3 for the whitespace
		return heightNumer + spaceAroundBar;
	}

	public void setHeightDenom(int heightDenom) {
		this.heightDenom = heightDenom;
	}

	public int getHeightDenom() {
		//adds 3 for the whitespace
		return heightDenom + spaceAroundBar;
	}

}
