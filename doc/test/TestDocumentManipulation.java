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

import doc.Document;
import doc.mathobjects.MathObject;

import static doc.test.TestUtils.assertEquals;

public class TestDocumentManipulation {

	public void testDocCreation() {
		Document doc = new Document("doc");
		doc.addBlankPage();
		assertEquals(doc.getPages().size(), 1);
	}

	public void testObjectClone() {
		for (MathObject mObj : MathObject.objects) {
			// TODO - remove this once the list of objects no longer contains nulls
			if (mObj == null) {
				continue;
			}
			assertEquals(mObj, mObj.clone());
		}
	}

	// TODO - remove this and replace with JUnit when dependencies are
	// managed by Maven
	public static void main(String[] args) {
		TestDocumentManipulation tests = new TestDocumentManipulation();
		tests.testDocCreation();
		tests.testObjectClone();
	}
}
