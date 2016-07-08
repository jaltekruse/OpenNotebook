/* MathBraces.java
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

/**
 * Braces. This class is part of model.
 * 
 * @author Bea Petrovicova
 */
public class MathBraces extends MathContainer {

	public static final int REGULAR = 1;
	public static final int SQUARE = 2;
	public static final int CURLY = 3;
	public static final int APOSTROPHES = 4;

	private int classif;

	public MathBraces(MathFormula formula, int classif) {
		super(formula, 1);
		this.classif = classif;
	}

	/** Gets parent of this component. */
	public MathSequence getParent() {
		return (MathSequence)super.getParent();
	}

	public MathSequence getArgument(int i) {
		return (MathSequence)super.getArgument(i);
	}

	public void setArgument(int i, MathSequence argument) {
		super.setArgument(i, argument);
	}

	public MathContainer clone(MathFormula formula) {
		MathBraces braces = new MathBraces(formula, classif);
		MathComponent component = getArgument(0);
		MathComponent newComponent = component.clone(formula);
		braces.setArgument(0,newComponent);
		return braces;
	}

	public int getClassif() {
		return classif;
	}

}
