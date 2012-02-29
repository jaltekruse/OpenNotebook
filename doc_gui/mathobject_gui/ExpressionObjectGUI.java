/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

import math_rendering.RootNodeGraphic;
import tree.ExpressionParser;
import doc.attributes.StringAttribute;
import doc.mathobjects.ExpressionObject;
import expression.Node;
import expression.NodeException;

public class ExpressionObjectGUI extends MathObjectGUI<ExpressionObject> {

	private ExpressionParser parser;
	public static final String EX_ERROR = "Expression Error";

	public ExpressionObjectGUI(){
		parser = new ExpressionParser();
	}

	public void drawInteractiveComponents(ExpressionObject object, Graphics g, Point pageOrigin, float zoomLevel){
		g.setColor(Color.BLACK);
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int fontSize = (int) (object.getFontSize() * zoomLevel);
		int outerBufferSpace = (int) (5 * zoomLevel);
		int stepBufferSpace = (int) (10 * zoomLevel);
		Graphics2D g2d = (Graphics2D) g;

		RootNodeGraphic rootGraphic;
		if ( ! object.getExpression().equals("")){
			// if any of the steps cannot be rendered, this information will allow
			// space to be left for printing an error message in its place
			g.setFont(new Font("Sans Serif", 0, fontSize));
			int errorMessageHeight = g.getFontMetrics().getHeight();
			int errorMessageWidth = g.getFontMetrics().stringWidth(EX_ERROR);

			Node n = null;
			int totalHeight = 0;
			int greatestWidth = 0;
			Vector<Integer> indeciesInError = new Vector<Integer>();
			Vector<Integer> yPosOfSteps = new Vector<Integer>();
			int currentIndex = 0;
			Vector<RootNodeGraphic> expressions = new Vector<RootNodeGraphic>();

			rootGraphic = new RootNodeGraphic(n);
			try {
				n = Node.parseNode(object.getExpression());
				rootGraphic = new RootNodeGraphic(n);
				rootGraphic.generateExpressionGraphic(g, outerBufferSpace + xOrigin,
						outerBufferSpace + yOrigin, fontSize, zoomLevel);
				// keep these next three lines in the try catch block, they should only happen
				// if they line above does not throw an error
				expressions.add(rootGraphic);
				yPosOfSteps.add(rootGraphic.yPos);
				totalHeight = rootGraphic.getHeight();
				greatestWidth = rootGraphic.getWidth();
			} catch (Exception e) {
				indeciesInError.add(currentIndex);
				yPosOfSteps.add(outerBufferSpace + yOrigin);
				expressions.add(null);
				totalHeight += errorMessageHeight;
				greatestWidth = errorMessageWidth;
			}

			String s;
			for (StringAttribute mAtt : (Vector<StringAttribute>)
					object.getListWithName(ExpressionObject.STEPS).getValues()){
				s = mAtt.getValue();
				currentIndex++;
				totalHeight += stepBufferSpace;
				try{
					n = Node.parseNode(s);
					rootGraphic = new RootNodeGraphic(n);
					rootGraphic.generateExpressionGraphic(g, xOrigin + outerBufferSpace,
							outerBufferSpace + yOrigin + totalHeight, fontSize, zoomLevel);
					expressions.add(rootGraphic);
					yPosOfSteps.add(rootGraphic.yPos);
					if (rootGraphic.getWidth() > greatestWidth){
						greatestWidth = rootGraphic.getWidth();
					}
					totalHeight += rootGraphic.getHeight();
				}catch (Exception e) {
					indeciesInError.add(currentIndex);
					totalHeight += errorMessageHeight;
					if (errorMessageWidth > greatestWidth){
						greatestWidth = errorMessageWidth;
					}
					yPosOfSteps.add(outerBufferSpace + yOrigin + totalHeight);
				}
			}
			if ( object.getColor() != null){
				g.setColor(object.getColor());
			}
			else{
				g.setColor(Color.white);
			}
			g.fillRect(xOrigin, yOrigin, greatestWidth + 2 * outerBufferSpace,
					totalHeight + 2 * outerBufferSpace);
			g.setColor(Color.BLACK);
			int index = 0;
			for (RootNodeGraphic r : expressions){
				try {
					if ( indeciesInError.contains(index)){
						g.setFont(new Font("SansSerif", 0, fontSize));
						g.setColor(Color.RED);
						g.drawString(EX_ERROR, xOrigin + outerBufferSpace,
								yPosOfSteps.get(index) + errorMessageHeight);
						index++;
						continue;
					}
					else{
						r.draw();
					}
				} catch (NodeException e) {
					// TODO Auto-generated catch block
					g.setFont(new Font("SansSerif", 0, fontSize));
					g.setColor(Color.RED);
					g.drawString(EX_ERROR, r.xPos, r.yPos + errorMessageHeight);
				}
				index++;
			}
			if (greatestWidth > 0 && totalHeight > 0){
				object.setWidth( (int) ((greatestWidth + 2 * outerBufferSpace) / zoomLevel ));
				object.setHeight( (int) ((totalHeight + 2 * outerBufferSpace ) / zoomLevel ));
			}
		}
		else{
			g2d.setStroke(new BasicStroke());
			g2d.setPaint(Color.BLACK);
			g.drawRect(xOrigin, yOrigin , width, height);
		}

	};

	public void drawMathObject(ExpressionObject object, Graphics g, Point pageOrigin,
			float zoomLevel) {
		if ( object.isAlwaysShowingSteps()){
			drawInteractiveComponents(object, g, pageOrigin, zoomLevel);
			return;
		}

		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		int fontSize = (int) (object.getFontSize() * zoomLevel);
		int bufferSpace = (int) (5 * zoomLevel);

		// if any of the steps cannot be rendered, this information will allow
		// space to be left to print an error message in its place
		g.setFont(new Font("SansSerif", 0, fontSize));
		int errorMessageHeight = g.getFontMetrics().getHeight();
		int errorMessageWidth = g.getFontMetrics().stringWidth(EX_ERROR);

		RootNodeGraphic ceg;
		try {
			g.setColor(object.getColor());
			if ( ! object.getExpression().equals("")){
				Node n = Node.parseNode(object.getExpression());
				ceg = new RootNodeGraphic(n);
				ceg.generateExpressionGraphic(g, bufferSpace + xOrigin,
						bufferSpace + yOrigin, fontSize, zoomLevel);
				object.setWidth( (int) (ceg.getWidth() / zoomLevel) + 10);
				object.setHeight( (int) (ceg.getHeight() / zoomLevel) + 10);
				if ( object.getColor() != null){
					g.fillRect(xOrigin, yOrigin, (int) (object.getWidth() * zoomLevel),
							(int) (object.getHeight() * zoomLevel));
				}
				g.setColor(Color.BLACK);
				ceg.draw();
			}
			else{
				if ( object.getColor() != null){
					g.fillRect(xOrigin, yOrigin, (int) (object.getWidth() * zoomLevel),
							(int) (object.getHeight() * zoomLevel));
				}
				g.setColor(Color.BLACK);
				g.drawRect(xOrigin, yOrigin, width, height);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			object.setHeight( (int) (errorMessageHeight / zoomLevel) + 10);
			object.setWidth( (int) (errorMessageWidth / zoomLevel) + 10);
			g.setFont(new Font("SansSerif", 0, fontSize));
			g.setColor(Color.RED);
			g.drawString(EX_ERROR, xOrigin + bufferSpace, yOrigin + bufferSpace + errorMessageHeight);
		}
	}
}
