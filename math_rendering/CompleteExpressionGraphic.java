/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Vector;

import tree.BinExpression;
import tree.Expression;
import tree.Fraction;
import tree.Decimal;
import tree.MissingValue;
import tree.Operator;
import tree.Expression;

// good test case for broken cursor movement currently (x^2+3x-10)/3^(2x+6)*(x^2-x-12)
public class CompleteExpressionGraphic{

	ValueGraphic root;
	
	int xSize, ySize;
	private Graphics2D graphics;
	private Font bigFont, smallFont;
	private Vector<ValueGraphic> selectedVals;
	private Vector<ValueGraphic> components;
	private Cursor cursor;
	private ValueGraphic firstSel;
	public int xPos, yPos, bigFontSize;
	public static final int defaultBigFontSize = 12;

	public float DOC_ZOOM_LEVEL;
	
	//will be replaced by completeExpression later
	Expression v;
	
	public enum FontSize{
		BIG_FONT, SMALL_FONT
	};
	
	public CompleteExpressionGraphic(Expression v){
		this.v = v;
		cursor = new Cursor();
		bigFont =  new Font("SansSerif", 0, defaultBigFontSize);
		smallFont = new Font("SansSerif", 0, (int) (defaultBigFontSize * (4.0/5)) );
	}
	
	public float getSizeAdjustment(){
		return (bigFontSize + 0.0f )/ defaultBigFontSize;
	}
	
	public void draw(){

		draw(root);
//		for (ValueGraphic vg : components){
//			graphics.setFont(vg.getFont());
//			vg.draw();
//		}
//		System.out.println("CEG drawCursor?");
//		if (cursor.getValueGraphic() != null){
//			cursor.getValueGraphic().drawCursor();
//			System.out.println("CEG drawCursor");
//		}
	}
	
	public void draw(ValueGraphic v){
		
		graphics.setColor(Color.black);
		graphics.setFont(v.getFont());
//		graphics.setColor(Color.white);
//		graphics.fillRect(v.getX1(), v.getY1(), v.getWidth(), v.getHeight());
		graphics.setColor(Color.black);
		v.draw();
		Vector<ValueGraphic> temp = v.getComponents(); 
		for (ValueGraphic vg : temp){
			draw(vg);
		}
	}
	
	public void clearSelection(){
//		System.out.println("clear all, root:" + root);
		root.setSelectAllBelow(false);
	}

	public void generateExpressionGraphic(Graphics g, 
			int x1, int y1, int fontSize, float zoomLevel) throws Exception{
		
		xPos = x1;
		yPos = y1;
		graphics = (Graphics2D)g;
		bigFontSize = fontSize;
		bigFont =  new Font("SansSerif", 0, bigFontSize);
		smallFont = new Font("SansSerif", 0, (int) (bigFontSize * (3.0/4)));
		DOC_ZOOM_LEVEL = zoomLevel;
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int[] tempSize = {0, 0};
		components = new Vector<ValueGraphic>();
		selectedVals = new Vector<ValueGraphic>();
		ValueGraphic temp = new NothingGraphic(new MissingValue(), this);
		temp = temp.makeValueGraphic(v);
		
		components.add(temp);
		tempSize = temp.requestSize(g, bigFont, x1, y1);
		root = temp;
		
		temp.getMostInnerWest().setCursorPos(x1);
		xSize = tempSize[0];
		ySize = tempSize[1];
	}
	
	public void generateExpressionGraphic(Graphics2D g, int x1, int y1) throws Exception{
		generateExpressionGraphic(g, x1, y1, 12, 1);
	}
	
    public int[] requestSize(Graphics g, int x1, int y1, int fontSize, float zoomLevel) throws Exception {
        xPos = x1;
        yPos = y1;
        graphics = (Graphics2D)g;
        bigFontSize = fontSize;
        bigFont =  new Font("SansSerif", 0, bigFontSize);
        smallFont = new Font("SansSerif", 0, (int) (bigFontSize * (3.0/4)));
        DOC_ZOOM_LEVEL = zoomLevel;
        int[] ret = root.requestSize(g, bigFont, x1, y1);
        return ret;
    }
	
	
	public int getStringWidth(String s, Font f){
		graphics.setFont(f);
		return graphics.getFontMetrics().stringWidth(s);
	}
	
	public int getFontHeight(Font f) throws RenderException{
		graphics.setFont(f);
		if (f.equals(bigFont)){
			return (int) (graphics.getFontMetrics().getHeight() * (.6));
		}
		else if (f.equals(smallFont)){
			return (int) (graphics.getFontMetrics().getHeight() * (.6));
		}
		else
		{
			throw new RenderException("unsupported Font");
		}
	}
	
	public void setGraphics(Graphics2D graphics) {
//		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.graphics = graphics;
	}
	
	public float getFontSizeAdjustment(){
		return ((float)bigFontSize) / defaultBigFontSize;
	}
	
	public int getWidth(){
		return xSize;
	}
	
	public int getHeight(){
		return ySize;
	}
	

	public Graphics2D getGraphics() {
		return graphics;
	}
	
	public Font getBigFont(){
		return bigFont;
	}
	
	public Font getSmallFont(){
		return smallFont;
	}

	public void setSelectedVals(Vector<ValueGraphic> selectedVals) {
		this.selectedVals = selectedVals;
	}

	public Vector<ValueGraphic> getSelectedVals() {
		return selectedVals;
	}

	public void setComponents(Vector<ValueGraphic> components) {
		this.components = components;
	}

	public Vector<ValueGraphic> getComponents() {
		return components;
	}
	
	public ValueGraphic getRoot(){
		return root;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public Cursor getCursor() {
		return cursor;
	}
}
