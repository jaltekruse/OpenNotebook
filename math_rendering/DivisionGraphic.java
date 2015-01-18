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
import java.awt.FontMetrics;
import java.awt.Graphics;

import tree.BinExpression;
import tree.Fraction;
import tree.Operator;
import tree.Expression;

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
	
	public DivisionGraphic(BinExpression b, CompleteExpressionGraphic gr) {
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
			super.getCompExGraphic().getGraphics().drawString(getValue().getOp().getSymbol(),
					symbolX1, symbolY2);
		}
		else if (style == Style.HORIZONTAL){
			if (isSelected()){
				super.getCompExGraphic().getGraphics().setColor(getSelectedColor());
				super.getCompExGraphic().getGraphics().fillRect(symbolX1, 
						symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
				super.getCompExGraphic().getGraphics().setColor(Color.black);
			}
			super.getCompExGraphic().getGraphics().setStroke(new BasicStroke(
					(int) (1 * super.getCompExGraphic().DOC_ZOOM_LEVEL)));
			super.getCompExGraphic().getGraphics().drawLine(symbolX1, symbolY1 + spaceAroundBar + 1, symbolX2, 
					symbolY1 + spaceAroundBar + 1); 
			super.getCompExGraphic().getGraphics().setStroke(new BasicStroke());
		}
	}
	
	public void drawCursor(){
		String numberString = getValue().toString();
		
		int xPos = findCursorXPos();
		
		super.getCompExGraphic().getGraphics().setColor(Color.BLACK);
		super.getCompExGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1()+ 5);
		
	}
	
	public void setCursorPos(int xPixelPos){
		//cursor does not exist in this graphic, send to upper child
		super.getLeftGraphic().getMostInnerSouth().setCursorPos(xPixelPos);
	}
	
	public void moveCursorWest(){
		if (super.getCompExGraphic().getCursor().getPos() == 1){
			super.getCompExGraphic().getCursor().setValueGraphic(
					getLeftGraphic().getMostInnerEast());
			super.getCompExGraphic().getCursor().setPos(
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
	
	public void moveCursorEast(){
		if (super.getCompExGraphic().getCursor().getPos() == 0){
			super.getCompExGraphic().getCursor().setValueGraphic(
					getLeftGraphic().getMostInnerWest());
			super.getCompExGraphic().getCursor().setPos(0); 
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
	
	public void moveCursorNorth(){
		if (getNorth() == null)
		{
			System.out.println("nothing to north");
			return;
		}
		else
		{
			getNorth().getMostInnerNorth().sendCursorInFromSouth(findCursorXPos(), this);
			return;
		}
	}
	
	public int findCursorXPos() {
		// TODO Auto-generated method stub
		return getX1() + super.getCompExGraphic().getCursor().getPos() * (getX2() - getX1()); 
	}

	public void moveCursorSouth(){
		if (getSouth() == null)
		{
			System.out.println("nothing to south");
			return;
		}
		else
		{
			getSouth().getMostInnerSouth().sendCursorInFromNorth(findCursorXPos(), this);
			return;
		}
	}
	
	public void sendCursorInFromEast(int yPos, ValueGraphic vg)
	{
		if (super.containedBelow(vg)){
			System.out.println("move into division from east, containedbelow");
			super.getCompExGraphic().getCursor().setValueGraphic(this);
			super.getCompExGraphic().getCursor().setPos(0);
		}
		else{
			super.getCompExGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerEast());
			super.getCompExGraphic().getCursor().setPos(getLeftGraphic().getMostInnerEast().getMaxCursorPos());
		}
//		getLeftGraphic().sendCursorInFromEast(yPos);
	}
	
	public void sendCursorInFromWest(int yPos, ValueGraphic vg)
	{
		if (super.containedBelow(vg)){
			System.out.println("move into division from west, containedbelow");
			super.getCompExGraphic().getCursor().setValueGraphic(this);
			super.getCompExGraphic().getCursor().setPos(getMaxCursorPos());
		}
		else{
			super.getCompExGraphic().getCursor().setValueGraphic(getLeftGraphic().getMostInnerWest());
			super.getCompExGraphic().getCursor().setPos(0);
		}
//		getLeftGraphic().sendCursorInFromWest(yPos);
	}
	
	public void sendCursorInFromNorth(int xPos, ValueGraphic vg){
		System.out.println("Division send in from north, xPos: " + xPos);
		getRightGraphic().getMostInnerNorth().sendCursorInFromNorth(xPos, this);
	}
	
	public void sendCursorInFromSouth(int xPos, ValueGraphic vg){
		System.out.println("division send from south");
		getLeftGraphic().getMostInnerSouth().sendCursorInFromSouth(xPos, this);
	}
	
	/**
	 * The cursor can either be before or after the bar.
	 */
	public int getMaxCursorPos(){
		return 1;
	}
	
	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {
		// TODO Auto-generated method stub
		
		g.setFont(f);
		setFont(f);
		
		sizeOverhang = (int) (3 * getCompExGraphic().DOC_ZOOM_LEVEL);
		spaceAroundBar = (int) (5 * getCompExGraphic().DOC_ZOOM_LEVEL);
		
		Expression tempLeft = ((BinExpression)super.getValue()).getLeftChild();
		Expression tempRight = ((BinExpression)super.getValue()).getRightChild();
		ValueGraphic leftValGraphic = null;
		ValueGraphic rightValGraphic = null; 
		int[] rightSize = {0,0};
		int[] leftSize = {0, 0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		if (style == Style.SLASH)
		{
			
			leftValGraphic = makeValueGraphic(tempLeft);
			
			super.getCompExGraphic().getComponents().add(leftValGraphic);
			leftSize = leftValGraphic.requestSize(g, f, x1, y1);
			symbolSize[0] = getCompExGraphic().getStringWidth(getValue().getOp().getSymbol(), f);
			symbolSize[1] = getCompExGraphic().getFontHeight(f);
			symbolX1 = x1 + leftSize[0];
			System.out.println(leftSize[1]/2.0);
			symbolY1 = y1 + ((int)Math.round(leftSize[1]/2.0) - (int) (Math.round(symbolSize[1])/2.0));
			symbolX2 = x1 + leftSize[0] + symbolSize[0];
			symbolY2 = symbolSize[1] + symbolY1;
			
			rightValGraphic = makeValueGraphic(tempRight);
			
			rightSize = rightValGraphic.requestSize(g, f, symbolX2, y1);
			super.getCompExGraphic().getComponents().add(rightValGraphic);
			
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
			
			leftValGraphic = makeValueGraphic(tempLeft);
			
			leftSize = leftValGraphic.requestSize(g, f, x1, y1);
			super.getCompExGraphic().getComponents().add(leftValGraphic);
			
			//other if statements for checking the left, decimal, imaginary, other val types
			
			rightValGraphic = makeValueGraphic(tempRight);
			
			rightSize = rightValGraphic.requestSize(g, f, symbolX2, y1);
			super.getCompExGraphic().getComponents().add(rightValGraphic);
			setHeightNumer(leftSize[1]);
			setHeightDenom(rightSize[1]);
			
			//set the west and east fields for inside an outside of the expression
			setMostInnerWest(this);
			System.out.println("left child of div:" + leftValGraphic.getMostInnerSouth().getComponents());
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
			System.out.println("symbolY2: " + symbolY2);
			
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
