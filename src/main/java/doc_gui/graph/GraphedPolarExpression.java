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

import expression.NodeException;

public class GraphedPolarExpression extends GraphWithExpression {

	/**
	 * The default constructor, set the equation equal to an empty string,
	 * makes it not currently graphing, integral and tracing values are
	 * false.
	 * @throws ParseException 
	 */
	
	public GraphedPolarExpression(String s, Graph g, Color c) throws NodeException {
		super(s, g, c);
	}
	
	/**
	 * Constructor that takes all attributes of a function.
	 * 
	 * @param exParser- associated 
	 * @param eqtn - string of equation
	 * @param ind - string of independent var
	 * @param dep - string of dependent var
	 * @param connected - boolean for points to be connected when graphed
	 * @param c - a color to display the function with
	 * @throws NodeException 
	 */
	public GraphedPolarExpression(Graph g, String eqtn, String ind, String dep, 
			boolean connected, Color c) throws NodeException {
		super( g, eqtn, ind, dep, connected, c);

	}
	
	public GraphedPolarExpression( Graph graph, Color color) {
		super(graph, color);
	}

//	@Override
//	public void draw(Graphics g){
//		
//		String eqtn = getFuncEqtn();
//		Var ind = getIndependentVar();
//		Var dep = getDependentVar();
//		Color color = getColor();
//		int angleUnits = getParser().getAngleUnits();
//		
//		double currR, currT, lastX, lastY, currX, currY;
//		g.setColor(color);
//		
//			ind.setValue(new Decimal(graph.THETA_MIN));
//			Expression expression = getParser().ParseExpression(eqtn);
//			expression.eval();
//			currR = dep.getValue().toDec().getValue();
//			currT = ind.getValue().toDec().getValue();
//			
//			lastX = currR * Math.cos(currT);
//			lastY = currR * Math.sin(currT);
//			int numCalcs = (int)((graph.THETA_MAX-graph.THETA_MIN)/graph.THETA_STEP);
//			for (int i = 1; i <= numCalcs; i++) {
//				ind.updateValue(graph.THETA_STEP);
//				expression.eval();
//				currR = dep.getValue().toDec().getValue();
//				currT = ind.getValue().toDec().getValue();
//				
//				if(angleUnits == 2)
//					currT *= (Math.PI/180);
//				if(angleUnits == 3)
//					currT *= (Math.PI/200);
//				currX = currR * Math.cos(currT);
//				currY = currR * Math.sin(currT);
//				//polPtOn(currT, currR, g);
//				drawLineSeg(lastX, lastY, currX, currY, color, g);
//				lastX = currX;
//				lastY = currY;
//			}
//
//	}

}
