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

package math_rendering;

public class Cursor {
	
	private NodeGraphic nodeGraphic;
	private int pos;
	
	public Cursor(){}
	
	public Cursor(NodeGraphic vg, int p){
		setValueGraphic(vg);
		setPos(p);
	}

	public void setValueGraphic(NodeGraphic nodeGraphic) {
		this.nodeGraphic = nodeGraphic;
	}

	public NodeGraphic getValueGraphic() {
		return nodeGraphic;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}
}
