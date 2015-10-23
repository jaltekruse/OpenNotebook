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

import expression.NodeException;

public abstract class GraphComponent {

	protected Graph graph;
	
	public GraphComponent(Graph g){
		graph = g;
	}
	
	protected int gridxToScreen(double x){
		return graph.gridxToScreen(x);
	}
	
	protected double screenxToGrid(int x){
		return graph.screenxToGrid(x);
	}
	
	protected double screenyToGrid(int y){
		return graph.screenyToGrid(y);
	}
	
	protected int gridyToScreen(double y){
		return graph.gridyToScreen(y);
	}
	
	protected void setLineSize(int sizeInPixels) {
		graph.LINE_SIZE = sizeInPixels;
	}
	
	protected void drawTracer(double x, double y, Graphics g) {
		g.drawOval(gridxToScreen(x) - 5, gridyToScreen(y) - 5, 10, 10);
		g.fillOval(gridxToScreen(x) - 3, gridyToScreen(y) - 3, 6, 6);
	}
	
	protected void ptOn(double x, double y, Graphics g) {
		if (x <= graph.X_MAX && x >= graph.X_MIN && y <= graph.Y_MAX && y >= graph.Y_MIN) {
			
			int thickness = (int) (graph.LINE_SIZE * graph.DOC_ZOOM_LEVEL);
			
			g.fillRect(gridxToScreen(x) - thickness/2, gridyToScreen(y) - thickness/2
					, thickness, thickness);
//			if (graph.LINE_SIZE == 2){
//				Color c = g.getColor();
////				if (g.getColor().equals(Color.black))
////				{
////					g.setColor(Color.gray.brighter());
////				}
////				else
////				{
//					g.setColor(g.getColor().brighter());
////				}
//				g.fillRect((int)Math.round((a - graph.X_MIN) / graph.X_PIXEL - graph.LINE_SIZE/2.0) - 1,
//						(int)Math.round((graph.Y_SIZE - graph.LINE_SIZE/2.0) - (b - graph.Y_MIN) / graph.Y_PIXEL) - 1,
//						3, 3);
//				g.setColor(c);
//			}
//			g.fillRect((int)Math.round((a - graph.X_MIN) / graph.X_PIXEL - graph.LINE_SIZE/2.0),
//					(int)Math.round((graph.Y_SIZE - graph.LINE_SIZE/2.0) - (b - graph.Y_MIN) / graph.Y_PIXEL),
//					1, 1);
		}
	}
	
	protected static boolean numbersClose(double num1, double num2){
		if ( Math.abs( num1 - num2) < .00001){
			return true;
		}
		return false;
	}
	
	public static String doubleToString(double d, double round){
		String ptText;
		if ( numbersClose(d % round, 0)){
			d = d / round * round;
		}
		else if ( numbersClose( d % round, round)){
			d = ((d / round) + 1 ) * round;
		}
		if ( ((Double)d).toString().length() > 5){
			ptText = String.format("%.4G", d);
		}
		else{
			if ( (int) d == d){
				ptText = (int) d + ""; 
			}
			else{
				ptText = d + "";
			}
		}
		return ptText;
	}
	
	protected void drawTangent(double x, double y, double m, Color c, Graphics g){
		int length = 15;
		int screenX = gridxToScreen(x);
		int screenY = gridyToScreen(y);
		double xChange = 0, yChange = 0;
		
		double angle = Math.atan( Math.abs(m));
		
//		System.out.println();
//		System.out.println("class graphComponent (drawTan):");
//		System.out.println("angle (radians): " + angle);
		
		xChange = (Math.cos(angle) * length);
		yChange = (Math.sin(angle) * length);
		
		graph.LINE_SIZE = 3;
		
		if (m > 0){
			drawLineSeg(x, y, x + xChange * graph.X_PIXEL, y + yChange * graph.Y_PIXEL, c , g);
			drawLineSeg(x, y, x - xChange * graph.X_PIXEL, y - yChange * graph.Y_PIXEL, c , g);
		}
		else{
			drawLineSeg(x, y, x + xChange * graph.X_PIXEL, y - yChange * graph.Y_PIXEL, c , g);
			drawLineSeg(x, y, x - xChange * graph.X_PIXEL, y + yChange * graph.Y_PIXEL, c , g);
		}
	}

	protected void drawLineSeg(double x1, double y1, double x2, double y2,
			Color color, Graphics g) {
		//right now this ignores the LINE_SIZE currently set, but it draws much faster than before
		//I'll modify it to handle line thickness soon
		
		g.setColor(color);
		
		int screenX1 = gridxToScreen(x1);
		int screenX2 = gridxToScreen(x2);
		int screenY1 = gridyToScreen(y1);
		int screenY2 = gridyToScreen(y2);
		
		double slope = (y1-y2)/(x1-x2);
		
		if (Double.isNaN(x1) || Double.isNaN(x2) || Double.isNaN(y1) || Double.isNaN(y2)){
			return;
		}
		if (x1 > graph.X_MAX){
			screenX1 = graph.X_PIC_ORIGIN + graph.X_SIZE;
			screenY1 = gridyToScreen(y1 - slope * (x1 - graph.X_MAX));
		}
		if 	(x2 > graph.X_MAX){
			screenX2 = graph.X_PIC_ORIGIN + graph.X_SIZE;
			screenY2 = gridyToScreen(y2 - slope * (x2 - graph.X_MAX));
			if (screenX2 == screenX1){//they were both offscreen
				return;
			}
		}
		if (x1 < graph.X_MIN){
			screenX1 = graph.X_PIC_ORIGIN;
			screenY1 = gridyToScreen(y1 + slope * (graph.X_MIN - x1));
		}
		if 	(x2 < graph.X_MIN){
			screenX2 = graph.X_PIC_ORIGIN;
			screenY2 = gridyToScreen(y2 + slope * (graph.X_MIN - x2));
			if (screenX2 == screenX1){//they were both offscreen
				return;
			}
		}
		
		if (y1 < graph.Y_MIN){
			screenY1 = graph.Y_PIC_ORIGIN + graph.Y_SIZE;
			//screenX1 = gridxToScreen(x1 + slope * (graph.Y_MIN - y1));
		}
		if 	(y2 < graph.Y_MIN){
			screenY2 = graph.Y_PIC_ORIGIN + graph.Y_SIZE;
			//screenX2 = gridxToScreen(x2 + slope * (graph.Y_MIN - y2));
			if (screenY2 == screenY1){//they were both offscreen
				return;
			}
		}
		
		if (y1 > graph.Y_MAX){
			screenY1 = graph.Y_PIC_ORIGIN;
		}
		if 	(y2 > graph.Y_MAX){
			screenY2 = graph.Y_PIC_ORIGIN;
			if (screenY2 == screenY1){//they were both offscreen
				return;
			}
		}
		
		int thickness = (int) (graph.LINE_SIZE * graph.DOC_ZOOM_LEVEL);
		Graphics2D g2d = (Graphics2D)g; 
		g2d.setStroke(new BasicStroke(thickness));
		
		g.setColor(color);	
		
		g.drawLine(screenX1, screenY1, screenX2, screenY2);
		g2d.setStroke(new BasicStroke());
	}
	
	public abstract void draw(Graphics g) throws NodeException;
	
	public Graph getGraph(){
		return graph;
	}
}
