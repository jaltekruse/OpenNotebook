/* MathMLParser.java
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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.ResourceParseException;
import java.util.HashMap;

/**
 * Provides various static methods for parsing MathML input from a file to a
 * {@link TeXFormula}.
 * <p>
 * JDOM is used for parsing. For each "parse" method found here, there's a
 * corresponding "build" method in the class {@code org.jdom.input.SaxBuilder}.
 * For more information, see
 * {@link <a href="http://jdom.org" target="_blank">http://jdom.org </a>}.
 */
public class MathMLParser {
    
    private static class HexaDecimalException extends Exception {
        private final char c;
        
        public HexaDecimalException(char c) {
            this.c = c;
        }
        
        public String getChar() {
            return Character.toString(c);
        }
    }
    
    private static final String RESOURCE_DIR = "", RESOURCE_NAME = "unicode.properties";
    
    private static Map<String,Object> parsers = new HashMap<String,Object>();
    // TODO: find type which is more restrictive than Object
    private static Map<String,String> htmlColors = new HashMap<String,String>();
    // TODO: should be Color instead of String
    private static Map<Character,String> entities = new HashMap<Character,String>();
    private static Map<String,String> mathSpaces = new HashMap<String,String>();
    // TODO: should be Float instead of String ?
    
    private static final char UNI_BEGIN = 'U';
    
    private static final int W = 0, H = 1, D = 2;
    
    static {
        
      /*
       * Unicode mappings
       */
        
        loadUnicodeMappings();
        
      /*
       * MathML elements and corresponding parser objects
       */
        
        // token elements
        parsers.put("mi", new MiParser());
        parsers.put("mn", new MnParser());
        parsers.put("mo", new MoParser());
        parsers.put("mtext", new MtextParser());
        parsers.put("mspace", new MspaceParser());
        parsers.put("ms", new MsParser());
        parsers.put("mglyph", new MglyphParser());
        
        // general layout
        parsers.put("mrow", new MrowParser());
        parsers.put("mfrac", new MfracParser());
        parsers.put("msqrt", new MsqrtParser());
        parsers.put("mroot", new MrootParser());
        parsers.put("mstyle", new MstyleParser());
        parsers.put("merror", new MerrorParser());
        parsers.put("mpadded", new MpaddedParser());
        parsers.put("mphantom", new MphantomParser());
        parsers.put("mfenced", new MfencedParser());
        parsers.put("menclose", new MencloseParser());
        
        // scripts and limits
        parsers.put("msub", new MsubParser());
        parsers.put("msup", new MsupParser());
        parsers.put("msubsup", new MsubsupParser());
        parsers.put("munder", new MunderParser());
        parsers.put("mover", new MoverParser());
        parsers.put("munderover", new MunderoverParser());
        
        // tables and matrices
        parsers.put("mtable", new MtableParser());
        parsers.put("mtr", new MtrParser());
        parsers.put("mtd", new MtdParser());
        parsers.put("maligngroup", new MaligngroupParser());
        parsers.put("malignmark", new MalignmarkParser());
        parsers.put("mlabeledtr", new MlabeledtrParser());
        
        // actions
        parsers.put("maction", new MactionParser());
        
      /*
       * HTML colors
       */
        htmlColors.put("black", "#000000");
        htmlColors.put("silver", "#C0C0C0");
        htmlColors.put("gray", "#808080");
        htmlColors.put("white", "#FFFFFF");
        htmlColors.put("maroon", "#800000");
        htmlColors.put("red", "#FF0000");
        htmlColors.put("purple", "#800080");
        htmlColors.put("fuchsia", "#FF00FF");
        htmlColors.put("green", "#008000");
        htmlColors.put("lime", "#00FF00");
        htmlColors.put("olive", "#808000");
        htmlColors.put("yellow", "#FFFF00");
        htmlColors.put("navy", "#000080");
        htmlColors.put("blue", "#0000FF");
        htmlColors.put("teal", "#008080");
        htmlColors.put("aqua", "#00FFFF");
        
      /*
       * predefined MathML math spaces
       */
        mathSpaces.put("veryverythinmathspace", "0.0555556em");
        mathSpaces.put("verythinmathspace", "0.111111em");
        mathSpaces.put("thinmathspace", "0.166667em");
        mathSpaces.put("mediummathspace", "0.222222em");
        mathSpaces.put("thickmathspace", "0.277778em");
        mathSpaces.put("verythickmathspace", "0.333333em");
        mathSpaces.put("veryverythickmathspace", "0.388889em");
        
    }
    
    /**
     * A private default constructor is added to make sure that this class cannot
     * be instantiated.
     *
     */
    private MathMLParser() {
    }
    
   /*
    * Looks up the right parser object for the given element and
    * passes the arguments on. Returns the created Formula object.
    */
    private static TeXFormula buildFormula(Element el, Environment env)
    throws MathMLException {
        String name = el.getName();
        Object parser = parsers.get(name);
        if (parser == null)
            throw new MathMLException(
                    "unknown or unsupported presentation element : '" + name + "'");
        else
            return ((PresentationElementParser) parser).buildFormula(el, env);
    }
    
   /*
    * Tries to convert the given unicode string to a Character object.
    */
    private static Character convertToChar(String unicode)
    throws ResourceParseException {
        if (unicode.length() != 6 || unicode.charAt(0) != UNI_BEGIN)
            throw new MathMLResourceParseException(RESOURCE_NAME,
                    "invalid Unicode character specification '" + unicode + "'!");
        
        if (unicode.charAt(1) != '0')
            throw new MathMLResourceParseException(RESOURCE_NAME, "'" + unicode
                    + "' does not specify a 2-byte Unicode character!");
        
        try {
            return new Character(hexaToChar(unicode.substring(2)));
        } catch (HexaDecimalException e) {
            throw new MathMLResourceParseException(RESOURCE_NAME,
                    "The Unicode character specification '" + unicode
                    + "' contains an invalid hexadecimal character '"
                    + e.getChar() + "'!");
        }
    }
    
   /*
    * Creates a Formula object that represents whitespace.
    * Used by the "mspace"-parser.
    */
    protected static TeXFormula createSpace(String[] attr) throws MathMLException {
        // default values
        int[] units = { TeXConstants.UNIT_EM, TeXConstants.UNIT_EM, TeXConstants.UNIT_EM };
        float[] values = { 0, 0, 0 };
        
        for (int i = 0; i < attr.length; i++) {
            if (attr[i] != null) { // null = attribute not set
                String value;
                
                // check for predefined math space
                Object preDefined = mathSpaces.get(attr[i]);
                if (preDefined == null)
                    value = attr[i];
                else // predefined math space found
                    value = (String) preDefined;
                
                // parse attribute
                NumberUnit nu = new NumberUnit(value, true);
                units[i] = nu.getUnit();
                values[i] = nu.getNumber();
            }
        }
        
        // units are guaranteed to be valid
        return new TeXFormula().addStrut(units[W], values[W], units[H], values[H],
                units[D], values[D]);
    }
    
   /*
    * Tries to parse the given mathml color string to a Color object.
    */
    protected static Color getColor(String s) throws MathMLException {
        if (s == null)
            return null;
        String str = s.trim();
        if ("transparent".equals(str))
            return null; // null = transparent
        else {
            // check if html-color
            Object color = htmlColors.get(str.toLowerCase());
            if (color != null) // color found, set str to hexadecimal value
                str = (String) color;
        }
        
        try {
            if (str.length() == 4 && str.charAt(0) == '#') {
                // x -> xx
                int r = hexaToInt(str.charAt(1), str.charAt(1));
                int g = hexaToInt(str.charAt(2), str.charAt(2));
                int b = hexaToInt(str.charAt(3), str.charAt(3));
                return new Color(r, g, b);
            } else if (str.length() == 7 && str.charAt(0) == '#') {
                int r = hexaToInt(str.charAt(1), str.charAt(2));
                int g = hexaToInt(str.charAt(3), str.charAt(4));
                int b = hexaToInt(str.charAt(5), str.charAt(6));
                return new Color(r, g, b);
            } else
                throw new MathMLException("Invalid color specification : '" + str
                        + "'!");
        } catch (HexaDecimalException e) {
            throw new MathMLException("The color specification '" + str
                    + "' contains an invalid hexadecimal character '" + e.getChar()
                    + "'!");
        }
    }
    
   /*
    * Converts a hexadecimal character to an integer value (0..15)
    */
    private static int getDecVal(char c) throws HexaDecimalException {
        if (c >= '0' && c <= '9')
            return c - '0';
        else if (c >= 'A' && c <= 'F') // uppercase
            return c - 'A' + 10;
        else if (c >= 'a' && c <= 'f') // lowercase
            return c - 'a' + 10;
        else
            throw new HexaDecimalException(c);
    }
    
   /*
    * Creates and returns a list of Formula objects from the given
    * list of MathML elements.
    */
    protected static List<TeXFormula> getFormulaList(List l, Environment env)
    throws MathMLException {
        List<TeXFormula> formulas = new LinkedList<TeXFormula>();
        for (Object obj : l)
            formulas.add(buildFormula((Element) obj, env));
        return formulas;
    }
    
   /*
    * Looks up and returns the mapping for the given Unicode character.
    */
    protected static Object getUnicodeMapping(char unicode) {
        return entities.get(Character.valueOf(unicode));
    }
    
   /*
    * Converts the given 4-digit hexadecimal stringvalue to it's char value
    */
    private static char hexaToChar(String str) throws HexaDecimalException {
        return (char) (getDecVal(str.charAt(3)) + getDecVal(str.charAt(2)) * 16
                + getDecVal(str.charAt(1)) * 16 * 16 + getDecVal(str.charAt(0)) * 16 * 16 * 16);
    }
    
   /*
    * Converts the 2-digit hexadecimal value "ab" to it's integer value
    */
    private static int hexaToInt(char a, char b) throws HexaDecimalException {
        int c = getDecVal(a), d = getDecVal(b);
        return c * 16 + d;
    }
    
   /*
    * Reads the Unicode mapping resource in a hash table
    */
    private static void loadUnicodeMappings() throws ResourceParseException {
        // TODO: use property files
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    MathMLParser.class.getResourceAsStream(RESOURCE_DIR + RESOURCE_NAME)));
            String line = in.readLine();
            while (line != null) {
                if (line.length() != 0 && line.charAt(0) != '#') {
                    StringTokenizer st = new StringTokenizer(line, "\t");
                    String unicode = st.nextToken();
                    String mapping = "";
                    if (st.hasMoreTokens())
                        mapping = st.nextToken();
                    entities.put(convertToChar(unicode), mapping);
                }
                line = in.readLine();
            }
       } catch (IOException e) {
            throw new MathMLResourceParseException(RESOURCE_NAME, e.getMessage());
        }
    }
    
   /*
    * Parses the given JDOM Document to a Formula object
    */
    private static TeXFormula parse(Document doc) throws MathMLException {
        Element root = doc.getRootElement();
        if (!root.getName().equals("math"))
            throw new MathMLException("The root element must allways be 'math'!");
        // treat children of the root element as children of an mrow
        return new TeXFormula(getFormulaList(root.getChildren(), new Environment()));
    }
    
    /**
     * Parses a MathML input file identified by a {@link File}.
     *
     * @param file
     *           identifies the file to read from
     * @param validate
     *           whether the input file should be validated first
     * @return the resulting {@link TeXFormula}
     * @throws MathMLException
     *            if the MathML syntax wasn't correct
     * @throws IOException
     *            if there was an error reading the file
     */
    public static TeXFormula parse(File file, boolean validate)
    throws MathMLException, IOException {
        try {
            return parse(new SAXBuilder(validate).build(file));
        } catch (JDOMException e) {
            throw new MathMLException("Error parsing xml!", e);
        }
    }
    
    /**
     * Parses MathML input from the specified {@link InputStream}.
     *
     * @param stream
     *           the stream to read from
     * @param validate
     *           whether the input file should be validated first
     * @return the resulting {@link TeXFormula}
     * @throws MathMLException
     *            if the MathML syntax wasn't correct
     * @throws IOException
     *            if there was an error reading the file
     */
    public static TeXFormula parse(InputStream stream, boolean validate)
    throws MathMLException, IOException {
        try {
            return parse(new SAXBuilder(validate).build(stream));
        } catch (JDOMException e) {
            throw new MathMLException("Error parsing xml!", e);
        }
    }
    
    /**
     * Parses MathML input from the specified {@link InputStream} and URI base.
     *
     * @param stream
     *           the stream to read from
     * @param uri
     *           base for resolving relative URI's
     * @param validate
     *           whether the input file should be validated first
     * @return the resulting {@link TeXFormula}
     * @throws MathMLException
     *            if the MathML syntax wasn't correct
     * @throws IOException
     *            if there was an error reading the file
     */
    public static TeXFormula parse(InputStream stream, String uri, boolean validate)
    throws MathMLException, IOException {
        try {
            return parse(new SAXBuilder(validate).build(stream, uri));
        } catch (JDOMException e) {
            throw new MathMLException("Error parsing xml!", e);
        }
    }
    
    /**
     * Parses MathML input from the specified {@link Reader}.
     *
     * @param reader the {@link Reader} to read from
     * @param validate
     *           whether the input file should be validated first
     * @return the resulting {@link TeXFormula}
     * @throws MathMLException
     *            if the MathML syntax wasn't correct
     * @throws IOException
     *            if there was an error reading the file
     */
    public static TeXFormula parse(Reader reader, boolean validate)
    throws MathMLException, IOException {
        try {
            return parse(new SAXBuilder(validate).build(reader));
        } catch (JDOMException e) {
            throw new MathMLException("Error parsing xml!", e);
        }
    }
    
    /**
     * Parses MathML input from the specified {@link Reader} and URI base.
     *
     * @param reader the {@link Reader} to read from
     * @param uri base for resolving relative URI's
     * @param validate
     *           whether the input file should be validated first
     * @return the resulting {@link TeXFormula}
     * @throws MathMLException
     *            if the MathML syntax wasn't correct
     * @throws IOException
     *            if there was an error reading the file
     */
    public static TeXFormula parse(Reader reader, String uri, boolean validate)
    throws MathMLException, IOException {
        try {
            return parse(new SAXBuilder(validate).build(reader, uri));
        } catch (JDOMException e) {
            throw new MathMLException("Error parsing xml!", e);
        }
    }
    
    /**
     * Parses a MathML input file identified by the specified URI.
     *
     * @param uri identifies the file to read from
     * @param validate
     *           whether the input file should be validated first
     * @return the resulting {@link TeXFormula}
     * @throws MathMLException
     *            if the MathML syntax wasn't correct
     * @throws IOException
     *            if there was an error reading the file
     */
    public static TeXFormula parse(String uri, boolean validate)
    throws MathMLException, IOException {
        try {
            return parse(new SAXBuilder(validate).build(uri));
        } catch (JDOMException e) {
            throw new MathMLException("Error parsing xml!", e);
        }
    }
    
    /**
     * Parses a MathML input file identified by the specified URL.
     *
     * @param url identifies the file to read from
     * @param validate
     *           whether the input file should be validated first
     * @return the resulting {@link TeXFormula}
     * @throws MathMLException
     *            if the MathML syntax wasn't correct
     * @throws IOException
     *            if there was an error reading the file
     */
    public static TeXFormula parse(URL url, boolean validate)
    throws MathMLException, IOException {
        try {
            return parse(new SAXBuilder(validate).build(url));
        } catch (JDOMException e) {
            throw new MathMLException("Error parsing xml!", e);
        }
    }
}
