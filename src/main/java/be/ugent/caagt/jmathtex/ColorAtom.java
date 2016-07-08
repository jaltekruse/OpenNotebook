/* ColorAtom.java
 * =========================================================================
 * This file is part of the JMathTeX Library - http://jmathtex.sourceforge.net
 * 
 * Copyright (C) 2004-2007 Universiteit Gent
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 * 
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 * 
 */

package be.ugent.caagt.jmathtex;

import java.awt.Color;

/**
 * An atom representing the foreground and background color of an other atom. 
 */
class ColorAtom extends Atom implements Row {

   // background color
   private final Color background;

   // foreground color
   private final Color color;

   // RowAtom for which the colorsettings apply
   private final RowAtom elements;

   /**
    * Creates a new ColorAtom that sets the given colors for the given atom.
    * Null for a color means: no specific color set for this atom.
    * 
    * @param atom the atom for which the given colors have to be set 
    * @param bg the background color
    * @param c the foreground color
    */
   public ColorAtom(Atom atom, Color bg, Color c) {
      elements = new RowAtom(atom);
      background = bg;
      color = c;
   }

   /**
    * Creates a ColorAtom that overrides the colors of the given ColorAtom if the given
    * colors are not null. If they're null, the old values are used.
    * 
    * @param bg the background color
    * @param c the foreground color
    * @param old the ColorAtom for which the colorsettings should be overriden with the
    * 			given colors.
    */
   public ColorAtom(Color bg, Color c, ColorAtom old) {
      elements = new RowAtom(old.elements);
      background = (bg == null ? old.background : bg);
      color = (c == null ? old.color : c);
   }

   public Box createBox(TeXEnvironment env) {
      TeXEnvironment copy = env.copy();
      if (background != null)
         copy.setBackground(background);
      if (color != null)
         copy.setColor(color);
      return elements.createBox(copy);
   }

   public int getLeftType() {
      return elements.getLeftType();
   }

   public int getRightType() {
      return elements.getRightType();
   }

   public void setPreviousAtom(Dummy prev) {
      elements.setPreviousAtom(prev);
   }

}
