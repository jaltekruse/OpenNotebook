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
package doc.test;

import doc.attributes.AttributeException;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.IntegerAttribute;

import java.awt.*;

import static doc.test.TestUtils.assertEquals;

public class TestAttributeValidation {

	public void testIntAttribute() {
		IntegerAttribute intAtt = new IntegerAttribute("name", 0, 10);
		intAtt.setValue(0);
		intAtt.setValue(5);
		intAtt.setValue(10);
		boolean validationCheckFailed = false;
		try {
			intAtt.setValue(11);
			validationCheckFailed = true;
		} catch (Exception ex) {
			// test success
		}
		if (validationCheckFailed) {
			throw new RuntimeException("Integer attribute validation failed");
		}
	}

	public void testBooleanAttribute() throws AttributeException {
		BooleanAttribute boolAtt = new BooleanAttribute("name", true);
		assertEquals(boolAtt.getValue(), true);
		boolAtt.setValue(false);
		assertEquals(boolAtt.getValue(), false);
		assertEquals(boolAtt.readValueFromString("true"), true);
		assertEquals(boolAtt.readValueFromString("false"), false);
	}

	public void testColorAttribute() throws AttributeException {
		ColorAttribute colorAtt = new ColorAttribute("name", new Color(1,1,1));
		assertEquals(colorAtt.readValueFromString("10,10,10"), new Color(10,10,10));
		boolean validationCheckFailed = false;
		try {
			colorAtt.readValueFromString("10,10,-10");
			validationCheckFailed = true;
		} catch (AttributeException ex) {
			// test success
		}
		if (validationCheckFailed) {
			throw new RuntimeException("Color attribute validation failed");
		}
	}

	public void testDateAttribute() {

	}

	public void testDoubleAttribute() {

	}

	public void testEmailAttribute() {

	}

	public void testEnumeratedAttribute() {

	}

	public void testGridPointAttribute() {

	}

	public void testListAttribute() {

	}

	public void testSelectionAttribute() {

	}
}
