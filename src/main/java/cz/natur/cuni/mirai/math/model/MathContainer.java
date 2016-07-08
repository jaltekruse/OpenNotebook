/* MathContainer.java
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

import java.util.ArrayList;

import cz.natur.cuni.mirai.math.algebra.TeXSerializer;

/**
 * This class represents abstract model element.
 * 
 * @author Bea Petrovicova
 */
abstract public class MathContainer extends MathComponent {

	protected ArrayList<MathComponent> arguments = null;

	protected void ensureArguments(int size) {
		if(arguments==null) {
			arguments = new ArrayList<MathComponent>(size);			
		} else {
			arguments.ensureCapacity(size);
		}
		while(arguments.size()<size) {
			arguments.add(null);
		}
	}
	
	MathContainer(MathFormula formula, int size) {
		super(formula);
		if(size>0) {
			ensureArguments(size);
		}
	}

	/** Returns i'th argument . */
	public MathComponent getArgument(int i) {
		return (arguments!=null ? arguments.get(i) : null);
	}

	/** Sets i'th argument. */
	public void setArgument(int i, MathComponent argument) {
		if(arguments==null) {
			arguments = new ArrayList<MathComponent>(i+1);
		}
		if(argument!=null) {
			argument.setParent(this);
		}
		arguments.set(i, argument);
		formula.setModified(true);
	}

	/** Returns number of arguments. */
	public int size() {
		return arguments!=null?arguments.size():0;
	}

	/** Get index of first argument. */
	public int first() {
		// strange but correct
		return next(-1);
	}

	/** Get index of last argument. */
	public int last() {
		return prev(arguments!=null?arguments.size():0); 
	}

	/** Is there a next argument? */
	public boolean hasNext(int current) {
		for(int i=current+1;i<(arguments!=null?arguments.size():0);i++) {
			if(getArgument(i) instanceof MathContainer) {
				return true;
			}
		}
		return false;
	}

	/** Get index of next argument. */
	public int next(int current) {
		for(int i=current+1;i<(arguments!=null?arguments.size():0);i++) {
			if(getArgument(i) instanceof MathContainer) {
				return i;
			}
		}		
		throw new ArrayIndexOutOfBoundsException(
			"Index out of array bounds.");
	}

	/** Is there previous argument? */
	public boolean hasPrev(int current) {
		for(int i=current-1;i>=0;i--) {
			if(getArgument(i) instanceof MathContainer) {
				return true;
			}
		}
		return false;
	}

	/** Get index of previous argument. */
	public int prev(int current) {
		for(int i=current-1;i>=0;i--) {
			if(getArgument(i) instanceof MathContainer) {
				return i;
			}
		}
		throw new ArrayIndexOutOfBoundsException(
			"Index out of array bounds.");
	}

	/** Are there any arguments? */
	public boolean hasChildren() {
		for(int i=0;i<(arguments!=null?arguments.size():0);i++) {
			if(getArgument(i) instanceof MathContainer) {
				return true;
			}
		}
		return false;
	}

	abstract public MathContainer clone(MathFormula formula);

	public int getInsertIndex() {
		return 0;
	}

	public int getInitialIndex() {
		return 0;
	}

	/* Translates CAS argument order into input argument order. *
	int getArgumentInputIndex(int casOrder) {
		return casOrder;			
	} */

	public String toString() {
		TeXSerializer serializer = new TeXSerializer();
		return serializer.serialize(this,null,0);
	}
}
