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
