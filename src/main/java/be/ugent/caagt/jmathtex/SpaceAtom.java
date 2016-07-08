/* SpaceAtom.java
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

/**
 * An atom representing whitespace. The dimension values can be set using different
 * unit types.
 */
class SpaceAtom extends Atom {
    
    private static interface UnitConversion { // NOPMD
        public float getPixelConversion(TeXEnvironment env);
    }
    
    // TODO: UNIT_EM and UNIT_EX yield the same converters ??
    
    private static UnitConversion[] unitConversions = new UnitConversion[] {
        
        new UnitConversion() {
            public float getPixelConversion(TeXEnvironment env) {
                return env.getTeXFont().getXHeight(env.getStyle(), env.getLastFontId());
            }
        },
        
        new UnitConversion() {
            public float getPixelConversion(TeXEnvironment env) {
                return env.getTeXFont().getXHeight(env.getStyle(), env.getLastFontId());
            }
        },
        
        new UnitConversion() {
            public float getPixelConversion(TeXEnvironment env) {
                return 1 / env.getSize();
            }
        },
        
        new UnitConversion() {
            public float getPixelConversion(TeXEnvironment env) {
                return TeXFont.PIXELS_PER_POINT / env.getSize();
            }
        },
        
        new UnitConversion() {
            public float getPixelConversion(TeXEnvironment env) {
                return (12 * TeXFont.PIXELS_PER_POINT) / env.getSize();
            }
        },
        
        new UnitConversion() {
            public float getPixelConversion(TeXEnvironment env) {
                TeXFont tf = env.getTeXFont();
                return tf.getQuad(env.getStyle(), tf.getMuFontId()) / 18;
            }
        }
    };
    
    // whether a hard space should be represented
    private boolean blankSpace = false;
    
    // dimensions
    private float width;
    private float height;
    private float depth;
    
    // units for the dimensions
    private int wUnit;
    private int hUnit;
    private int dUnit;
    
    public SpaceAtom() {
        blankSpace = true;
    }
    
    public SpaceAtom(int unit, float width, float height, float depth)
    throws InvalidUnitException {
        // check if unit is valid
        checkUnit(unit);
        
        // unit valid
        this.wUnit = unit;
        this.hUnit = unit;
        this.dUnit = unit;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    /**
     * Check if the given unit is valid
     *
     * @param unit the unit's integer representation (a constant)
     * @throws InvalidUnitException if the given integer value does not represent
     * 			a valid unit
     */
    public static void checkUnit(int unit) throws InvalidUnitException {
        if (unit < 0 || unit >= unitConversions.length)
            throw new InvalidUnitException();
    }
    
    public SpaceAtom(int widthUnit, float width, int heightUnit, float height,
            int depthUnit, float depth) throws InvalidUnitException {
        // check if units are valid
        checkUnit(widthUnit);
        checkUnit(heightUnit);
        checkUnit(depthUnit);
        
        // all units valid
        wUnit = widthUnit;
        hUnit = heightUnit;
        dUnit = depthUnit;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    public Box createBox(TeXEnvironment env) {
        if (blankSpace)
            return new StrutBox(env.getSpace(), 0, 0, 0);
        else
            return new StrutBox(width * getFactor(wUnit, env), height
                    * getFactor(hUnit, env), depth * getFactor(dUnit, env), 0);
    }
    
    private float getFactor(int unit, TeXEnvironment env) {
        return unitConversions[unit].getPixelConversion(env);
    }
}
