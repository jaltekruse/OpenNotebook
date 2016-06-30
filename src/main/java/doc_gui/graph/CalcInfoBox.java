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

public class CalcInfoBox {

	//storage for where on the calculation the user clicked to generate box
	double xClick, yClick;
	
	//storage for where the upper left corner of the box is currently Located
	int x, y;
	
	//the position of the box depends on the graphics object, so it cannot be determined
	//until it is drawn for the first time, this constant is stored in the x and y values
	//as a flag to indicate that it has not been drawn
	public static final int NOT_DRAWN_YET = Integer.MAX_VALUE;
	private int width, height;
	private GraphCalculation calc;
	
	public CalcInfoBox(GraphCalculation gc, double xClick, double yClick){
		calc = gc;
		this.xClick = xClick;
		this.yClick = yClick;
		x = y = NOT_DRAWN_YET;
	}
	
	public GraphCalculation getGraphCalc(){
		return calc;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public double getxClick(){
		return xClick;
	}
	
	public double getyClick(){
		return yClick;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void setWidth(int i){
		width = i;
	}
	
	public void setHeight(int i){
		height = i;
	}
	
	public int getHeight(){
		return height;
	}
}
