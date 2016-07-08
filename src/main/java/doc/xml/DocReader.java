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

package doc.xml;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


import doc.Document;
import doc.Page;
import doc.ProblemDatabase;
import doc.attributes.AttributeException;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.mathobjects.ExpressionObject;
import doc.mathobjects.GeneratedProblem;
import doc.mathobjects.GraphObject;
import doc.mathobjects.Grouping;
import doc.mathobjects.MathObject;
import doc.mathobjects.PolygonObject;
import doc.mathobjects.ProblemGenerator;
import doc.mathobjects.RegularPolygonObject;
import doc.mathobjects.TextObject;
import doc.mathobjects.VariableValueInsertionProblem;

import javax.xml.bind.DatatypeConverter;

public class DocReader extends DefaultHandler {

	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String PROBLEM = "Problem";
	private Document doc;
	private Page page;
	private Vector<Grouping> containerStack;
	private boolean DEBUG = true;

	// objects added to Groupings ( or subclasses of Grouping ) need to have their attributes 
	// read in before being added, so their positions can be calculated relative
	// to the size of the group. Below is a vector to store the objects temporarily, they will
	// only be added when a grouping tag is closed
	private Vector<Vector<MathObject>> objectsInGroup;

	// to import data stored that was as a string, that should have been stored
	// as a list all along, there are a few hacks where the string attribute
	// should be added to the object
	// this list holds the hacked list names, so they are not reset when the list is found later
	private Vector<String> overridenLists;

	private ProblemDatabase database;
	private MathObject mObj;
	private ListAttribute list;
	boolean hadAttributeError, foundBadTag, readingGenerators, readingList, readingProblemDatabase;
	int versionNumber;
	String badTag;
	String attributeNameInError;
	String attributeValueInError;
	String objectWithError;
	String fileName;

	public DocReader(){
		doc = null;
		page = null;
		mObj = null;
		hadAttributeError = false;
		foundBadTag = false;
		objectWithError = null;
		attributeNameInError = null;
		attributeValueInError = null;
	}

	public ProblemDatabase readDatabase(InputStreamReader file)
			throws SAXException, IOException{
		readFile(file, "");
		return database;
	}
	
	public Document readServerDoc(String doc, String docName) throws SAXException, IOException{
		byte[] docBytes = DatatypeConverter.parseBase64Binary(doc);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(docBytes);
		InputStreamReader reader = new InputStreamReader(inputStream);
		return readDoc(reader, docName);
	}
	
	public static String getRandomMessage(){
		return "sdfsdfewbtyasdfasdf";
	}

	public Document readDoc (InputStreamReader file, String docName) throws SAXException, IOException{
		readFile(file, docName);
		return doc;
	}

	public void readFile (InputStreamReader file, String docName) throws SAXException, IOException{
		doc = null;
		readingProblemDatabase = false;
		page = null;
		mObj = null;
		hadAttributeError = false;
		foundBadTag = false;
		readingGenerators = false;
		readingList = false;
		list = null;
		attributeNameInError = null;
		attributeValueInError = null;
		objectWithError = null;
		fileName = docName;

		containerStack = new Vector<>();
		objectsInGroup = new Vector<>();
		overridenLists = new Vector<>();

		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(this);
		reader.setErrorHandler(this);

		reader.parse(new InputSource(file));
		try { 
			if ( doc == null && ! readingProblemDatabase ){
				throw new IOException("improper document format");
			}
			if ( database == null && readingProblemDatabase){
				throw new IOException("improper problem database format");	
			}
			if (hadAttributeError){
				throw new IOException("improper document format, error with attribute '" + 
						attributeNameInError + "' with a value of '" + attributeValueInError + "'" 
						+ " in object '" + objectWithError + "'");
			}
			if (foundBadTag){
				throw new IOException("found bad tag, " + badTag);
			}
		} catch (IOException e){
			e.printStackTrace();

			OldReader oldReader = new OldReader();
			doc = oldReader.readFile(file);
		}
	}

	@Override
	public void startDocument(){
	}

	@Override
	public void endDocument(){
	}

	@Override
	public void startElement (String uri, String name,
			String qName, Attributes atts){

		if (qName.equals(Document.OPEN_NOTEBOOK_DOC)){
			// the name of the file in the filesystem
			// will allow documents that are renamed outside of
			// the application to take their new name
			doc = new Document(fileName);
			doc.setAuthor(atts.getValue(Document.AUTHOR));
			if ( atts.getValue(Document.LAST_PROBLEM_NUMBER) != null)
			{
				doc.setLastProblemNumber(Integer.parseInt(atts.getValue(Document.LAST_PROBLEM_NUMBER)));
			}
			if ( atts.getValue(Document.X_MARGIN) != null)
			{
				doc.setxMargin(Double.parseDouble(atts.getValue(Document.X_MARGIN)));
			}
			if ( atts.getValue(Document.Y_MARGIN) != null)
			{
				doc.setyMargin(Double.parseDouble(atts.getValue(Document.Y_MARGIN)));
			}
			return;
		}
		else if ( qName.equals(ProblemDatabase.NAME)){
			database = new ProblemDatabase();
			readingProblemDatabase = true;
			return;
		}
		else if (qName.equals(Document.GENERATORS)){
			readingGenerators = true;
			return;
		}
		else if (qName.equals(ListAttribute.LIST)){
			if (mObj != null)
			{// grab the list from the current object with the specified name
				list = mObj.getListWithName(atts.getValue(NAME));
				if (list != null && ! overridenLists.contains(atts.getValue(ListAttribute.NAME)))
				{// the object had a list with the given name
					list.removeAll();
					readingList = true;
				}
				if ( DEBUG ){
					System.out.println("list added: " + list);
				}
				return;
			}
		}
		else if ( qName.equals(ListAttribute.ENTRY)){
			if ( ! readingList )
			{// this is an entry for a list that is currently not in use
				if ( DEBUG ){
					System.out.println("  found entry tag, but not reading a list");
				}
				return;
			}
			try {
				list.addValueWithString(atts.getValue(ListAttribute.VAL));
				return;
			} catch (AttributeException e) {
				hadAttributeError = true;
				attributeNameInError = atts.getValue(NAME);
				attributeValueInError = atts.getValue(VALUE);
				objectWithError = mObj.getClass().getSimpleName();
				return;
			}
		}
		else if (doc != null || readingProblemDatabase){
			if (qName.equals("Page")){
				page = new Page(doc);
				doc.addPage(page);
				return;
			}
			if (page != null || readingGenerators || readingProblemDatabase){
				if (mObj != null){
					if ( DEBUG ){
						System.out.println("in object, should be finding attributes, or other objects if in group");
					}
					if (readAttribute(uri, name, qName, atts))
					{// if the current tag was an attribute
						if (DEBUG){
							System.out.println("return found attribute");
						}
						return;
					}
				}
				if (DEBUG){
					System.out.println("tag was not attribute " + qName);
				}
				if (qName.equals("survey")){
					int j = 1;
				}
				mObj = MathObject.newInstanceWithType(qName);
				if ( mObj == null){
					mObj = readOldObjectName(qName);
				}
				if ( mObj != null){
					if ( DEBUG ){
						System.out.println();
						System.out.println("added Object: " + mObj.getClass().getSimpleName());
					}
					if (page != null){
						mObj.setParentContainer(page);
					}
				}
				else{
					foundBadTag = true;
					badTag = qName;
					return;
				}

				if (!containerStack.isEmpty())
				{// if an object tag was found in a group, there is an object in a group
					if (mObj != null){
						objectsInGroup.get(objectsInGroup.size() - 1 ).add(mObj);
						if (mObj instanceof Grouping){
							containerStack.add( (Grouping) mObj);
							objectsInGroup.add(new Vector<MathObject>());
						}
					}
				}
				else{
					// wait until the generators id has been found to add it to the lists
					// in the document, or in the background of the application
					if (readingGenerators || readingProblemDatabase){
						if ( DEBUG ){
							System.out.println("wait until attributes are read before adding to database");
						}
					}
					else{
						page.addObject(mObj);
					}
					if (mObj instanceof Grouping){
						containerStack.add( (Grouping) mObj);
						objectsInGroup.add(new Vector<MathObject>());
					}
				}
			}

		}
	}

	public boolean readAttribute(String uri, String name,
			String qName, Attributes atts){
		boolean justAddedAttribute = true;
		if ( qName.equals("enumeratedValue")){
			boolean tr = true;
		}
		if (true || DEBUG){
			System.out.println("check tag " + atts.getValue(NAME) + " " +
					"To see if it is an attribute");
		}

		if (MathObjectAttribute.isAttributeType(qName) ||
				MathObjectAttribute.isAttributeType(subInOldAttributeTag(qName)))
		{
			if (DEBUG){
				System.out.println("found att name " + qName);
			}
			// if code is needed when an MathObjectAttribte tag is found
		}
		else{
			justAddedAttribute = false;
			if (!containerStack.isEmpty() && mObj == containerStack.get(containerStack.size() - 1)){
				return false;
			}
			if (DEBUG){
				System.out.println("bad attribute found! " + qName);
			}
			justAddedAttribute = false;
		}
		if (justAddedAttribute){
			if (DEBUG){
				System.out.println("attribute not in error, trying to add it");
			}
			try {
				if ( mObj instanceof GraphObject && atts.getValue(
						NAME).equalsIgnoreCase(GraphObject.EXPRESSION))
				{// temporary fix to import documents with old graphs
					mObj.getListWithName(GraphObject.EXPRESSIONS).removeAll();
					mObj.getListWithName(GraphObject.EXPRESSIONS)
					.addValueWithString(atts.getValue(VALUE));
					overridenLists.add(GraphObject.EXPRESSIONS);
					return true;
				}
				if ( mObj instanceof GeneratedProblem && atts.getValue(
						NAME).equalsIgnoreCase(GeneratedProblem.UUID_STR))
				{// temporary fix to import documents with old graphs
					mObj.getListWithName(GeneratedProblem.GEN_LIST).removeAll();
					mObj.getListWithName(GeneratedProblem.GEN_LIST)
					.addValueWithString(atts.getValue(VALUE));
					overridenLists.add(GeneratedProblem.GEN_LIST);
					return true;
				}
				if ( mObj instanceof ExpressionObject && atts.getValue(
						NAME).equalsIgnoreCase(ExpressionObject.STEPS))
				{// temporary fix to import documents with old expressions
					mObj.getListWithName(ExpressionObject.STEPS).removeAll();
					if ( ! atts.getValue(VALUE).equals("")){
						String[] steps = atts.getValue(VALUE).split(";");
						overridenLists.add(ExpressionObject.STEPS);
						for (String s : steps){
							mObj.getListWithName(ExpressionObject.STEPS).addValueWithString(s);
						}
					}
					return true;
				}
				if ( mObj instanceof VariableValueInsertionProblem && atts.getValue(
						NAME).equalsIgnoreCase(VariableValueInsertionProblem.SCRIPTS))
				{// temporary fix to import documents with old problems
					mObj.getListWithName(VariableValueInsertionProblem.SCRIPTS).removeAll();
					if ( ! atts.getValue(VALUE).equals("")){
						String[] scripts = atts.getValue(VALUE).split(";");
						overridenLists.add(VariableValueInsertionProblem.SCRIPTS);
						for (String s : scripts){
							mObj.getListWithName(VariableValueInsertionProblem.SCRIPTS).addValueWithString(s);
						}
					}
					return true;
				}
				String attName = subInOldAttributeName(atts.getValue(MathObjectAttribute.NAME));

				if ( mObj.getAttributeWithName(attName) == null){
					// this attribute is no longer used in this object, ignore it
					// this is an attribute, so still return true, otherwise the parent
					// method will try to read it as a MathObject
					return true;
				}


				mObj.setAttributeValueWithString(
						attName,
						atts.getValue(MathObjectAttribute.VALUE));
				return true;
			} catch (AttributeException e) {
				if (DEBUG){
					System.out.println(e.getMessage());
				}
				hadAttributeError = true;
				attributeNameInError = atts.getValue(MathObjectAttribute.NAME);
				attributeValueInError = atts.getValue(MathObjectAttribute.VALUE);
				objectWithError = mObj.getClass().getSimpleName();
				justAddedAttribute = false;
				return false;
			}
		}
		else{
			for (int i = 0; i < atts.getLength(); i++){
				if (DEBUG){
					System.out.println(atts.getValue(i).toString());
				}
			}
			hadAttributeError = true;
			attributeNameInError = atts.getValue(NAME);
			attributeValueInError = atts.getValue(VALUE);
			objectWithError = mObj.getClass().getSimpleName();
			justAddedAttribute = false;
			return false;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName){
		if (MathObject.isMathObjectType(qName)){
			mObj = null;
			overridenLists = new Vector<>();
		}
		if (qName.equals(MathObject.GROUPING) ||
				qName.equals(MathObject.VAR_INSERTION_PROBLEM) ||
				qName.equals(PROBLEM) ||
				qName.equals(MathObject.GENERATED_PROBLEM)){
			if (qName.equals(MathObject.VAR_INSERTION_PROBLEM) ||
					qName.equals(PROBLEM) ||
					qName.equals(MathObject.GENERATED_PROBLEM)){

				if (readingGenerators ){
					try {
						doc.addGenerator( (ProblemGenerator)
								containerStack.get(containerStack.size() - 1) );
					} catch (Exception e) {
						if (DEBUG){
							e.printStackTrace();
						}
					}
				}
				else if ( readingProblemDatabase ){
					database.addProblem((VariableValueInsertionProblem)
							containerStack.get(containerStack.size() - 1));
				}
			}
			for ( MathObject mathObj : objectsInGroup.get(objectsInGroup.size() - 1)){
				containerStack.get(containerStack.size() - 1).addObjectFromPage(mathObj);
			}
			objectsInGroup.remove(objectsInGroup.size() - 1);
			containerStack.remove(containerStack.get(containerStack.size() - 1));
		}
		if (qName.equals("Page")){
			page = null;
		}
		else if (qName.equals(ListAttribute.LIST)){
			readingList = false;
		}
		else if (qName.equals(Document.OPEN_NOTEBOOK_DOC)){
		}
		else if (qName.equals(Document.GENERATORS)){
			readingGenerators = false;
		}
	}

	public String subInOldAttributeName(String s){
		if ( s.equals("fontSize"))
		{// this works for everything the had the old font size name
			// and was replaced by the new one
			// including graphs and number lines
			return TextObject.FONT_SIZE;
		}
		else if ( s.equals("numSides")){
			return RegularPolygonObject.NUM_SIDES;
		}
		else if ( s.equals("showBox")){
			return TextObject.SHOW_BOX;
		}
		else if ( s.equals("showAxis")){
			return GraphObject.SHOW_AXIS;
		}
		else if ( s.equals("showNumbers")){
			return GraphObject.SHOW_NUMBERS;
		}
		else if ( s.equals("showGrid")){
			return GraphObject.SHOW_GRID;
		}
		else if ( s.equals("thickness")){
			return PolygonObject.LINE_THICKNESS;
		}
		else if (s.equals( "tags(seperate with commas")){
			return VariableValueInsertionProblem.TAGS;
		}
		else{
			return s;
		}
	}

	public String subInOldAttributeTag(String s){
		if ( s.equals("BooleanAttribute"))
		{// this works for everything the had the old font size name
			// and was replaced by the new one
			// including graphs and number lines
			return MathObjectAttribute.BOOLEAN_ATTRIBUTE;
		}
		else if ( s.equals("DoubleAttribute")){
			return MathObjectAttribute.DOUBLE_ATTRIBUTE;
		}
		else if ( s.equals("GridPointAttribute")){
			return MathObjectAttribute.GRID_POINT_ATTRIBUTE;
		}
		else if ( s.equals("IntegerAttribute")){
			return MathObjectAttribute.INTEGER_ATTRIBUTE;
		}
		else if ( s.equals("StringAttribute")){
			return MathObjectAttribute.STRING_ATTRIBUTE;
		}
		else{
			return s;
		}
	}

	public MathObject readOldObjectName(String qName){
		MathObject mObj = null;
		// DO NOT GET RID OF THIS!! IT WAS A RECENT CHANGE AND IS NEEDED TO IMPORT
		// RECENTLY CREATED DOCUMENTS
		if (qName.equals(PROBLEM)){
			mObj = new VariableValueInsertionProblem();
		}
		return mObj;
	}
}
