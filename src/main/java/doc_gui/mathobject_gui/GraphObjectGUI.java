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

package doc_gui.mathobject_gui;


import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import doc.GridPoint;
import doc.attributes.DoubleAttribute;
import doc.attributes.GridPointAttribute;
import doc.attributes.StringAttribute;
import doc.mathobjects.GraphObject;
import doc_gui.graph.Graph;
import doc_gui.graph.Selection;
import doc_gui.graph.GraphedCartFunction;
import expression.NodeException;

public class GraphObjectGUI extends MathObjectGUI<GraphObject> {

	private Graph graph;

	public static final Color[] graphColors = {Color.BLUE.darker().darker(), Color.GREEN.darker().darker(),
		Color.RED.darker().darker(), Color.MAGENTA, Color.ORANGE, Color.BLACK};

	public GraphObjectGUI(){
		graph = new Graph();
	}

	public void drawMathObject(GraphObject object, Graphics g,
			Point pageOrigin, float zoomLevel) {

		ScaledSizeAndPosition sap = getSizeAndPosition(object, pageOrigin, zoomLevel);

		graph.removeAllSingleGraphs();
		graph.removeAllPoints();
		int colorIndex = 0;
		boolean hadError = false;
		for ( StringAttribute ex : object.getExpressions()){
			if ( ! ex.getValue().equals("")){
				try {
					graph.AddGraph(new GraphedCartFunction(graph, "y=" + ex.getValue(),"x", "y", true,
							graphColors[colorIndex]));
				} catch (NodeException e) {
					// TODO Auto-generated catch block
					hadError = true;
				}
			}
			colorIndex++;
		}
		//graph.lineGraph.linePoints = object.getPoints();
		graph.lineGraph.linePoints.removeAllElements();
		graph.removeAllPoints();
		for ( GridPointAttribute pt : object.getPoints()){
			if ( pt != null){
				graph.addPoint(pt.getValue().getx(), pt.getValue().gety());
			}
		}
		graph.barGraph.values.removeAllElements();
		for ( DoubleAttribute d : object.getBarGraphValues().getValues()){
			graph.barGraph.values.add(d.getValue());
		}
		graph.barGraph.labels.removeAllElements();
		for ( StringAttribute strAttr : object.getBarGraphLabels().getValues()){
			graph.barGraph.labels.add(strAttr.getValue());
		}
		graph.barGraph.groupSize = object.getBarGraphGroupSize();

		graph.lineGraph.setColor(object.getLineGraphColor());
		synchronized ( object.getLineGraphPoints().getValues() ){ 
			for ( GridPointAttribute pt : object.getLineGraphPoints().getValues()){
				if ( pt != null){
					graph.lineGraph.linePoints.add(new GridPoint(pt.getValue().getx(), pt.getValue().gety()));
				}
			}
		}

		graph.repaint(g, sap.getWidth() , sap.getHeight(), zoomLevel, sap.getxOrigin(), sap.getyOrigin(), object);

		if (hadError){
			FontMetrics fm = g.getFontMetrics();
			int errorWidth = fm.stringWidth("error");
			g.setColor(Color.WHITE);
			g.fillRect((sap.getxOrigin() + sap.getWidth()/2) - errorWidth/2
					, (sap.getyOrigin() + sap.getHeight()/2) - fm.getHeight()/2,
					errorWidth + 4, fm.getHeight() + 4);
			g.setColor(Color.BLACK);
			g.drawRect((sap.getxOrigin() + sap.getWidth()/2) - errorWidth/2
					, (sap.getyOrigin() + sap.getHeight()/2) - fm.getHeight()/2,
					errorWidth + 4, fm.getHeight() + 4);
			g.setColor(Color.RED);
			g.drawString("error", (sap.getxOrigin() + sap.getWidth()/2) - errorWidth/2 + 2
					, (sap.getyOrigin() + sap.getHeight()/2) + fm.getHeight()/2);
		}

	}

	public void mouseClicked(GraphObject gObj, int x , int y, float zoomLevel){
    // disable for now, its just annoying because the selection is not indicated on the menu frame
    // and cannot be removed
    if (false) { // deliberately disabled, but this makes sure this code is still compiling (as commenting out would not do
      //add a point to the graph
      gObj.getPoints().add(new GridPointAttribute("", new GridPoint(graph.screenxToGrid(x), -1 * graph.screenyToGrid(y))));
      // add a single x value "selection"
      graph.pullVarsFromGraphObject(gObj, (int) (gObj.getWidth() * zoomLevel),
          (int) (gObj.getHeight() * zoomLevel) );
      ((Selection)gObj.getAttributeWithName(GraphObject.SELECTION).getValue()).setStart(graph.screenxToGrid(x));
    }
    return;
	}
}
