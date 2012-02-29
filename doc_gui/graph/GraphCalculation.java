/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

import java.util.Vector;

public class GraphCalculation {
	
	private Graph graph;
	private Vector<SingleGraph> involvedGraphs;
	private Selection selection;
	private double result;
	private double gridx, gridy;
	public static final double EMPTY = Double.MAX_VALUE;
	
	public GraphCalculation(Graph g, Vector<SingleGraph> graphs, Selection s){
		setGraph(g);
		setInvolvedGraphs(graphs);
		setSelection(s);
		setResult(EMPTY);
	}

	public void setInvolvedGraphs(Vector<SingleGraph> involvedGraphs) {
		this.involvedGraphs = involvedGraphs;
	}

	public Vector<SingleGraph> getInvolvedGraphs() {
		return involvedGraphs;
	}

	public void setSelection(Selection selection) {
		this.selection = selection;
	}

	public Selection getSelection() {
		return selection;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public double getResult() {
		return result;
	}

	public void setGridX(double x) {
		this.gridx = x;
	}

	public double getGridX() {
		return gridx;
	}

	public void setGridY(double y) {
		this.gridy = y;
	}

	public double getGridY() {
		return gridy;
	}
	
}
