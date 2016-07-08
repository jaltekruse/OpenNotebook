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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;

import expression.EmptyValue;

public class NothingGraphic extends NodeGraphic<EmptyValue> {

	public NothingGraphic(EmptyValue v, RootNodeGraphic compExGraphic) {
		super(v, compExGraphic);
		setMostInnerWest(this);
		setMostInnerEast(this);
		setMostInnerNorth(this);
		setMostInnerSouth(this);
	}

	@Override
	public void draw() {
		getRootNodeGraphic().getGraphics().setFont(getFont());
		super.getRootNodeGraphic().getGraphics().setStroke(new BasicStroke(
				(int) (1 * super.getRootNodeGraphic().DOC_ZOOM_LEVEL)));
		getRootNodeGraphic().getGraphics().drawRect(getX1(), getY1(), getX2() - getX1(), getY2() - getY1());
		super.getRootNodeGraphic().getGraphics().setStroke(new BasicStroke());
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
		g.setFont(f);
		setFont(f);
		
		
		//random character that happens to be the size needed
		String s = "8";
		
		int[] size = new int[2];
		size[0] = getRootNodeGraphic().getStringWidth(s, f);
		size[1] = getRootNodeGraphic().getFontHeight(f);
		setUpperHeight((int) Math.round(size[1]/2.0));
		setLowerHeight(getUpperHeight());
		super.setX1(x1);
		super.setY1(y1);
		super.setX2(x1 + size[0]);
		super.setY2(y1 + size[1]);
		return size;
	}

	@Override
	public Vector getComponents() {
		return new Vector<>();
	}

}
