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
