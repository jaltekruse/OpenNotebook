/* TeXFormulaParser.java
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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.jdom.Element;

/**
 * Parses a "TeXFormula"-element representing a predefined TeXFormula's from an XML-file.
 */
class TeXFormulaParser {
    
    private interface ActionParser { // NOPMD
        public void parse(Element el) throws ResourceParseException;
    }
    
    private interface ArgumentValueParser { // NOPMD
        public Object parseValue(String value, String type)
        throws ResourceParseException;
    }
    
    private class MethodInvocationParser implements ActionParser {
        
        MethodInvocationParser () {
            // avoids creation of special accessor type
        }
        
        public void parse(Element el) throws ResourceParseException {
            // get required string attributes
            String methodName = getAttrValueAndCheckIfNotNull("name", el);
            String objectName = getAttrValueAndCheckIfNotNull(ARG_OBJ_ATTR, el);
            // check if temporary TeXFormula exists
            Object object = tempFormulas.get(objectName);
            if (object == null) // doesn't exist
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_OBJ_ATTR,
                        "has an unknown temporary TeXFormula name as value : '"
                        + objectName + "'!");
            else {
                // parse arguments
                List args = el.getChildren("Argument");
                // get argument classes and values
                Class[] argClasses = getArgumentClasses(args);
                Object[] argValues = getArgumentValues(args);
                // invoke method
                try {
                    TeXFormula.class.getMethod(methodName, argClasses).invoke(
                            (TeXFormula) object, argValues);
                } catch (Exception e) {
                    throw new XMLResourceParseException(
                            "Error invoking the method '" + methodName
                            + "' on the temporary TeXFormula '" + objectName
                            + "' while constructing the predefined TeXFormula '"
                            + formulaName + "'!", e);
                }
            }
        }
    }
    
    private class CreateTeXFormulaParser implements ActionParser {
        
        CreateTeXFormulaParser () {
            // avoids creation of special accessor type
        }
        
        public void parse(Element el) throws ResourceParseException {
            // get required string attribute
            String name = getAttrValueAndCheckIfNotNull("name", el);
            // parse arguments
            List args = el.getChildren("Argument");
            // get argument classes and values
            Class[] argClasses = getArgumentClasses(args);
            Object[] argValues = getArgumentValues(args);
            // create TeXFormula object
            try {
                TeXFormula f = (TeXFormula) TeXFormula.class.getConstructor(
                        argClasses).newInstance(argValues);
                // succesfully created, so add to "temporary formula's"-hashtable
                tempFormulas.put(name, f);
            } catch (Exception e) {
                throw new XMLResourceParseException(
                        "Error creating the temporary TeXFormula '" + name
                        + "' while constructing the predefined TeXFormula '"
                        + formulaName + "'!", e);
            }
        }
    }
    
    private class FloatValueParser implements ArgumentValueParser {
        
        FloatValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            try {
                return new Float(Float.parseFloat(value));
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an invalid '" + type + "'-value : '"
                        + value + "'!", e);
            }
        }
    }
    
    private class CharValueParser implements ArgumentValueParser {
        
        CharValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            if (value.length() == 1)
                return new Character(value.charAt(0));
            else
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR,
                        "must have a value that consists of exactly 1 character!");
        }
    }
    
    private class BooleanValueParser implements ArgumentValueParser {
        
        BooleanValueParser () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            if ("true".equals(value))
                return Boolean.TRUE;
            else if ("false".equals(value))
                return Boolean.FALSE;
            else
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an invalid '" + type + "'-value : '"
                        + value + "'!");
            
        }
    }
    
    private class IntValueParser implements ArgumentValueParser {
        
        IntValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            try {
                int val = Integer.parseInt(value);
                return new Float((int) val);
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an invalid '" + type + "'-value : '"
                        + value + "'!", e);
            }
        }
    }
    
    private class ReturnParser implements ActionParser {
        
        ReturnParser () {
            // avoids creation of special accessor type
        }
        
        public void parse(Element el) throws ResourceParseException {
            // get required string attribute
            String name = getAttrValueAndCheckIfNotNull("name", el);
            Object res = tempFormulas.get(name);
            if (res == null)
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, RETURN_EL, "name",
                        "contains an unknown temporary TeXFormula variable name '"
                        + name + "' for the predefined TeXFormula '"
                        + formulaName + "'!");
            else
                result = (TeXFormula) res;
        }
    }
    
    private class StringValueParser implements ArgumentValueParser {
        
        StringValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            return value;
        }
    }
    
    private class TeXFormulaValueParser implements ArgumentValueParser {
        
        TeXFormulaValueParser () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            if (value == null) // null pointer argument
                return null;
            else {
                Object formula = tempFormulas.get(value);
                if (formula == null) // unknown temporary TeXFormula!
                    throw new XMLResourceParseException(
                            PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                            ARG_VAL_ATTR,
                            "has an unknown temporary TeXFormula name as value : '"
                            + value + "'!");
                else
                    return (TeXFormula) formula;
            }
        }
    }
    
    private class TeXConstantsValueParser implements ArgumentValueParser {
        
        TeXConstantsValueParser () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            try {
                // get constant value (if present)
                int constant = TeXConstants.class.getDeclaredField(value).getInt(
                        null);
                // return constant integer value
                return Integer.valueOf(constant);
            } catch (Exception e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an unknown constant name as value : '"
                        + value + "'!", e);
            }
        }
    }
    
    private class ColorConstantValueParser implements ArgumentValueParser {
        
        ColorConstantValueParser () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            try {
                // return Color constant (if present)
                return Color.class.getDeclaredField(value).get(null);
            } catch (Exception e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR,
                        "has an unknown color constant name as value : '" + value
                        + "'!", e);
            }
        }
    }
    
    private static final String ARG_VAL_ATTR = "value", RETURN_EL = "Return",
            ARG_OBJ_ATTR = "formula";
    
    private static Map<String,Class<?>> classMappings = new HashMap<String,Class<?>>();
    
    private final Map<String,ArgumentValueParser> argValueParsers = new HashMap<String,ArgumentValueParser>();
    private final Map<String,ActionParser> actionParsers = new HashMap<String,ActionParser>();
    private final Map<String,TeXFormula> tempFormulas = new HashMap<String,TeXFormula>();
    
    private TeXFormula result = new TeXFormula();
    
    private final String formulaName;
    
    private final Element formula;
    
    static {
        // string-to-class mappings
        classMappings.put("TeXConstants", int.class); // all integer constants
        classMappings.put("TeXFormula", TeXFormula.class);
        classMappings.put("String", String.class);
        classMappings.put("float", float.class);
        classMappings.put("int", int.class);
        classMappings.put("boolean", boolean.class);
        classMappings.put("char", char.class);
        classMappings.put("ColorConstant", Color.class);
    }
    
    public TeXFormulaParser(String name, Element formula) {
        formulaName = name;
        this.formula = formula;
        
        // action parsers
        actionParsers.put("CreateTeXFormula", new CreateTeXFormulaParser());
        actionParsers.put("MethodInvocation", new MethodInvocationParser());
        actionParsers.put(RETURN_EL, new ReturnParser());
        
        // argument value parsers
        argValueParsers.put("TeXConstants", new TeXConstantsValueParser());
        argValueParsers.put("TeXFormula", new TeXFormulaValueParser());
        argValueParsers.put("String", new StringValueParser());
        argValueParsers.put("float", new FloatValueParser());
        argValueParsers.put("int", new IntValueParser());
        argValueParsers.put("boolean", new BooleanValueParser());
        argValueParsers.put("char", new CharValueParser());
        argValueParsers.put("ColorConstant", new ColorConstantValueParser());
    }
    
    public TeXFormula parse() throws ResourceParseException {
        // parse and execute actions
        for (Object obj : formula.getChildren()) {
            Element el = (Element) obj;
            ActionParser p = actionParsers.get(el.getName());
            if (p != null) // ignore unknown elements
               p.parse(el);
        }
        return result;
    }
    
    private Object[] getArgumentValues(List args) {
        Object[] res = new Object[args.size()];
        int i = 0;
        for (Object obj : args) {
            Element arg = (Element) obj;
            // get required string attribute
            String type = getAttrValueAndCheckIfNotNull("type", arg);
            // get value, not present means a nullpointer
            String value = arg.getAttributeValue(ARG_VAL_ATTR);
            // parse value, hashtable will certainly contain a parser for the class type,
            // because the class types have been checked before!
            res[i] = ((ArgumentValueParser) argValueParsers.get(type)).parseValue(
                    value, type);
            i++;
        }
        return res;
    }
    
    private static Class[] getArgumentClasses(List args)
    throws ResourceParseException {
        Class[] res = new Class[args.size()];
        int i = 0;
        for (Object obj : args) {
            Element arg = (Element) obj;
            // get required string attribute
            String type = getAttrValueAndCheckIfNotNull("type", arg);
            // find class mapping
            Object cl = classMappings.get(type);
            if (cl == null) // no class mapping found
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument", "type",
                        "has an invalid class name value!");
            else
                res[i] = (Class) cl;
            i++;
        }
        return res;
    }
    
    private static void checkNullValue(String value, String type)
    throws ResourceParseException {
        if (value == null)
            throw new XMLResourceParseException(
                    PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                    ARG_VAL_ATTR, "is required for an argument of type '" + type
                    + "'!");
    }
    
    private static String getAttrValueAndCheckIfNotNull(String attrName,
            Element element) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null)
            throw new XMLResourceParseException(
                    PredefinedTeXFormulaParser.RESOURCE_NAME, element.getName(),
                    attrName, null);
        return attrValue;
    }
}
