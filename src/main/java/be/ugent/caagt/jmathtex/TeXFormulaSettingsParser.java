/* TeXFormulaSettingsParser.java
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Parses predefined TeXFormula's from an XML-file.
 */
class TeXFormulaSettingsParser {
    
    public static final String RESOURCE_NAME = "TeXFormulaSettings.xml";
    public static final String CHARTODEL_MAPPING_EL = "Map";
    
    private Element root;
    
    public TeXFormulaSettingsParser() throws ResourceParseException {
        try {
            root = new SAXBuilder().build(
                    TeXFormulaSettingsParser.class.getResourceAsStream(RESOURCE_NAME))
                    .getRootElement();
            
        } catch (Exception e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        }
    }
    
    public String[] parseSymbolMappings() throws ResourceParseException {
        String[] mappings = new String[FontInfo.NUMBER_OF_CHAR_CODES];
        Element charToSymbol = root.getChild("CharacterToSymbolMappings");
        if (charToSymbol != null) // element present
            addToMap(charToSymbol.getChildren("Map"), mappings);
        return mappings;
    }
    
    public String[] parseDelimiterMappings() throws ResourceParseException {
        String[] mappings = new String[FontInfo.NUMBER_OF_CHAR_CODES];
        Element charToDelimiter = root.getChild("CharacterToDelimiterMappings");
        if (charToDelimiter != null) // element present
            addToMap(charToDelimiter.getChildren(CHARTODEL_MAPPING_EL),
                    mappings);
        return mappings;
    }
    
    private static void addToMap(List mapList, String[] table) throws ResourceParseException {
        for (Object obj : mapList) {
            Element map = (Element) obj;
            String ch = map.getAttributeValue("char");
            String symbol = map.getAttributeValue("symbol");
            // both attributes are required!
            if (ch == null)
                throw new XMLResourceParseException(RESOURCE_NAME, map.getName(),
                        "char", null);
            else if (symbol == null)
                throw new XMLResourceParseException(RESOURCE_NAME, map.getName(),
                        "symbol", null);
            if (ch.length() == 1) // valid element found
                table[ch.charAt(0)] =  symbol;
            else
                // only single-character mappings allowed, ignore others
                throw new XMLResourceParseException(RESOURCE_NAME, map.getName(),
                        "char",
                        "must have a value that contains exactly 1 character!");
        }
    }
    
    public Set<String> parseTextStyles() throws ResourceParseException {
        Set<String> res = new HashSet<String>();
        Element textStyles = root.getChild("TextStyles");
        if (textStyles != null) { // element present
            for (Object obj : textStyles.getChildren("TextStyle")) {
                Element style = (Element) obj;
                String name = style.getAttributeValue("name");
                if (name == null)
                    throw new XMLResourceParseException(RESOURCE_NAME, style
                            .getName(), "name", null);
                else
                    res.add(name);
            }
        }
        return res;
    }
}
