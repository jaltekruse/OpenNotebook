/* NumberUnit.java
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

import java.util.Map;

import be.ugent.caagt.jmathtex.TeXConstants;
import java.util.HashMap;

/**
 * Responsible for parsing attribute values that can have units.
 */
class NumberUnit {
    // no unit found
    public static final int NO_UNIT = -1;
    
    // possible units
    private static Map<String,Integer> mathUnits = new HashMap<String,Integer> ();
    
    // parsed value
    private float number;
    
    // parsed unit
    private final int unit;
    
    static {
      /*
       * supported MathML unit types
       */
        mathUnits.put("em", TeXConstants.UNIT_EM);
        mathUnits.put("ex", TeXConstants.UNIT_EX);
        mathUnits.put("px", TeXConstants.UNIT_PIXEL);
        mathUnits.put("pt", TeXConstants.UNIT_POINT);
        mathUnits.put("pc", TeXConstants.UNIT_PICA); // autoboxing
    }
    
    public NumberUnit(String value, boolean unitAllowed) throws MathMLException {
        // remove surrounding whitespace
        value = value.trim();
        // find unit
        boolean noUnit = false;
        if (value.length() >= 2) { // unit = 2 characters
            Object u = mathUnits.get(value.substring(value.length() - 2)
            .toLowerCase());
            if (u == null) {
                noUnit = true;
                unit = 0;
            } else {// unit found
                // set unit
                unit = ((Integer) u).intValue();
                // remove unit from the string
                value = value.substring(0, value.length() - 2);
            }
            
        } else {
            noUnit = true;
            unit = 0;
        }
        
        // parse value
        try {
            double val = Double.parseDouble(value);
            // if the value is 0 then the unit doesn't have to be specified, even
            // if unitAllowed=true
            if (unitAllowed && noUnit && val != 0)
                throw new MathMLException("Invalid unit in argument!");
            else
                // set value
                number = (float) val;
        } catch (NumberFormatException e) {
            throw new MathMLException("Invalid number in argument : '" + value
                    + "'!", e);
        }
    }
    
    public float getNumber() {
        return number;
    }
    
    public int getUnit() {
        return unit;
    }
}
