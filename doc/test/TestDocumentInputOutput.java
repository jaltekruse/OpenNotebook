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
import doc.mathobjects.GeneratedProblem;
import doc.mathobjects.MathObject;
import doc.xml.DocReader;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import static doc.test.TestUtils.assertEquals;
import static doc.test.TestUtils.assertTrue;

public class TestDocumentInputOutput {

	public void testAllObjectsBasicExport() throws IOException, SAXException {
		Document doc = new Document("doc");
		doc.addBlankPage();
		for (MathObject mObj : MathObject.objects) {
			// TODO - remove this once the list of objects no longer contains nulls
			if (mObj == null || mObj instanceof GeneratedProblem) {
				continue;
			}
			doc.getPage(0).addObject(mObj);
		}
		String output = doc.exportToXML();
		StringReader stringReader = new StringReader(output);
		Document deserializedDoc = new DocReader().readDoc(stringReader, "doc");
		for (MathObject mObj : MathObject.objects) {
			// TODO - remove this once the list of objects no longer contains nulls
			if (mObj == null || mObj instanceof GeneratedProblem) {
				continue;
			}
			assertTrue(deserializedDoc.getPage(0).getObjects().contains(mObj), "Expected object not found in doc " + mObj.exportToXML());
		}
	}

	public void testDocWithGeneratedProblems() {
		Document doc = new Document("doc");
		doc.addBlankPage();
//		doc.generateProblem();
	}
}
