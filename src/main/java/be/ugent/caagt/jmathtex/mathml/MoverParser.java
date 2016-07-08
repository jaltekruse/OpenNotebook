/* MoverParser.java
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
import be.ugent.caagt.jmathtex.JMathTeXException;
import be.ugent.caagt.jmathtex.TeXFormula;

class MoverParser extends PresentationElementParser {

   private static final String ACCENT = "accent";

   public TeXFormula buildFormula(Element el, Environment env)
         throws MathMLException {
      List formulas = MathMLParser.getFormulaList(el.getChildren(), env);
      if (formulas.size() != 2)
         throw new MathMLException("mover needs exactly 2 child elements!");

      // construct formula
      try {
         return new TeXFormula().addAcc((TeXFormula) formulas.get(0),
               ((TeXFormula) formulas.get(1)));
      } catch (JMathTeXException e) { // use other methods
         // get element attribute value
         String accent = el.getAttributeValue(ACCENT);

         // if attribute value not set, use environment value if set
         if (accent == null)
            accent = env.get(ACCENT);

         if (accent != null && accent.equals("true"))
            // units are guaranteed to be valid
            return ((TeXFormula) formulas.get(0)).putUnderAndOver(null,
                  ACC_LIM_UNIT, 0, false, (TeXFormula) formulas.get(1), ACC_LIM_UNIT,
                  ACC_SPACE, false);
         else if (accent == null || accent.equals("false")) // default
            return new TeXFormula().addOp((TeXFormula) formulas.get(0), null,
                  (TeXFormula) formulas.get(1), true);
         else
            throw new MathMLException(
                  "Invalid attribute value for 'accent' : '" + accent + "'!", e);
      }
   }
}
