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

import doc.mathobjects.DecimalRectangle;
import doc.mathobjects.MathObject;
import doc_gui.PageGUI;
import math_rendering.CompleteExpressionGraphic;
import math_rendering.Cursor;
import tree.Expression;
import tree.ExpressionParser;
import doc.attributes.StringAttribute;
import doc.mathobjects.ExpressionObject;
import expression.Node;
import expression.NodeException;

public class ExpressionObjectGUI extends MathObjectGUI<ExpressionObject> {

	public static final String EX_ERROR = "Expression Error";
    private CompleteExpressionGraphic current;
    private ExpressionObject currentExObj;
    private String currentEx;
    boolean currentSelectionStep = false;
    int currentStepIndex;
    DecimalRectangle currentPosSize;
    float currentZoom;
    ExpressionParser parser;

	public ExpressionObjectGUI(){
		parser = new ExpressionParser();
	}

    @Override
    public boolean keyPressed(MathObject mObj, char key) throws NodeException {
        switch (key){
            case PageGUI.UP:
                current.getCursor().getValueGraphic().moveCursorNorth();
                break;
            case PageGUI.DOWN:
                current.getCursor().getValueGraphic().moveCursorSouth();
                break;
            case PageGUI.LEFT:
                current.getCursor().getValueGraphic().moveCursorWest();
                break;
            case PageGUI.RIGHT:
                current.getCursor().getValueGraphic().moveCursorEast();
                break;
            case 'c' :
                mObj.performSpecialObjectAction(ExpressionObject.COMBINE_LIKE_TERMS);
                break;
            case 's' :
                mObj.performSpecialObjectAction(ExpressionObject.SUB_IN_VALUE);
                break;
            case 't':
                mObj.performSpecialObjectAction(ExpressionObject.MANUALLY_TYPE_STEP);
                break;
            case 'm':
                mObj.performSpecialObjectAction(ExpressionObject.MODIFY_EXPRESSION);
                break;
            case 'o':
                mObj.performSpecialObjectAction(ExpressionObject.OTHER_OPERATIONS);
                break;
            case 'u':
                mObj.performSpecialObjectAction(ExpressionObject.UNDO_STEP);
                break;
            case '-':
                mObj.performSpecialObjectAction(ExpressionObject.SUBTRACT_FROM_BOTH_SIDES);
                break;
            case '+':
                mObj.performSpecialObjectAction(ExpressionObject.ADD_TO_BOTH_SIDES);
                break;
            case '*':
                mObj.performSpecialObjectAction(ExpressionObject.MULTIPLY_BOTH_SIDES);
                break;
            case '/':
                mObj.performSpecialObjectAction(ExpressionObject.DIVIDE_BOTH_SIDES);
                break;
            default:
                return false;
        }
        return true;
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

		CompleteExpressionGraphic rootGraphic;
		if ( ! object.getExpression().equals("")){
			// if any of the steps cannot be rendered, this information will allow
			// space to be left for printing an error message in its place
			g.setFont(new Font("Sans Serif", 0, fontSize));
			int errorMessageHeight = g.getFontMetrics().getHeight();
			int errorMessageWidth = g.getFontMetrics().stringWidth(EX_ERROR);

			Expression n = null;
			int totalHeight = 0;
			int greatestWidth = 0;
			Vector<Integer> indeciesInError = new Vector<Integer>();
			Vector<Integer> yPosOfSteps = new Vector<Integer>();
			int currentIndex = 0;
			Vector<CompleteExpressionGraphic> expressions = new Vector<CompleteExpressionGraphic>();

			// add the expression
			try {
				n = parser.ParseExpression(object.getExpression());
                boolean objectChanged = currentSelectionStep || current == null || currentExObj != object ||
                        ! object.getDecimalRectangleBounds().equals(currentPosSize) || ! currentEx.equals(object.getExpression());
                if ( objectChanged){
                    rootGraphic = new CompleteExpressionGraphic(n);
                    rootGraphic.generateExpressionGraphic(g, outerBufferSpace + xOrigin,
                            outerBufferSpace + yOrigin, fontSize, zoomLevel);
                }
                else if (currentZoom != zoomLevel){
                    rootGraphic = current;
                    rootGraphic.requestSize(g, outerBufferSpace + xOrigin,
                            outerBufferSpace + yOrigin, fontSize, zoomLevel);
                }
                else{
                    rootGraphic = current;
                    rootGraphic.setGraphics( (Graphics2D) g);
                }
				// keep these next three lines in the try catch block, they should only happen
				// if they line above does not throw an error
				expressions.add(rootGraphic);
				yPosOfSteps.add(rootGraphic.yPos);
				totalHeight = rootGraphic.getHeight();
				greatestWidth = rootGraphic.getWidth();
                currentSelectionStep = false;
                current = rootGraphic;
                currentExObj = object;
                currentEx = object.getExpression();
                currentPosSize = object.getDecimalRectangleBounds();
                currentZoom = zoomLevel;
			} catch (Exception e) {
				indeciesInError.add(currentIndex);
				yPosOfSteps.add(outerBufferSpace + yOrigin);
				expressions.add(null);
				totalHeight += errorMessageHeight;
				greatestWidth = errorMessageWidth;
			}

			Vector<StringAttribute> steps = (Vector<StringAttribute>)
					object.getListWithName(ExpressionObject.STEPS).getValues();
			// add all of the correct answers to the list of steps (prevents need for another loop
			// they are removed from the list after the following loop
            // TODO - confirm if this works with multiple correct answers
			for (StringAttribute strAtt : object.getCorrectAnswers()){
				if ( ! strAtt.getValue().equals("")){
					steps.add(strAtt);
				}
			}

			// add the steps to the list of expressions to render
			String s;
            int i = 0;
			for (StringAttribute mAtt : steps){
				s = mAtt.getValue();
				currentIndex++;
				totalHeight += stepBufferSpace;
				try{
					n = parser.ParseExpression(s);
					rootGraphic = new CompleteExpressionGraphic(n);
//                    current = rootGraphic;
					rootGraphic.generateExpressionGraphic(g, xOrigin + outerBufferSpace,
							outerBufferSpace + yOrigin + totalHeight, fontSize, zoomLevel);
					expressions.add(rootGraphic);
					yPosOfSteps.add(rootGraphic.yPos);
					if (rootGraphic.getWidth() > greatestWidth){
						greatestWidth = rootGraphic.getWidth();
					}
					totalHeight += rootGraphic.getHeight();
                    i++;
				}catch (Exception e) {
					indeciesInError.add(currentIndex);
					totalHeight += errorMessageHeight;
					if (errorMessageWidth > greatestWidth){
						greatestWidth = errorMessageWidth;
					}
					yPosOfSteps.add(outerBufferSpace + yOrigin + totalHeight);
				}
			}
			// remove the correct answers, so they are not permanently added as steps
			steps.removeAll(object.getCorrectAnswers());

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
			int numberOfSteps = steps.size() + 1;
			for (CompleteExpressionGraphic r : expressions){
				try {
					if ( indeciesInError.contains(index)){
						g.setFont(new Font("SansSerif", 0, fontSize));
						g.setColor(Color.RED);
						g.drawString(EX_ERROR, xOrigin + outerBufferSpace,
								yPosOfSteps.get(index) + errorMessageHeight);
						g.setColor(Color.BLACK);
						index++;
						continue;
					}
					else{
						if ( index >= numberOfSteps)
						{// draw the answers with a highlight
							g.setColor(new Color(180, 255, 100));
							g.fillRect(r.xPos - 4, r.yPos - 4, r.getWidth() + 8, r.getHeight() + 8);
							g.setColor(Color.BLACK);
						}
                        if ( index != 0)
    						r.setCursor(new Cursor(null,0));
						r.draw();
					}
				} catch (NodeException e) {
                    e.printStackTrace();
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

		CompleteExpressionGraphic ceg;
		try {
			g.setColor(object.getColor());
			if ( ! object.getExpression().equals("")){
				Expression n = parser.ParseExpression(object.getExpression());
				ceg = new CompleteExpressionGraphic(n);
				ceg.generateExpressionGraphic(g, bufferSpace + xOrigin,
						bufferSpace + yOrigin, fontSize, zoomLevel);
				object.setWidth( (int) (ceg.getWidth() / zoomLevel) + 10);
				object.setHeight( (int) (ceg.getHeight() / zoomLevel) + 10);
				if ( object.getColor() != null){
					g.fillRect(xOrigin, yOrigin, (int) (object.getWidth() * zoomLevel),
							(int) (object.getHeight() * zoomLevel));
				}
				g.setColor(Color.BLACK);
                ceg.getCursor().setValueGraphic(null);
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
