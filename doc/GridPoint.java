/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc;

import doc_gui.graph.CartAxis;

public class GridPoint implements Cloneable {

	double x, y;
	
	public GridPoint(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public GridPoint() {
	}

	public void setx(double x){
		this.x = x;
	}
	
	public void sety(double y){
		this.y = y;
	}
	
	public double getx(){
		return x;
	}
	
	public double gety(){
		return y;
	}
	
	@Override
	public GridPoint clone(){
		return new GridPoint(x, y);
	}
	
	@Override
	public String toString(){
		return "(" + CartAxis.doubleToString(x, 1) + "," +
				CartAxis.doubleToString(y, 1) + ")";
	}
}
