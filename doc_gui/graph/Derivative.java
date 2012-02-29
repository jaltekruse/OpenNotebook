/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

import java.util.Vector;

public class Derivative extends GraphCalculation {

	public Derivative(Graph g, Vector<SingleGraph> graphs, Selection s)
	{//trying to think of a good way to restrict the selection used to be correct
		//a single point is required, instead of a range
		super(g, graphs, s);
		// TODO Auto-generated constructor stub
	}

}
