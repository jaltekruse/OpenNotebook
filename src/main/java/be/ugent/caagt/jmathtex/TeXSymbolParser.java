/* TeXSymbolParser.java
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

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Parses TeX symbol definitions from an XML-file.
 */
class TeXSymbolParser {

   private static final String RESOURCE_DIR = "";

   public static final String RESOURCE_NAME = "TeXSymbols.xml",
         DELIMITER_ATTR = "del", TYPE_ATTR = "type";

   private static Map<String,Integer> typeMappings = new HashMap<String,Integer>();

   private Element root;

   public TeXSymbolParser() throws ResourceParseException {
      try {
         root = new SAXBuilder().build(
               getClass().getResourceAsStream(RESOURCE_DIR + RESOURCE_NAME))
               .getRootElement();
         // set possible valid symbol type mappings
         setTypeMappings();
      } catch (Exception e) { // JDOMException or IOException
         throw new XMLResourceParseException(RESOURCE_NAME, e);
      }
   }

   public Map<String,SymbolAtom> readSymbols() throws ResourceParseException {
      Map<String,SymbolAtom> res = new HashMap<String,SymbolAtom>();
      // iterate all "symbol"-elements
      for (Object obj : root.getChildren("Symbol")) {
         Element symbol = (Element) obj;
         // retrieve and check required attributes
         String name = getAttrValueAndCheckIfNotNull("name", symbol), type = getAttrValueAndCheckIfNotNull(
               TYPE_ATTR, symbol);
         // retrieve optional attribute
         String del = symbol.getAttributeValue(DELIMITER_ATTR);
         boolean isDelimiter = (del != null && del.equals("true"));
         // check if type is known
         Object typeVal = typeMappings.get(type);
         if (typeVal == null) // unknown type
            throw new XMLResourceParseException(RESOURCE_NAME, "Symbol",
                  "type", "has an unknown value '" + type + "'!");
         // add symbol to the hash table
         res.put(name, new SymbolAtom(name, ((Integer) typeVal).intValue(),
               isDelimiter));
      }
      return res;
   }

   private void setTypeMappings() {
      typeMappings.put("ord", TeXConstants.TYPE_ORDINARY);
      typeMappings.put("op", TeXConstants.TYPE_BIG_OPERATOR);
      typeMappings.put("bin", TeXConstants.TYPE_BINARY_OPERATOR);
      typeMappings.put("rel", TeXConstants.TYPE_RELATION);
      typeMappings.put("open", TeXConstants.TYPE_OPENING);
      typeMappings.put("close", TeXConstants.TYPE_CLOSING);
      typeMappings.put("punct", TeXConstants.TYPE_PUNCTUATION);
      typeMappings.put("acc", TeXConstants.TYPE_ACCENT);
   }

   private static String getAttrValueAndCheckIfNotNull(String attrName,
         Element element) throws ResourceParseException {
      String attrValue = element.getAttributeValue(attrName);
      if (attrValue == null)
         throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
               attrName, null);
      return attrValue;
   }
}
