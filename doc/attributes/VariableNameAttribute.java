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


public class VariableNameAttribute extends MathObjectAttribute<String>{
	
	public VariableNameAttribute(String n) {
		super(n);
		setValue("");
	}
	
	public VariableNameAttribute(String n, boolean userEditable) {
		super(n, userEditable);
		setValue("");
	}
	
	public VariableNameAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue("");
	}
	
	public VariableNameAttribute(String n, String s) {
		super(n);
		setValue(s);
	}
	
	public VariableNameAttribute(String n, String s, boolean userEditable) {
		super(n, userEditable);
		setValue(s);
	}
	
	public VariableNameAttribute(String n, String s, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue(s);
	}
	
	
	@Override
	public String getType() {
		return VAR_NAME_ATTRIBUTE;
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		if ( s.length() != 1){
			throw new AttributeException("Variables must be one chracter in length.");
		}
		if ( ! Character.isLetter(s.charAt(0))){
			throw new AttributeException("Variable name must be a letter.");
		}
		else{
			return s;
		}
	}

	@Override
	public void resetValue() {
		setValue("");
	}

}
