package doc.xml;

import java.io.IOException;
import java.io.InputStreamReader;
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
import doc.attributes.BooleanAttribute;
import doc.attributes.DoubleAttribute;
import doc.attributes.GridPointAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc.mathobjects.AnswerBoxObject;
import doc.mathobjects.CubeObject;
import doc.mathobjects.ExpressionObject;
import doc.mathobjects.GraphObject;
import doc.mathobjects.Grouping;
import doc.mathobjects.MathObject;
import doc.mathobjects.NumberLineObject;
import doc.mathobjects.OvalObject;
import doc.mathobjects.ParallelogramObject;
import doc.mathobjects.RectangleObject;
import doc.mathobjects.TextObject;
import doc.mathobjects.TrapezoidObject;
import doc.mathobjects.TriangleObject;

public class OldReader extends DefaultHandler {

	private Document doc;
	private Page page;
	private Grouping group;
	private ProblemDatabase database;
	MathObject mObj;

	private Vector<Grouping> containerStack;

	// objects added to Groupings ( or subclasses of Grouping ) need to have their attributes 
	// read in before being added, so their positions can be calculated relative
	// to the size of the group. Below is a vector to store the objects temporarily, they will
	// only be added when a grouping tag is closed
	private Vector<MathObject> objectsInGroup;

	private boolean hadAttributeError;
	private String attributeNameInError;
	private String attributeValueInError;
	private String objectWithError;
	private boolean DEBUG = false;

	public OldReader(){
		doc = null;
		page = null;
		mObj = null;
		group = null;
		hadAttributeError = false;
		attributeNameInError = null;
		attributeValueInError = null;
		objectWithError = null;
	}

	public Document readFile (InputStreamReader file) throws SAXException, IOException{

		attributeNameInError = null;
		attributeValueInError = null;
		objectWithError = null;

		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(this);
		reader.setErrorHandler(this);

		reader.parse(new InputSource(file));
		if (doc == null){
			if (DEBUG){
				System.out.println("error 1");
			}
			throw new IOException("improper document format");
		}
		if (hadAttributeError){
			if (DEBUG){
				System.out.println("error 2");
			}
			throw new IOException("improper document format, error with attribute '" + 
					attributeNameInError + "' with a value of '" + attributeValueInError + "'"
					+ " in object '" + objectWithError + "'");
		}
		return doc;
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

		boolean justAddedObject = false;

		if (qName.equals("OpenNotebookDoc")){
			doc = new Document(atts.getValue(Document.FILENAME));
			doc.setAuthor(atts.getValue(Document.AUTHOR));
		}
		if (doc != null){
			if (qName.equals("Page")){
				page = new Page(doc);
				doc.addPage(page);
				return;
			}
			if (page != null){
				if (mObj != null){
					readAttribute(uri, name, qName, atts);
				}

				if (qName.equals("AnswerBox")){
					mObj = new AnswerBoxObject(page);
					justAddedObject = true;
				}
				if (qName.equals("CubeObject")){
					mObj = new CubeObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("ExpressionObject")){
					mObj = new ExpressionObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("GraphObject")){
					mObj = new GraphObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("NumberLineObject")){
					mObj = new NumberLineObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("OvalObject")){
					mObj = new OvalObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("ParallelogramObject")){
					mObj = new ParallelogramObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("RectangleObject")){
					mObj = new RectangleObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("TextObject")){
					mObj = new TextObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("TrapezoidObject")){
					mObj = new TrapezoidObject(page);
					justAddedObject = true;
				}
				else if (qName.equals("TriangleObject")){
					mObj = new TriangleObject(page);
					justAddedObject = true;
				}

				if (justAddedObject){
					if ( page != null){
						page.addObject(mObj);
					}
				}

			}
		}
	}

	public void readAttribute(String uri, String name,
			String qName, Attributes atts){
		MathObjectAttribute mAtt = null;
		boolean justAddedAttribute = false;

		if (qName.equals("BooleanAttribute")){
			mAtt = new BooleanAttribute(atts.getValue("name"));
			justAddedAttribute = true;
		}
		else if (qName.equals("DoubleAttribute")){
			mAtt = new DoubleAttribute(atts.getValue("name"));
			justAddedAttribute = true;
		}
		else if (qName.equals("GridPointAttribute")){
			mAtt = new GridPointAttribute(atts.getValue("name"));
			justAddedAttribute = true;
		}
		else if (qName.equals("IntegerAttribute")){
			mAtt = new IntegerAttribute(atts.getValue("name"));
			justAddedAttribute = true;

		}
		else if (qName.equals("StringAttribute")){
			mAtt = new StringAttribute(atts.getValue("name"));
			justAddedAttribute = true;

		}
		else{
			if (mObj == group){
				return;
			}
			if (DEBUG){
				System.out.println("bad attribute found! " + qName);
			}
		}
		if (justAddedAttribute){
			try {
				mAtt.setValueWithString(atts.getValue("value"));
				mObj.addAttribute(mAtt);
				mObj.setAttributeValue(mAtt.getName(), mAtt.getValue());
			} catch (AttributeException e) {
				if (DEBUG){
					System.out.println(e.getMessage());
				}
				hadAttributeError = true;
				attributeNameInError = atts.getValue("name");
				attributeValueInError = atts.getValue("value");
				objectWithError = mObj.getClass().getSimpleName();
				justAddedAttribute = false;
				return;
			}
		}
		else{
			for (int i = 0; i < atts.getLength(); i++){
				if (DEBUG){
					System.out.println(atts.getValue(i).toString());
				}
			}
			hadAttributeError = true;
			attributeNameInError = atts.getValue("name");
			attributeValueInError = atts.getValue("value");
			objectWithError = mObj.getClass().getSimpleName();
			justAddedAttribute = false;
			return;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName){
		if (qName.equals("TriangleObject") || 
				qName.equals("TrapezoidObject") ||
				qName.equals("TextObject") ||
				qName.equals("RectangleObject") ||
				qName.equals("ParallelogramObject") ||
				qName.equals("OvalObject") ||
				qName.equals("NumberLineObject") ||
				qName.equals("HexagonObject") ||
				qName.equals("GraphObject") ||
				qName.equals("ExpressionObject") ||
				qName.equals("CubeObject") ||
				qName.equals("AnswerBox")
				)
		{
			mObj = null;
		}
		if (qName.equals(MathObject.GROUPING) ||
				qName.equals(MathObject.VAR_INSERTION_PROBLEM) ){
			group = null;
		}
		if (qName.equals("Page")){
			page = null;
		}

		if (qName.equals("OpenNotebookDoc")){
		}
	}

}

