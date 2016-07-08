/* MathComponent.java
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
 * This class represents abstract model element.
 * 
 * @author Bea Petrovicova
 */
abstract public class MathComponent {

	protected MathFormula formula;
	private MathContainer parent;
	private int id;
	
	MathComponent(MathFormula formula) {
		this.formula = formula;
		this.id = formula.newID();
	}
	
	public MathFormula getFormula() {
		return formula;
	}
	
	/** Sets parent of this component. */
	void setParent(MathContainer container) {
		this.parent = container;
	}

	/** Gets parent of this component. */
	public MathContainer getParent() {
		return parent;
	}
	/** Gets index of this comonent within its parent component. */
	public int getParentIndex() {
		if(parent==null) {
			return 0;
		}
		for(int i=0;i<parent.size();i++) {
			if(parent.getArgument(i)==this) {
				return i;
			}
		}
		// this should not happen
		return 0;
	}

	abstract public MathComponent clone(MathFormula formula);

	/** Returns unique component id */
	public int getID() {
		return id;
	}

}
