/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import expression.NodeException;

public abstract class SingleGraph extends GraphComponent {
	
	private Vector<Integer> xVals;
	private Vector<Integer> yVals;
	boolean hasFocus;
	private Color color;
	
	public SingleGraph(Graph g){
		super(g);
		xVals = new Vector<Integer>();
		yVals = new Vector<Integer>();
	}

	@Override
	public abstract void draw(Graphics g) throws NodeException;

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public void setxVals(Vector<Integer> xVals) {
		this.xVals = xVals;
	}

	public Vector<Integer> getxVals() {
		return xVals;
	}

	public void setyVals(Vector<Integer> yVals) {
		this.yVals = yVals;
	}

	public Vector<Integer> getyVals() {
		return yVals;
	}
	
	protected void addPt(Integer x, Integer y){
		xVals.add(x);
		yVals.add(y);
	}
	
	protected void clearPts(){
		xVals = new Vector<Integer>();
		yVals = new Vector<Integer>();
	}
	
	public boolean hasFocus(){
		return hasFocus;
	}
	
	public void setfocus(boolean b){
		hasFocus = b;
	}
}
