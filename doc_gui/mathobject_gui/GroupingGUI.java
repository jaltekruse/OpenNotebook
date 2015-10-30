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

import doc.mathobjects.Grouping;
import doc_gui.PageGUI;

import java.awt.*;

public class GroupingGUI extends MathObjectGUI<Grouping> {

  private final PageGUI pageGui;

  public GroupingGUI(PageGUI pageGui) {
    this.pageGui = pageGui;
  }

  @Override
  public void drawMathObject(Grouping object, Graphics g, Point pageOrigin, float zoomLevel) {

  }
}
