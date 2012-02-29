/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

public class CalcInfoBox {

	//storage for where on the calculation the user clicked to generate box
	double xClick, yClick;
	
	//storage for where the upper left corner of the box is currently Located
	int x, y;
	
	//the position of the box depends on the graphics object, so it cannot be determined
	//until it is drawn for the first time, this constant is stored in the x and y values
	//as a flag to indicate that it has not been drawn
	public static final int NOT_DRAWN_YET = Integer.MAX_VALUE;
	private int width, height;
	private GraphCalculation calc;
	
	public CalcInfoBox(GraphCalculation gc, double xClick, double yClick){
		calc = gc;
		this.xClick = xClick;
		this.yClick = yClick;
		x = y = NOT_DRAWN_YET;
	}
	
	public GraphCalculation getGraphCalc(){
		return calc;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public double getxClick(){
		return xClick;
	}
	
	public double getyClick(){
		return yClick;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void setWidth(int i){
		width = i;
	}
	
	public void setHeight(int i){
		height = i;
	}
	
	public int getHeight(){
		return height;
	}
}
