/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

import java.awt.Color;
import java.awt.Graphics;

import expression.Node;
import expression.NodeException;

public class GraphWithExpression extends SingleGraph{

/**
 * A Function stores all of the data necessary for graphing.
 * 
 * @author jason
 *
 */
	private String funcEqtn;
	private boolean graphing;
	private boolean connected;
	private String independentVar;
	private String dependentVar;
	private Node expression;
	
	/**
	 * The default constructor, set the equation equal to an empty string,
	 * makes it not currently graphing, integral and tracing values are
	 * false.
	 */
	public GraphWithExpression( Graph g, Color c) {
		super(g);
		funcEqtn = "";
		graphing = false;
		connected = true;
		setColor(c);
		setIndependentVar("x");
		setDependentVar("y");
	}
	
	public GraphWithExpression(String s, Graph g, Color c) throws NodeException {
		super(g);
		funcEqtn = s;
		graphing = false;
		connected = true;
		setColor(c);
		setIndependentVar("x");
		setDependentVar("y");
		expression = Node.parseNode(s);
	}
	
	/**
	 * Constructor that takes all attributes of a function.
	 * 
	 * @param eqtn - string of equation
	 * @param ind - string of independent var
	 * @param dep - string of dependent var
	 * @param connected - boolean for points to be connected when graphed
	 * @param c - a color to display the function with
	 * @throws NodeException 
	 */
	public GraphWithExpression(Graph g, String eqtn,
			String ind, String dep, boolean connected, Color c) throws NodeException {
		super(g);
		setIndependentVar(ind);
		setDependentVar(dep);
		graphing = true;
		this.connected = connected;
		funcEqtn = eqtn;
		setColor(c);
		expression = Node.parseNode(eqtn);
	}

	public void setFuncEqtn(String s) throws NodeException {
		funcEqtn = s;
		expression = Node.parseNode(s);
	}

	public String getFuncEqtn() {
		return funcEqtn;
	}

	public void setGraphing(boolean graphing) {
		this.graphing = graphing;
	}

	public boolean isGraphing() {
		return graphing;
	}
	
	public void setIndependentVar(String varName) {
		independentVar = varName;
	}
	
	public void setDependentVar(String varName) {
		dependentVar = varName;
	}


	public String getIndependentVar() {
		return independentVar;
	}
	
	public String getDependentVar() {
		return dependentVar;
	}

	public void setConneted(boolean conneted) {
		this.connected = conneted;
	}

	public boolean isConnected() {
		return connected;
	}

	@Override
	public void draw(Graphics g) throws NodeException {
	}
}