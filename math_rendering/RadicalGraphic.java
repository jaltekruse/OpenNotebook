/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import expression.Expression;
import expression.Node;

public class RadicalGraphic extends UnaryExpressionGraphic {

	private int space;
	private int widthFront;
	private int heightLeadingTail;
	private int lengthLittleTail;
	
	public RadicalGraphic(Expression v, RootNodeGraphic compExGraphic) {
		super(v, compExGraphic);
		setMostInnerSouth(this);
		setMostInnerNorth(this);
	}

	@Override
	public void draw() {
		
		if (isSelected()){
			super.getRootNodeGraphic().getGraphics().setColor(getSelectedColor());
			super.getRootNodeGraphic().getGraphics().fillRect(symbolX1, symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
			super.getRootNodeGraphic().getGraphics().setColor(Color.black);
		}
		getRootNodeGraphic().getGraphics().setStroke(new BasicStroke(
				(int) (1 * getRootNodeGraphic().DOC_ZOOM_LEVEL * getRootNodeGraphic().getFontSizeAdjustment())));
		getRootNodeGraphic().getGraphics().drawLine(symbolX1, symbolY2 - heightLeadingTail + lengthLittleTail,
				symbolX1 + 3, symbolY2 - heightLeadingTail);
		getRootNodeGraphic().getGraphics().drawLine(symbolX1 + 3, symbolY2 - heightLeadingTail,
				symbolX1 + (int) Math.round(0.5 * widthFront), symbolY2);
		getRootNodeGraphic().getGraphics().drawLine(symbolX1 + (int) Math.round(0.5 * widthFront),
				symbolY2, symbolX1 + widthFront, symbolY1);
		getRootNodeGraphic().getGraphics().drawLine(symbolX1 + widthFront, symbolY1, 
				symbolX2, symbolY1);
		getRootNodeGraphic().getGraphics().drawLine(symbolX2, symbolY1, symbolX2, symbolY1 + lengthLittleTail);
		getRootNodeGraphic().getGraphics().setStroke(new BasicStroke());
	}
	
	@Override
	public void drawCursor(int pos){
		
	}

	@Override
	public int[] requestSize(Graphics g, Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1)
			throws Exception {
		g.setFont(f);
		setFont(f);
		
		space = (int) (4 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL * getRootNodeGraphic().getFontSizeAdjustment());
		widthFront = (int) (8 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL* getRootNodeGraphic().getFontSizeAdjustment());
		heightLeadingTail = (int) (6 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL* getRootNodeGraphic().getFontSizeAdjustment());
		lengthLittleTail = (int) (3 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL* getRootNodeGraphic().getFontSizeAdjustment());
		
		// The call to getChild() skips the first paren inside of the operator, the parens are needed to have
		// an expression inside of a UnaryOp, but they are not usually displayed
		// if a user wants to show parens, the can use  two pairs of parens: sqrt((5/6))
		Node tempChild = super.getValue().getChild(0);
		NodeGraphic childValGraphic = null;
		int[] childSize = {0,0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		childValGraphic = makeNodeGraphic(tempChild);
		childSize = childValGraphic.requestSize(g, f, x1 + widthFront + space, y1 + space);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(this);
		setEast(childValGraphic.getMostInnerWest());
		childValGraphic.getMostInnerWest().setWest(this);
		setMostInnerEast(childValGraphic.getMostInnerEast());
		
		setChildGraphic(childValGraphic);
		super.getRootNodeGraphic().getComponents().add(childValGraphic);
		
		widthFront += (int) Math.round(childSize[1]/14.0);
		
		if (widthFront > 20)
		{
			widthFront = 20;
		}
		heightLeadingTail += (int) Math.round(childSize[1]/5.0);
		
		if (heightLeadingTail > 40)
		{
			heightLeadingTail = 40;
		}
		childValGraphic.shiftToX1(x1 + widthFront + space);
		
		symbolSize[0] = childSize[0] + space * 2 + widthFront;
		symbolSize[1] = childSize[1] + space;
		
		symbolY1 = y1;
		symbolY2 = symbolY1 + symbolSize[1];
		symbolX1 = x1;
		symbolX2 = x1 + symbolSize[0];
		
		setUpperHeight(childValGraphic.getUpperHeight() + space);
		setLowerHeight(childValGraphic.getLowerHeight() );
		
		totalSize[0] = symbolSize[0];
		totalSize[1] = symbolSize[1];
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + totalSize[0]);
		super.setY2(y1 + totalSize[1]);
		
		return totalSize;
	}

}
