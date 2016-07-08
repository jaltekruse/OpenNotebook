/* MathModel.java
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

import cz.natur.cuni.mirai.math.meta.MetaModel;

public class MathModel {
	
	private boolean modified = false;
	private ElementList elementList;
	private MetaModel meta;

	public interface ElementList {
		void clear();
		Object get(int i);
		void add(int i, Object e);
		void remove(int from, int to);
		int size();
	}

	private static class SimpleList implements ElementList {
		private ArrayList<Object> es = new ArrayList<Object>();

		public void clear() {
			es.clear();
		}

		public Object get(int i) {
			return es.get(i);
		}

		public void add(int i, Object e) {
			es.add(i, e);
		}

		public void remove(int fm, int to) {
			for(int i=fm;i<=to;i++) {
				es.remove(fm);
			}
		}

		public int size() {
			return es.size();
		}
	}

	public static MathModel newModel(MetaModel meta) {
		// create initial workbook, formula and sequence
		MathModel model = new MathModel(meta);
		MathFormula formula = new MathFormula(meta);
		formula.setRootComponent(new MathSequence(formula));
		model.addElement(formula);
		model.setModified(false);
		return model;
	}
	
	public MathModel(MetaModel meta) {
		this.elementList = new SimpleList();
		this.meta=meta;
	}

	public MetaModel getMetaModel() {
		return meta;
	}

	public void replaceElementList() {
		replaceElementList(new SimpleList());
	}

	public void replaceElementList(ElementList newList) {
		newList.clear();
		for(int i=0;i<elementList.size();i++) {
			newList.add(i,elementList.get(i));
		}
		elementList = newList;
	}
	
	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;

		// to debug change tracking
		//if(modified) { 
		//	new Exception("Model has been modified.").printStackTrace();
		//}

		// if not modified; propagate to children
		if(!modified) { 
			int i = firstFormula();
			MathFormula formula = (MathFormula)getElement(i);
			formula.setModified(false);
			while(hasNextFormula(i)) {
				i = nextFormula(i);
				formula = (MathFormula)getElement(i);
				formula.setModified(false);
			}
		}
	}

	public void addElement(Object element) {
		addElement(size(),element);
	}
	
	public void addElement(int i, Object element) {
		elementList.add(i, element);
		setModified(true);
		if(element instanceof MathFormula) {
			MathFormula formula = (MathFormula)element;
			formula.setModel(this);
		}
	}

	public Object getElement(int i) {
		return elementList.get(i);
	}

	public void removeElements(int fm, int to) {
		for(int l=fm;l<to;l++) {
			if(getElement(l) instanceof MathFormula) {
				MathFormula formula = (MathFormula)getElement(l);
				formula.setModel(null);
			}			
		}
		elementList.remove(fm, to);
		setModified(true);
	}

	public void removeResults(int i) {
		int to = hasNextFormula(i) ? nextFormula(i) : size();
		for(int l=i+1;l<to-1;l++) {
			if(getElement(l) instanceof MathFormula) {
				MathFormula formula = (MathFormula)getElement(l);
				formula.setModel(null);
			}			
		}
		removeElements(i+1,to-1);
	}

	/** Get index of first formula. */
	public int firstFormula() {
		// strange but correct
		return nextFormula(-1);
	}

	/** Get index of last formula. */
	public int lastFormula() {
		return prevFormula(elementList!=null?elementList.size():0); 
	}

	/** Is there a next formula? */
	public boolean hasNextFormula(int current) {
		for(int i=current+1;i<(elementList!=null?elementList.size():0);i++) {
			if(getElement(i) instanceof MathFormula) {
				return true;
			}
		}
		return false;
	}

	/** Get index of next formula. */
	public int nextFormula(int current) {
		for(int i=current+1;i<(elementList!=null?elementList.size():0);i++) {
			if(getElement(i) instanceof MathFormula) {
				return i;
			}
		}
		throw new ArrayIndexOutOfBoundsException(
			"Index out of array bounds.");
	}

	/** Is there previous formula? */
	public boolean hasPrevFormula(int current) {
		for(int i=current-1;i>=0;i--) {
			if(getElement(i) instanceof MathFormula) {
				return true;
			}
		}
		return false;
	}

	/** Get index of previous formula. */
	public int prevFormula(int current) {
		for(int i=current-1;i>=0;i--) {
			if(getElement(i) instanceof MathFormula) {
				return i;
			}
		}
		throw new ArrayIndexOutOfBoundsException(
			"Index out of array bounds.");
	}

	public int size() {
		return elementList.size();
	}
}
