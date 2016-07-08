/* MathCharacter.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
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
package cz.natur.cuni.mirai.math.model;

import cz.natur.cuni.mirai.math.meta.MetaCharacter;

/**
 * Character. This class is part of model.
 * 
 * x^2
 * x_j
 * 
 * @author Bea Petrovicova
 */
public class MathCharacter extends MathComponent {

	private MetaCharacter meta;

	/** Use MathFormula.newCharacter(...) */
	public MathCharacter(MathFormula formula, MetaCharacter meta) {
		super(formula);
		this.meta = meta;
	}

	/** Gets parent of this component. */
	public MathSequence getParent() {
		return (MathSequence)super.getParent();
	}

	public MathComponent clone(MathFormula formula) {
		MathCharacter symbol = new MathCharacter(formula, meta);
		return symbol;
	}
	
	public String getName() {
		return meta.getName();
	}

	public String getCasName() {
		return meta.getCasName();
	}

	public String getTexName() {
		return meta.getTexName();
	}

	/** Is Character. */
	public boolean isCharacter() {
		return meta.getType() == MetaCharacter.CHARACTER;
	}

	/** Is Operator. */
	public boolean isOperator() {
		return meta.getType() == MetaCharacter.OPERATOR;
	}

	/** Is Symbol. */
	public boolean isSymbol() {
		return meta.getType() == MetaCharacter.SYMBOL;
	}
}
