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

package doc_gui.mathobject_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import doc.mathobjects.OvalObject;

public class OvalObjectGUI extends MathObjectGUI<OvalObject> {

	public void drawMathObject(OvalObject object, Graphics g, Point pageOrigin,
			float zoomLevel) {
		g.setColor(Color.BLACK);
		ScaledSizeAndPosition sap = getSizeAndPositionWithLineThickness(object, pageOrigin,
				zoomLevel, object.getThickness());

		Graphics2D g2d = (Graphics2D)g; 
		g2d.setStroke(new BasicStroke(sap.getLineThickness()));
		
		if (object.getColor() != null){
			g2d.setColor(object.getColor());
			g2d.fill(new Ellipse2D.Double(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight()));
		}
		g2d.setColor(Color.BLACK);
		g2d.draw(new Ellipse2D.Double(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight()));
		
		//reset graphics object to draw without additional thickness
		g2d.setStroke(new BasicStroke());
	}

}
