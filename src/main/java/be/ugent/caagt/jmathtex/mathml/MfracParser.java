/* MfracParser.java
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

package be.ugent.caagt.jmathtex.mathml;

import java.util.List;

import org.jdom.Element;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.TeXConstants;

class MfracParser extends PresentationElementParser {

   private static final String THICKNESS = "linethickness",
         NUM_ALIGN = "numalign", DENOM_ALIGN = "denomalign";

   public TeXFormula buildFormula(Element el, Environment env)
         throws MathMLException {
      List formulas = MathMLParser.getFormulaList(el.getChildren(), env);

      if (formulas.size() != 2)
         throw new MathMLException("mfrac needs exactly 2 child elements!");

      // get element attribute values
      String lineThickness = el.getAttributeValue(THICKNESS), numAlign = el
            .getAttributeValue(NUM_ALIGN), denomAlign = el
            .getAttributeValue(DENOM_ALIGN);

      // if attribute values not set, use environment values if set
      if (lineThickness == null)
         lineThickness = env.get(THICKNESS);
      if (numAlign == null)
         numAlign = env.get(NUM_ALIGN);
      if (denomAlign == null)
         denomAlign = env.get(DENOM_ALIGN);
 
      // get unit and linethickness value
      float thickness = 1;
      boolean def = true;
      int unit = TeXConstants.UNIT_PIXEL;
      if (lineThickness != null) {
         lineThickness = lineThickness.trim();
         // ignore predefined linethickness values
         if (!"thin".equals(lineThickness) && !"thick".equals(lineThickness)
               && !"medium".equals(lineThickness)) {
            // unit doesn't have to be specified (see MathML-specification)
            NumberUnit nu = new NumberUnit(lineThickness, false);
            thickness = nu.getNumber();
            unit = nu.getUnit();
            if (unit != NumberUnit.NO_UNIT)
               def = false;
         }
      }

      if (def) // no unit -> use default thickness
         return ((TeXFormula) formulas.get(0)).fraction((TeXFormula) formulas
               .get(1), thickness, getAlignment(numAlign),
               getAlignment(denomAlign));
      else
         return ((TeXFormula) formulas.get(0)).fraction((TeXFormula) formulas
               .get(1), unit, thickness, getAlignment(numAlign),
               getAlignment(denomAlign));
   }

   private static int getAlignment(String align) throws MathMLException {
      if (align == null) 
         // default
         return TeXConstants.ALIGN_CENTER;
      else {
         if ("left".equals(align))
            return TeXConstants.ALIGN_LEFT;
         else if ("center".equals(align))
            return TeXConstants.ALIGN_CENTER;
         else if ("right".equals(align))
            return TeXConstants.ALIGN_RIGHT;
         else
            throw new MathMLException("Invalid alignment argument : '" + align
                  + "'!");
      }
   }
}
