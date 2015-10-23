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

import expression.Expression;
import expression.Node;

public class RadicalGraphic extends UnaryExpressionGraphic {

	private int space;
	private int widthFront;
	private int heightLeadingTail;
	private int lengthLittleTail;
    private int widthParens = 3;
	
	public RadicalGraphic(Expression v, RootNodeGraphic compExGraphic) {
		super(v, compExGraphic);
		setMostInnerSouth(this);
		setMostInnerNorth(this);
	}

	@Override
	public void draw() {
		
		if (isSelected()){
			super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
			super.getRootNodeGraphic().getGraphics().fillRect(symbolX1, symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
			super.getRootNodeGraphic().getGraphics().setColor(Color.black);
		}
		getRootNodeGraphic().getGraphics().setStroke(new BasicStroke(
				(int) (1 * getRootNodeGraphic().DOC_ZOOM_LEVEL * getRootNodeGraphic().getFontSizeAdjustment())));
		getRootNodeGraphic().getGraphics().drawLine(symbolX1, symbolY2 - heightLeadingTail + lengthLittleTail,
				symbolX1 + 3, symbolY2 - heightLeadingTail);
		getRootNodeGraphic().getGraphics().drawLine(symbolX1 + 3, symbolY2 - heightLeadingTail,
				symbolX1 + (int) Math.round(0.5 * widthFront), symbolY2);
		getRootNodeGraphic().getGraphics().drawLine(symbolX1 + (int) Math.round(0.5 * widthFront),
				symbolY2, symbolX1 + widthFront, symbolY1);
		getRootNodeGraphic().getGraphics().drawLine(symbolX1 + widthFront, symbolY1, 
				symbolX2, symbolY1);
		getRootNodeGraphic().getGraphics().drawLine(symbolX2, symbolY1, symbolX2, symbolY1 + lengthLittleTail);
		getRootNodeGraphic().getGraphics().setStroke(new BasicStroke());
	}
	
    @Override
    public void drawCursor(){
        int cursorPos = super.getRootNodeGraphic().getCursor().getPos();

        super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
        int height = getY2() - getY1();
        int extraShift = 5;
        if (getRootNodeGraphic().getCursor().getPos() == 0 || getRootNodeGraphic().getCursor().getPos() == getMaxCursorPos()){
            height += 5;
            extraShift = 0;
        }
        super.getRootNodeGraphic().getGraphics().fillRect(findCursorXPos(), getY1() - 3 + extraShift, 2, height);
        return;

    }

    public int findCursorXPos(){
        super.getRootNodeGraphic().getGraphics().setFont(getFont());
        int cursorPos = super.getRootNodeGraphic().getCursor().getPos();

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

    @Override
    public int getMaxCursorPos(){
        return 3;
    }

    public void setCursorPos(int xPixelPos){

        if (xPixelPos < getX1() + widthParens + space){
            if (xPixelPos < getX1() + widthParens/2){
                super.getRootNodeGraphic().getCursor().setPos(0);
                super.getRootNodeGraphic().getCursor().setValueGraphic(this);
                return;
            }
            else{
                super.getRootNodeGraphic().getCursor().setPos(1);
                super.getRootNodeGraphic().getCursor().setValueGraphic(this);
                return;
            }
        }
        else{
            if (xPixelPos > getX2() - widthParens - space){
                if (xPixelPos < getX2() - widthParens/2){
                    super.getRootNodeGraphic().getCursor().setPos(2);
                    super.getRootNodeGraphic().getCursor().setValueGraphic(this);
                    return;
                }
                else{
                    super.getRootNodeGraphic().getCursor().setPos(3);
                    super.getRootNodeGraphic().getCursor().setValueGraphic(this);
                    return;
                }
            }
            else{
                getChildGraphic().setCursorPos(xPixelPos);
                return;
            }
        }
    }

    @Override
    public void moveCursorWest(){
        int cursorPos = getRootNodeGraphic().getCursor().getPos();
        if (cursorPos == 3 || cursorPos == 1){
            super.getRootNodeGraphic().getCursor().setPos( cursorPos - 1);
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

    @Override
    public void moveCursorEast(){
        int cursorPos = getRootNodeGraphic().getCursor().getPos();
        if (cursorPos == 2 || cursorPos == 0){
            super.getRootNodeGraphic().getCursor().setPos( cursorPos + 1);
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

    @Override
    public void moveCursorNorth(){
        if (getNorth() == null)
        {
            System.out.println("nothing to north");
            return;
        }
        else
        {
            getNorth().sendCursorInFromSouth(findCursorXPos(), this);
            return;
        }
    }

    @Override
    public void moveCursorSouth(){
        if (getSouth() == null)
        {
            System.out.println("nothing to south");
            return;
        }
        else
        {
            getSouth().sendCursorInFromNorth(findCursorXPos(), this);
            return;
        }
    }

    @Override
    public void sendCursorInFromEast(int yPos, NodeGraphic vg){
        if (super.hasDescendent(vg)){
            System.out.println("move into division from east, containedbelow");
            super.getRootNodeGraphic().getCursor().setValueGraphic(this);
            super.getRootNodeGraphic().getCursor().setPos(0);
        }
        else{
            super.getRootNodeGraphic().getCursor().setValueGraphic(getChildGraphic().getMostInnerEast());
            super.getRootNodeGraphic().getCursor().setPos(getChildGraphic().getMostInnerEast().getMaxCursorPos());
        }
    }

    @Override
    public void sendCursorInFromWest(int yPos, NodeGraphic vg){
        if (super.hasDescendent(vg)){
            System.out.println("move into division from west, containedbelow");
            super.getRootNodeGraphic().getCursor().setValueGraphic(this);
            super.getRootNodeGraphic().getCursor().setPos(getMaxCursorPos());
        }
        else{
            super.getRootNodeGraphic().getCursor().setValueGraphic(getChildGraphic().getMostInnerWest());
            super.getRootNodeGraphic().getCursor().setPos(0);
        }
    }

    @Override
    public void sendCursorInFromNorth(int xPos, NodeGraphic vg){
        setCursorPos(xPos);
    }

    @Override
    public void sendCursorInFromSouth(int xPos, NodeGraphic vg){
        setCursorPos(xPos);
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
		
		space = (int) (4 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL * getRootNodeGraphic().getFontSizeAdjustment());
		widthFront = (int) (8 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL* getRootNodeGraphic().getFontSizeAdjustment());
		heightLeadingTail = (int) (6 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL* getRootNodeGraphic().getFontSizeAdjustment());
		lengthLittleTail = (int) (3 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL* getRootNodeGraphic().getFontSizeAdjustment());
		
		// The call to getChild() skips the first paren inside of the operator, the parens are needed to have
		// an expression inside of a UnaryOp, but they are not usually displayed
		// if a user wants to show parens, the can use  two pairs of parens: sqrt((5/6))
		Node tempChild = super.getValue().getChild(0);
		NodeGraphic childValGraphic = null;
		int[] childSize = {0,0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		childValGraphic = makeNodeGraphic(tempChild);
		childSize = childValGraphic.requestSize(g, f, x1 + widthFront + space, y1 + space);
		
		//set the west and east fields for inside an outside of the expression

        setMostInnerWest(this);
        childValGraphic.getMostInnerEast().setEast(this);

        setMostInnerEast(this);
        childValGraphic.getMostInnerWest().setWest(this);

        setMostInnerNorth(this);
        setMostInnerSouth(this);

		setChildGraphic(childValGraphic);
		super.getRootNodeGraphic().getComponents().add(childValGraphic);
		
		widthFront += (int) Math.round(childSize[1]/14.0);
		
		if (widthFront > 20)
		{
			widthFront = 20;
		}
		heightLeadingTail += (int) Math.round(childSize[1]/5.0);
		
		if (heightLeadingTail > 40)
		{
			heightLeadingTail = 40;
		}
		childValGraphic.shiftToX1(x1 + widthFront + space);
		
		symbolSize[0] = childSize[0] + space * 2 + widthFront;
		symbolSize[1] = childSize[1] + space;
		
		symbolY1 = y1;
		symbolY2 = symbolY1 + symbolSize[1];
		symbolX1 = x1;
		symbolX2 = x1 + symbolSize[0];
		
		setUpperHeight(childValGraphic.getUpperHeight() + space);
		setLowerHeight(childValGraphic.getLowerHeight() );
		
		totalSize[0] = symbolSize[0];
		totalSize[1] = symbolSize[1];
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + totalSize[0]);
		super.setY2(y1 + totalSize[1]);
		
		return totalSize;
	}

}
