/* SymbolAtom.java
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

import java.util.BitSet;
import java.util.Map;

/**
 * A box representing a symbol (a non-alphanumeric character).
 */
class SymbolAtom extends CharSymbol {
    
    // whether it's is a delimiter symbol
    private final boolean delimiter;
    
    // symbol name
    private final String name;
    
    // contains all defined symbols
    private static Map<String,SymbolAtom> symbols;
    
    // contains all the possible valid symbol types
    private static BitSet validSymbolTypes;
    
    static {
        symbols = new TeXSymbolParser().readSymbols();
        
        // set valid symbol types
        validSymbolTypes =  new BitSet(16);
        validSymbolTypes.set(TeXConstants.TYPE_ORDINARY);
        validSymbolTypes.set(TeXConstants.TYPE_BIG_OPERATOR);
        validSymbolTypes.set(TeXConstants.TYPE_BINARY_OPERATOR);
        validSymbolTypes.set(TeXConstants.TYPE_RELATION);
        validSymbolTypes.set(TeXConstants.TYPE_OPENING);
        validSymbolTypes.set(TeXConstants.TYPE_CLOSING);
        validSymbolTypes.set(TeXConstants.TYPE_PUNCTUATION);
        validSymbolTypes.set(TeXConstants.TYPE_ACCENT);
    }
    
    public SymbolAtom(SymbolAtom s, int type) throws InvalidSymbolTypeException {
        if (!validSymbolTypes.get(type))
            throw new InvalidSymbolTypeException(
                    "The symbol type was not valid! "
                    + "Use one of the symbol type constants from the class 'TeXConstants'.");
        name = s.name;
        this.type = type;
        delimiter = s.delimiter;
    }
    
    /**
     * Constructs a new symbol. This used by "TeXSymbolParser" and the symbol
     * types are guaranteed to be valid.
     *
     * @param name symbol name
     * @param type symbol type constant
     * @param del whether the symbol is a delimiter
     */
    public SymbolAtom(String name, int type, boolean del) {
        this.name = name;
        this.type = type;
        delimiter = del;
    }
    
    /**
     * Looks up the name in the table and returns the corresponding SymbolAtom representing
     * the symbol (if it's found).
     *
     * @param name the name of the symbol
     * @return a SymbolAtom representing the found symbol
     * @throws SymbolNotFoundException if no symbol with the given name was found
     */
    public static SymbolAtom get(String name) throws SymbolNotFoundException {
        Object obj = symbols.get(name);
        if (obj == null) // not found
            throw new SymbolNotFoundException(name);
        else
            return (SymbolAtom) obj;
    }
    
    /**
     *
     * @return true if this symbol can act as a delimiter to embrace formulas
     */
    public boolean isDelimiter() {
        return delimiter;
    }
    
    public String getName() {
        return name;
    }
    
    public Box createBox(TeXEnvironment env) {
        TeXFont tf = env.getTeXFont();
        int style = env.getStyle();
        return new CharBox(tf.getChar(name, style));
    }
    
    public CharFont getCharFont(TeXFont tf) {
        // style doesn't matter here
        return tf.getChar(name, TeXConstants.STYLE_DISPLAY).getCharFont();
    }
}
