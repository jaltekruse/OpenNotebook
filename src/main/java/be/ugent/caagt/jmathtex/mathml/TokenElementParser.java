/* TokenElementParser.java
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

abstract class TokenElementParser extends PresentationElementParser {
    
    private static final String BACKGROUND = "mathbackground",
            COLOR = "mathcolor";
    
    /**
     * Processes the two common attributes for all MathML token elements ("mathbackground"
     * and "mathcolor").
     *
     * @param formula the TeXFormula for which the colors must be set
     * 			(if attributes present)
     * @param el the MathML token element
     * @param env the rendering environment
     * @return the possibly modified TeXFormula
     * @throws MathMLException if the syntax of one of the color attributes was not correct
     */
    protected TeXFormula processAtrributes(TeXFormula formula, Element el,
            Environment env) throws MathMLException {
        // get element attribute values
        String background = el.getAttributeValue(BACKGROUND), color = el
                .getAttributeValue(COLOR);
        
        // if attribute values not set, use environment values if set
        if (background == null)
            background = env.get(BACKGROUND);
        if (color == null)
            color = env.get(COLOR);
        
        return formula.setBackground(MathMLParser.getColor(background)).setColor(
                MathMLParser.getColor(color));
    }
    
    // makes PMD happy
    public abstract TeXFormula buildFormula(Element el, Environment env) throws MathMLException;
}
