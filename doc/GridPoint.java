/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc;

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
		String output = "";
		output += "(";
		String xString = Double.toString(x);
		if (xString.length() > 6)
			output += xString.substring(0, 6);
		else
			output += xString;
		output += ",";
		String yString = Double.toString(y);
		if (yString.length() > 6)
			output += yString.substring(0, 6);
		else
			output += yString;
		output += ")";
		return output;
	}
}
