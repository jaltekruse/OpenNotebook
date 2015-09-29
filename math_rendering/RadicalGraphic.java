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

import tree.Expression;
import tree.Operator;
import tree.UnaryExpression;
import tree.Expression;

public class RadicalGraphic extends UnaryExpressionGraphic {

	private int space;
	private int widthFront;
	private int heightLeadingTail;
	private int lengthLittleTail;
	
	public RadicalGraphic(UnaryExpression v, CompleteExpressionGraphic compExGraphic) {
		super(v, compExGraphic);
		if (v.getChild() instanceof UnaryExpression){
			if (((UnaryExpression)v.getChild()).getOp() == Operator.PAREN)
			{//if there is a set of parenthesis inside, remove them
				v.setChild(((UnaryExpression)v.getChild()).getChild());
			}
		}
		setMostInnerSouth(this);
		setMostInnerNorth(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		
		
		if (isSelected()){
			super.getCompExGraphic().getGraphics().setColor(getSelectedColor());
			super.getCompExGraphic().getGraphics().fillRect(symbolX1, symbolY1, symbolX2 - symbolX1, symbolY2 - symbolY1);
			super.getCompExGraphic().getGraphics().setColor(Color.black);
		}
		super.getCompExGraphic().getGraphics().setStroke(new BasicStroke(
				(int) (1 * super.getCompExGraphic().DOC_ZOOM_LEVEL)));
		super.getCompExGraphic().getGraphics().drawLine(symbolX1, symbolY2 - heightLeadingTail + lengthLittleTail,
				symbolX1 + 3, symbolY2 - heightLeadingTail);
		super.getCompExGraphic().getGraphics().drawLine(symbolX1 + 3, symbolY2 - heightLeadingTail,
				symbolX1 + (int) Math.round(0.5 * widthFront), symbolY2);
		super.getCompExGraphic().getGraphics().drawLine(symbolX1 + (int) Math.round(0.5 * widthFront),
				symbolY2, symbolX1 + widthFront, symbolY1);
		super.getCompExGraphic().getGraphics().drawLine(symbolX1 + widthFront, symbolY1, 
				symbolX2, symbolY1);
		super.getCompExGraphic().getGraphics().drawLine(symbolX2, symbolY1, symbolX2, symbolY1 + 5);
		super.getCompExGraphic().getGraphics().setStroke(new BasicStroke());
	}
	
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
		
		space = (int) (4 * super.getCompExGraphic().DOC_ZOOM_LEVEL);
		widthFront = (int) (8 * super.getCompExGraphic().DOC_ZOOM_LEVEL);
		heightLeadingTail = (int) (8 * super.getCompExGraphic().DOC_ZOOM_LEVEL);
		lengthLittleTail = (int) (3 * super.getCompExGraphic().DOC_ZOOM_LEVEL);
		
		Expression tempChild = ((UnaryExpression)super.getValue()).getChild();
		ValueGraphic childValGraphic = null;
		int[] childSize = {0,0};
		int[] symbolSize = {0, 0};
		int[] totalSize = {0, 0};
		
		childValGraphic = makeValueGraphic(tempChild);
		childSize = childValGraphic.requestSize(g, f, x1 + widthFront + space, y1 + space);
		
		//set the west and east fields for inside an outside of the expression
		setMostInnerWest(this);
		setEast(childValGraphic.getMostInnerWest());
		childValGraphic.getMostInnerWest().setWest(this);
		setMostInnerEast(childValGraphic.getMostInnerEast());
		
		setChildGraphic(childValGraphic);
		super.getCompExGraphic().getComponents().add(childValGraphic);
		
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
		// TODO Auto-generated method stub
		return totalSize;
	}

}
