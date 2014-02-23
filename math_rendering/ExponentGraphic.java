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

import expression.Expression;
import expression.Node;
import expression.NodeException;

public class ExponentGraphic extends BinExpressionGraphic {

	public static enum Style{
		CARET, SUPERSCRIPT
	}
	
	private int spaceBetweenBaseAndSuper;
	private int extraShiftUp;
	private Style style;

	public ExponentGraphic(Expression b, RootNodeGraphic gr) {
		super(b, gr);
		style = Style.SUPERSCRIPT;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw(){
		//no symbol to draw
		if (isSelected()){
			super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
			super.getRootNodeGraphic().getGraphics().fillRect(getX1(), getY1(), getX2() - getX1(), getY2() - getY1());
			super.getRootNodeGraphic().getGraphics().setColor(Color.black);
		}
	}
	
	@Override
	public void drawCursor(){
		String numberString = getValue().toString();
		
		int xPos = findCursorXPos();
		
		super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		super.getRootNodeGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1()+ 5);
		
	}
	
	@Override
	public void setCursorPos(int xPixelPos) throws NodeException {
		//cursor does not exist in this graphic, send to upper child
		super.getLeftGraphic().getMostInnerSouth().setCursorPos(xPixelPos);
	}

    @Override
    public void moveCursorWest() throws NodeException {
        if (super.getRootNodeGraphic().getCursor().getPos() == 1){
            getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerEast());
            getRootNodeGraphic().getCursor().setPos(getRightGraphic().getMostInnerEast().getMaxCursorPos());
            return;
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
//            super.getRootNodeGraphic().getCursor().setPos( super.getRootNodeGraphic().getCursor().getPos() + 1);
            getLeftGraphic().sendCursorInFromWest((getY2() - getY1())/2, this);
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

	protected int findCursorXPos() {
		// TODO Auto-generated method stub
		return getX1() + super.getRootNodeGraphic().getCursor().getPos() * (getX2() - getX1()); 
	}

	@Override
	public void sendCursorInFromEast(int yPos, NodeGraphic vg) throws NodeException {
		if (getLeftGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getWest().sendCursorInFromEast(yPos, vg);
			return;
		}
		else if (getRightGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerWest());
			getRootNodeGraphic().getCursor().setPos(getLeftGraphic().getMaxCursorPos());
		}
		else
		{//the cursor was outside of this expression, moving in
			getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerEast());
            getRootNodeGraphic().getCursor().setPos(getRightGraphic().getMostInnerEast().getMaxCursorPos());
		}
	}
	
	@Override
	public void sendCursorInFromWest(int yPos, NodeGraphic vg)
	{
		if (getLeftGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerWest());
			getRootNodeGraphic().getCursor().setPos(0);
		}
		else if (getRightGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the right child of this exponent
			System.out.println("send west from rightChild");
            if (getEast() instanceof ParenGraphic || getEast() instanceof  RadicalGraphic){
                getRootNodeGraphic().getCursor().setValueGraphic(getEast());
       			getRootNodeGraphic().getCursor().setPos(getRootNodeGraphic().getCursor().getValueGraphic().getMaxCursorPos() - 1);
                return;
            }
            else if ( getEast() instanceof DivisionGraphic) {
                getRootNodeGraphic().getCursor().setValueGraphic(this);
                getRootNodeGraphic().getCursor().setPos(getMaxCursorPos());
                return;
            }
            getRootNodeGraphic().getCursor().setValueGraphic(this);
			//getRootNodeGraphic().getCursor().setValueGraphic(getEast());
			getRootNodeGraphic().getCursor().setPos(getMaxCursorPos());
			return;
		}
		else
		{//the cursor was outside of this expression, moving in
			getLeftGraphic().sendCursorInFromWest(yPos, vg);
		}
	}
	
	@Override
	public void sendCursorInFromNorth(int xPos, NodeGraphic vg) throws NodeException {
		if (getRightGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerEast());
			getRootNodeGraphic().getCursor().setPos(getLeftGraphic().getMostInnerEast().getMaxCursorPos());
		}
		else if (getLeftGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
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
            if (xPos >= getX1() && xPos <= getLeftGraphic().getX2()){
                getLeftGraphic().sendCursorInFromNorth(xPos, this);
                return;
            }
            else if (xPos >= getRightGraphic().getX1() && xPos <= getRightGraphic().getX2()){
               getRightGraphic().sendCursorInFromNorth(xPos, this);
                return;
            }
            else {
                setCursorPos(xPos);
            }
		}
	}
	
	@Override
	public void sendCursorInFromSouth(int xPos, NodeGraphic vg) throws NodeException {
		if (getLeftGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getLeftGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
		{//if the cursor was in the left child of this exponent
			getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic().getMostInnerWest());
			getRootNodeGraphic().getCursor().setPos(0);
		}
		if (getRightGraphic().hasDescendent(getRootNodeGraphic().getCursor().getValueGraphic()) ||
				this.getRightGraphic().equals(getRootNodeGraphic().getCursor().getValueGraphic()))
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
            if (xPos >= getX1() && xPos <= getLeftGraphic().getX2()){
                getLeftGraphic().sendCursorInFromSouth(xPos, this);
                return;
            }
            else if (xPos >= getRightGraphic().getX1() && xPos <= getRightGraphic().getX2()){
                getRightGraphic().sendCursorInFromSouth(xPos, this);
                return;
            }
            else {
                setCursorPos(xPos);
            }
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
		
		spaceBetweenBaseAndSuper = (int) (4 * getRootNodeGraphic().getFontSizeAdjustment());
		extraShiftUp = (int) (2 * getRootNodeGraphic().getFontSizeAdjustment());
		
		
		Node tempLeft = (super.getValue()).getChild(0);
		Node tempRight = (super.getValue()).getChild(1);
		NodeGraphic leftValGraphic = null;
		NodeGraphic rightValGraphic = null; 
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

			leftValGraphic = makeNodeGraphic(tempLeft);
			
			leftSize = leftValGraphic.requestSize(g, f, x1, y1);
			super.getRootNodeGraphic().getComponents().add(leftValGraphic);
			
			rightValGraphic = makeNodeGraphic(tempRight);
			
			rightSize = rightValGraphic.requestSize(g, getRootNodeGraphic().getSmallFont(), x1, y1);
			super.getRootNodeGraphic().getComponents().add(rightValGraphic);
			
			//set the west and east fields for inside and outside of the expression
			setMostInnerEast(this);
			setMostInnerWest(this);
			setMostInnerSouth(this);
            setMostInnerNorth(this);

			//the request to move to the east of the exponent will need to go down first
			//before being sent into the element east of this ExponentGraphic
			//usual moveEast: 45|+67, cursor at end of 45 -> 45+|67 cursor at end of +
			//skips the 0 position of the next element
			//In this case, the cursor clearly moves down from the superscript
			//moveEast: 4^5|+67, cursor at end of 5 in superscript -> 4^5|+/67 cursor at start of +
			
//			leftValGraphic.setNorth(this);
//			rightValGraphic.setSouth(this);
			rightValGraphic.getMostInnerWest().setWest(this);
			rightValGraphic.getMostInnerEast().setEast(this);
			leftValGraphic.getMostInnerWest().setWest(this);
			leftValGraphic.getMostInnerEast().setEast(this);

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
