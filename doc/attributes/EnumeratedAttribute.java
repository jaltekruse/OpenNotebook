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

public class EnumeratedAttribute extends MathObjectAttribute<String>{
	
	private String[] possibleValues;
	
	public EnumeratedAttribute(String n, String[] possibleValues) {
		super(n);
		setValue("");
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, boolean userEditable, String[] possibleValues) {
		super(n, userEditable);
		setValue("");
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, boolean userEditable, boolean studentEditable, String[] possibleValues) {
		super(n, userEditable, studentEditable);
		setValue("");
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, String s, String[] possibleValues) {
		super(n);
		setValue(s);
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, String s, boolean userEditable, String[] possibleValues) {
		super(n, userEditable);
		setValue(s);
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, String s, boolean userEditable,
			boolean studentEditable, String[] possibleValues) {
		super(n, userEditable, studentEditable);
		setValue(s);
		setPossibleValues(possibleValues);
	}
	
	@Override
	public String getType() {
		return ENUMERATED_ATTRIUBTE;
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		for (String str : possibleValues){
			if (s.equals(str)){
				return s;
			}
		}
		throw new AttributeException("The value does not match one of the" +
				" enumerated values allowed for this attribute.");
	}

	@Override
	public void resetValue() {
		setValue("");
	}

	public String[] getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(String[] possibleValues) {
		this.possibleValues = possibleValues;
	}

}
