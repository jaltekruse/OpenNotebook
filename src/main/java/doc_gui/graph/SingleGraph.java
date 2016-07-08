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
		xVals = new Vector<>();
		yVals = new Vector<>();
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
		xVals = new Vector<>();
		yVals = new Vector<>();
	}
	
	public boolean hasFocus(){
		return hasFocus;
	}
	
	public void setfocus(boolean b){
		hasFocus = b;
	}
}
