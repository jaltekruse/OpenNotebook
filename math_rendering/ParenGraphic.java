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
import java.util.Vector;

import expression.Node;

public class ParenGraphic extends NodeGraphic<Node> {

	private int space;
	private int overhang;
	private int widthParens;
	private NodeGraphic child;
	
	public ParenGraphic(Node e, RootNodeGraphic compExGraphic) {
		super(e, compExGraphic);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		if (isSelected()){
			super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
			super.getRootNodeGraphic().getGraphics().fillRect(getX1(), getX2(), getWidth(), getHeight());
			super.getRootNodeGraphic().getGraphics().setColor(Color.black);
		}
		
		getRootNodeGraphic().getGraphics().setStroke(new BasicStroke(
				(int) (1 * getRootNodeGraphic().DOC_ZOOM_LEVEL * getRootNodeGraphic().getFontSizeAdjustment())));
		super.getRootNodeGraphic().getGraphics().drawArc(getX1(), getY1(),
				widthParens * 2, getY2() - getY1(), 90, 180);
		super.getRootNodeGraphic().getGraphics().drawArc(getX2() - widthParens * 2,
				getY1(), widthParens * 2, getY2() - getY1(), 270, 180);
		super.getRootNodeGraphic().getGraphics().setStroke(new BasicStroke());
	}
	
	public void setChildGraphic(NodeGraphic n){
		child = n;
	}
	
	public NodeGraphic getChildGraphic(){
		return child;
	}

	@Override
	public void drawCursor(){
		int cursorPos = super.getRootNodeGraphic().getCursor().getPos();
		
		super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		super.getRootNodeGraphic().getGraphics().fillRect(findCursorXPos(), getY1() - 3, 2,
				getY2() - getY1()+ 5);
		return;
		
	}
	
	@Override
	void shiftToX1(int x1) {
		int xChange = x1 - getX1();
		for (NodeGraphic vg : getComponents()){
			vg.shiftToX1(vg.getX1() + xChange);
		}
		setX2(getX2() + xChange);
		setX1(x1);
	}

	@Override
	void shiftToY1(int y1) {
		int yChange = y1 - getY1();
		for (NodeGraphic vg : getComponents()){
			vg.shiftToY1(vg.getY1() + yChange);
		}
		setY2(getY2() + yChange);
		setY1(y1);
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
		if (super.containedBelow(vg)){
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
		if (super.containedBelow(vg)){
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
		//g is used to decide the size of the text to display for this element
		g.setFont(f);
		
		//to draw this element later, the font must be the same, so its stored in this object
		setFont(f);
		
		space = (int) (2 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL);
		overhang = (int) (3 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL);
		widthParens = 3;
		
		Node tempChild = super.getValue();
		// prevent the call below to make a NodeGrpahic from making an infinite number of parenthesis
		// as the displayParenthesis flag is used to determine if a set of parens should be added
		tempChild.setDisplayParentheses(false);
		NodeGraphic childValGraphic = null;
		
		
		int[] childSize = {0,0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		childValGraphic = makeNodeGraphic(tempChild);
		childSize = childValGraphic.requestSize(g, f, x1 + widthParens + space, y1 + overhang);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(this);
		childValGraphic.getMostInnerEast().setEast(this);
		
		setMostInnerEast(this);
		childValGraphic.getMostInnerWest().setWest(this);
		
		setMostInnerNorth(this);
		setMostInnerSouth(this);
		
		setChildGraphic(childValGraphic);
		super.getRootNodeGraphic().getComponents().add(childValGraphic);
		
		widthParens += (int) Math.round(childSize[1]/14.0);
		childValGraphic.shiftToX1(x1 + widthParens + space);
		
		symbolSize[0] = childSize[0] + space * 2 + widthParens * 2;
		symbolSize[1] = childSize[1] + overhang * 2;
		
		int symbolY1 = y1;
		int symbolY2 = symbolY1 + symbolSize[1];
		
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

	@Override
	public Vector<NodeGraphic> getComponents() {
		// TODO Auto-generated method stub
		Vector<NodeGraphic> children = new Vector<NodeGraphic>();
		children.add(child);
		return children;
	}

}
