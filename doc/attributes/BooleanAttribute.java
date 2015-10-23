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


public class BooleanAttribute extends MathObjectAttribute<Boolean> {
	
	public BooleanAttribute(String n){
		super(n);
	}
	
	public BooleanAttribute(String n, boolean b){
		super(n);
		// this needs to have the next line instead of calling a different
		// super constructor because of collision with superclass constructor
		// designed to just set an attribute name and userEditable variable
		setValue(b);
	}
	
	public BooleanAttribute(String n, boolean b, boolean userEditable){
		super(n, userEditable);
		// need this next line, see comment in above constructor
		setValue(b);
	}
	
	public BooleanAttribute(String n, boolean b, boolean userEditable, boolean studentEditable){
		super(n, b, userEditable, studentEditable);
	}

	@Override
	public String getType() {
		return BOOLEAN_ATTRIBUTE;
	}

	@Override
	public Boolean readValueFromString(String s) throws AttributeException {
		if (s.equals("true")){
			return true;
		}
		else if (s.equals("false")){
			return false;
		}
		throw new AttributeException(getName() + " must be 'true' or 'false' without single quotes");
	}

	@Override
	public void resetValue() {
		setValue(false);
	}
}
