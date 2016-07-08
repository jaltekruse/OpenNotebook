/* MunderoverParser.java
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

class MunderoverParser extends PresentationElementParser {

   private static final String ACC_UNDER = "accentunder", ACC_OVER = "accent";

   private static final float LIM_SPACE = 0.8f;

   public TeXFormula buildFormula(Element el, Environment env)
         throws MathMLException {
      List formulas = MathMLParser.getFormulaList(el.getChildren(), env);
      if (formulas.size() != 3)
         throw new MathMLException("munderover needs exactly 3 child elements!");

      // get element attribute value
      String accentUnder = el.getAttributeValue(ACC_UNDER), accentOver = el
            .getAttributeValue(ACC_OVER);

      // if attribute value not set, use environment value if set
      if (accentUnder == null)
         accentUnder = env.get(ACC_UNDER);
      if (accentOver == null)
         accentOver = env.get(ACC_OVER);

      boolean accent = false;
      float u = LIM_SPACE, o = LIM_SPACE;
      boolean uScript = true, oScript = true;
      if (accentUnder != null && accentUnder.equals("true")) {
         accent = true;
         u = ACC_SPACE;
         uScript = false;
      }
      if (accentOver != null && accentOver.equals("true")) {
         accent = true;
         o = ACC_SPACE;
         oScript = false;
      }

      if (accent)
         // units are guranteed to be valid
         return ((TeXFormula) formulas.get(0)).putUnderAndOver(
               (TeXFormula) formulas.get(1), ACC_LIM_UNIT, u, uScript,
               (TeXFormula) formulas.get(2), ACC_LIM_UNIT, o, oScript);
      else
         // default
         return new TeXFormula()
               .addOp((TeXFormula) formulas.get(0), (TeXFormula) formulas
                     .get(1), (TeXFormula) formulas.get(2), true);
   }
}
