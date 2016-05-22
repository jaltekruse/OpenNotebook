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

import java.awt.Font;
import java.awt.Graphics;

import expression.Expression;

public abstract class ExpressionGraphic extends NodeGraphic<Expression> {

	protected int symbolX1, symbolX2, symbolY1, symbolY2;
	
	public ExpressionGraphic(Expression e, RootNodeGraphic compExGraphic) {
		super(e, compExGraphic);
	}

	@Override
	public void draw() {

	}
	
	public void drawCursor(int pos){
		
	}

	@Override
	public int[] requestSize(Graphics g, Font f) {
		return null;
	}

	@Override
	public int[] requestSize(Graphics g, Font f, int x1, int y1)
			throws Exception {
		return null;
	}

	@Override
	void shiftToX1(int x1) {
		int xChange = x1 - getX1();
		for (NodeGraphic vg : getComponents()){
			vg.shiftToX1(vg.getX1() + xChange);
		}
		setX2(getX2() + xChange);
		symbolX1 += xChange;
		symbolX2 += xChange;
		setX1(x1);
	}

	@Override
	void shiftToY1(int y1) {
		int yChange = y1 - getY1();
		for (NodeGraphic vg : getComponents()){
			vg.shiftToY1(vg.getY1() + yChange);
		}
		setY2(getY2() + yChange);
		symbolY1 += yChange;
		symbolY2 += yChange;
		setY1(y1);
	}
}
