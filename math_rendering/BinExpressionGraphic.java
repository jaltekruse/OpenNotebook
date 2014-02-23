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
import java.util.Vector;

import expression.Expression;
import expression.Node;
import expression.NodeException;

public class BinExpressionGraphic extends ExpressionGraphic{

	private int space;
	private NodeGraphic leftChild, rightChild;
	
	public BinExpressionGraphic(Expression e, RootNodeGraphic gr) {
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
		getRootNodeGraphic().getGraphics().setFont(getFont());
		getRootNodeGraphic().getGraphics().drawString(getValue().getOperator().getSymbol(),
				symbolX1 + space, symbolY2);
	}
	
	@Override
	public void drawCursor() throws NodeException {
	    int xPos = findCursorXPos();
		super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		super.getRootNodeGraphic().getGraphics().fillRect(xPos, super.symbolY1 - 3, 2, super.symbolY2 - super.symbolY1 + 5);
		
	}

    protected int findCursorXPos(){
        String opString = getValue().getOperator().getSymbol();
        int xPos = symbolX1 + super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
                opString.substring(0, super.getRootNodeGraphic().getCursor().getPos()));

        if ( super.getRootNodeGraphic().getCursor().getPos() == getMaxCursorPos()){
            xPos += 2 * space;
        }
        return xPos;
    }
	
	@Override
	public int getMaxCursorPos(){
		return getValue().getOperator().getSymbol().length();
	}
	
	@Override
	public void setCursorPos(int xPixelPos) throws NodeException {
		
		String numberString = getValue().getOperator().getSymbol();
		
		if (xPixelPos < super.symbolX1){
            getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic());
			getLeftGraphic().setCursorPos(xPixelPos);
			return;
		}
			
		else if (xPixelPos > super.symbolX2){
            getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic());
			getRightGraphic().setCursorPos(xPixelPos);
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
        else if (xPixelPos < startX){
            getWest().setCursorPos(xPixelPos);
            getRootNodeGraphic().getCursor().setValueGraphic(getWest());
        }
        else if (xPixelPos > startX){
            getEast().setCursorPos(xPixelPos);
            getRootNodeGraphic().getCursor().setValueGraphic(getEast());
        }
	}
	
	@Override
	public void moveCursorWest() throws NodeException {
		if (super.getRootNodeGraphic().getCursor().getPos() == getMaxCursorPos()){
            getRootNodeGraphic().getCursor().setPos(getRootNodeGraphic().getCursor().getPos() - 1);
            return;
        }
        else if (super.getRootNodeGraphic().getCursor().getPos() == 1){
            getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerEast());
            getLeftGraphic().getMostInnerEast().sendCursorInFromEast((getY2() - getY1())/ 2, this);
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

    @Override
    public void moveCursorNorth() throws NodeException {
        if (getNorth() == null)
        {
//			System.out.println("nothing to north");
            return;
        }
        else
        {
            getNorth().sendCursorInFromSouth(findCursorXPos(), this);
            return;
        }
    }

    @Override
    public void moveCursorSouth() throws NodeException {
        if (getSouth() == null)
        {
//			System.out.println("nothing to south");
            return;
        }
        else
        {
            getSouth().sendCursorInFromNorth(findCursorXPos(), this);
            return;
        }
    }
	@Override
	public void sendCursorInFromEast(int yPos, NodeGraphic vg) throws NodeException {
        if (rightChild.hasDescendent(vg) ||  rightChild == vg){
		    super.getRootNodeGraphic().getCursor().setValueGraphic(this);
		    super.getRootNodeGraphic().getCursor().setPos(getMaxCursorPos() - 1);
        }
        else{
            getRightGraphic().sendCursorInFromEast(yPos, this);
        }
	}
	
	@Override
	public void sendCursorInFromWest(int yPos, NodeGraphic vg){
		super.getRootNodeGraphic().getCursor().setValueGraphic(this);
		super.getRootNodeGraphic().getCursor().setPos(1);
	}

    @Override
    public void sendCursorInFromNorth(int xPos, NodeGraphic vg){

        if (xPos < super.symbolX1){
            getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic());
            getLeftGraphic().sendCursorInFromNorth(xPos, this);
            return;
        }

        else if (xPos > super.symbolX2){
            getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic());
            getRightGraphic().sendCursorInFromNorth(xPos, this);
            return;
        }
        else {
            getRootNodeGraphic().getCursor().setValueGraphic(this);
            setCursorPos(xPos);
        }
    }

    @Override
    public void sendCursorInFromSouth(int xPos, NodeGraphic vg){

        if (xPos < super.symbolX1){
            getRootNodeGraphic().getCursor().setValueGraphic(getLeftGraphic());
            getLeftGraphic().sendCursorInFromSouth(xPos, this);
            return;
        }

        else if (xPos > super.symbolX2){
            getRootNodeGraphic().getCursor().setValueGraphic(getRightGraphic());
            getRightGraphic().sendCursorInFromSouth(xPos, this);
            return;
        }
        else {
            getRootNodeGraphic().getCursor().setValueGraphic(this);
            setCursorPos(xPos);
        }
    }

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {
		// TODO Auto-generated method stub
		
		g.setFont(f);
		setFont(f);
		
		space = (int) (4 * getRootNodeGraphic().getFontSizeAdjustment());
		
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
		
		symbolSize[0] = getRootNodeGraphic().getStringWidth(getValue().getOperator().getSymbol(), f) + 2 * space;
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
	
	protected void setLeftGraphic(NodeGraphic vg){
		leftChild = vg;
	}
	
	public NodeGraphic getLeftGraphic(){
		return leftChild;
	}

	protected void setRightGraphic(NodeGraphic vg){
		rightChild = vg;
	}
	
	public NodeGraphic getRightGraphic(){
		return rightChild;
	}
	
	@Override
	public Vector<NodeGraphic> getComponents() {
		// TODO Auto-generated method stub
		Vector<NodeGraphic> children = new Vector<NodeGraphic>();
		children.add(leftChild);
		children.add(rightChild);
		return children;
	}
}
