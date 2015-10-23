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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import expression.Expression;
import expression.Identifier;
import expression.Node;
import expression.NodeException;
import expression.Number;
import expression.Operator;

public class GraphedCartFunction extends GraphWithExpression {

	/**
	 * The default constructor, set the equation equal to an empty string,
	 * makes it not currently graphing, integral and tracing values are
	 * false.
	 * @throws NodeException 
	 */

	public GraphedCartFunction(String s, Graph g, Color c) throws NodeException{
		super(s, g, c);
	}

	/**
	 * Constructor that takes all attributes of a function.
	 * 
	 * @param exParser - associated 
	 * @param eqtn - string of equation
	 * @param ind - string of independent var
	 * @param dep - string of dependent var
	 * @param connected - boolean for points to be connected when graphed
	 * @param c - a color to display the function with
	 * @throws NodeException 
	 */
	public GraphedCartFunction( Graph g, String eqtn, String ind, String dep, 
			boolean connected, Color c) throws NodeException {
		super(g, eqtn, ind, dep, connected, c);

	}

	public GraphedCartFunction( Graph graph, Color color) {
		super( graph, color);
	}

	@Override
	public void draw(Graphics g){

		//used to temporarily store the value stored in the independent and dependent vars,
		//this will allow it to be restored after graphing, so that if in the terminal a
		//value was assigned to x, it will not be overriden by the action of graphing
		//		Number xVal = getIndependentVar().getValue();
		//		Number yVal = getDependentVar().getValue();

		super.clearPts();
		Graphics2D g2d = ((Graphics2D)g);

		g.setColor(getColor());
		if (hasFocus()){
			graph.LINE_SIZE = 3;
		}
		else{
			graph.LINE_SIZE = 2;
		}

		int tempLineSize = graph.LINE_SIZE;

		double lastX = 0, lastY = 0, currX = 0, currY = 0;
		
		Node origionalEx = null;
		Node ex;
		try {
			lastX = graph.X_MIN;
			origionalEx = Node.parseNode(getFuncEqtn());
			ex = origionalEx.cloneNode();
			ex = ex.replace(new Identifier(getIndependentVar()), new Number(lastX));
			ex = ex.replace("\u03C0", new Number(Math.PI));
			ex = ex.replace("e", new Number(Math.E));
			ex = ex.numericSimplify();
			if ( ex instanceof Expression){
				if ( ((Expression)ex).getOperator() instanceof Operator.Equals){
					if ( ((Expression)ex).getChild(1) instanceof Number){
						lastY = ((Number)((Expression)ex).getChild(1)).getValue();
					}
				}
			}
			if (gridxToScreen(lastX) <= graph.X_SIZE + graph.X_PIC_ORIGIN &&  
					gridxToScreen(lastX) >= + graph.X_PIC_ORIGIN
					&& gridyToScreen(lastY) <= graph.Y_SIZE + graph.Y_PIC_ORIGIN 
					&& gridyToScreen(lastY) >= + graph.Y_PIC_ORIGIN)
			{//if the current point is on the screen, add it to the list of points
				addPt(gridxToScreen(lastX), gridyToScreen(lastY));
			}
		} catch (Exception e1) {
			currX = graph.X_MIN;
			lastY = graph.Y_MIN;
			;
		}
		currX = graph.X_MIN;

		boolean validExpression;

		for (int i = 1; i < graph.X_SIZE; i += 1) {
			validExpression = false;
			try {
				currX = currX + graph.X_PIXEL;
				ex = origionalEx.cloneNode();
				ex = ex.replace(getIndependentVar(), new Number(currX));
				ex = ex.replace("\u03C0", new Number(Math.PI));
				ex = ex.replace("e", new Number(Math.E));
				ex = ex.numericSimplify();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lastX = currX;
				lastY = graph.Y_MIN;
				continue;
			}
			if ( ex instanceof Expression){
				if ( ((Expression)ex).getOperator() instanceof Operator.Equals){
					if ( ((Expression)ex).getChild(1) instanceof Number){
						currY = ((Number)((Expression)ex).getChild(1)).getValue();
						validExpression = true;
					}
				}
			}
			else if ( ! validExpression ){
				lastX = currX;
				lastY = graph.Y_MIN;
				continue;
			}

			if (gridxToScreen(currX) <= graph.X_SIZE + graph.X_PIC_ORIGIN
					&&  gridxToScreen(currX) >= graph.X_PIC_ORIGIN
					&& gridyToScreen(currY) <= graph.Y_SIZE + graph.Y_PIC_ORIGIN
					&& gridyToScreen(currY) >= graph.Y_PIC_ORIGIN)
			{//if the current point is on the screen, add it to the list of points
				if (lastY <= graph.Y_MIN){
					addPt(gridxToScreen(lastX), graph.Y_SIZE + graph.Y_PIC_ORIGIN);
				}
				if (lastY >= graph.Y_MAX){
					addPt(gridxToScreen(lastX), 0 + graph.Y_PIC_ORIGIN);
				}
				addPt(gridxToScreen(currX), gridyToScreen(currY));
			}
			else if (gridxToScreen(lastX) <= graph.X_SIZE + graph.X_PIC_ORIGIN
					&&  gridxToScreen(lastX) >= graph.X_PIC_ORIGIN
					&& gridyToScreen(lastY) <= graph.Y_SIZE + graph.Y_PIC_ORIGIN
					&& gridyToScreen(lastY) >= graph.Y_PIC_ORIGIN)
			{//if the last point is on the screen, add the correct boundary for this point to the list
				addPt(gridxToScreen(lastX), gridyToScreen(lastY));
				if (currY <= graph.Y_MIN){
					addPt(gridxToScreen(currX), graph.Y_SIZE + graph.Y_PIC_ORIGIN);
				}
				if (currY >= graph.Y_MAX){
					addPt(gridxToScreen(currX), 0 + graph.Y_PIC_ORIGIN);
				}
			}
			else if (lastY >= graph.Y_MAX && currY <= graph.Y_MIN)
			{//if the last point was off the the top of the screen, and this one is off
				//the bottom, add the two to the list of points
				addPt(gridxToScreen(lastX), graph.Y_SIZE + graph.Y_PIC_ORIGIN);
				addPt(gridxToScreen(currX), 0 + graph.Y_PIC_ORIGIN);
			}
			else if (currY >= graph.Y_MAX && lastY <= graph.Y_MIN)
			{//if the last point was off the the bottom of the screen, and this one is off
				//the top, add the two to the list of points
				addPt(gridxToScreen(lastX), 0 + graph.Y_PIC_ORIGIN);
				addPt(gridxToScreen(currX), graph.Y_SIZE + graph.Y_PIC_ORIGIN);
			}
			
			if (isConnected()){
				drawLineSeg(lastX, lastY, currX, currY, getColor(), g);
			}

			lastX = currX;
			lastY = currY;
		}

//		g2d.setStroke(new BasicStroke(graph.LINE_SIZE * graph.DOC_ZOOM_LEVEL,
//				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//		if ( this.getxVals().size() > 0){
//			GeneralPath polyline = 
//					new GeneralPath(GeneralPath.WIND_EVEN_ODD, this.getxVals().size());
//			polyline.moveTo (this.getxVals().get(0), this.getyVals().get(0));
//			for (int i = 1; i < this.getxVals().size(); i++) {
//				polyline.lineTo( getxVals().get(i), getyVals().get(i));
//			};
//			g2d.draw(polyline);
//		}
		graph.LINE_SIZE = 2;
		g2d.setStroke(new BasicStroke(1));
	}

}
