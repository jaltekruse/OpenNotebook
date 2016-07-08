/* MfencedParser.java
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
import java.util.ListIterator;

import org.jdom.Element;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.ParseException;
import java.util.Arrays;

class MfencedParser extends PresentationElementParser {
    
    private static final String DEF_OPEN = "\\lbrack", DEF_CLOSE = "\\rbrack",
            DEF_SEP = ",";
    
    private static final String OPEN = "open", CLOSE = "close",
            SEP = "separators";
    
    public TeXFormula buildFormula(Element el, Environment env)
    throws MathMLException {
        List<TeXFormula> formulas = MathMLParser.getFormulaList(el.getChildren(), env);
        // get element attribute values
        String open = el.getAttributeValue(OPEN), close = el
                .getAttributeValue(CLOSE), sep = el.getAttributeValue(SEP);
        
        // if attribute values not set, use environment values if set
        if (open == null)
            open = env.get(OPEN);
        if (close == null)
            close = env.get(CLOSE);
        if (sep == null)
            sep = env.get(SEP);
        
        // if fence-attributes are still not set, use default values, otherwise
        // convert to TeX
        open = (open == null ? DEF_OPEN : convertMathMLToTeX(new Element("mo")
        .addContent(open)));
        close = (close == null ? DEF_CLOSE : convertMathMLToTeX(new Element("mo")
        .addContent(close)));
        
        // build separator-array
        if (sep == null) // if still not set, use default value
            sep = DEF_SEP;
        String[] separators = buildSeparatorArray(sep, formulas.size() - 1);
        
        try {
            int i = -1;
            ListIterator<TeXFormula> it = formulas.listIterator();
            // TODO: make for each ?
            while (it.hasNext()) {
                if (it.nextIndex() == 0)
                    it.add(new TeXFormula(open));
                else if (separators != null) // null means no separators at all
                    it.add(new TeXFormula(separators[i]));
                
                it.next();
                i++;
            }
            formulas.add(new TeXFormula(close));
        } catch (ParseException e) {
            throw new MathMLException(null, e);
        }
        return new TeXFormula(formulas);
    }
    
    private String[] buildSeparatorArray(String separators, int size) {
        String[] res = new String[size];
        
        // if there are too many separators, the last ones are ignored
        int sepCnt = 0;
        for (int i = 0; i < separators.length() && sepCnt < size; i++) {
            char ch = separators.charAt(i);
            if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') { // ignore
                // whitespace
                Object mapping = MathMLParser.getUnicodeMapping(ch);
                if (mapping == null)
                    res[sepCnt] = Character.toString(ch);
                else
                    res[sepCnt] = (String) mapping;
                sepCnt++;
            }
        }
        
        if (sepCnt == 0) // no separators!
            return null;
        else {
            // if there are too few separators, the last one is repeated
            Arrays.fill(res, sepCnt, size, res[sepCnt-1]);
            return res;
        }
    }
}
