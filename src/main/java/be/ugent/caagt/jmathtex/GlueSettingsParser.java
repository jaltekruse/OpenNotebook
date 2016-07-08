/* GlueSettingsParser.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Parses the glue settings (different types and rules) from an XML-file.
 */
class GlueSettingsParser {
    
    private static final String RESOURCE_NAME = "GlueSettings.xml";
    
    private final Map<String,Integer> typeMappings = new HashMap<String,Integer>();
    private final Map<String,Integer> glueTypeMappings = new HashMap<String,Integer>();
    private Glue[] glueTypes;
    
    private final Map<String,Integer> styleMappings = new HashMap<String,Integer>();
    
    private Element root;
    
    public GlueSettingsParser() throws ResourceParseException {
        try {
            setTypeMappings();
            setStyleMappings();
            
            root = new SAXBuilder().build(
                    GlueSettingsParser.class.getResourceAsStream(RESOURCE_NAME))
                    .getRootElement();
            
            parseGlueTypes();
        } catch (IOException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        } catch (JDOMException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        }
    }
    
    private void setStyleMappings() {
        styleMappings.put("display", TeXConstants.STYLE_DISPLAY / 2);
        styleMappings.put("text", TeXConstants.STYLE_TEXT / 2);
        styleMappings.put("script", TeXConstants.STYLE_SCRIPT / 2);
        styleMappings.put("script_script", TeXConstants.STYLE_SCRIPT_SCRIPT / 2); // autoboxing
    }
    
    private void parseGlueTypes() throws ResourceParseException {
        List<Glue> glueTypesList = new ArrayList<Glue> ();
        Element types = root.getChild("GlueTypes");
        int defaultIndex = -1;
        int index = 0;
        if (types != null) { // element present
            for (Object obj : types.getChildren("GlueType")) {
                Element type = (Element) obj;
                // retrieve required attribute value, throw exception if not set
                String name = getAttrValueAndCheckIfNotNull("name", type);
                Glue glue = createGlue(type, name);
                if (name.equalsIgnoreCase("default")) // default must have value
                    defaultIndex = index;
                glueTypesList.add(glue);
                index ++;
            }
        }
        if (defaultIndex < 0) {
            // create a default glue object if missing
            defaultIndex = index;
            glueTypesList.add(new Glue(0,0,0,"default"));
        }
        
        glueTypes = glueTypesList.toArray(new Glue[glueTypesList.size()]);
        
        // make sure default glue is at the front
        if (defaultIndex > 0) {
            Glue tmp = glueTypes[defaultIndex];
            glueTypes[defaultIndex] = glueTypes[0];
            glueTypes[0] = tmp;
        }
        
        // make reverse map
        for (int i = 0; i < glueTypes.length; i++)
            glueTypeMappings.put(glueTypes[i].getName(), i);
    }
    
    private Glue createGlue(Element type, String name) throws ResourceParseException {
        final String[] names = { "space", "stretch", "shrink" };
        float[] values = new float[names.length];
        for (int i = 0; i < names.length; i++) {
            double val = 0; // default value if attribute not present
            String attrVal = null;
            try {
                attrVal = type.getAttributeValue(names[i]);
                if (attrVal != null) // attribute present
                    val = Double.parseDouble(attrVal);
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(RESOURCE_NAME, "GlueType",
                        names[i], "has an invalid real value '" + attrVal + "'!");
            }
            values[i] = (float) val;
        }
        return new Glue(values[0], values[1], values[2], name);
    }
    
    private void setTypeMappings() {
        typeMappings.put("ord",   TeXConstants.TYPE_ORDINARY);
        typeMappings.put("op",    TeXConstants.TYPE_BIG_OPERATOR);
        typeMappings.put("bin",   TeXConstants.TYPE_BINARY_OPERATOR);
        typeMappings.put("rel",   TeXConstants.TYPE_RELATION);
        typeMappings.put("open",  TeXConstants.TYPE_OPENING);
        typeMappings.put("close", TeXConstants.TYPE_CLOSING);
        typeMappings.put("punct", TeXConstants.TYPE_PUNCTUATION);
        typeMappings.put("inner", TeXConstants.TYPE_INNER); // autoboxing
    }
    
    public Glue[] getGlueTypes() {
        return glueTypes;
    }
    
    public int[][][] createGlueTable() throws ResourceParseException {
        int size = typeMappings.size();
        int[][][] table = new int[size][size][styleMappings.size()];
        Element glueTable = root.getChild("GlueTable");
        if (glueTable != null) { // element present
            // iterate all the "Glue"-elements
            for (Object obj : glueTable.getChildren("Glue")) {
                Element glue = (Element) obj;
                // retrieve required attribute values and throw exception if they're not set
                String left = getAttrValueAndCheckIfNotNull("lefttype", glue), right = getAttrValueAndCheckIfNotNull(
                        "righttype", glue), type = getAttrValueAndCheckIfNotNull(
                        "gluetype", glue);
                
                // iterate all the "Style"-elements
                for (Object object : glue.getChildren("Style")) {
                    Element style = (Element) object;
                    String styleName = getAttrValueAndCheckIfNotNull("name", style);
                    // retrieve mappings
                    Object l = (Integer) typeMappings.get(left), r = (Integer) typeMappings
                            .get(right), st = (Integer) styleMappings.get(styleName), val = (Integer) glueTypeMappings
                            .get(type);
                    // throw exception if unknown value set
                    checkMapping(l, "Glue", "lefttype", left);
                    checkMapping(r, "Glue", "righttype", right);
                    checkMapping(val, "Glue", "gluetype", type);
                    checkMapping(st, "Style", "name", styleName);
                    
                    // put value in table
                    table[((Integer) l).intValue()][((Integer) r).intValue()][((Integer) st)
                    .intValue()] = ((Integer) val).intValue();
                }
            }
        }
        return table;
    }
    
    private static void checkMapping(Object val, String elementName,
            String attrName, String attrValue) throws ResourceParseException {
        if (val == null)
            throw new XMLResourceParseException(RESOURCE_NAME, elementName,
                    attrName, "has an unknown value '" + attrValue + "'!");
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
