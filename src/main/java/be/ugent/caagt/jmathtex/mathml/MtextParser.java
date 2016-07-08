/* MtextParser.java
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

import org.jdom.Element;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.ParseException;

class MtextParser extends TokenElementParser {

   public TeXFormula buildFormula(Element el, Environment env)
         throws MathMLException {
      String texString = convertMathMLToTeX(el);
      if (texString.length() == 0)
         return super.processAtrributes(new TeXFormula(), el, env);
      else {
         try {
            return super.processAtrributes(new TeXFormula("\\mathrm{" + texString
                  + "}"), el, env);
         } catch (ParseException e) {
            throw new MathMLException("couldn't parse mtext element data : '"
                  + el.getTextNormalize() + "'", e);
         }
      }
   }
}
