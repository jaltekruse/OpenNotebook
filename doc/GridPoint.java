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

package doc;

import doc_gui.graph.CartAxis;

public class GridPoint implements Cloneable {

	double x, y;
	
	public GridPoint(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public GridPoint() {
	}

	public void setx(double x){
		this.x = x;
	}
	
	public void sety(double y){
		this.y = y;
	}
	
	public double getx(){
		return x;
	}
	
	public double gety(){
		return y;
	}
	
	@Override
	public GridPoint clone(){
		return new GridPoint(x, y);
	}
	
	@Override
	public String toString(){
		return "(" + CartAxis.doubleToString(x, 1) + "," +
				CartAxis.doubleToString(y, 1) + ")";
	}
}
