/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

public class Selection {

	private double start, end;
	public static final double EMPTY = Double.MAX_VALUE;
	
	public Selection(){
		start = end = EMPTY;
	}
	
	public Selection(double a){
		start = a;
		end = EMPTY;
	}
	
	public Selection(double a, double b){
		start = a;
		end = b;
	}
	
	public void setStart(double d){
		start = d;
	}

	public void setEnd(double d){
		end = d;
	}
	
	public double getStart(){
		return start;
	}
	
	public double getEnd(){
		return end;
	}
}
