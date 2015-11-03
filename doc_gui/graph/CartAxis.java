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
import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;

public class CartAxis extends GraphComponent{

	public CartAxis(Graph g) {
		super(g);
	}

	@Override
	public void draw(Graphics g) {

		Font oldFont = g.getFont();

		g.setFont(g.getFont().deriveFont(graph.FONT_SIZE * graph.DOC_ZOOM_LEVEL));

		// finds the fist factor of the graph.Y_STEP on the screen
		// used to draw the first dash mark on the y-axis
		double tempY = (int) (graph.Y_MIN / graph.Y_STEP);
		tempY *= graph.Y_STEP;
		int width;
		int height = (int) ( g.getFontMetrics().getHeight() * (.6));
		int numberAndAxisSpace = (int) ( 5 * graph.DOC_ZOOM_LEVEL );
		int numberInset = (int) (1 * graph.DOC_ZOOM_LEVEL );
		Vector<Double> yNumbers = new Vector<Double>();

		while (tempY <= graph.Y_MAX) 
		{//there are still more dashes to draw
			if (graph.SHOW_GRID){
				setLineSize(1);
				drawLineSeg(graph.X_MIN, tempY, graph.X_MAX, tempY, Color.GRAY, g);
			}
			if(tempY%(graph.Y_STEP) == 0 && tempY != 0){
				yNumbers.add(tempY);
			}
			if (graph.SHOW_AXIS){
				setLineSize(graph.LINE_SIZE_DEFAULT);
				g.setColor(Color.BLACK);
				if (graph.X_MIN <= 0 && graph.X_MAX >= 0){
					drawLineSeg(1.5 * graph.LINE_SIZE * graph.DOC_ZOOM_LEVEL * graph.X_PIXEL, tempY, -1.5 * graph.LINE_SIZE
							* graph.DOC_ZOOM_LEVEL * graph.X_PIXEL, tempY, Color.BLACK, g);
				}
				else if (graph.X_MIN >= 0){
					drawLineSeg(0, tempY, 2 * graph.LINE_SIZE * graph.X_PIXEL, tempY, Color.BLACK, g);
				}
				else{
					ptOn(graph.X_MAX - graph.LINE_SIZE_DEFAULT * graph.X_PIXEL, tempY, g);
				}
			}

			tempY += graph.Y_STEP;
		}

		// finds the fist factor of the graph.X_STEP on the screen
		// used to draw the first dash mark on the x-axis
		double tempX = (int) (graph.X_MIN / graph.X_STEP);
		tempX *= graph.X_STEP;
		Vector<Double> xNumbers = new Vector<Double>();

		int tempWidth;

		tempX = (int) (graph.X_MIN / graph.X_STEP);
		tempX *= graph.X_STEP;
		while (tempX <= graph.X_MAX) {
			if (graph.SHOW_GRID){
				setLineSize(1);
				drawLineSeg(tempX, graph.Y_MIN, tempX, graph.Y_MAX, Color.GRAY, g);
			}
			if(tempX%(graph.X_STEP) == 0 && tempX != 0)
			{//if its reached a place where a number should be drawn
				xNumbers.add(tempX);
			}

			if (graph.SHOW_AXIS){
				setLineSize(graph.LINE_SIZE_DEFAULT);
				g.setColor(Color.BLACK);
				if (graph.Y_MIN <= 0 && graph.Y_MAX >= 0) {
					drawLineSeg(tempX, 1.5 * graph.LINE_SIZE * graph.DOC_ZOOM_LEVEL * graph.Y_PIXEL, tempX, 
							-1.5 * graph.LINE_SIZE * graph.DOC_ZOOM_LEVEL * graph.Y_PIXEL, Color.BLACK, g);
				}
				else if( graph.Y_MIN >= 0){
					ptOn(tempX, graph.Y_MIN, g);
				}
				else{
					ptOn(tempX, graph.Y_MAX - 1 * graph.LINE_SIZE * graph.Y_PIXEL, g);
				}
			}
			tempX += graph.X_STEP;
		}

		if (graph.SHOW_NUMBERS){

			// run through all of the x numbers to find the longest one and 
			// set the number frequency appropriately
			// the frequency is shared by the x and y axis, except where the graph is
			// very long and short
			int tempFreq = 1, longestWidth = 0;
			int xNumFreq = 1;
			int currentSpace;
			String ptText;

			for (Double d : xNumbers){
				ptText = doubleToString(d, graph.X_STEP);
				tempWidth = g.getFontMetrics().stringWidth(ptText) + (int) (6 * graph.DOC_ZOOM_LEVEL);
				if ( tempWidth > longestWidth){
					longestWidth = tempWidth;
				}
			}
			currentSpace = (int) (graph.X_SIZE / ((graph.X_MAX-graph.X_MIN)/(graph.X_STEP * xNumFreq)));
			if(longestWidth > currentSpace){
				tempFreq = (int) Math.round( ((graph.X_MAX-graph.X_MIN)
						/ graph.X_STEP) / (graph.X_SIZE / (double)longestWidth) );
				tempFreq++;
				if (tempFreq > xNumFreq){
					xNumFreq = tempFreq;
				}
			}
			int yNumFreq;
			int fontHeight = g.getFontMetrics().getHeight() + (int) (6 * graph.DOC_ZOOM_LEVEL);
			currentSpace = (int) (graph.Y_SIZE / ((graph.Y_MAX-graph.Y_MIN)/(graph.Y_STEP * xNumFreq)));
			if ( fontHeight > currentSpace)
			{// the graph is too short to have the y number share their number frequency with the x's
				yNumFreq = (int) Math.round(((graph.Y_MAX-graph.Y_MIN)
						/graph.Y_STEP)/((graph.Y_SIZE)/fontHeight));
				yNumFreq++;
			}
			else{// the number frequency from the x calculation will work for the y numbers
				yNumFreq = xNumFreq;
			}

			//draw y numbers on top, so the lines don't draw over them
			for (Double d : yNumbers){
				if ( ! numbersClose(d %( graph.Y_STEP * yNumFreq), graph.Y_STEP * yNumFreq) &&
						! numbersClose(d %( graph.Y_STEP * yNumFreq), 0) )
				{// this number should not be drawn
					continue;
				}
				ptText = doubleToString(d, graph.X_STEP);
				width = g.getFontMetrics().stringWidth(ptText);
				g.setColor(Color.white);
				if ( ! (gridyToScreen(d) - (height/2) - numberInset < graph.Y_PIC_ORIGIN ||
						gridyToScreen(d) + (height/2) + numberInset > graph.Y_PIC_ORIGIN + graph.Y_SIZE))
				{// if the number does not bleed off of the graph
					if(graph.X_MAX <= 0)
					{// the axis is not showing, numbers must be drawn on the left side
						g.fillRect(gridxToScreen(graph.X_MAX) - width - 2 * numberInset - numberAndAxisSpace
								, gridyToScreen(d)- height/2 - numberInset,
								width + 2 * numberInset, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(graph.X_MAX) - width - numberAndAxisSpace - numberInset,
								gridyToScreen(d) + height/2 );
					}
					else if (graph.X_MIN >= 0)
					{// the axis is not showing, numbers must be drawn on the right side
						g.fillRect(gridxToScreen(graph.X_MIN) + numberInset + numberAndAxisSpace
								, gridyToScreen(d) - height/2 - numberInset,
								width + 2 * numberInset, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(graph.X_MIN) + numberAndAxisSpace + numberInset,
								gridyToScreen(d) + height/2 );
					}

					else if (graph.X_MIN <= 0 && graph.X_MAX >= 0 &&
							! (gridxToScreen(0) - width - numberInset
									- numberAndAxisSpace < graph.X_PIC_ORIGIN))
					{// the axis is showing, default to drawing numbers on the left side of the axis
						g.fillRect(gridxToScreen(0) - width - 2 * numberInset - numberAndAxisSpace
								, gridyToScreen(d)- height/2 - numberInset,
								width + 2 * numberInset, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(0) - width - numberAndAxisSpace - numberInset,
								gridyToScreen(d) + height/2 );
					}
					else
					{// draw numbers on right side of axis
						g.fillRect(gridxToScreen(0) + numberInset + numberAndAxisSpace
								, gridyToScreen(d) - height/2 - numberInset,
								width + 2 * numberInset, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(0) + numberAndAxisSpace + numberInset,
								gridyToScreen(d) + height/2 );
					}
				}
			}

			//draw x numbers on top, so the lines don't draw over them
			for (Double d : xNumbers){
				if ( graph.barGraph.values.size() > 0 ) // don't draw x numbers on bar graphs
					break;
				if ( ! numbersClose(d %( graph.X_STEP * xNumFreq), graph.X_STEP * xNumFreq) &&
						! numbersClose(d %( graph.X_STEP * xNumFreq), 0) )
				{// this number should not be drawn
					continue;
				}

				ptText = doubleToString(d, graph.X_STEP);
				width = g.getFontMetrics().stringWidth(ptText);
				g.setColor(Color.white);
				if ( ! (gridxToScreen(d) - (width/2) - numberInset < graph.X_PIC_ORIGIN ||
						gridxToScreen(d) + (width/2) + numberInset > graph.X_PIC_ORIGIN + graph.X_SIZE))
				{// if the number does not bleed off of the graph
					if(graph.Y_MAX <= 0)
					{// the axis is not showing, numbers must be drawn on the left side
						g.fillRect(gridxToScreen(d) - (width/2) - numberInset,
								gridyToScreen(graph.Y_MAX) + numberAndAxisSpace
								, width + numberInset * 2, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(d) - (width/2)
								, gridyToScreen(graph.Y_MAX) + numberAndAxisSpace + numberInset + height);
					}
					else if (graph.Y_MIN >= 0)
					{// the axis is not showing, numbers must be drawn on the right side
						g.fillRect(gridxToScreen(d) - (width/2) - numberInset,
								gridyToScreen(graph.Y_MIN) - height - numberInset * 2 - numberAndAxisSpace
								, width + numberInset * 2, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(d) - (width/2)
								, gridyToScreen(graph.Y_MIN) - numberAndAxisSpace - numberInset);
					}
					else if (graph.Y_MIN <= 0 && graph.Y_MAX >= 0 &&
							! (gridyToScreen(0) - height - numberInset
									- numberAndAxisSpace < graph.Y_PIC_ORIGIN) )
					{// the axis is showing, default to drawing numbers on the top of the axis
						g.fillRect(gridxToScreen(d) - (width/2) - numberInset,
								gridyToScreen(0) - height - numberAndAxisSpace - 2 * numberInset,
								width + numberInset * 2, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(d) - (width/2) ,
								gridyToScreen(0) - numberAndAxisSpace - numberInset);
					}
					else{
						g.fillRect(gridxToScreen(d) - (width/2) - numberInset,
								gridyToScreen(0) + numberAndAxisSpace,
								width + numberInset * 2, height + 2 * numberInset);
						g.setColor(Color.black);
						g.drawString(ptText, gridxToScreen(d) - (width/2) ,
								gridyToScreen(0) + numberAndAxisSpace + numberInset + height);
					}
				}

			}
		}

		if (graph.SHOW_AXIS){
			if (graph.X_MIN <= 0 && graph.X_MAX >= 0)
				drawLineSeg(0, graph.Y_MIN, 0, graph.Y_MAX, Color.BLACK, g);

			if (graph.Y_MIN <= 0 && graph.Y_MAX >= 0)
				drawLineSeg(graph.X_MIN, 0, graph.X_MAX, 0, Color.BLACK, g);
		}

		g.setFont(oldFont);
	}


}

