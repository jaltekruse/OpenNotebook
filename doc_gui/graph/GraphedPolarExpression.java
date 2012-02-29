/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
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
	
	public GraphedPolarExpression(String s, Graph g, Color c) {
		super(s, ep, g, c);
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
		// TODO Auto-generated constructor stub
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
