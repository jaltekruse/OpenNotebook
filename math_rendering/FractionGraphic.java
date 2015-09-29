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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Vector;

import tree.Expression;
import tree.Fraction;

public class FractionGraphic extends ValueGraphic<Fraction>{
	
	public static enum Style{
		SLASH, DIAGONAL, HORIZONTAL
	}
	
	private Style style;
	private int spaceAroundBar;
	private int sizeOverHang;
	
	public FractionGraphic(Fraction f, CompleteExpressionGraphic gr) {
		super(f, gr);
		style = Style.HORIZONTAL;
		setMostInnerWest(this);
		setMostInnerEast(this);
		setMostInnerNorth(this);
		setMostInnerSouth(this);
	}

	@Override
	public void draw() {
		Graphics g = super.getCompExGraphic().getGraphics();
		g.setFont(getFont());
		System.out.println(getFont().getName());
		FontMetrics fm = g.getFontMetrics();
		if (style == Style.SLASH || (style == Style.HORIZONTAL && getValue().getDenominator() == 1)){
			if (isSelected())
			{
				super.getCompExGraphic().getGraphics().setColor(getSelectedColor());
				super.getCompExGraphic().getGraphics().fillRect(getX1() - 2, getY1() - 2,
						getX2() - getX1() + 4, getY2() - getY1() + 4);
				super.getCompExGraphic().getGraphics().setColor(Color.black);
			}
			g.drawString(getValue().toString(), getX1(), getY2());
		}
		else if (style == Style.HORIZONTAL){
			g.drawString("" + getValue().getDenominator(),
					(int)Math.round(((getX2() - getX1()) - getCompExGraphic().getStringWidth("" 
							+ getValue().getDenominator(), getFont()))/2.0) + getX1(), getY2());
			int heightDenom = (int) Math.round((getY2() - getY1() - (2 * spaceAroundBar + 1))/2.0);
			int lineY = heightDenom + spaceAroundBar + getY1();
			
			super.getCompExGraphic().getGraphics().setStroke(new BasicStroke(
					(int) (1 * super.getCompExGraphic().DOC_ZOOM_LEVEL)));
			g.drawLine(getX1(), lineY, getX2(), lineY);
			super.getCompExGraphic().getGraphics().setStroke(new BasicStroke());
			
			g.drawString("" + getValue().getNumerator(),
					(int)Math.round(((getX2() - getX1()) - fm.stringWidth("" + getValue().getNumerator()))/2.0)
					+ getX1(), getY1() + heightDenom);
		}
	}
	
public void drawCursor(){
		
		int xPos = findCursorXPos();
		
		super.getCompExGraphic().getGraphics().setColor(Color.BLACK);
		super.getCompExGraphic().getGraphics().fillRect(xPos, getY1() - 3, 2, getY2() - getY1()+ 5);
		
	}
	
	public int getMaxCursorPos(){
		return getValue().toString().length();
	}
	
	public int findCursorXPos(){
		super.getCompExGraphic().getGraphics().setFont(getFont());
		String numberString = getValue().toString();
		return getX1() + super.getCompExGraphic().getGraphics().getFontMetrics().stringWidth(
				numberString.substring(0, super.getCompExGraphic().getCursor().getPos()));
	}
	
	public void setCursorPos(int xPixelPos){
		
		String numberString = getValue().toString();
		super.getCompExGraphic().getCursor().setValueGraphic(this);
		super.getCompExGraphic().getGraphics().setFont(getFont());
		
		if (xPixelPos <= getX1())
		{//if the pixel is in front of the graphic
			super.getCompExGraphic().getCursor().setPos(0);
			super.getCompExGraphic().getCursor().setValueGraphic(this);
			return;
		}
			
		else if (xPixelPos >= getX2())
		{// if the pixel is after the graphic
			super.getCompExGraphic().getCursor().setPos(numberString.length());
			super.getCompExGraphic().getCursor().setValueGraphic(this);
			return;
		}
		
		int startX, endX, xWidth;
		for (int i = 0; i < numberString.length() ; i++){
			
			startX = super.getCompExGraphic().getGraphics().getFontMetrics().stringWidth(
					numberString.substring(0, i)) + getX1();
			endX = super.getCompExGraphic().getGraphics().getFontMetrics().stringWidth(
					numberString.substring(0, i + 1)) + getX1();
			xWidth = endX - startX;
			if (startX <= xPixelPos && endX >= xPixelPos)
			{//if the x position is inside of a character, check if it is on the first or second
				//half of the character and set the cursor accordingly
				if (endX - xPixelPos > xWidth/2){
					super.getCompExGraphic().getCursor().setPos( i );
				}
				else{
					super.getCompExGraphic().getCursor().setPos( i + 1 );
				}
				super.getCompExGraphic().getCursor().setValueGraphic(this);
				return;
			}
		}
	}
	
	public void moveCursorWest(){
		if (super.getCompExGraphic().getCursor().getPos() > 0){
			super.getCompExGraphic().getCursor().setPos( super.getCompExGraphic().getCursor().getPos() - 1); 
		}
		else{
			if (getWest() == null)
			{
				return;
			}
			else
			{
				getWest().sendCursorInFromEast((getY2() - getY1())/2, this);
				return;
			}
		}
	}
	
	public void moveCursorEast(){
		if (super.getCompExGraphic().getCursor().getPos() < getValue().toString().length()){
			super.getCompExGraphic().getCursor().setPos( super.getCompExGraphic().getCursor().getPos() + 1); 
		}
		else{
			if (getEast() == null)
			{
				return;
			}
			else
			{
				getEast().sendCursorInFromWest((getY2() - getY1())/2, this);
				return;
			}
		}
	}
	
	public void moveCursorNorth(){
		if (getNorth() == null)
		{
			System.out.println("nothing to north");
			return;
		}
		else
		{
			getNorth().sendCursorInFromSouth(findCursorXPos(), this);
			return;
		}
	}
	
	public void moveCursorSouth(){
		if (getSouth() == null)
		{
			System.out.println("nothing to south");
			return;
		}
		else
		{
			getSouth().sendCursorInFromNorth(findCursorXPos(), this);
			return;
		}
	}
	
	public void sendCursorInFromEast(int yPos, ValueGraphic vg)
	{
		super.getCompExGraphic().getCursor().setValueGraphic(this);
		super.getCompExGraphic().getCursor().setPos(getMaxCursorPos() - 1);
	}
	
	public void sendCursorInFromWest(int yPos, ValueGraphic vg)
	{
		super.getCompExGraphic().getCursor().setValueGraphic(this);
		super.getCompExGraphic().getCursor().setPos(1);
	}

	public void sendCursorInFromNorth(int xPos, ValueGraphic vg){
//		super.getCompExGraphic().getCursor().setValueGraphic(this);
		setCursorPos(xPos);
		System.out.println("Dec graphic in from north, cursor: " +
				super.getCompExGraphic().getCursor().getValueGraphic().getValue().toString());
	}
	
	public void sendCursorInFromSouth(int xPos, ValueGraphic vg){
//		super.getCompExGraphic().getCursor().setValueGraphic(this);
		setCursorPos(xPos);
	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1) throws Exception {
		// TODO right now prints toString and horizontal representation,
			// need to implement slash representations soon
		g.setFont(f);
		setFont(f);
		
		spaceAroundBar = (int) (2 * getCompExGraphic().DOC_ZOOM_LEVEL);
		sizeOverHang = (int) (2 * getCompExGraphic().DOC_ZOOM_LEVEL);
		
		if (style == Style.SLASH){
			String s = getValue().toString();
			int[] size = new int[2];
			size[0] = getCompExGraphic().getStringWidth(s, f);
			size[1] = getCompExGraphic().getFontHeight(f);
			setUpperHeight((int) Math.round(size[1]/2.0));
			setLowerHeight(getUpperHeight());
			super.setX1(x1);
			super.setY1(y1);
			super.setX2(x1 + size[0]);
			super.setY2(y1 + size[1]);
			return size;
		}
		else if (style == Style.HORIZONTAL){
			int numerator = getValue().getNumerator();
			int denominator = getValue().getDenominator();
			
			if (denominator != 1){
				int[] size = new int[2];
				int numerWidth = getCompExGraphic().getStringWidth("" + numerator, f);
				
				int denomWidth = getCompExGraphic().getStringWidth("" + denominator, f);
				if (numerWidth > denomWidth){
					size[0] = numerWidth + 2 * sizeOverHang;
				}
				else
				{
					size[0] = denomWidth + 2 * sizeOverHang;
				}
				size[1] = getCompExGraphic().getFontHeight(f) * 2 + 2 * spaceAroundBar + 1;
				setUpperHeight((int) Math.round(size[1]/2.0));
				setLowerHeight(getUpperHeight());
				super.setX1(x1);
				super.setY1(y1);
				super.setX2(x1 + size[0]);
				super.setY2(y1 + size[1]);
				return size;
			}
			else{
				String s = getValue().toString();
				int[] size = new int[2];
				size[0] = getCompExGraphic().getStringWidth(s, f);
				size[1] = getCompExGraphic().getFontHeight(f);
				setUpperHeight((int) Math.round(size[1]/2.0));
				setLowerHeight(getUpperHeight());
				super.setX1(x1);
				super.setY1(y1);
				super.setX2(x1 + size[0]);
				super.setY2(y1 + size[1]);
				return size;
			}
		}
		throw new Exception("error rendering fraction");
	}

	@Override
	public int[] requestSize(Graphics g, Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<ValueGraphic> getComponents() {
		// TODO Auto-generated method stub
		return new Vector<ValueGraphic>();
	}
	
	
}
