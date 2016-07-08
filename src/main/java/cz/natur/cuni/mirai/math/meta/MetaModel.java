/* MetaModel.java
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
package cz.natur.cuni.mirai.math.meta;

import java.util.ArrayList;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 
 * Distinction between operators, core functions and functions is based on
 * visual aspects of math elements rather than based on mathematical analogy.
 * 
 * operator) a * b
 * 
 * core function) common math symbols with custom appearance such as fraction,
 * square root, integration
 * 
 * function(...)
 * 
 */
public class MetaModel {

	/* Arrays and matrices. */
	public static final String ARRAY = "Array";
	public static final String MATRIX = "Matrix";

	/* Categories. */
	public static final String CHARACTERS = "Characters";
	public static final String GENERAL = "General";
	public static final String OPERATORS = "Operators";
	public static final String SYMBOLS = "Symbols";
	public static final String FUNCTIONS = "Functions";

	/* Array and matrix elements. */
	static final String OPEN = "Open";
	static final String CLOSE = "Close";
	static final String FIELD = "Field";
	static final String ROW = "Row";

	/* Category element. */
	private static final String CHARACTER = "Character";
	private static final String OPERATOR = "Operator";
	private static final String SYMBOL = "Symbol";
	private static final String FUNCTION = "Function";
	private static final String PARAMETER = "Parameter";

	/* Category attributes. */
	private static final String GROUP = "group";
	private static final String COLUMNS = "columns";

	/* Element attributes. */
	private static final String NAME = "name";
	private static final String DESC = "desc";
	private static final String TYPE = "type";
	private static final String CAS = "cas";
	private static final String TEX = "tex";
	private static final String KEY = "key";
	private static final String IMG = "img";

	private static final String UP = "up";
	private static final String DOWN = "down";

	private static final String CODE = "code";
	private static final String FONTID = "fontId";

	private static final String INITIAL = "initial";
	private static final String INSERT = "insert";

	private ArrayList<MetaGroup> groups = new ArrayList<MetaGroup>();

	private int defaultArraySize = 1;
	private int defaultVectorSize = 1;
	private int defaultMatrixColumns = 2;
	private int defaultMatrixRows = 2;

	public MetaModel(String filename) {
		try {
			Element root = new SAXBuilder().build(
				MetaModel.class.getClassLoader().getResourceAsStream(filename)).getRootElement();
			// keyboard input, characters and operators
			parseComponents(root);
		} catch (Exception e) {
			System.out.println("parse failed: "+filename+"\n");
			e.printStackTrace();
		}
	}

	/** get array */
	public MetaArray getArray() {
		return (MetaArray) getGroup(ARRAY);
	}

	/** get matrix */
	public MetaArray getMatrix() {
		return (MetaArray) getGroup(MATRIX);
	}

	/** Character. */
	public boolean isCharacter(String name) {
		try {
			getCharacter(name);
			return true;
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/** get character */
	public MetaCharacter getCharacter(String name) {
		return (MetaCharacter) getComponent(CHARACTERS, name);
	}

	/** Operator. */
	public boolean isOperator(String name) {
		try {
			getOperator(name);
			return true;
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/** get operator */
	public MetaCharacter getOperator(String name) {
		return (MetaCharacter) getComponent(OPERATORS, name);
	}


	/** Symbol. */
	public boolean isSymbol(String name) {
		try {
			getSymbol(name);
			return true;
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/** get symbol */
	public MetaSymbol getSymbol(String name) {
		return (MetaSymbol) getComponent(SYMBOLS, name);
	}

	/** get symbols */
	public MetaComponent[] getSymbols() {
		return getComponents(SYMBOLS);
	}

	/** Custom Function. */
	public boolean isGeneral(String name) {
		try {
			getComponent(GENERAL, name);
			return true;
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/** get custom function */
	public MetaFunction getGeneral(String name) {
		return (MetaFunction) getComponent(GENERAL, name);
	}

	/** Function. */
	public boolean isFunction(String casName) {
		try {
			getFunction(casName);
			return true;
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/** get function */
	public MetaFunction getFunction(String name) {
		return (MetaFunction) getComponent(FUNCTIONS, name);
	}

	/** get functions */
	public MetaComponent[] getFunctions() {
		return getComponents(FUNCTIONS);
	}

	/** get component */
	public MetaComponent getComponent(String tabName, String name) {
		MetaGroup group[] = getGroups(tabName);
		for(int i=0;i<group.length;i++) {
			MetaComponent meta = group[i].getComponent(name);
			if(meta!=null) {
				return meta;
			}
		}
		
		throw new ArrayIndexOutOfBoundsException(
				"Component Not found "+name);
	}

	/** get components */
	public MetaComponent[] getComponents(String groupName) {
		MetaGroup group = getGroup(groupName);
		return group.getComponents();
	}

	/** get group */
	public MetaGroup getGroup(String groupName) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getName().equals(groupName)) {
				return groups.get(i);
			}
		}

		throw new ArrayIndexOutOfBoundsException("MetaGroup Not found "
				+ groupName);
	}

	/** get groups */
	public MetaGroup[] getGroups(String tab) {
		ArrayList<MetaGroup> arrayList = new ArrayList<MetaGroup>();
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getGroup().equals(tab)) {
				arrayList.add(groups.get(i));
			}
		}
		return (MetaGroup[]) arrayList.toArray(new MetaGroup[arrayList.size()]);
	}

	public int getDefaultArraySize() {
		return defaultArraySize;
	}

	public void setDefaultArraySize(int defaultArraySize) {
		this.defaultArraySize = defaultArraySize;
	}

	public int getDefaultVectorSize() {
		return defaultVectorSize;
	}

	public void setDefaultVectorSize(int defaultVectorSize) {
		this.defaultVectorSize = defaultVectorSize;
	}

	public int getDefaultMatrixColumns() {
		return defaultMatrixColumns;
	}

	public void setDefaultMatrixColumns(int defaultMatrixColumns) {
		this.defaultMatrixColumns = defaultMatrixColumns;
	}

	public int getDefaultMatrixRows() {
		return defaultMatrixRows;
	}

	public void setDefaultMatrixRows(int defaultMatrixRows) {
		this.defaultMatrixRows = defaultMatrixRows;
	}

	private MetaCharacter parseArrayElement(Element element) throws Exception {

		String name = ((Element) element).getName();
		String cas = getStringAttribute(CAS, (Element) element);
		String tex = cas;
		char key = cas.length() > 0 ? cas.charAt(0) : 0;

		try {
			tex = getStringAttribute(TEX, (Element) element);
		} catch (Exception e) {}

		try {
			key = getCharAttribute(KEY, (Element) element);
		} catch (Exception e) {}

		MetaCharacter metaCharacter = new MetaCharacter(name, cas, tex, key, MetaCharacter.CHARACTER);

		return metaCharacter;
	}

	private MetaCharacter parseCharacter(Element element) throws Exception {

		String name = getStringAttribute(NAME, (Element) element);
		String cas = name;
		String tex = name;
		char key = name.length() > 0 ? name.charAt(0) : 0;

		try {
			cas = getStringAttribute(CAS, (Element) element);
		} catch (Exception e) {}

		try {
			tex = getStringAttribute(TEX, (Element) element);
		} catch (Exception e) {}

		try {
			key = getCharAttribute(KEY, (Element) element);
		} catch (Exception e) {}

		MetaCharacter metaCharacter = new MetaCharacter(name, cas, tex, key, MetaCharacter.CHARACTER);

		return metaCharacter;
	}

	private MetaSymbol parseSymbol(Element element) throws Exception {
		String elementName = ((Element) element).getName();
		int type = elementName.equals(OPERATOR) ? MetaCharacter.OPERATOR : MetaCharacter.SYMBOL; 

		String name = getStringAttribute(NAME, (Element) element);
		String cas = name;
		String tex = name;
		char key = name.length() > 0 ? name.charAt(0) : 0;

		try {
			cas = getStringAttribute(CAS, (Element) element);
		} catch (Exception e) {
		}

		try {
			tex = getStringAttribute(TEX, (Element) element);
		} catch (Exception e) {
		}

		try {
			key = getCharAttribute(KEY, (Element) element);
		} catch (Exception e) {
		}

		int code = getIntAttribute(CODE, (Element) element);
		int fontId = getIntAttribute(FONTID, (Element) element);
		MetaSymbol metaSymbol = new MetaSymbol(name, cas, tex, key, (char) code, fontId, type);

		try {
			String img = getStringAttribute(IMG, (Element) element);
			metaSymbol.setIcon(img);
		} catch (Exception e) {
		}

		return metaSymbol;
	}

	private MetaFunction parseFunction(Element element) throws Exception {
		String name = getStringAttribute(NAME, (Element) element);
		String cas = name;
		String tex = name;
		char key = name.length() > 0 ? name.charAt(0) : 0;

		try {
			cas = getStringAttribute(CAS, (Element) element);
		} catch (Exception e) {}

		try {
			tex = getStringAttribute(TEX, (Element) element);
		} catch (Exception e) {}

		try {
			key = getCharAttribute(KEY, (Element) element);
		} catch (Exception e) {}

		ArrayList<MetaParameter> parameterArray = new ArrayList<MetaParameter>();
		for (Object parameter : ((Element) element).getChildren(PARAMETER)) {
			String paramName = getStringAttribute(NAME, (Element) parameter);
			int order = getIntAttribute("order", (Element) parameter);

			MetaParameter metaParameter = new MetaParameter(paramName, order);
			parameterArray.add(metaParameter);

			try {
				String type = getStringAttribute(TYPE, (Element) parameter);
				metaParameter.setType(type);
			} catch (Exception e) {}

			try {
				String desc = getStringAttribute(DESC, (Element) parameter);
				metaParameter.setDescription(desc);
			} catch (Exception e) {}

			try {
				int upIndex = getIntAttribute(UP, (Element) parameter);
				metaParameter.setUpIndex(upIndex);
			} catch (Exception e) {}

			try {
				int downIndex = getIntAttribute(DOWN, (Element) parameter);
				metaParameter.setDownIndex(downIndex);
			} catch (Exception e) {}

		}
		MetaParameter parameters[] = parameterArray.toArray(new MetaParameter[parameterArray.size()]);
		MetaFunction metaFunction = new MetaFunction(name, cas, tex, key, parameters);

		try {
			String img = getStringAttribute(IMG, (Element) element);
			metaFunction.setIcon(img);
		} catch (Exception e) {}

		try {
			String desc = getStringAttribute(DESC, (Element) element);
			metaFunction.setDescription(desc);
		} catch (Exception e) {}

		try {
			int initialIndex = getIntAttribute(INITIAL, (Element) element);
			metaFunction.setInitialIndex(initialIndex);
		} catch (Exception e) {}

		try {
			int insertIndex = getIntAttribute(INSERT, (Element) element);
			metaFunction.setInsertIndex(insertIndex);
		} catch (Exception e) {}

		return metaFunction;
	}

	private void parseComponents(Element parent) throws Exception {
		for (Object elements : parent.getChildren()) {
			String groupName = ((Element) elements).getName(), group = groupName;
			int columns = 0;
			try {
				groupName = getStringAttribute(NAME, (Element) elements);
			} catch (Exception e) {}

			try {
				group = getStringAttribute(GROUP, (Element) elements);
			} catch (Exception e) {}

			try {
				columns = getIntAttribute(COLUMNS, (Element) elements);
			} catch (Exception e) {}

			ArrayList<MetaComponent> arrayList = new ArrayList<MetaComponent>();
			for (Object element : ((Element) elements).getChildren()) {
				String name = ((Element) element).getName();
				if (name.equals(OPEN) || name.equals(CLOSE) || 
					name.equals(FIELD) || name.equals(ROW)) {
					MetaComponent metaComponent = parseArrayElement((Element) element);
					arrayList.add(metaComponent);

				} else if (name.equals(CHARACTER)) {
					MetaCharacter metaCharacter = parseCharacter((Element) element);
					arrayList.add(metaCharacter);

				} else if (name.equals(OPERATOR)) {
					MetaSymbol metaOperator = parseSymbol((Element) element);
					arrayList.add(metaOperator);

				} else if (name.equals(SYMBOL)) {
					MetaSymbol metaSymbol = parseSymbol((Element) element);
					arrayList.add(metaSymbol);

				} else if (name.equals(FUNCTION)) {
					MetaFunction metaFunction = parseFunction((Element) element);
					arrayList.add(metaFunction);
				}
			}
			MetaComponent metas[] = arrayList
					.toArray(new MetaComponent[arrayList.size()]);

			if(groupName.equals(ARRAY) || groupName.equals(MATRIX)) {
				groups.add(new MetaArray(groupName, group, metas));
			} else {
				groups.add(new MetaGroup(groupName, group, metas, columns));
			}
		}
	}

	private static String getStringAttribute(String attrName, Element element) throws Exception {
		String attrValue = element.getAttributeValue(attrName);
		if (attrValue == null)
			throw new Exception(element.getName() + " is null.");
		return attrValue;
	}

	private static int getIntAttribute(String attrName, Element element) throws Exception {
		String attrValue = getStringAttribute(attrName, element);
		int res = 0;
		try {
			res = Integer.parseInt(attrValue);
		} catch (NumberFormatException e) {
			throw new Exception(element.getName() + " has invalid value.");
		}
		return res;
	}

	private static char getCharAttribute(String attrName, Element element) throws Exception {
		String attrValue = getStringAttribute(attrName, element);
		char res = 0;
		try {
			res = attrValue.length() > 0 ? attrValue.charAt(0) : 0;
		} catch (NumberFormatException e) {
			throw new Exception(element.getName() + " has invalid value.");
		}
		return res;
	}
}
