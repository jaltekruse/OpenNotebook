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

package math_rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import expression.Expression;
import expression.Node;
import expression.NodeException;
import expression.Number;

public class RootNodeGraphic{

	NodeGraphic root;
	
	int xSize, ySize;
	private Graphics2D graphics;
	private Font bigFont, smallFont;
	private Vector<NodeGraphic> selectedVals;
	private Vector<NodeGraphic> components;
	private Cursor cursor;
	private NodeGraphic firstSel;
	public int xPos, yPos, bigFontSize;
	public static final int defaultBigFontSize = 12;

	public float DOC_ZOOM_LEVEL;
	
	Node n;
	
	public enum FontSize{
		BIG_FONT, SMALL_FONT
	};
	
	public RootNodeGraphic(Node n){
		this.n = n;
		cursor = new Cursor();
		bigFont =  new Font("SansSerif", 0, defaultBigFontSize);
		smallFont = new Font("SansSerif", 0, (int) (defaultBigFontSize * (4.0/5)) );
	}
	
	public float getFontSizeAdjustment(){
		return ((float)bigFontSize)/ defaultBigFontSize;
	}
	
	public void draw() throws NodeException{

		draw(root);
//		for (ValueGraphic vg : components){
//			graphics.setFont(vg.getFont());
//			vg.draw();
//		}
//		System.out.println("CEG drawCursor?");
		if (cursor.getValueGraphic() != null){
			//cursor.getValueGraphic().drawCursor();
//			System.out.println("CEG drawCursor");
		}
	}
	
	public void draw(NodeGraphic n) throws NodeException{
		
		graphics.setColor(Color.black);
		graphics.setFont(n.getFont());
//		graphics.setColor(Color.white);
//		graphics.fillRect(v.getX1(), v.getY1(), v.getWidth(), v.getHeight());
		graphics.setColor(Color.black);
		n.draw();
		Vector<NodeGraphic> temp = n.getComponents(); 
		for (NodeGraphic ng : temp){
			draw(ng);
		}
	}
	
	public void clearSelection(){
//		System.out.println("clear all, root:" + root);
		root.setSelectAllBelow(false);
	}

    public void setGraphics(Graphics g){
        graphics = (Graphics2D)g;
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
		components = new Vector<NodeGraphic>();
		selectedVals = new Vector<NodeGraphic>();
		NodeGraphic temp = new DecimalGraphic(new Number(3), this);
		temp = temp.makeNodeGraphic(n);
		
		components.add(temp);
		tempSize = temp.requestSize(g, bigFont, x1, y1);
		root = temp;
		
		temp.getMostInnerWest().setCursorPos(x1);
		xSize = tempSize[0];
		ySize = tempSize[1];
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
	
	public int getWidth(){
		return xSize;
	}
	
	public int getHeight(){
		return ySize;
	}
	
	public void generateExpressionGraphic(Graphics2D g, int x1, int y1) throws Exception{
		generateExpressionGraphic(g, x1, y1, 12, 1);
	}
	
	public int[] requestSize(Expression v, Graphics g, Font f){
		
		return null;
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

	public Graphics2D getGraphics() {
		return graphics;
	}
	
	public Font getBigFont(){
		return bigFont;
	}
	
	public Font getSmallFont(){
		return smallFont;
	}

	public void setSelectedVals(Vector<NodeGraphic> selectedVals) {
		this.selectedVals = selectedVals;
	}

	public Vector<NodeGraphic> getSelectedVals() {
		return selectedVals;
	}

	public void setComponents(Vector<NodeGraphic> components) {
		this.components = components;
	}

	public Vector<NodeGraphic> getComponents() {
		return components;
	}
	
	public NodeGraphic getRoot(){
		return root;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public Cursor getCursor() {
		return cursor;
	}
}
