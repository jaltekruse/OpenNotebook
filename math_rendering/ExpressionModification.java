/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

import java.awt.Graphics;
import java.util.Vector;

public class ExpressionModification {

	Vector<ValueGraphic> expressions;
	
	public ExpressionModification(){
		expressions = new Vector<ValueGraphic>();
	}
	
	public void draw(){
		for (ValueGraphic vg : expressions){
			vg.drawAllBelow();
		}
	}
	
	public void addVG(ValueGraphic vg){
		expressions.add(vg);
	}
	
	public int getHeight(){
		if (expressions.size() > 0){
			return expressions.get(0).getHeight();
		}
		return 0;
	}
}
