/* PresentationElementParser.java
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
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;

abstract class PresentationElementParser {
    
    /**
     * Space between base and underscript/overscript, if marked as accent.
     * Used by elements "mover", "munder" and "munderover"
     */
    protected static final float ACC_SPACE = 0.2f;
    
    /**
     * unit used for ACC_SPACE en LIM_SPACE ("munderover")
     */
    protected static final int ACC_LIM_UNIT = TeXConstants.UNIT_EX;
    
    /**
     * Parses the given element and transforms it into a TeXFormula, using the
     * given rendering environment.
     *
     * @param el the element to be parsed and transformed into a TeXFormula
     * @param env the rendering environment for the given element
     * @return the transformed TeXFormula
     * @throws MathMLException if the MathML syntax wasn't correct
     */
    public abstract TeXFormula buildFormula(Element el, Environment env)
    throws MathMLException;
    
    /**
     * Gets the string content of the given MathML token element, removes all leading
     * and trailing whitespace, normalizes internal whitespace (according to the MathML
     * specification), replaces remaining internal whitespace by the special "\nbsp"
     * JmathTeX command (a hard space) and replaces all unicode characters in the string
     * with their TeX mapping (if defined).
     *
     * @param el a MathML token element
     * @return the string content of the given element, with all whitespace normalized
     * 			and all unicode characters replaced with their TeX mapping (if defined)
     */
    protected String convertMathMLToTeX(Element el) {
        String mathMLString = el.getTextNormalize();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < mathMLString.length(); i++) {
            char ch = mathMLString.charAt(i);
            if (ch == ' ') // space not to be ignored!!
                buf.append("\\nbsp");
            else {
                Object mapping = MathMLParser.getUnicodeMapping(ch);
                if (mapping == null)
                    buf.append(ch);
                else
                    buf.append("{" + (String) mapping + "}");
            }
        }
        return buf.toString();
    }
}
