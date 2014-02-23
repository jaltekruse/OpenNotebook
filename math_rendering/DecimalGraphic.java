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

import expression.NodeException;
import expression.Number;

public class DecimalGraphic extends NodeGraphic<Number> {

	public DecimalGraphic(Number n, RootNodeGraphic compExGraphic) {
		super(n, compExGraphic);
		setMostInnerWest(this);
		setMostInnerEast(this);
		setMostInnerNorth(this);
		setMostInnerSouth(this);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		if (isSelected()){
//			drawSelection();
		}
		getRootNodeGraphic().getGraphics().setFont(getFont());
		getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		getRootNodeGraphic().getGraphics().drawString(
				getValue().toStringRepresentation(), getX1(), getY2());
	}
	
	public void drawSelection(){
		super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
		super.getRootNodeGraphic().getGraphics().fillRect(
				getX1(), getY1(), getX2() - getX1(), getY2() - getY1());
	}
	
	@Override
	public void drawCursor(){
		System.out.println("draw cursor in decimal");
		int xPos = findCursorXPos();
		
		super.getRootNodeGraphic().getGraphics().setColor(Color.BLACK);
		super.getRootNodeGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1()+ 5);
		
	}
	
	@Override
	public int getMaxCursorPos(){
		return getValue().toStringRepresentation().length();
	}
	
	public int findCursorXPos(){
		super.getRootNodeGraphic().getGraphics().setFont(getFont());
		String numberString = getValue().toStringRepresentation();
		return getX1() + super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
				numberString.substring(0, super.getRootNodeGraphic().getCursor().getPos()));
	}
	
	@Override
	public void setCursorPos(int xPixelPos){
		
		String numberString = getValue().toStringRepresentation();
		super.getRootNodeGraphic().getCursor().setValueGraphic(this);
		super.getRootNodeGraphic().getGraphics().setFont(getFont());
		
		if (xPixelPos <= getX1())
		{//if the pixel is in front of the graphic
			super.getRootNodeGraphic().getCursor().setPos(0);
			super.getRootNodeGraphic().getCursor().setValueGraphic(this);
			return;
		}
			
		else if (xPixelPos >= getX2())
		{// if the pixel is after the graphic
			super.getRootNodeGraphic().getCursor().setPos(numberString.length());
			super.getRootNodeGraphic().getCursor().setValueGraphic(this);
			return;
		}
		
		int startX, endX, xWidth;
		for (int i = 0; i < numberString.length() ; i++){
			
			startX = super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
					numberString.substring(0, i)) + getX1();
			endX = super.getRootNodeGraphic().getGraphics().getFontMetrics().stringWidth(
					numberString.substring(0, i + 1)) + getX1();
			xWidth = endX - startX;
			if (startX <= xPixelPos && endX >= xPixelPos)
			{//if the x position is inside of a character, check if it is on the first or second
				//half of the character and set the cursor accordingly
				if (endX - xPixelPos > xWidth/2){
					super.getRootNodeGraphic().getCursor().setPos( i );
				}
				else{
					super.getRootNodeGraphic().getCursor().setPos( i + 1 );
				}
				super.getRootNodeGraphic().getCursor().setValueGraphic(this);
				return;
			}
		}
	}
	
	@Override
	public void moveCursorWest() throws NodeException {
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
		if (super.getRootNodeGraphic().getCursor().getPos() < getValue().toStringRepresentation().length()){
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
	public void sendCursorInFromEast(int yPos, NodeGraphic vg)
	{
		super.getRootNodeGraphic().getCursor().setValueGraphic(this);
		super.getRootNodeGraphic().getCursor().setPos(getMaxCursorPos() - 1);
	}
	
	@Override
	public void sendCursorInFromWest(int yPos, NodeGraphic vg)
	{
		super.getRootNodeGraphic().getCursor().setValueGraphic(this);
		super.getRootNodeGraphic().getCursor().setPos(1);
	}

	@Override
	public void sendCursorInFromNorth(int xPos, NodeGraphic vg){
//		super.getCompExGraphic().getCursor().setValueGraphic(this);
		setCursorPos(xPos);
//		System.out.println("Dec graphic in from north, cursor: " +
//				super.getRootNodeGraphic().getCursor().getValueGraphic().getValue().toStringRepresentation());
	}
	
	@Override
	public void sendCursorInFromSouth(int xPos, NodeGraphic vg){
//		super.getCompExGraphic().getCursor().setValueGraphic(this);
		setCursorPos(xPos);
	}
	
	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {
		// TODO right now prints toString representation, need to make horizonal, and slash representations soon
		g.setFont(f);
		setFont(f);
		String s = getValue().toStringRepresentation();
		int[] size = new int[2];
		size[0] = getRootNodeGraphic().getStringWidth(s, f);
		size[1] = getRootNodeGraphic().getFontHeight(f);
		setUpperHeight((int) Math.round(size[1]/2.0));
		setLowerHeight(getUpperHeight());
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + size[0]);
		super.setY2(y1 + size[1]);
		return size;
	}

	@Override
	public int[] requestSize(Graphics g, Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<NodeGraphic> getComponents() {
		// TODO Auto-generated method stub
		return new Vector<NodeGraphic>();
	}
}
