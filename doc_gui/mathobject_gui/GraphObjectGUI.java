/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
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
