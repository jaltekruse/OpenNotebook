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

import java.awt.*;

import doc.PointInDocument;
import doc.mathobjects.LineObject;
import doc.mathobjects.MathObject;
import doc_gui.DocMouseListener;
import expression.NodeException;

public abstract class MathObjectGUI<K extends MathObject>{

	private static int resizeDotDiameter = 10;

	private static int resizeDotRadius = 5;

	private int currResizeDot;

	private boolean resizeDotBeingDragged;

	// Create one of these that will be reused during each rendering of an object
	// to avoid short lived object creation
	private ScaledSizeAndPosition cachedScaleAndPositionObject = new ScaledSizeAndPosition();

	/**
	 * Get a {@link ScaledSizeAndPosition} object for use in the various methods
	 * for drawing a particular type of MathObject.
	 * @return - description of position and measurements of the object taking into account the
	 *           the provided page origin and zoom level
	 */
	protected ScaledSizeAndPosition getSizeAndPosition(MathObject object, Point pageOrigin,
			float zoomLevel) {
		return cachedScaleAndPositionObject.populateValues(object, pageOrigin, zoomLevel);
	}

	/**
	 * Alternative to the method {@code getSizeAndPosition(MathObject, Point, float)} which will
	 * populate the font size of the {@link ScaledSizeAndPosition} object as well.
	 * @return - description of position and measurements of the object taking into account the
	 *           the provided page origin and zoom level
	 */
	protected ScaledSizeAndPosition getSizeAndPositionWithFontSize(MathObject object, Point pageOrigin,
																																 float zoomLevel, int fontSize) {
		return cachedScaleAndPositionObject.populateValuesWithFontSize(object, pageOrigin, zoomLevel, fontSize);
	}

	protected ScaledSizeAndPosition getSizeAndPositionWithLineThickness(MathObject object, Point pageOrigin,
																																 float zoomLevel, int lineThickness) {
		return cachedScaleAndPositionObject.populateValuesWithLineThickness(object, pageOrigin, zoomLevel, lineThickness);
	}

	protected ScaledSizeAndPosition getSizeAndPositionWithFontAndLineThickness(MathObject object, Point pageOrigin,
																																			float zoomLevel, int fontSize,
																																			int lineThickness) {
		return cachedScaleAndPositionObject.populateAllValues(object, pageOrigin, zoomLevel, fontSize, lineThickness);
	}

	/**
	 * Represents the value of the upper left resizing dot.
	 */
	public static final int NORTHWEST_DOT = 0;
	public static final int NORTH_DOT = 1;
	public static final int NORTHEAST_DOT = 2;
	public static final int EAST_DOT = 3;
	public static final int SOUTHEAST_DOT = 4;
	public static final int SOUTH_DOT = 5;
	public static final int SOUTHWEST_DOT = 6;
	public static final int WEST_DOT = 7;

	public abstract void drawMathObject(K object, Graphics g, Point pageOrigin, float zoomLevel);

  public Polygon getCollisionAndSelectionPolygon(MathObject mObj, Point pageOrigin, float zoomLevel) {
		int width = (int) (mObj.getWidth() * zoomLevel);
		int height = (int) (mObj.getHeight() * zoomLevel);
		int xPos = (int) (pageOrigin.getX() + mObj.getxPos() * zoomLevel);
		int yPos = (int) (pageOrigin.getY() + mObj.getyPos() * zoomLevel);
		int[] xVals = { xPos, xPos + width, xPos + width, xPos};
		int[] yVals = { yPos, yPos, yPos + height, yPos + height};
		return new Polygon(xVals, yVals, 4);
  }

  public void mouseClicked(MathObject mObj, int x , int y, float zoomLevel){}

  // returns true if this key event was consumed by this method
  public boolean keyPressed(MathObject mObj, char key) throws NodeException {
      return false;
  }

	public void drawInteractiveComponents(K object, Graphics g, Point pageOrigin, float zoomLevel){}

	public static void drawResizingDots(MathObject object, Graphics g, Point pageOrigin, float zoomLevel){

		if ( object instanceof LineObject ){
			boolean flipped = false;
			if ( object.isFlippedHorizontally()) flipped = !flipped;
			if ( object.isFlippedVertically()) flipped = !flipped;
			if ( flipped ){
				drawResizeDot(object, g, pageOrigin, zoomLevel, NORTHEAST_DOT);
				drawResizeDot(object, g, pageOrigin, zoomLevel, SOUTHWEST_DOT);
			}
			else{
				drawResizeDot(object, g, pageOrigin, zoomLevel, NORTHWEST_DOT);
				drawResizeDot(object, g, pageOrigin, zoomLevel, SOUTHEAST_DOT);
			}
			return;
		}
		else if ( object.isVerticallyResizable() && object.isHorizontallyResizable()){
			for (int i = NORTHWEST_DOT; i <= WEST_DOT; i++){
				drawResizeDot(object, g, pageOrigin, zoomLevel, i);
			}
			return;
		}
		if ( object.isVerticallyResizable()){
			drawResizeDot(object, g, pageOrigin, zoomLevel, NORTH_DOT);
			drawResizeDot(object, g, pageOrigin, zoomLevel, SOUTH_DOT);
			return;
		}
		if ( object.isHorizontallyResizable()){
			drawResizeDot(object, g, pageOrigin, zoomLevel, WEST_DOT);
			drawResizeDot(object, g, pageOrigin, zoomLevel, EAST_DOT);
			return;
		}

	}

	/**
	 * Draws a single white circle (dot) used to show a user that they can click and drag
	 * to resize an object.
	 * 
	 * @param object - the object able to be resized
	 * @param g - the graphics object to draw on
	 * @param pageOrigin - the point on the graphics object where the objects parent page
	 * 
	 * @param zoomLevel
	 * @param dot
	 */
	private static void drawResizeDot(MathObject object, Graphics g, Point pageOrigin, float zoomLevel, int dot){
		Point p;
		p = getPosResizeDot(object, pageOrigin, zoomLevel, dot);
		g.setColor(Color.WHITE);
		g.fillOval( (int) p.getX(), (int) p.getY(), resizeDotDiameter, resizeDotDiameter);
		g.setColor(Color.BLACK);
		g.drawOval((int) p.getX(), (int) p.getY(), resizeDotDiameter, resizeDotDiameter);
	}

	public static Point getPosResizeDot(MathObject object, Point pageOrigin, float zoomLevel, int dotVal){
		if (object instanceof LineObject){
			return getPosResizeDot(object, pageOrigin, zoomLevel, dotVal, false);
		}
		return getPosResizeDot(object, pageOrigin, zoomLevel, dotVal, true);
	}
	
	private static Point getPosResizeDot(MathObject object, Point pageOrigin,
			float zoomLevel, int dotVal, boolean outSideObject){
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		
		int shiftIn, shiftOut;
		if ( outSideObject ){
			shiftIn = 0;
			shiftOut = resizeDotDiameter;
		}
		else{
			shiftIn = resizeDotRadius;
			shiftOut = resizeDotRadius;
		}

		if (dotVal == NORTHWEST_DOT)
			return new Point(xOrigin - shiftOut, yOrigin - shiftOut);
		else if (dotVal == NORTH_DOT)
			return new Point(xOrigin + width/2 - resizeDotRadius, yOrigin - shiftOut);
		else if (dotVal == NORTHEAST_DOT)
			return new Point(xOrigin + width - shiftIn, yOrigin - shiftOut);
		else if (dotVal == EAST_DOT)
			return new Point(xOrigin + width - shiftIn, yOrigin  + height/2 - resizeDotRadius);
		else if (dotVal == SOUTHEAST_DOT)
			return new Point(xOrigin + width - shiftIn, yOrigin + height - shiftIn);
		else if (dotVal == SOUTH_DOT)
			return new Point(xOrigin + width/2 - resizeDotRadius, yOrigin + height - shiftIn);
		else if (dotVal == SOUTHWEST_DOT)
			return new Point(xOrigin - shiftOut, yOrigin + height - shiftIn);
		else if (dotVal == WEST_DOT)
			return new Point(xOrigin - shiftOut, yOrigin + height/2 - resizeDotRadius);
		else{
			// TODO - logging?
//			System.out.println("Invalid resize dot value");
			return null;
		}
	}

	public static Color brightenColor(Color c){
		if ( c.equals(Color.WHITE)){
			return c;
		}
		else{
			int newRed = (int) (c.getRed() * 1.4);
			if (newRed > 255) newRed = 255;
			int newGreen = (int) (c.getGreen() * 1.4);
			if (newGreen > 255) newGreen = 255;
			int newBlue = (int) (c.getBlue() * 1.4);
			if (newBlue > 255) newBlue = 255;
			
			return new Color(newRed, newGreen, newBlue);
		}
	}

	public static int detectResizeDotCollision(Point clickPos, MathObject object,
			Point pageOrigin, float zoomLevel){

		if ( object instanceof LineObject){
			boolean flipped = false;
			if ( ((LineObject)object).isFlippedHorizontally()) flipped = !flipped;
			if ( ((LineObject)object).isFlippedVertically()) flipped = !flipped;
			if ( flipped ){
				if (detectDotCollision(NORTHEAST_DOT, clickPos, object, pageOrigin, zoomLevel))
					return NORTHEAST_DOT;
				else if (detectDotCollision(SOUTHWEST_DOT, clickPos, object, pageOrigin, zoomLevel))
					return SOUTHWEST_DOT;
			}
			else{
				if (detectDotCollision(NORTHWEST_DOT, clickPos, object, pageOrigin, zoomLevel))
					return NORTHWEST_DOT;
				else if (detectDotCollision(SOUTHEAST_DOT, clickPos, object, pageOrigin, zoomLevel))
					return SOUTHEAST_DOT;
			}
			return Integer.MAX_VALUE;
		}
		if ( object.isVerticallyResizable() && object.isHorizontallyResizable()){
			for (int i = NORTHWEST_DOT; i <= WEST_DOT; i++){
				if (detectDotCollision(i, clickPos, object, pageOrigin, zoomLevel))
					return i;
			}
		}
		if ( object.isVerticallyResizable()){
			if (detectDotCollision(NORTH_DOT, clickPos, object, pageOrigin, zoomLevel))
				return NORTH_DOT;
			else if (detectDotCollision(SOUTH_DOT, clickPos, object, pageOrigin, zoomLevel))
				return SOUTH_DOT;
		}
		if ( object.isHorizontallyResizable()){
			if (detectDotCollision(NORTH_DOT, clickPos, object, pageOrigin, zoomLevel))
				return NORTH_DOT;
			else if (detectDotCollision(EAST_DOT, clickPos, object, pageOrigin, zoomLevel))
				return EAST_DOT;
		}
		return Integer.MAX_VALUE;
	}
	
	public static boolean detectDotCollision(int dotVal, Point clickPos, MathObject object,
			Point pageOrigin, float zoomLevel){
		int extraBuffer = 1;
		Point p = getPosResizeDot(object, pageOrigin, zoomLevel, dotVal);
		if ( Math.sqrt( Math.pow( (p.getX() + resizeDotRadius - clickPos.getX() ), 2) +
				Math.pow( (p.getY() + resizeDotRadius - clickPos.getY() ), 2))
				<= resizeDotRadius + extraBuffer){
			return true;
		}
		return false;
	}

	public static boolean detectObjectCollision(Point clickPos, MathObject object,
			Point pageOrigin, float zoomLevel){
		//(x0,y0)----(x1,y0)
		//  |           |
		//  |           |
		//(x0,y1)----(x1,y1)
		int x0 = (int) (object.getxPos() * zoomLevel + pageOrigin.getX());
		int x1 = (int) ((object.getxPos() + object.getWidth()) * zoomLevel + pageOrigin.getX());
		int y0 = (int) (object.getyPos() * zoomLevel + pageOrigin.getY());
		int y1 = (int) ((object.getyPos() + object.getHeight()) * zoomLevel + pageOrigin.getY());
		int x =  (int) clickPos.getX();
		int y = (int) clickPos.getY();

		Rectangle bigRect = new Rectangle(x0 - resizeDotDiameter, y0 - resizeDotDiameter,
				x1 - x0 + 2 * resizeDotDiameter, y1 - y0 + 2 * resizeDotDiameter);

		if (bigRect.contains(x,y)){
			return true;
		}
		return false;
	}

	public static boolean detectBorderCollision(Point clickPos, MathObject object,
			Point pageOrigin, float zoomLevel){
		//(x0,y0)----(x1,y0)
		//  |           |
		//  |           |
		//(x0,y1)----(x1,y1)
		int x0 = (int) (object.getxPos() * zoomLevel + pageOrigin.getX());
		int x1 = (int) ((object.getxPos() + object.getWidth()) * zoomLevel + pageOrigin.getX());
		int y0 = (int) (object.getyPos() * zoomLevel + pageOrigin.getY());
		int y1 = (int) ((object.getyPos() + object.getHeight()) * zoomLevel + pageOrigin.getY());
		int x =  (int) clickPos.getX();
		int y = (int) clickPos.getY();

		Rectangle smallRect = new Rectangle(x0 , y0 ,
				x1 - x0 , y1 - y0 - 2 );

		Rectangle bigRect = new Rectangle(x0 - resizeDotDiameter, y0 - resizeDotDiameter,
				x1 - x0 + 2 * resizeDotDiameter, y1 - y0 + 2 * resizeDotDiameter);

		if (bigRect.contains(x,y) && ! smallRect.contains(x,y)){
			return true;
		}
		return false;
	}

	public static void moveBoxToPoint(Point clickPos, MathObject object,
			Point pageOrigin, float zoomLevel, int xBoxOffset, int yBoxOffset){
		object.setxPos((int) ( (clickPos.getX() - pageOrigin.getX() - xBoxOffset * zoomLevel) / zoomLevel ));
		object.setyPos((int) ( (clickPos.getY() - pageOrigin.getY() - yBoxOffset * zoomLevel) / zoomLevel ));
		if (object.getxPos() + object.getWidth() > object.getParentPage().getWidth()){
			object.setxPos(object.getParentPage().getWidth() - object.getWidth());
		}
		else if (object.getxPos() < 0){
			object.setxPos( 0 );
		}
		if (object.getyPos() < 0){
			object.setyPos( 0 );
		}
		else if (object.getyPos() + object.getHeight() > object.getParentPage().getHeight()){
			object.setyPos(object.getParentPage().getHeight() - object.getHeight());
		}
	}

	/**
	 * @param dotVal - the code for one of the 8 dots
	 * @param dragPos - a point on the page in the user space
	 */
	public static void moveResizeDot(MathObject object, int dotVal,
			PointInDocument dragPos, DocMouseListener docMouseListener){
		int verticalMovement, horizontalMovement;

		// TODO
		// there is a bug with how the object handles positioning the corner if you 
		// cross over an edge with a drag dot, these values below could be used to solve
		// the problem later
		int prevX = object.getxPos(), prevY = object.getyPos(),
				prevWidth = object.getWidth(), prevHeight = object.getHeight();

		if (isSouthDot(dotVal)){
			verticalMovement = ( dragPos.getyPos() - object.getyPos() - object.getHeight());
			object.setHeight(object.getHeight() + verticalMovement);
		}
		else if (isNorthDot(dotVal)){
			verticalMovement = ( dragPos.getyPos() - object.getyPos());
			object.setHeight(object.getHeight() - verticalMovement);
			object.setyPos(object.getyPos() + verticalMovement);
		}
		if (isWestDot(dotVal)){
			horizontalMovement = ( dragPos.getxPos() - object.getxPos());
			object.setWidth(object.getWidth() - horizontalMovement);
			object.setxPos(object.getxPos() + horizontalMovement);
		}
		else if (isEastDot(dotVal)){
			horizontalMovement = ( dragPos.getxPos() - (object.getxPos() + object.getWidth()));
			object.setWidth(object.getWidth() + horizontalMovement);
		}
		
		if (object.getHeight() <= 0){
			object.flipVertically();
			object.setHeight(Math.abs(object.getHeight()));
			object.setyPos(object.getyPos() - object.getHeight());
			docMouseListener.setCurrentDragDot(getVerticallyOppositeDot(docMouseListener.getCurrentDragDot()));
		}

		if (object.getWidth() <= 0){
			object.flipHorizontally();
			object.setWidth(Math.abs(object.getWidth()));
			object.setxPos(object.getxPos() - object.getWidth());
			docMouseListener.setCurrentDragDot(getHorizontallyOppositeDot(docMouseListener.getCurrentDragDot()));
		}
		if (object.getHeight() == 0){
			object.setHeight(1);
		}
		if (object.getWidth() == 0){
			object.setWidth(1);
		}
	}

	public static boolean isNorthDot(int dotVal){
		return ( dotVal == NORTH_DOT || dotVal == NORTHWEST_DOT
				|| dotVal == NORTHEAST_DOT );
	}

	public static boolean isSouthDot(int dotVal){
		return ( dotVal == SOUTH_DOT || dotVal == SOUTHWEST_DOT
				|| dotVal == SOUTHEAST_DOT );
	}

	public static boolean isEastDot(int dotVal){
		return ( dotVal == EAST_DOT || dotVal == SOUTHEAST_DOT
				|| dotVal == NORTHEAST_DOT );
	}

	public static boolean isWestDot(int dotVal){
		return ( dotVal == WEST_DOT || dotVal == SOUTHWEST_DOT
				|| dotVal == NORTHWEST_DOT );
	}

	private static int getVerticallyOppositeDot(int dotVal){
		if (dotVal == NORTH_DOT) return SOUTH_DOT;
		else if (dotVal == SOUTH_DOT) return NORTH_DOT;
		else if (dotVal == SOUTHEAST_DOT) return NORTHEAST_DOT;
		else if (dotVal == SOUTHWEST_DOT) return NORTHWEST_DOT;
		else if (dotVal == NORTHWEST_DOT) return SOUTHWEST_DOT;
		else if (dotVal == NORTHEAST_DOT) return SOUTHEAST_DOT;
		// could throw error, but the method is private and simple
		else return Integer.MAX_VALUE;
	}

	private static int getHorizontallyOppositeDot(int dotVal){
		if (dotVal == EAST_DOT) return WEST_DOT;
		else if (dotVal == WEST_DOT) return EAST_DOT;
		else if (dotVal == SOUTHEAST_DOT) return SOUTHWEST_DOT;
		else if (dotVal == SOUTHWEST_DOT) return SOUTHEAST_DOT;
		else if (dotVal == NORTHWEST_DOT) return NORTHEAST_DOT;
		else if (dotVal == NORTHEAST_DOT) return NORTHWEST_DOT;
		// could throw error, but the method is private and simple
		else return Integer.MAX_VALUE;
	}

	public void setResizeDotBeingDragged(boolean resizeDotBeingDragged) {
		this.resizeDotBeingDragged = resizeDotBeingDragged;
	}

	public boolean resizeDotBeingDragged() {
		return resizeDotBeingDragged;
	}

	public void setCurrResizeDot(int currResizeDot) {
		this.currResizeDot = currResizeDot;
	}

	public int getCurrResizeDot() {
		return currResizeDot;
	}
}
