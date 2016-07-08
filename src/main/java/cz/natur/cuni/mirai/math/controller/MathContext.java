/* MathContext.java
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
package cz.natur.cuni.mirai.math.controller;

import cz.natur.cuni.mirai.math.model.MathComponent;
import cz.natur.cuni.mirai.math.model.MathContainer;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathFunction;
import cz.natur.cuni.mirai.math.model.MathSequence;

/**
 * Controller context.
 * 
 * Handles caret position on container, and letters offset.
 * 
 * beforeField indicates caret before formula.
 * afterField indicates caret after formula.
 * 
 * @author Bea Petrovicova
 */
abstract class MathContext {

	protected MathFormula formula = null;
	protected MathSequence currentField = null;
	protected int currentOffset = 0;

	public MathFormula getFormula() {
		return formula;
	}

	public void setFormula(MathFormula formula) {
		this.formula = formula;		
		if(formula.getRootComponent() instanceof MathSequence) {
			currentField = formula.getRootComponent();
			currentOffset = currentField.size();
		} else {
			lastField();
		}
	}

	public MathFormula cut() {
		MathFormula newFormula = MathFormula.newFormula(formula.getMetaModel());
		MathFormula cutFormula = formula;
		setFormula(newFormula);
		return cutFormula;
	}

	public void paste(MathFormula formula) {		
		// clone first
		MathSequence rootComponent = (MathSequence)formula.getRootComponent().clone(this.formula);

		// insert then
		int size = rootComponent.size();
		while(rootComponent.size()>0) {
			MathComponent element = rootComponent.getArgument(rootComponent.size()-1);
			rootComponent.delArgument(rootComponent.size()-1);
			currentField.addArgument(currentOffset, element);
		}
		currentOffset += size;
	}

	/** Next character -> key. */
	public void nextCharacter() {
		if (currentOffset < currentField.size() &&
			currentField.getArgument(currentOffset) != null &&
			currentField.getArgument(currentOffset) instanceof MathContainer &&
			((MathContainer)currentField.getArgument(currentOffset)).hasChildren()) {
			MathComponent component = currentField.getArgument(currentOffset);
			firstField((MathContainer)component);

		} else if(currentOffset<currentField.size()) {
			currentOffset++;

		} else {
			nextField();
		}
	}

	/** Previous character <- key. */
	public void prevCharacter() {
		if (currentOffset-1 >= 0 &&
			currentField.getArgument(currentOffset-1) != null &&
			currentField.getArgument(currentOffset-1) instanceof MathContainer &&
			((MathContainer)currentField.getArgument(currentOffset-1)).hasChildren()) {
			MathComponent component = currentField.getArgument(currentOffset-1);
			lastField((MathContainer)component);

		} else if(currentOffset>0) {
			currentOffset--;

		} else {
			prevField();
		}
	}

	/** Select first field. */
	public void firstField() {
		MathContainer component = formula.getRootComponent();
		firstField(component);
	}

	/** Select last field. */
	public void lastField() {
		MathContainer component = formula.getRootComponent();
		lastField(component);
	}

	protected void firstField(MathContainer component) {
		// surface to first symbol
		while(!(component instanceof MathSequence)) {
			int current = component.first();
			component = (MathContainer)component.getArgument(current);
		}
		currentField = (MathSequence)component;
		currentOffset = 0;
	}

	protected void lastField(MathContainer component) {
		// surface to last symbol
		while(!(component instanceof MathSequence)) {
			int current = component.last();
			component = (MathContainer)component.getArgument(current);
		}
		currentField = (MathSequence)component;
		currentOffset = currentField.size();
	}

	/** Next field. */
	public void nextField() {
		nextField(currentField);
	}

	/** Find previous field. */
	public void prevField() {
		prevField(currentField);
	}

	/* Find next character. */
	private void nextField(MathContainer component) {
		// retrieve parent
		MathContainer container = component.getParent();
		int current = component.getParentIndex();

		if(container==null) {
			// this component has no parent
			// previous component doesn't exist
			return; 

		// try sequence
		} else if (container instanceof MathSequence) {
			currentField = (MathSequence)container;
			currentOffset = component.getParentIndex()+1;

		// try to find next sibling
		} else if (container.hasNext(current)) {
			current = container.next(current);
			component = (MathContainer)container.getArgument(current);
			firstField(component); 

		// try to delve down the tree
		} else {
			nextField(container);
		}
	}

	/* Search for previous component */
	private void prevField(MathContainer component) {
		// retrieve parent
		MathContainer container = component.getParent();
		int current = component.getParentIndex();

		if(container==null) {
			// this component has no parent
			// previous component doesn't exist
			return; 

		// try sequence
		} else if (container instanceof MathSequence) {
			currentField = (MathSequence)container;
			currentOffset = component.getParentIndex(); 

		// try to find previous sibling
		} else if (container.hasPrev(current)) {
			current = container.prev(current);
			component = (MathContainer)container.getArgument(current);
			lastField(component);

		// delve down the tree
		} else {
			prevField(container);
		} 
	}

	/** Up field. */
	public boolean upField() {
		return upField(currentField);
	}

	/** Down field. */
	public boolean downField() {
		return downField(currentField);
	}

	/** Up field. */
	private boolean upField(MathContainer component) {
		if(component instanceof MathSequence) {
			if(component.getParent() instanceof MathFunction) {
				MathFunction function = (MathFunction)component.getParent();
				int upIndex = function.getUpIndex(component.getParentIndex());
				if(upIndex >= 0) {
					currentField = function.getArgument(upIndex);
					currentOffset = 0;
					return true;
				}
			}
			// matrix goes here
		} 
		if(component.getParent()!=null) {
			return upField(component.getParent());
		}			
		return false;
	}
	
	/** Down field. */
	private boolean downField(MathContainer component) {
		if(component instanceof MathSequence) {
			if(component.getParent() instanceof MathFunction) {
				MathFunction function = (MathFunction)component.getParent();
				int downIndex = function.getDownIndex(component.getParentIndex());
				if(downIndex >= 0) {
					currentField = function.getArgument(downIndex);
					currentOffset = 0;
					return true;
				}
			}
			// matrix goes here
		} 
		if(component.getParent()!=null) {
			return downField(component.getParent());
		}			
		return false;
	}
}
