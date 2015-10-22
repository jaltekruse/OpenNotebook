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

import tree.BinExpression;
import tree.Fraction;
import tree.Identifier;
import tree.Operator;
import tree.Expression;
import tree.MissingValue;
import tree.UnaryExpression;
import tree.Decimal;

public abstract class ValueGraphic<E extends Expression> {
	
	//used to store the amount of space above the object,used to center later
	//objects added to ExpressionGraphic
	private int upperHeight, lowerHeight;
	
	private int x1, y1, x2, y2;
	
	public static int NO_VALID_CURSOR_POS = Integer.MAX_VALUE;
	
	private int numCursorPositions;
	
	protected E value;
	private ValueGraphic north, south, east, west;
	private ValueGraphic mostInnerNorth, mostInnerSouth, mostInnerWest, mostInnerEast;
	
	//components that are children of this graphic
	private CompleteExpressionGraphic compExGraphic;
	private Font f;
	private boolean selected;
	
	public ValueGraphic(E v, CompleteExpressionGraphic compExGraphic){
		this.compExGraphic = compExGraphic;
		value = v;
	}
	
	public void drawCursor(){}
	
	public void setCursorPos(int xPixelPos){}
	
	public void moveCursorWest(){}
	
	public void moveCursorEast(){}
	
	public void moveCursorNorth(){}
	
	public void moveCursorSouth(){}
	
	

	/**
	 * 	This value is assumed to be right up against its neighbor, so it jumps right
	 * to the position one in from the end.    e.g.    3.23|+432.3
	 * where the cursor was previously in the Addition, now it should move to
	 * 3.2|3+432.3, which is one less than the last position in decimalGraphic
	 * Therefore there are redundancies in the system, each end of each graphic has
	 * to be able to hold a cursor
	 * 
	 * @param yPos
	 */
	public void sendCursorInFromEast(int yPos, ValueGraphic vg) {}
	
	public void sendCursorInFromWest(int yPos, ValueGraphic vg) {}

	public void sendCursorInFromNorth(int xPos, ValueGraphic vg) {}
	
	public void sendCursorInFromSouth(int xPos, ValueGraphic vg) {}
	
	public int getMaxCursorPos() { return 0; }
	
	public void setSelectAllBelow(boolean b){
		setSelected(b);
		for (ValueGraphic v : getComponents()){
			v.setSelectAllBelow(b);
		}
	}
	
	public ValueGraphic findValueGraphic(Expression v){
		ValueGraphic found;
		for (ValueGraphic vg : getComponents()){
			if (v.equals( vg.getValue() )){
				return vg;
			}
			else{
				found = vg.findValueGraphic(v);
				if (found != null){
					return found;
				}
			}
		}
		return null;
	}
	
	public boolean containedBelow(ValueGraphic vg){
		for (ValueGraphic v : getComponents()){
			if (v.equals( vg )){
				return true;
			}
			else{
				if (v.containedBelow(vg))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public ValueGraphic makeValueGraphic(Expression v) throws RenderException{
		if (v instanceof Fraction)
		{
			return new FractionGraphic((Fraction)v, getCompExGraphic());
		}
		if (v instanceof Decimal){
			return new DecimalGraphic((Decimal)v, getCompExGraphic());
		}
		else if (v instanceof Identifier)
		{
			return new ValueWithNameGraphic(v, getCompExGraphic());
		}
		else if (v instanceof MissingValue){
			return new NothingGraphic((MissingValue)v, getCompExGraphic());
		}
		else if (v instanceof BinExpression)
		{
			if (((BinExpression)v).getOp() == Operator.DIVIDE){
				return new DivisionGraphic((BinExpression)v, getCompExGraphic());
			}
			else if (((BinExpression)v).getOp() == Operator.POWER){
				return new ExponentGraphic((BinExpression)v, getCompExGraphic());
			}
			else if (((BinExpression)v).getOp() == Operator.IMP_MULT){
				return new ImpliedMultGraphic((BinExpression)v, getCompExGraphic());
			}
			else{
				return new BinExpressionGraphic((BinExpression)v, getCompExGraphic());
			}
			
		}
		else if (v instanceof UnaryExpression){
			if (((UnaryExpression)v).getOp() == Operator.PAREN)
			{
				return new ParenGraphic(((UnaryExpression)v), getCompExGraphic());
			}
			else if (((UnaryExpression)v).getOp() == Operator.ABS)
			{
				return new AbsoluteValueGraphic(((UnaryExpression)v), getCompExGraphic());
			}
			else if (((UnaryExpression)v).getOp() == Operator.NEG)
			{
				return new NegationGraphic(((UnaryExpression)v), getCompExGraphic());
			}
			else if (((UnaryExpression)v).getOp() == Operator.SQRT)
			{
				return new RadicalGraphic(((UnaryExpression)v), getCompExGraphic());
			}
			else if (((UnaryExpression)v).getOp() == Operator.FACT)
			{
				return new UnaryPostGraphic(((UnaryExpression)v), getCompExGraphic());
			}
			return new UnaryExpressionGraphic((UnaryExpression)v, getCompExGraphic());
		}
		throw new RenderException("unsupported Value");
	}
	
	void setX1(int x1) {
		this.x1 = x1;
	}

	//decide on what visibility these methods should have
	public int getX1() {
		return x1;
	}
	
	void setX2(int x2) {
		this.x2 = x2;
	}

	public int getX2() {
		return x2;
	}

	void setY1(int y1) {
		this.y1 = y1;
	}

	public int getY1() {
		return y1;
	}
	
	void setY2(int y2) {
		this.y2 = y2;
	}

	public int getY2() {
		return y2;
	}

	void shiftToX1(int x1) {
		int xChange = x1 - this.x1;
		for (ValueGraphic vg : getComponents()){
			vg.setX1(vg.getX1() + xChange);
			vg.setX2(vg.getX2() + xChange);
		}
		this.x2 += xChange;
		this.x1 = x1;
	}

	void shiftToY1(int y1) {
		int yChange = y1 - this.y1;
		for (ValueGraphic vg : getComponents()){
			vg.setY1(vg.getY1() + yChange);
			vg.setY2(vg.getY2() + yChange);
		}
		this.y2 += yChange;
		this.y1 = y1;
	}
	
	int getHeight(){
		return y2 - y1;
	}
	
	int getWidth(){
		return x2 - x1;
	}
	
	void setValue(E v){
		value = v;
	}

	public E getValue(){
		return value;
	}

	public void setNorth(ValueGraphic north) {
		this.north = north;
		for (ValueGraphic vg : getComponents()){
			vg.setNorth(north);
		}
	}

	public ValueGraphic getNorth() {
		return north;
	}

	public void setSouth(ValueGraphic south) {
		this.south = south;
		for (ValueGraphic vg : getComponents()){
			vg.setSouth(south);
		}
	}

	public ValueGraphic getSouth() {
		return south;
	}

	public void setEast(ValueGraphic east) {
		this.east = east;
	}

	public ValueGraphic getEast() {
		return east;
	}

	public void setWest(ValueGraphic west) {
		this.west = west;
	}

	public ValueGraphic getWest() {
		return west;
	}
	
	public abstract int[] requestSize(Graphics g, Font f);
	
	public abstract void draw();

	public int[] requestSize(Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	public CompleteExpressionGraphic getCompExGraphic() {
		return compExGraphic;
	}


	public abstract int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception;

	public void drawAllBelow(){
		draw();
		for (ValueGraphic vg : getComponents()){
			vg.draw();
			vg.drawAllBelow();
		}
	}

	public abstract Vector<ValueGraphic> getComponents();

	public void setUpperHeight(int upperHeight) {
		this.upperHeight = upperHeight;
	}

	public int getUpperHeight() {
		return upperHeight;
	}

	public void setLowerHeight(int lowerHeight) {
		this.lowerHeight = lowerHeight;
	}

	public int getLowerHeight() {
		return lowerHeight;
	}

	public void setFont(Font f) {
		this.f = f;
	}

	public Font getFont() {
		return f;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public Color getSelectedColor(){
		return Color.gray.brighter();
	}

	public void setMostInnerNorth(ValueGraphic mostInnerNorth) {
		this.mostInnerNorth = mostInnerNorth;
	}

	public ValueGraphic getMostInnerNorth() {
		return mostInnerNorth;
	}

	public void setMostInnerSouth(ValueGraphic mostInnerSouth) {
		this.mostInnerSouth = mostInnerSouth;
	}

	public ValueGraphic getMostInnerSouth() {
		return mostInnerSouth;
	}

	public void setMostInnerWest(ValueGraphic mostInnerWest) {
		this.mostInnerWest = mostInnerWest;
	}

	public ValueGraphic getMostInnerWest() {
		return mostInnerWest;
	}

	public void setMostInnerEast(ValueGraphic mostInnerEast) {
		this.mostInnerEast = mostInnerEast;
	}

	public ValueGraphic getMostInnerEast() {
		return mostInnerEast;
	}

	public void setNumCursorPositions(int numCursorPositions) {
		this.numCursorPositions = numCursorPositions;
	}

	public int getNumCursorPositions() {
		return numCursorPositions;
	}
}
