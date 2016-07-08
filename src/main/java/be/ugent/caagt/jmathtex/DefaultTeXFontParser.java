/* DefaultTeXFontParser.java
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

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Parses the font information from an XML-file.
 */
class DefaultTeXFontParser {
    
    /**
     * Number of font ids in a single font description file.
     */
    private static final int NUMBER_OF_FONT_IDS = 4;
    
    private static interface CharChildParser { // NOPMD
        public void parse(Element el, char ch, FontInfo info) throws XMLResourceParseException;
    }
    
    private static class ExtensionParser implements CharChildParser {
        
        
        ExtensionParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        throws ResourceParseException {
            int[] extensionChars = new int[4];
            // get required integer attributes
            extensionChars[DefaultTeXFont.REP] = DefaultTeXFontParser
                    .getIntAndCheck("rep", el);
            // get optional integer attributes
            extensionChars[DefaultTeXFont.TOP] = DefaultTeXFontParser
                    .getOptionalInt("top", el, DefaultTeXFont.NONE);
            extensionChars[DefaultTeXFont.MID] = DefaultTeXFontParser
                    .getOptionalInt("mid", el, DefaultTeXFont.NONE);
            extensionChars[DefaultTeXFont.BOT] = DefaultTeXFontParser
                    .getOptionalInt("bot", el, DefaultTeXFont.NONE);
            
            // parsing OK, add extension info
            info.setExtension(ch, extensionChars);
        }
    }
    
    private static class KernParser implements CharChildParser {
        
        KernParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        throws ResourceParseException {
            // get required integer attribute
            int code = DefaultTeXFontParser.getIntAndCheck("code", el);
            // get required float attribute
            float kernAmount = DefaultTeXFontParser.getFloatAndCheck("val", el);
            
            // parsing OK, add kern info
            info.addKern(ch, (char) code, kernAmount);
        }
    }
    
    private static class LigParser implements CharChildParser {
        
        LigParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        throws ResourceParseException {
            // get required integer attributes
            int code = DefaultTeXFontParser.getIntAndCheck("code", el);
            int ligCode = DefaultTeXFontParser.getIntAndCheck("ligCode", el);
            
            // parsing OK, add ligature info
            info.addLigature(ch, (char) code, (char) ligCode);
        }
    }
    
    private static class NextLargerParser implements CharChildParser {
        
        NextLargerParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        throws ResourceParseException {
            // get required integer attributes
            int fontId = DefaultTeXFontParser.getIntAndCheck("fontId", el);
            int code = DefaultTeXFontParser.getIntAndCheck("code", el);
            
            // parsing OK, add "next larger" info
            info.setNextLarger(ch, (char) code, fontId);
        }
    }
    
    public static final String RESOURCE_NAME = "DefaultTeXFont.xml";
    
    public static final String STYLE_MAPPING_EL = "TextStyleMapping";
    public static final String SYMBOL_MAPPING_EL = "SymbolMapping";
    public static final String GEN_SET_EL = "GeneralSettings";
    public static final String MUFONTID_ATTR = "mufontid";
    public static final String SPACEFONTID_ATTR = "spacefontid";
    
    private static Map<String,Integer> rangeTypeMappings = new HashMap<String,Integer>();
    private static Map<String,CharChildParser>
            charChildParsers = new HashMap<String,CharChildParser>();
    
    private Map<String,CharFont[]> parsedTextStyles;
    
    private Element root;
    
    static {
        // string-to-constant mappings
        setRangeTypeMappings();
        // parsers for the child elements of a "Char"-element
        setCharChildParsers();
    }
    
    public DefaultTeXFontParser() throws ResourceParseException {
        try {
            root = new SAXBuilder().build(
                    DefaultTeXFontParser.class.getResourceAsStream(RESOURCE_NAME)
                    ).getRootElement();
            
            // parse textstyles ahead of the rest, because it's used while
            // parsing the default text style
            parsedTextStyles = parseStyleMappings();
        } catch (JDOMException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        } catch (IOException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        }
    }
    
    private static void setCharChildParsers() {
        charChildParsers.put("Kern", new KernParser());
        charChildParsers.put("Lig", new LigParser());
        charChildParsers.put("NextLarger", new NextLargerParser());
        charChildParsers.put("Extension", new ExtensionParser());
    }
    
    public FontInfo[] parseFontDescriptions() throws ResourceParseException {
        FontInfo[] res = new FontInfo[NUMBER_OF_FONT_IDS];
        Element fontDescriptions = root.getChild("FontDescriptions");
        if (fontDescriptions != null) { // element present
            // iterate all "Font"-elements
            for (Object obj : fontDescriptions.getChildren("Font")) {
                Element font = (Element) obj;
                // get required string attribute
                String fontName = getAttrValueAndCheckIfNotNull("name", font);
                // get required integer attribute
                int fontId = getIntAndCheck("id", font);
                if (fontId < 0) // id must be greater than 0!!
                    throw new XMLResourceParseException(RESOURCE_NAME, "Font", "id",
                            "must have a positive integer value!");
                // get required real attributes
                float space = getFloatAndCheck("space", font);
                float xHeight = getFloatAndCheck("xHeight", font);
                float quad = getFloatAndCheck("quad", font);
                
                // get optional integer attribute
                int skewChar = getOptionalInt("skewChar", font, -1);
                
                // try reading the font
                Font f = createFont(fontName);
                
                // create FontInfo-object
                FontInfo info = new FontInfo(fontId, f, xHeight, space, quad);
                if (skewChar != -1) // attribute set
                    info.setSkewChar((char) skewChar);
                
                // process all "Char"-elements
                for (Object object : font.getChildren("Char"))
                    processCharElement((Element) object, info);
                
                // parsing OK, add to table
                if (res[fontId] == null)
                    res[fontId] = info;
                else
                    throw new XMLResourceParseException(RESOURCE_NAME, "Font", "id",
                            "occurs more than once");
            }
        }
        return res;
    }
    
    private static void processCharElement(Element charElement, FontInfo info)
    throws ResourceParseException {
        // retrieve required integer attribute
        char ch = (char) getIntAndCheck("code", charElement);
        // retrieve optional float attributes
        float[] metrics = new float[4];
        metrics[DefaultTeXFont.WIDTH] = getOptionalFloat("width", charElement, 0);
        metrics[DefaultTeXFont.HEIGHT] = getOptionalFloat("height", charElement,
                0);
        metrics[DefaultTeXFont.DEPTH] = getOptionalFloat("depth", charElement, 0);
        metrics[DefaultTeXFont.IT] = getOptionalFloat("italic", charElement, 0);
        // set metrics
        info.setMetrics(ch, metrics);
        
        // process children
        for (Object obj : charElement.getChildren()) {
            Element el = (Element)obj;
            Object parser = charChildParsers.get(el.getName());
            if (parser == null) // unknown element
                throw new XMLResourceParseException(RESOURCE_NAME
                        + ": a <Char>-element has an unknown childelement '"
                        + el.getName() + "'!");
            else
                // process the child element
                ((CharChildParser) parser).parse(el, ch, info);
        }
    }
    
    private Font createFont(String name) throws ResourceParseException {
        InputStream fontIn = null;
        try {
            fontIn = DefaultTeXFontParser.class.getResourceAsStream(name);
            return Font.createFont(java.awt.Font.TRUETYPE_FONT, fontIn);
        } catch (Exception e) {
            throw new XMLResourceParseException(RESOURCE_NAME
                    + ": error reading font '" + name + "'. Error message: "
                    + e.getMessage());
        } finally {
            try {
                if (fontIn != null)
                    fontIn.close();
            } catch (IOException ioex) {
                throw new RuntimeException("Close threw exception", ioex);
            }
        }
    }
    
    public Map<String,CharFont> parseSymbolMappings() throws ResourceParseException {
        Map<String,CharFont> res = new HashMap<String,CharFont>();
        Element symbolMappings = root.getChild("SymbolMappings");
        if (symbolMappings == null)
            // "SymbolMappings" is required!
            throw new XMLResourceParseException(RESOURCE_NAME, "SymbolMappings");
        else { // element present
            // iterate all mappings
            for (Object obj : symbolMappings.getChildren(SYMBOL_MAPPING_EL)) {
                Element mapping = (Element) obj;
                // get string attribute
                String symbolName = getAttrValueAndCheckIfNotNull("name", mapping);
                // get integer attributes
                int ch = getIntAndCheck("ch", mapping), fontId = getIntAndCheck(
                        "fontId", mapping);
                // put mapping in table
                res.put(symbolName, new CharFont((char) ch, fontId));
            }
        }
        
        // "sqrt" must allways be present (used internally only!)
        if (res.get("sqrt") == null)
            throw new XMLResourceParseException(
                    RESOURCE_NAME
                    + ": the required mapping <SymbolMap name=\"sqrt\" ... /> is not found!");
        else
            // parsing OK
            return res;
    }
    
    public String[] parseDefaultTextStyleMappings()
    throws ResourceParseException {
        String[] res = new String[3];
        Element defaultTextStyleMappings = root
                .getChild("DefaultTextStyleMapping");
        if (defaultTextStyleMappings == null)
            // "DefaultTextStyleMappings" is required!
            throw new XMLResourceParseException(RESOURCE_NAME,
                    "DefaultTextStyleMapping");
        else { // element present
            // iterate all mappings
            for (Object obj : defaultTextStyleMappings.getChildren("MapStyle")) {
                Element mapping = (Element) obj;
                // get range name and check if it's valid
                String code = getAttrValueAndCheckIfNotNull("code", mapping);
                Object codeMapping = rangeTypeMappings.get(code);
                if (codeMapping == null) // unknown range name
                    throw new XMLResourceParseException(RESOURCE_NAME, "MapStyle",
                            "code", "contains an unknown \"range name\" '" + code
                            + "'!");
                // get mapped style and check if it exists
                String textStyleName = getAttrValueAndCheckIfNotNull("textStyle",
                        mapping);
                Object styleMapping = parsedTextStyles.get(textStyleName);
                if (styleMapping == null) // unknown text style
                    throw new XMLResourceParseException(RESOURCE_NAME, "MapStyle",
                            "textStyle", "contains an unknown text style '"
                            + textStyleName + "'!");
                // now check if the range is defined within the mapped text style
                CharFont[] charFonts = (CharFont[]) parsedTextStyles
                        .get(textStyleName);
                int index = ((Integer) codeMapping).intValue();
                if (charFonts[index] == null) // range not defined
                    throw new XMLResourceParseException(RESOURCE_NAME
                            + ": the default text style mapping '" + textStyleName
                            + "' for the range '" + code
                            + "' contains no mapping for that range!");
                else
                    // everything OK, put mapping in table
                    res[index] = textStyleName;
            }
        }
        return res;
    }
    
    public Map<String,Float> parseParameters() throws ResourceParseException {
        Map<String,Float> res = new HashMap<String,Float>();
        Element parameters = root.getChild("Parameters");
        if (parameters == null)
            // "Parameters" is required!
            throw new XMLResourceParseException(RESOURCE_NAME, "Parameters");
        else { // element present
            // iterate all attributes
            for (Object obj : parameters.getAttributes()) {
                String name = ((Attribute) obj).getName();
                // set float value (if valid)
                res.put(name, new Float(getFloatAndCheck(name, parameters)));
            }
            return res;
        }
    }
    
    public Map<String,Number> parseGeneralSettings() throws ResourceParseException {
        Map <String,Number>res = new HashMap<String,Number>();
        // TODO: must this be 'Number' ?
        Element generalSettings = root.getChild("GeneralSettings");
        if (generalSettings == null)
            // "GeneralSettings" is required!
            throw new XMLResourceParseException(RESOURCE_NAME, "GeneralSettings");
        else { // element present
            // set required int values (if valid)
            res.put(MUFONTID_ATTR, getIntAndCheck(MUFONTID_ATTR,
                    generalSettings)); // autoboxing
            res.put(SPACEFONTID_ATTR, getIntAndCheck(SPACEFONTID_ATTR,
                    generalSettings)); // autoboxing
            // set required float values (if valid)
            res.put("scriptfactor", getFloatAndCheck("scriptfactor",
                    generalSettings)); // autoboxing
            res.put("scriptscriptfactor", getFloatAndCheck(
                    "scriptscriptfactor", generalSettings)); // autoboxing
            
        }
        return res;
    }
    
    public Map<String,CharFont[]> parseTextStyleMappings() {
        return parsedTextStyles;
    }
    
    private Map<String,CharFont[]> parseStyleMappings() throws ResourceParseException {
        Map<String,CharFont[]> res = new HashMap<String,CharFont[]>();
        Element textStyleMappings = root.getChild("TextStyleMappings");
        if (textStyleMappings == null)
            // "TextStyleMappings" is required!
            throw new XMLResourceParseException(RESOURCE_NAME, "TextStyleMappings");
        else { // element present
            // iterate all mappings
            for (Object obj : textStyleMappings.getChildren(STYLE_MAPPING_EL)) {
                Element mapping = (Element) obj;
                // get required string attribute
                String textStyleName = getAttrValueAndCheckIfNotNull("name",
                        mapping);
                List mapRangeList = mapping.getChildren("MapRange");
                // iterate all mapping ranges
                CharFont[] charFonts = new CharFont[3];
                for (Object object : mapRangeList) {
                    Element mapRange = (Element) object;
                    // get required integer attributes
                    int fontId = getIntAndCheck("fontId", mapRange);
                    int ch = getIntAndCheck("start", mapRange);
                    // get required string attribute and check if it's a known range
                    String code = getAttrValueAndCheckIfNotNull("code", mapRange);
                    Object codeMapping = rangeTypeMappings.get(code);
                    if (codeMapping == null)
                        throw new XMLResourceParseException(RESOURCE_NAME,
                                "MapRange", "code",
                                "contains an unknown \"range name\" '" + code + "'!");
                    else
                        charFonts[((Integer) codeMapping).intValue()] = new CharFont(
                                (char) ch, fontId);
                }
                res.put(textStyleName, charFonts);
            }
        }
        return res;
    }
    
    private static void setRangeTypeMappings() {
        rangeTypeMappings.put("numbers", DefaultTeXFont.NUMBERS); // autoboxing
        rangeTypeMappings.put("capitals", DefaultTeXFont.CAPITALS); // autoboxing
        rangeTypeMappings.put("small", DefaultTeXFont.SMALL); // autoboxing
    }
    
    private static String getAttrValueAndCheckIfNotNull(String attrName,
            Element element) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null)
            throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
                    attrName, null);
        return attrValue;
    }
    
    public static float getFloatAndCheck(String attrName, Element element)
    throws ResourceParseException {
        String attrValue = getAttrValueAndCheckIfNotNull(attrName, element);
        
        // try parsing string to float value
        float res = 0;
        try {
            res = (float) Double.parseDouble(attrValue);
        } catch (NumberFormatException e) {
            throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
                    attrName, "has an invalid real value!");
        }
        // parsing OK
        return res;
    }
    
    public static int getIntAndCheck(String attrName, Element element)
    throws ResourceParseException {
        String attrValue = getAttrValueAndCheckIfNotNull(attrName, element);
        
        // try parsing string to integer value
        int res = 0;
        try {
            res = Integer.parseInt(attrValue);
        } catch (NumberFormatException e) {
            throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
                    attrName, "has an invalid integer value!");
        }
        // parsing OK
        return res;
    }
    
    public static int getOptionalInt(String attrName, Element element,
            int defaultValue) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null) // attribute not present
            return defaultValue;
        else {
            // try parsing string to integer value
            int res = 0;
            try {
                res = Integer.parseInt(attrValue);
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(RESOURCE_NAME, element
                        .getName(), attrName, "has an invalid integer value!");
            }
            // parsing OK
            return res;
        }
    }
    
    public static float getOptionalFloat(String attrName, Element element,
            float defaultValue) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null) // attribute not present
            return defaultValue;
        else {
            // try parsing string to float value
            float res = 0;
            try {
                res = (float) Double.parseDouble(attrValue);
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(RESOURCE_NAME, element
                        .getName(), attrName, "has an invalid float value!");
            }
            // parsing OK
            return res;
        }
    }
}
