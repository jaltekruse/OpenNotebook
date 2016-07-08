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
	
	public String toString(){
		return start + "," + end;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (! (other instanceof Selection)) {
			return false;
		} else {
			Selection otherSelection = (Selection) other;
			if (otherSelection.getStart() == getStart() && otherSelection.getEnd() == getEnd()) {
				return true;
			} else {
				return false;
			}
		}
	}
}
