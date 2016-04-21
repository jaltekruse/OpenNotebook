/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
 */

package doc.attributes;

import java.awt.Color;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.UUID;

import doc.GridPoint;
import doc.mathobjects.MathObject;
import doc_gui.graph.Selection;

public abstract class MathObjectAttribute<K> implements Cloneable{


	public static final String BOOLEAN_ATTRIBUTE = "boolean", DOUBLE_ATTRIBUTE = "double",
			GRID_POINT_ATTRIBUTE = "point", INTEGER_ATTRIBUTE = "int",
			STRING_ATTRIBUTE = "string", COLOR_ATTRIBUTE = "color", DATE = "date",
			EMAIL_ATTRIBUTE = "email", UUID_ATTRIBUTE = "uuid", VAR_NAME_ATTRIBUTE = "varName",
			ENUMERATED_ATTRIUBTE = "enumeratedValue", VAR_VAL_GENERATOR = "varValGenerator",
			SELECTION = "selection";

	public static final String[] attTypes = {	BOOLEAN_ATTRIBUTE,		DOUBLE_ATTRIBUTE,
		GRID_POINT_ATTRIBUTE,				INTEGER_ATTRIBUTE,		STRING_ATTRIBUTE,
		COLOR_ATTRIBUTE, DATE,				EMAIL_ATTRIBUTE,		UUID_ATTRIBUTE,
		VAR_NAME_ATTRIBUTE,					ENUMERATED_ATTRIUBTE,	VAR_VAL_GENERATOR,
		SELECTION};

	public static final String NAME = "name", VALUE = "value";

	private String name;
	protected K value;
	// attributes that are not userEditable will never show on the interface for modification
	// attributes that are not studentEditable will not be shown on the main document interface
	// with the addition of the workspace interface, students will need to modify some attributes
	// that they previously could not modify when they create their own expressions/objects
	// however, some attributes (mostly just correct answer fields) will not make sense to keep on
	// the student workspace interface, so they will have the teacherEditableOnly flag set
	private boolean userEditable, studentEditable, 
		teacherEditableOnly = false;
	private MathObject parentObject;

	public MathObjectAttribute(String n){
		name = n;
		setStudentEditable(false);
		setUserEditable(true);
		resetValue();
	}

	public MathObjectAttribute(String n, boolean userEditable){
		name = n;
		setStudentEditable(false);
		setUserEditable(userEditable);
		resetValue();
	}

	public MathObjectAttribute(String n, boolean userEditable, boolean studentEditable){
		name = n;
		setStudentEditable(studentEditable);
		setUserEditable(userEditable);
		resetValue();
	}

	public MathObjectAttribute(String n, K val){
		name = n;
		setValue(val);
		setStudentEditable(false);
		setUserEditable(true);
	}

	public MathObjectAttribute(String n, K val, boolean userEditable){
		name = n;
		setValue(val);
		setStudentEditable(false);
		setUserEditable(userEditable);
	}

	public MathObjectAttribute(String n, K val, boolean userEditable, boolean studentEditable){
		name = n;
		setValue(val);
		setStudentEditable(studentEditable);
		setUserEditable(userEditable);
	}

	public static boolean isAttributeType(String type){
		for (String s : attTypes){
			if (type.equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isTeacherEditableOnly(){
		return teacherEditableOnly;
	}
	
	public void setTeacherEditableOnly(boolean b){
		teacherEditableOnly = b;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// TODO - review why this method returns the value
	public abstract K readValueFromString(String s) throws AttributeException;

	public void validate(K newValue) throws AttributeException {
		// TODO - make this abstract to force subclasses to implement it
	}

	public void setValue(K value){
		// TODO - decide if this should throw a checked exception
		try {
			validate(value);
		} catch (AttributeException ex) {
			throw new RuntimeException(ex);
		}
		this.value = value;
	}

	public K getValue() {
		return value;
	}

	public String exportToXML(){
		System.out.println(getName());
		return "\t<" + getType() + " " + NAME + "=\"" + formatForXML(getName())
				+ "\" " + VALUE + "=\"" + formatForXML(getValue().toString()) + "\"/>\n";
	}

	public abstract void resetValue();

	public static String formatForXML(String aText){
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character =  iterator.current();
		while (character != CharacterIterator.DONE ){
			if (character == '<') {
				result.append("&lt;");
			}
			else if (character == '>') {
				result.append("&gt;");
			}
			else if (character == '\"') {
				result.append("&quot;");
			}
			else if (character == '\'') {
				result.append("&#039;");
			}
			else if (character == '&') {
				result.append("&amp;");
			}
			else {
				//the char is not a special one
				//add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	@Override
	public abstract MathObjectAttribute clone();

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} else if (!(other instanceof MathObjectAttribute)) {
			return false;
		} else {
			MathObjectAttribute mAtt = (MathObjectAttribute) other;
			if (getValue() == null) {
				if (mAtt.getValue() == null) {
					return true;
				} else {
					return false;
				}
			} else {
				return getValue().equals(((MathObjectAttribute)other).getValue());
			}
		}
	}

	/**
	 * Sets all fields managed by this abstract class in the destination object.
	 *
	 * Removes the need to update the subclasses clone() methods if more data is stored
	 * at this level later. A more traditional approach would just modify all of the
	 * calls to constructors throughout all of the subclasses clone() methods.
	 *
	 * @param dest
	 */
	public void copyRootManagedFields(MathObjectAttribute dest) {
		dest.setStudentEditable(isStudentEditable());
		dest.setUserEditable(isUserEditable());
	}

	public void setValueWithString(String s) throws AttributeException{
		setValue(readValueFromString(s));
	}

	public abstract String getType();

	public void setStudentEditable(boolean studentEditable) {
		this.studentEditable = studentEditable;
	}

	public boolean isStudentEditable() {
		return studentEditable;
	}

	public void setUserEditable(boolean userEditable) {
		this.userEditable = userEditable;
	}

	public boolean isUserEditable() {
		return userEditable;
	}

	public void setParentObject(MathObject parentObject) {
		this.parentObject = parentObject;
	}

	public MathObject getParentObject() {
		return parentObject;
	}
}
