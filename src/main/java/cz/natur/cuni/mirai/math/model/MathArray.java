/* MathArray.java
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

import cz.natur.cuni.mirai.math.meta.MetaArray;
import cz.natur.cuni.mirai.math.meta.MetaComponent;
import cz.natur.cuni.mirai.math.meta.MetaModel;

/**
 * Array/array. This class is part of model.
 * 
 * array(i)
 * array(i*j)
 * vector(i)
 * array(i*j)
 * 
 * @author Bea Petrovicova
 */
public class MathArray extends MathContainer {
	private int columns, rows;
	private MetaArray meta;

	public MathArray(MathFormula formula, MetaArray meta, int columns) {
		super(formula, columns);
		this.meta = meta;
		this.columns = columns;
		this.rows = 1;
	}

	public MathArray(MathFormula formula, MetaArray meta, int columns, int rows) {
		super(formula, columns*rows);
		this.meta = meta;
		this.columns = columns;
		this.rows = rows;
	}

	public void addRow() {
		for(int i=0;i<columns;i++) {
			MathSequence argument = new MathSequence(formula);
			argument.setParent(this);
			arguments.add(argument);
		}
		rows+=1;
	}

	public MathSequence getArgument(int i) {
		return (MathSequence)super.getArgument(i);
	}

	public void setArgument(int i, MathSequence argument) {
		super.setArgument(i, argument);
	}

	public void addArgument() {
		MathSequence argument = new MathSequence(formula);
		argument.setParent(this);
		arguments.add(argument);
		columns+=1;
		formula.setModified(true);
	}

	public void addArgument(MathSequence argument) {
		if(argument!=null) {
			argument.setParent(this);
		}
		arguments.add(argument);
		columns+=1;
		formula.setModified(true);
	}

	public void addArgument(int i, MathSequence argument) {
		if(argument!=null) {
			argument.setParent(this);
		}
		arguments.add(i, argument);
		columns+=1;
		formula.setModified(true);
	}

	public void delArgument(int i) {
		arguments.remove(i);
		columns-=1;
		formula.setModified(true);
	}

	/** Sets i-th row, j-th column cell. */
	public void setArgument(int i, int j, MathSequence argument) {
		setArgument(i*columns+j, argument);
	}
	
	/** Returns i-th row, j-th column cell. */
	public MathSequence getArgument(int i, int j) {
		return getArgument(i*columns+j);
	}

	/** Uid name. */
	public String getName() {
		return meta.getName();
	}

	public MetaComponent getOpen() {
		return meta.getOpen();
	}

	public char getOpenKey() {
		return meta.getOpenKey();
	}

	public MetaComponent getClose() {
		return meta.getClose();
	}

	public char getCloseKey() {
		return meta.getCloseKey();
	}

	public MetaComponent getField() {
		return meta.getField();
	}

	public char getFieldKey() {
		return meta.getFieldKey();
	}

	public MetaComponent getRow() {
		return meta.getRow();
	}

	public char getRowKey() {
		return meta.getRowKey();
	}

	public boolean is1DArray() {
		return rows() == 1 && MetaModel.ARRAY.equals(getName());
	}

	public boolean isArray() {
		return MetaModel.ARRAY.equals(getName());
	}

	public boolean isVector() {
		return rows() == 1 && MetaModel.MATRIX.equals(getName());
	}

	public boolean isMatrix() {
		return  MetaModel.MATRIX.equals(getName());
	}

	public MathContainer clone(MathFormula formula) {
		MathArray array = new MathArray(formula, meta, columns, rows);
		array.copy(0, 0, this);
		return array;
	}

	/** Copy array into this object (with silent clipping).
	 * @param ioffset leading empty rows
	 * @param joffset leading empty columns. */
	public void copy(int ioffset, int joffset, MathArray array) {
		for(int i=0;(i<(rows+joffset)||i<array.rows);i++) {
			for(int j=0;(j<(columns+ioffset)||j<array.columns);j++) {
				MathSequence component = array.getArgument(i,j);
				component = (MathSequence)component.clone(formula);
				setArgument(i+ioffset, j+joffset, component);
			}
		}
	}

	/** Number of columns. */
	public int columns() {
		return columns;
	}

	/** Number of rows. */
	public int rows() {
		return rows;
	}

}