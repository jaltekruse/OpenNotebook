/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.mathobjects;

import java.awt.Rectangle;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JOptionPane;

import doc.GridPoint;
import doc.Page;
import doc.PointInDocument;
import doc.attributes.AttributeException;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.UUIDAttribute;

public abstract class MathObject implements Cloneable{

	protected MathObjectContainer parentContainer;

	private Vector<MathObjectAttribute<?>> attributes;

	private Vector<ListAttribute<?>> attrLists;
	
	private Vector<NamedObjectList> objectLists;
	
	// this will often be unused for now, only for identifying sub-objects in the above list
	// later it could be used to refer to objects in scripts
	private String name;

	// flag used for temporarily storing if an action was successful
	// prevents unnecessary undo states from being added when actions
	// are canceled
	private boolean actionCancelled = false;

	public static final String ANSWER_BOX_OBJ = "AnswerBox",
			EXPRESSION_OBJ = "Expression", GRAPH_OBJ = "Graph",
			NUMBER_LINE = "NumberLine", OVAL_OBJ = "Oval",
			RECTANGLE = "Rectangle", TEXT_OBJ = "Text",
			TRIANGLE_OBJ = "Triangle", TRAPEZOID_OBJ = "Trapezoid",
			PARALLELOGRAM_OBJ = "Parallelogram", CUBE_OBJECT = "Cube",
			GROUPING = "Grouping",
			VAR_INSERTION_PROBLEM = "VarInsertionProblem",
			CYLINDER_OBJ = "Cylinder", CONE_OBJECT = "Cone",
			REGULAR_POLYGON_OBJECT = "RegularPolygon", ARROW_OBJECT = "Arrow",
			PYRAMID_OBJECT = "Pyramid", GENERATED_PROBLEM = "GeneratedProblem",
			PROBLEM_NUMBER_OBJECT = "ProblemNumber", LINE_OBJECT = "Line", PAGE = "page";

	// the order of these three arrays is very important, they are parallel
	// arrays used to create new instances of objects when all you have is
	// the type string. They also are used to generate the toolbar for creating
	// new objects

	public static final String[] imgFilenames = { "line.png", "rectangle.png",
			"oval.png", "triangle.png", "regularPolygon.png", "trapezoid.png",
			"parallelogram.png", "arrow.png", "cube.png", "cylinder.png",
			"cone.png", "numberLine.png", "graph.png", "text.png",
			"expression.png", "answerBox.png", "pyramid.png", null, null, null, null };
    
	public static final MathObject[] objects = { new LineObject(),
			new RectangleObject(), new OvalObject(), new TriangleObject(),
			new RegularPolygonObject(), new TrapezoidObject(),
			new ParallelogramObject(), new ArrowObject(), new CubeObject(),
			new CylinderObject(), new ConeObject(), new NumberLineObject(),
			new GraphObject(), new TextObject(), new ExpressionObject(),
			new AnswerBoxObject(), null, new Grouping(),
			new VariableValueInsertionProblem(), new GeneratedProblem(),
			new ProblemNumberObject()};

	public static final String MAKE_SQUARE = "Make Height and Width equal",
			ALIGN_PAGE_LEFT = "Align page left",
			ALIGN_PAGE_HORIZONTAL_CENTER = "Align with hoizontal page center",
			ALIGN_PAGE_RIGHT = "Align page right",
			ALIGN_PAGE_TOP = "Align page top",
			ALIGN_PAGE_VERTICAL_CENTER = "Align with vertical page center",
			ALIGN_PAGE_BOTTOM = "Align page bottom",
			MAKE_INTO_PROBLEM = "Make into Problem",
			FLIP_HORIZONTALLY = "flip horizontally",
			FLIP_VERTICALLY = "flip vertically",
			ADJUST_SIZE_AND_POSITION = "Adjust size and position",
			ROTATE_CLOCKWISE_90 = "rotate clockwise (90)",
			LINE_THICKNESS = "line thickness", FILL_COLOR = "fill color",
			HORIZONTALLY_FLIPPED = "horizontally flipped",
			VERTICALLY_FLIPPED = "vertically flipped", FONT_SIZE = "font size";

	public static final String WIDTH = "width", HEIGHT = "height",
			X_POS = "xPos", Y_POS = "yPos", OBJECT_ID = "objectID",
			LINE_COLOR = "line color";

	// holds a list of function names to manipulate objects, such as
	// flip horizontally/vertically, actual code to change the object is
	// written in the performFunction method
	private Vector<String> actions;
	private Vector<String> studentActions;
	private boolean isHorizontallyResizable, isVerticallyResizable;

	// flag to indicate that the object has just been removed from the document,
	// it prevents actions from being performed as the fields in the frame are
	// being unfocused by clicking delete
	private boolean justDeleted = false;

	public MathObject() {
		attributes = new Vector<MathObjectAttribute<?>>();
		actions = new Vector<String>();
		attrLists = new Vector<ListAttribute<?>>();
		studentActions = new Vector<String>();
		setObjectLists(new Vector<NamedObjectList>());

		setHorizontallyResizable(true);
		setVerticallyResizable(true);

		addAction(MAKE_SQUARE);
		addAction(ALIGN_PAGE_LEFT);
		addAction(ALIGN_PAGE_RIGHT);
		addAction(ALIGN_PAGE_HORIZONTAL_CENTER);
		addAction(ALIGN_PAGE_TOP);
		addAction(ALIGN_PAGE_BOTTOM);
		addAction(ALIGN_PAGE_VERTICAL_CENTER);
		addGenericDefaultAttributes();
		addDefaultAttributes();
	}

	public MathObject(MathObjectContainer c) {
		this();
		setParentContainer(c);
	}

	public MathObject getObjectWithAnswer() {
		return this.clone();
	}

	public MathObject(MathObjectContainer c, int x, int y, int w, int h) {
		this(c);
		getAttributeWithName(X_POS).setValue(x);
		getAttributeWithName(Y_POS).setValue(y);
		getAttributeWithName(WIDTH).setValue(w);
		getAttributeWithName(HEIGHT).setValue(h);
	}

	public void addGenericDefaultAttributes() {
		addAttribute(new IntegerAttribute(X_POS, 1, 1, 610, false, false));
		addAttribute(new IntegerAttribute(Y_POS, 1, 1, 790, false, false));
		addAttribute(new IntegerAttribute(WIDTH, 1, 1, 610, false, false));
		addAttribute(new IntegerAttribute(HEIGHT, 1, 1, 790, false, false));
		addAttribute(new UUIDAttribute(OBJECT_ID, UUID.randomUUID(), false,
				false));
	}

	/**
	 * Method to add attributes in subclasses of {@code MathObject}. Is
	 * automatically called by MathObject constructor. Only works for classes
	 * directly inheriting from {@code MathObject} as additional attributes
	 * added in the classes between {@code MathObject} and the class at the
	 * bottom of the inheritance tree will not be added, as their versions of
	 * this method will be overridden by the ones below them in the inheritance
	 * tree.
	 */
	protected void addDefaultAttributes() {
	}

	public abstract String getType();

	public static String getObjectImageName(String type) {
		int i = 0;
		for (MathObject mObj : objects) {
			if (mObj != null && type.equals(mObj.getType())) {
				return imgFilenames[i];
			}
			i++;
		}
		return null;
	}

	public static boolean isMathObjectType(String type) {
		for (MathObject mObj : objects) {
			if (mObj != null && type.equals(mObj.getType())) {
				return true;
			}
		}
		return false;
	}

	public String exportToXML() {
		String output = "";
		output += "<" + getType() + ">\n";
		for (MathObjectAttribute mAtt : attributes) {
			output += mAtt.exportToXML();
		}
		for (ListAttribute lAtt : attrLists) {
			output += lAtt.exportToXML();
		}
		output += "</" + getType() + ">\n";
		return output;
	}

	public void setParentContainer(MathObjectContainer c) {
		this.parentContainer = c;
	}

	public MathObjectContainer getParentContainer() {
		return parentContainer;
	}

	public Page getParentPage() {
		if (parentContainer == null) {
			return null;
		}
		if (parentContainer instanceof Page) {
			return (Page) parentContainer;
		} else {
			return parentContainer.getParentPage();
		}
	}

	public Vector<String> getActions() {
		return actions;
	}

	public boolean addAction(String s) {
		if (!actions.contains(s)) {
			actions.add(s);
			return true;
		}
		return false;
	}

	public PointInDocument getPositionInDoc() {
		return new PointInDocument(getParentPage().getParentDoc().getPageIndex(
				getParentPage()), getxPos(), getyPos());
	}

	/**
	 * Indicated if students have permission to select an object.
	 * 
	 * @return - true if they can select this object
	 */
	public boolean isStudentSelectable() {
		return false;
	}

	public boolean canFlipVertically() {
		return (actions.contains(FLIP_VERTICALLY));
	}

	public boolean canFlipHorizontally() {
		return (actions.contains(FLIP_HORIZONTALLY));
	}

	public void flipVertically() {
		if (!canFlipVertically()) {
			return;
		}
		try {
			setAttributeValue(VERTICALLY_FLIPPED, !isFlippedVertically());
		} catch (AttributeException e) {
			// should not happen, just setting boolean
			throw new RuntimeException(e);
		}
	}

	public void flipHorizontally() {
		if (!canFlipHorizontally()) {
			return;
		}
		try {
			setAttributeValue(HORIZONTALLY_FLIPPED, !isFlippedHorizontally());
		} catch (AttributeException e) {
			// should not happen, just setting boolean
		}
	}

	public boolean isFlippedVertically() {
		if (canFlipVertically()) {
			return (Boolean) getAttributeWithName(VERTICALLY_FLIPPED)
					.getValue();
		}
		return false;
	}

	public boolean isFlippedHorizontally() {
		if (canFlipHorizontally()) {
			return (Boolean) getAttributeWithName(HORIZONTALLY_FLIPPED)
					.getValue();
		}
		return false;
	}

	@Override
	public MathObject clone() {
		MathObject o = newInstanceWithType(getType());
		o.setParentContainer(getParentContainer());
		o.removeAllAttributes();
		for (MathObjectAttribute mAtt : getAttributes()) {
			o.addAttribute(mAtt.clone());
		}
		o.removeAllLists();
		for (ListAttribute list : getLists()) {
			o.addList(list.clone());
		}
		return o;
	}

	public abstract MathObject newInstance();

	public static MathObject newInstanceWithType(String type) {
		for (MathObject mObj : objects) {
			if (mObj != null && type.equals(mObj.getType())) {
				return mObj.newInstance();
			}
		}
		return null;
	}

	public void removeAllAttributes() {
		attributes = new Vector<MathObjectAttribute<?>>();
	}

	public void removeAllLists() {
		attrLists = new Vector<ListAttribute<?>>();
	}

	public boolean removeAction(String s) {
		return actions.remove(s);
	}

	public void addStudentAction(String s) {
		if (!studentActions.contains(s)) {
			studentActions.add(s);
		}
	}

	/**
	 * Checks to see if this object is within the margins of the page it belongs
	 * to.
	 * 
	 * @return
	 */
	public boolean isOnPage() {
		Rectangle objRect = getBounds();
		if (getParentContainer() == null) {
			// something should be done to handle this error
		}
		if (getParentContainer() instanceof Page) {
			Rectangle pageRect = new Rectangle(
					((Page) getParentContainer()).getxMargin(),
					((Page) getParentContainer()).getyMargin(),
					getParentContainer().getWidth() - 2
							* ((Page) getParentContainer()).getxMargin(),
					getParentContainer().getHeight() - 2
							* ((Page) getParentContainer()).getyMargin());
			if (pageRect.contains(objRect)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
	
	public boolean isProblemMember(){
		MathObjectContainer ancestor = getParentContainer();
		while (ancestor != null){
			if (ancestor instanceof ProblemGenerator || ancestor instanceof GeneratedProblem){
				return true;
			}
			if (ancestor instanceof MathObject){
				ancestor = ((MathObject)ancestor).getParentContainer();
			}
			else{
				return false;
			}
		}
		return false;
	}

	public void performAction(String s) {
		setActionCancelled(false);
		if (justDeleted) {
			return;
		}
		if (s.equals(MAKE_INTO_PROBLEM)){
			if (isProblemMember()){
				JOptionPane.showMessageDialog(null,
						"This object already belongs to a problem, it will not be modified\n" +
						"as problems cannot be nested inside other problems.",
						"Problems Cannot Contain Problems",
						JOptionPane.WARNING_MESSAGE);
				setActionCancelled(true);
				return;
			}
			if (this instanceof Grouping)
			{// groupings have a different implementation for conversion to a problem
				performSpecialObjectAction(s);
			}
			else{
				VariableValueInsertionProblem newProblem = new VariableValueInsertionProblem(getParentContainer(), getxPos(),
						getyPos(), getWidth(), getHeight() );
				this.getParentContainer().getParentDoc().getDocViewerPanel().setFocusedObject(newProblem);
				newProblem.addObjectFromPage(this);
				getParentContainer().addObject(newProblem);
				getParentContainer().removeObject(this);
			}
			if (! actionCancelled){
				this.getParentPage().getParentDoc().getDocViewerPanel().propertiesFrameRefacoringNeeded = true;
			}
			return;
		}
		if (s.equals(MAKE_SQUARE)) {
			int area = this.getWidth() * this.getHeight();
			int sideLength = (int) Math.sqrt(area);
			this.setWidth(sideLength);
			this.setHeight(sideLength);
		}
		else if (s.equals(FLIP_HORIZONTALLY)) 	flipHorizontally();
		else if (s.equals(FLIP_VERTICALLY)) 	flipVertically();
		else if (s.equals(ALIGN_PAGE_LEFT))		setxPos(getParentPage().getxMargin());
		else if (s.equals(ALIGN_PAGE_RIGHT)){
			setxPos(getParentPage().getWidth() - getParentPage().getxMargin() - this.getWidth());
		}else if (s.equals(ALIGN_PAGE_HORIZONTAL_CENTER)){
			setxPos( getParentPage().getWidth()/2 - this.getWidth()/2);
		}else if (s.equals(ALIGN_PAGE_VERTICAL_CENTER)){
			setyPos( getParentPage().getHeight()/2 - this.getHeight()/2);
		}else if (s.equals(ALIGN_PAGE_BOTTOM)){
			setyPos(getParentPage().getHeight() - getParentPage().getyMargin() - this.getHeight());
		}else if (s.equals(ALIGN_PAGE_TOP)){
			setyPos(getParentPage().getyMargin());
		}else {
			// this call will send the request down to the object specific
			// actions
			performSpecialObjectAction(s);
		}
	}

	// this is overridden by any objects that extend MathObject and need to actions
	public void performSpecialObjectAction(String s) {

		if (!actions.contains(s)) {
			// TODO - logging
//			System.out.println("unrecognized action (MathObject)");
		}
	}

	public GridPoint flipPointVertically(GridPoint p) {
		if (p.gety() < .5) {
			return new GridPoint(p.getx(), p.gety() + 2 * (.5 - p.gety()));
		} else if (p.gety() > .5) {
			return new GridPoint(p.getx(), p.gety() + 2 * (.5 - p.gety()));
		} else {
			return new GridPoint(p.getx(), p.gety()); // y is .5, should not be
														// shifted
		}
	}

	public GridPoint flipPointHorizontally(GridPoint p) {
		if (p.getx() < .5) {
			return new GridPoint(p.getx() + 2 * (.5 - p.getx()), p.gety());
		} else if (p.getx() > .5) {
			return new GridPoint(p.getx() + 2 * (.5 - p.getx()), p.gety());
		} else {
			return new GridPoint(p.getx(), p.gety()); // y is .5, should not be
														// shifted
		}
	}

	protected GridPoint[] flipHorizontally(GridPoint[] points) {

		GridPoint[] flipped = new GridPoint[points.length];

		int i = 0;
		for (GridPoint p : points) {
			flipped[i] = flipPointHorizontally(p);
			i++;
		}
		return flipped;
	}

	protected GridPoint[] flipVertically(GridPoint[] points) {

		GridPoint[] flipped = new GridPoint[points.length];

		int i = 0;
		for (GridPoint p : points) {
			flipped[i] = flipPointVertically(p);
			i++;
		}
		return flipped;
	}

	public Rectangle getBounds() {
		return new Rectangle(getxPos(), getyPos(), getWidth(), getHeight());
	}


    public DecimalRectangle getDecimalRectangleBounds() {
        return new DecimalRectangle(getxPos(), getyPos(), getWidth(), getHeight());
    }

	public void setWidth(int width) {
		if (width == 0)
			width = 1;
		getAttributeWithName(WIDTH).setValue(width);
        // TODO - fix me, was trying to allow movement of single objects within a group
        // (after selecting them with a double click) but it broke general group movement
//        if (getParentContainer() instanceof Grouping) {
//            ((Grouping)getParentContainer()).adjustSizeToFitChildren();
//        }
	}

	public int getWidth() {
		return ((IntegerAttribute) getAttributeWithName(WIDTH)).getValue();
	}

	public void setHeight(int height) {
		if (height == 0)
			height = 1;
		getAttributeWithName(HEIGHT).setValue(height);
        // TODO - fix me, was trying to allow movement of single objects within a group
        // (after selecting them with a double click) but it broke general group movement
//        if (getParentContainer() instanceof Grouping) {
//            ((Grouping)getParentContainer()).adjustSizeToFitChildren();
//        }
	}

	public int getHeight() {
		return ((IntegerAttribute) getAttributeWithName(HEIGHT)).getValue();
	}

	public void setxPos(int xPos) {
		getAttributeWithName(X_POS).setValue(xPos);
	}

	public int getxPos() {
		return ((IntegerAttribute) getAttributeWithName(X_POS)).getValue();
	}

	public void setObjectID(UUID uuid) {
		getAttributeWithName(OBJECT_ID).setValue(uuid);
	}

	public UUID getObjectID() {
		return ((UUIDAttribute) getAttributeWithName(OBJECT_ID)).getValue();
	}

	public void setyPos(int yPos) {
		getAttributeWithName(Y_POS).setValue(yPos);
	}

	public int getyPos() {
		return ((IntegerAttribute) getAttributeWithName(Y_POS)).getValue();
	}

	public void setAttributes(Vector<MathObjectAttribute<?>> attributes) {
		this.attributes = attributes;
	}

	public Vector<MathObjectAttribute<?>> getAttributes() {
		return attributes;
	}

	public Vector<ListAttribute<?>> getLists() {
		return attrLists;
	}

	public synchronized ListAttribute<?> getListWithName(String n) {
		for (ListAttribute<?> list : attrLists) {
			if (list.getName().equals(n)) {
				return list;
			}
		}
		return null;
	}
	
	public synchronized NamedObjectList getObjListWithName(String n) {
		for (NamedObjectList list : objectLists) {
			if (list.getName().equals(n)) {
				return list;
			}
		}
		return null;
	}

	public boolean addList(ListAttribute l) {
		if (getListWithName(l.getName()) == null) {// the name is not in use
			attrLists.add(l);
			l.setParentObject(this);
			return true;
		}
		return false;
	}
	
	public boolean addObjectList(NamedObjectList l) {
		if (getListWithName(l.getName()) == null) {// the name is not in use
			objectLists.add(l);
			l.setParentObject(this);
			return true;
		}
		return false;
	}

	public MathObjectAttribute getAttributeWithName(String n) {
		for (MathObjectAttribute mAtt : attributes) {
			if (mAtt.getName().equals(n)) {
				return mAtt;
			}
		}
		return null;
	}

	public Object getAttributeValue(String n) {
		for (MathObjectAttribute mAtt : attributes) {
			if (mAtt.getName().equals(n)) {
				return mAtt.getValue();
			}
		}
		return null;
	}

	public void setAttributeValueWithString(String s, String val)
			throws AttributeException {
		int i  = 5;
		if ( s.equals("selection")){
			i++;
		}
		if (getAttributeWithName(s) == null) {
			throw new AttributeException(
					"Object does not have an attribute with that name");
		}
		setAttributeValue(s, getAttributeWithName(s).readValueFromString(val));
	}

	public boolean setAttributeValue(String n, Object o)
			throws AttributeException {
		getAttributeWithName(n).setValue(o);
		return true;
	}

	public boolean addAttribute(MathObjectAttribute a) {
		if (getAttributeWithName(a.getName()) == null) {
			attributes.add(a);
			a.setParentObject(this);
			return true;
		}
		return false;
	}

	public void removeAttribute(MathObjectAttribute a) {
		attributes.remove(a);
	}

	public void removeList(String s) {
		for (int i = 0; i < attrLists.size(); i++) {
			if (attrLists.get(i).getName().equals(s)) {
				attrLists.remove(i);
				return;
			}
		}
	}

	public void setStudentActions(Vector<String> studentActions) {
		this.studentActions = studentActions;
	}

	public Vector<String> getStudentActions() {
		return studentActions;
	}

	protected void setHorizontallyResizable(boolean isHorizontallyResizable) {
		this.isHorizontallyResizable = isHorizontallyResizable;
	}

	public boolean isHorizontallyResizable() {
		return isHorizontallyResizable;
	}

	protected void setVerticallyResizable(boolean isVerticallyResizable) {
		this.isVerticallyResizable = isVerticallyResizable;
	}

	public boolean isVerticallyResizable() {
		return isVerticallyResizable;
	}

	public void setJustDeleted(boolean b) {
		justDeleted = b;
	}

	public boolean actionWasCancelled() {
		return actionCancelled;
	}

	protected void setActionCancelled(boolean actionCancelled) {
		this.actionCancelled = actionCancelled;
	}

	public Vector<NamedObjectList> getObjectLists() {
		return objectLists;
	}

	public void setObjectLists(Vector<NamedObjectList> objectLists) {
		this.objectLists = objectLists;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
