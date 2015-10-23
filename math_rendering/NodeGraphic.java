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
import java.util.Vector;

import expression.EmptyValue;
import expression.Expression;
import expression.Identifier;
import expression.Node;
import expression.NodeException;
import expression.Number;
import expression.Operator;

public abstract class NodeGraphic<E extends Node> {
	
	//used to store the amount of space above the object,used to center later
	//objects added to ExpressionGraphic
	private int upperHeight, lowerHeight;
	
	private int x1, y1, x2, y2;
	
	public static int NO_VALID_CURSOR_POS = Integer.MAX_VALUE;
	
	private int numCursorPositions;
	
	protected E value;
	protected NodeGraphic north, south, east, west;
	private NodeGraphic mostInnerNorth, mostInnerSouth, mostInnerWest, mostInnerEast;
	
	//components that are children of this graphic
	private RootNodeGraphic rootNodeGraphic;
	private Font f;
	private boolean selected;
	
	public NodeGraphic(E v, RootNodeGraphic rootNodeGraphic){
		this.rootNodeGraphic = rootNodeGraphic;
		value = v;
	}
	
	public void drawCursor() throws NodeException {}
	
	public void setCursorPos(int xPixelPos) throws NodeException {}
	
	public void moveCursorWest() throws NodeException {}
	
	public void moveCursorEast() throws NodeException {}

	public void moveCursorNorth() throws NodeException {}
	
	public void moveCursorSouth() throws NodeException {}
	
	

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
	public void sendCursorInFromEast(int yPos, NodeGraphic vg) throws NodeException {}
	
	public void sendCursorInFromWest(int yPos, NodeGraphic vg) {}

	public void sendCursorInFromNorth(int xPos, NodeGraphic vg) throws NodeException {}
	
	public void sendCursorInFromSouth(int xPos, NodeGraphic vg) throws NodeException {}
	
	public int getMaxCursorPos() throws NodeException { return 0; }
	
	public void setSelectAllBelow(boolean b){
		setSelected(b);
		for (NodeGraphic v : getComponents()){
			v.setSelectAllBelow(b);
		}
	}
	
	public NodeGraphic findValueGraphic(Expression v){
		NodeGraphic found;
		for (NodeGraphic vg : getComponents()){
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
	
	public boolean hasDescendent(NodeGraphic vg){
		for (NodeGraphic v : getComponents()){
			if (v == vg ){
				return true;
			}
			else{
				if (v.hasDescendent(vg))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public NodeGraphic makeNodeGraphic(Node n) throws RenderException{
		
		if (n.displayParentheses()){
			return new ParenGraphic(n, getRootNodeGraphic());
		}
		else if (n instanceof Number){
			return new DecimalGraphic((Number)n, getRootNodeGraphic());
		}
		else if (n instanceof EmptyValue){
			return new NothingGraphic((EmptyValue)n, getRootNodeGraphic());
		}
		else if (n instanceof Expression){
			Expression ex = (Expression) n;
			if (ex.getOperator() instanceof Operator.BinaryOperator){
				if (ex.getOperator() instanceof Operator.Exponent){
					// skip the parenthesis around the upper child, as their smaller size
					// shows they must be evaluated first
					((Expression) n).getChild(1).setDisplayParentheses(false);
					return new ExponentGraphic((Expression)n, getRootNodeGraphic());
				}
				if (ex.getOperator() instanceof Operator.Division){
					return new DivisionGraphic((Expression)n, getRootNodeGraphic());
				}
				else if (ex.getOperator() instanceof Operator.Multiplication)
				{
					if ( ex.getChild(1) instanceof Identifier ||
							ex.getChild(1).displayParentheses() ||
							(ex.getChild(1) instanceof Expression && 
							(((Expression)ex.getChild(1)).getChild(0) instanceof Identifier ||
							((Expression)ex.getChild(1)).getOperator() instanceof Operator.UnaryFunction))
							){
						return new ImpliedMultGraphic((Expression)n, getRootNodeGraphic());
					}
					return new DotMultiplication((Expression)n, getRootNodeGraphic());
				}
				return new BinExpressionGraphic((Expression)n, getRootNodeGraphic());
			}
			else if (ex.getOperator() instanceof Operator.Function){
				if (ex.getOperator() instanceof Operator.UnaryFunction){
					if (ex.getOperator() instanceof Operator.SquareRoot){
						// have the radical skip the parenthesis
						((Expression) n).getChild(0).setDisplayParentheses(false);
						return new RadicalGraphic((Expression)n, getRootNodeGraphic());
					}
					else if (ex.getOperator() instanceof Operator.Negation ){
						return new NegationGraphic((Expression)n, getRootNodeGraphic());
					}
					else if (ex.getOperator() instanceof Operator.AbsoluteValue){
						((Expression) n).getChild(0).setDisplayParentheses(false);
						return new AbsoluteValueGraphic((Expression)n,getRootNodeGraphic());
					}
					return new UnaryExpressionGraphic((Expression)n, getRootNodeGraphic());
				}
			}
		}
		else if (n instanceof Identifier){
			return new IdentifierGraphic((Identifier) n, getRootNodeGraphic());
		}
		throw new RenderException("unsupported Node");
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
		for (NodeGraphic vg : getComponents()){
			vg.setX1(vg.getX1() + xChange);
			vg.setX2(vg.getX2() + xChange);
		}
		this.x2 += xChange;
		this.x1 = x1;
	}

	void shiftToY1(int y1) {
		int yChange = y1 - this.y1;
		for (NodeGraphic vg : getComponents()){
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

	public void setNorth(NodeGraphic north) {
		this.north = north;
//		System.out.println(getComponents());
		for (NodeGraphic vg : getComponents()){
//			System.out.println(vg.toString());
			vg.getMostInnerNorth().setNorth(north);
		}
	}

	public NodeGraphic getNorth() {
		return north;
	}

	public void setSouth(NodeGraphic south) {
		this.south = south;
//		System.out.println("setSouth: " + getValue().toStringRepresentation());
		for (NodeGraphic vg : getComponents()){
//			System.out.println("valGraphic class setChildSouth: " + vg.getValue().toStringRepresentation());
			vg.getMostInnerSouth().setSouth(south);
		}
	}

	public NodeGraphic getSouth() {
		return south;
	}

	public void setEast(NodeGraphic east) {
		this.east = east;
	}

	public NodeGraphic getEast() {
		return east;
	}

	public void setWest(NodeGraphic west) {
		this.west = west;
	}

	public NodeGraphic getWest() {
		return west;
	}
	
	public abstract int[] requestSize(Graphics g, Font f);
	
	public abstract void draw() throws NodeException;

	public int[] requestSize(Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootNodeGraphic getRootNodeGraphic() {
		return rootNodeGraphic;
	}


	public abstract int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception;

	public void drawAllBelow() throws NodeException{
		draw();
		for (NodeGraphic vg : getComponents()){
			vg.draw();
			vg.drawAllBelow();
		}
	}

	public abstract Vector<NodeGraphic> getComponents();

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

	public void setMostInnerNorth(NodeGraphic mostInnerNorth) {
		this.mostInnerNorth = mostInnerNorth;
	}

	public NodeGraphic getMostInnerNorth() {
		return mostInnerNorth;
	}

	public void setMostInnerSouth(NodeGraphic mostInnerSouth) {
		this.mostInnerSouth = mostInnerSouth;
	}

	public NodeGraphic getMostInnerSouth() {
		return mostInnerSouth;
	}

	public void setMostInnerWest(NodeGraphic mostInnerWest) {
		this.mostInnerWest = mostInnerWest;
	}

	public NodeGraphic getMostInnerWest() {
		return mostInnerWest;
	}

	public void setMostInnerEast(NodeGraphic mostInnerEast) {
		this.mostInnerEast = mostInnerEast;
	}

	public NodeGraphic getMostInnerEast() {
		return mostInnerEast;
	}

	public void setNumCursorPositions(int numCursorPositions) {
		this.numCursorPositions = numCursorPositions;
	}

	public int getNumCursorPositions() {
		return numCursorPositions;
	}
}
