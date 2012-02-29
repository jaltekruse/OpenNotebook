/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

import java.util.Vector;

import expression.Node;
import expression.NodeException;


public class Integral extends GraphCalculation {

	public Integral(Graph g, Vector<SingleGraph> graphs, Selection s) {
		super(g, graphs, s);
		double result;
		try {
			result = Node.parseNode(((GraphWithExpression)graphs.get(0)).getFuncEqtn()).integrate(
					getSelection().getStart(), getSelection().getEnd(),"x", "y").getValue();
			setResult(result);
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
