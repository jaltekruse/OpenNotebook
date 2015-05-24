/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.mathobjects;

import java.awt.Color;
import java.util.Vector;

import doc.attributes.AttributeException;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.DoubleAttribute;
import doc.attributes.GridPointAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.SelectionAttribute;
import doc.attributes.StringAttribute;
import doc_gui.graph.Selection;

public class GraphObject extends MathObject {
	
	public static final String X_MIN = "xMin", Y_MIN = "yMin",
			X_MAX = "xMax", Y_MAX = "yMax", EXPRESSION = "y=",
			X_STEP = "xStep", Y_STEP = "yStep",
			SHOW_AXIS = "show axis", SHOW_NUMBERS = "show numbers",
			SHOW_GRID = "show grid", EXPRESSIONS = "Expressions",
			POINTS = "points", LINE_GRAPH = "line graph points", LINE_GRAPH_COLOR = "line graph color",
			SELECTION = "selection",
			BAR_GRAPH_VALUES = "Bar graph values", BAR_GRAPH_GROUP_SIZE = "Bar graph group size",
			BAR_GRAPH_LABELS = "Bar graph labels";
	
	public static final String DEFAULT_GRID = "default grid",
			ZOOM_IN = "zoom in", ZOOM_OUT = "zoom out", MOVE_LEFT = "move left",
			MOVE_RIGHT = "move right", MOVE_DOWN = "move down", MOVE_UP = "move up";
	
	public GraphObject(MathObjectContainer p, int x, int y, int width, int height) {
		super(p, x, y, width, height);
		setDefaults();
		addGraphActions();
		getListWithName(POINTS).removeAll();
		getListWithName(LINE_GRAPH).removeAll();
		getListWithName(BAR_GRAPH_VALUES).removeAll();
	}
	
	public GraphObject(MathObjectContainer p){
		this(p, 1,1,1,1);
	}
	
	public GraphObject() {
		this(null, 1,1, 1,1);
	}
	
	public boolean isStudentSelectable(){
		return true;
	}
	
	private void addGraphActions(){
		addAction(MAKE_INTO_PROBLEM);
		addStudentAction(DEFAULT_GRID);
		addStudentAction(ZOOM_IN);
		addStudentAction(ZOOM_OUT);
		addStudentAction(MOVE_UP);
		addStudentAction(MOVE_DOWN);
		addStudentAction(MOVE_LEFT);
		addStudentAction(MOVE_RIGHT);
	}
	
	public void setDefaults(){
		try{
			addList(new ListAttribute<StringAttribute>(EXPRESSIONS,
					new StringAttribute(EXPRESSION), 6, true, false));
			addList(new ListAttribute<GridPointAttribute>(POINTS,
					new GridPointAttribute("", -7E8, 7E8,-7E8, 7E8), 1000000, true, false));
			addList(new ListAttribute<GridPointAttribute>(LINE_GRAPH,
					new GridPointAttribute("", -7E8, 7E8,-7E8, 7E8), 1000000, true, false));
			
			addList(new ListAttribute<DoubleAttribute>(BAR_GRAPH_VALUES,
					new DoubleAttribute("", -7E8, 7E8), 50, false, true));
			addAttribute(new IntegerAttribute(BAR_GRAPH_GROUP_SIZE, 1, 1, 100, false));
			
			addList(new ListAttribute<StringAttribute>(BAR_GRAPH_LABELS,
					new StringAttribute(""), 100, false, true));
			
			addAttribute(new SelectionAttribute(SELECTION, new Selection(), false));
			
			addAttribute(new DoubleAttribute(X_MIN, -7E8, 7E8, true, true));
			getAttributeWithName(X_MIN).setValueWithString("-5");
			addAttribute(new DoubleAttribute(X_MAX, -7E8, 7E8, true, true));
			getAttributeWithName(X_MAX).setValueWithString("5");
			addAttribute(new DoubleAttribute(Y_MIN, -7E8, 7E8, true, true));
			getAttributeWithName(Y_MIN).setValueWithString("-5");
			addAttribute(new DoubleAttribute(Y_MAX, -7E8, 7E8, true, true));
			getAttributeWithName(Y_MAX).setValueWithString("5");
			addAttribute(new DoubleAttribute(X_STEP, -3E8, 3E8, true, true));
			getAttributeWithName(X_STEP).setValueWithString("1");
			addAttribute(new DoubleAttribute(Y_STEP, -3E8, 3E8, true, true));
			getAttributeWithName(Y_STEP).setValueWithString("1");
			addAttribute(new IntegerAttribute(FONT_SIZE, 1, 20));
			getAttributeWithName(FONT_SIZE).setValueWithString("8");
			addAttribute(new BooleanAttribute(SHOW_AXIS));
			getAttributeWithName(SHOW_AXIS).setValue(true);
			addAttribute(new BooleanAttribute(SHOW_NUMBERS));
			getAttributeWithName(SHOW_NUMBERS).setValue(true);
			addAttribute(new BooleanAttribute(SHOW_GRID));
			getAttributeWithName(SHOW_GRID).setValue(true);
			addAttribute(new ColorAttribute(LINE_GRAPH_COLOR));
			getAttributeWithName(LINE_GRAPH_COLOR).setValue(Color.BLUE);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void autoAdjustGrid(){
		
		Double xMin =   (Double) getAttributeWithName(GraphObject.X_MIN).getValue();
		Double xMax =   (Double) getAttributeWithName(GraphObject.X_MAX).getValue();
		Double yMin =   (Double)getAttributeWithName(GraphObject.Y_MIN).getValue();
		Double yMax =   (Double)getAttributeWithName(GraphObject.Y_MAX).getValue();
		double xStep = (Double) getAttributeWithName(GraphObject.X_STEP).getValue();
		double yStep = (Double) getAttributeWithName(GraphObject.Y_STEP).getValue();
		int fontSize = (Integer) getAttributeWithName(GraphObject.FONT_SIZE).getValue();
		boolean showAxis = (Boolean) getAttributeWithName(GraphObject.SHOW_AXIS).getValue();
		boolean showGrid = (Boolean) getAttributeWithName(GraphObject.SHOW_GRID).getValue();
		boolean showNumbers = (Boolean) getAttributeWithName(GraphObject.SHOW_NUMBERS).getValue();
		
		if((xMax-xMin)/xStep >= 15)
		{
			if ((xMax-xMin)/15 > 1)
				xStep = (int)((xMax-xMin)/14);
			else
			{
				for (int i = 0; i < 25; i ++){
					if ((xMax-xMin)/20/Math.pow(.5, i) < .7)
						xStep = Math.pow(.5, i);
				}
			}
		}

		else if((xMax-xMin)/xStep < 7){
			if ((xMax-xMin)/7 > 1)
				xStep = (int)((xMax-xMin)/10);
			else
			{
				for (int i = 0; i < 25; i ++){
					if ((xMax-xMin)/20 < Math.pow(.5, i))
						xStep = Math.pow(.5, i);
				}
			}
		}

		if((yMax-yMin)/yStep >= 15)
		{
			if ((yMax-yMin)/15 > 1)
			{
				yStep = (int)((yMax-yMin)/10);
			}
			else
			{
				for (int i = 0; i < 25; i ++){
					if ((yMax-yMin)/20/Math.pow(.5, i) < .7)
						yStep = Math.pow(.5, i);
				}
			}
		}

		else if((yMax-yMin)/yStep < 7){
			if ((yMax-yMin)/7 > 1)
				yStep = (int)((yMax-yMin)/10);
			else
			{
				for (int i = 0; i < 25; i ++){
					if ((yMax-yMin)/20 < Math.pow(.5, i))
						yStep = Math.pow(.5, i);
				}
			}
		}
		
		getAttributeWithName(X_MIN).setValue(xMin);
		getAttributeWithName(X_MAX).setValue(xMax);
		getAttributeWithName(Y_MIN).setValue(yMin);
		getAttributeWithName(Y_MAX).setValue(yMax);
		getAttributeWithName(X_STEP).setValue(xStep);
		getAttributeWithName(Y_STEP).setValue(yStep);
		getAttributeWithName(FONT_SIZE).setValue(fontSize);
		getAttributeWithName(SHOW_AXIS).setValue(showAxis);
		getAttributeWithName(SHOW_GRID).setValue(showGrid);
		getAttributeWithName(SHOW_NUMBERS).setValue(showNumbers);
	}
	
	// synconizes the attribute values of two graphs so they are showing the
	// same ranges of values in the x and y direction
	public void syncGridWithOtherGraph(GraphObject o) {
		
		// deliberately bypassing method for setting value within object ot prevent collisions
		// where setting the minimum or maximum first from the other objects value will throw
		// an error because it is on the wrong side of the current other extrema of the graph
		
		// this method has been modified for use within the fitness app, where the y values are supposed
		// to scale differently but I also want to x values to always line up
		
		// TODO - move this functionality up to MathObject, send a list of attribute to sync along with an
		// object to sync up
		
		getAttributeWithName(X_MAX).setValue(o.getxMax());
		getAttributeWithName(X_MIN).setValue(o.getxMin());
		//getAttributeWithName(Y_MAX).setValue(o.getyMax());
		//getAttributeWithName(Y_MIN).setValue(o.getyMin());
		getAttributeWithName(X_STEP).setValue(o.getxStep());
		//getAttributeWithName(Y_STEP).setValue(o.getyStep());
	}

	@Override
	public void performSpecialObjectAction(String s){
		if (s.equals(DEFAULT_GRID)){
			setDefaults();
		}
		else if (s.equals(ZOOM_IN)){
			zoom(110);
		}
		else if (s.equals(ZOOM_OUT)){
			zoom(90);
		}
		else if (s.equals(MOVE_UP)){
			shiftGraph(0, 10);
		}
		else if (s.equals(MOVE_DOWN)){
			shiftGraph(0, -10);
		}
		else if (s.equals(MOVE_LEFT)){
			shiftGraph(-10, 0);
		}
		else if (s.equals(MOVE_RIGHT)){
			shiftGraph(10, 0);
		}
		this.autoAdjustGrid();
	}
	
	public void zoom(double rate){
		zoom(rate,rate);
	}
	
	public void zoom(double xRate, double yRate){
		double xMin = getxMin(), yMin = getyMin(), xMax = getxMax(), yMax = getyMax();
		
		//hacked solution to prevent drawing the grid, the auto-rescaling of the 
		//grid stops working after the numbers get too big
		if (xMin < -7E8 || xMax > 7E8 || yMin < -7E8 || yMax > 7E8){
			if (xRate < 100 || yRate < 100)
			{//if the user is trying to zoom out farther, do nothing
				return;
			}
		}
		
		try {
			setxMin( xMin - (xMax-xMin) * (100-xRate) / 100 );
			setxMax( xMax + (xMax-xMin) * (100-xRate) / 100 );
			if ( yRate != 0 ){
				setyMin( yMin - (yMax-yMin) * (100-yRate) / 100 );
				setyMax( yMax + (yMax-yMin) * (100-yRate) / 100 );
			}
		} catch (AttributeException e) {
			e.printStackTrace();
		}
		
	}
	
	public void shiftGraph(int xPercent, int yPercent){
		double xMin = getxMin(), yMin = getyMin(), xMax = getxMax(), yMax = getyMax();
		try{
			setxMin( xMin + ((double)xPercent/100) * (xMax - xMin) );
			setyMax( yMax + ((double)yPercent/100) * (yMax - yMin) );
			setyMin( yMin + ((double)yPercent/100) * (yMax - yMin) );
			setxMax( xMax + ((double)xPercent/100) * (xMax - xMin) );
		} catch (Exception ex){
			;
		}
	}
	
	public Selection getSelection(){
		 return (Selection) getAttributeWithName(SELECTION).getValue();
	}
	
	public double getxStep(){
		return ((DoubleAttribute) getAttributeWithName(X_STEP)).getValue();
	}
	
	public double getyStep(){
		return ((DoubleAttribute) getAttributeWithName(Y_STEP)).getValue();
	}

	public double getxMin(){
		return ((DoubleAttribute) getAttributeWithName(X_MIN)).getValue();
	}
	
	public double getyMin(){
		return ((DoubleAttribute) getAttributeWithName(Y_MIN)).getValue();
	}
	
	public double getxMax(){
		return ((DoubleAttribute) getAttributeWithName(X_MAX)).getValue();
	}
	
	public double getyMax(){
		return ((DoubleAttribute) getAttributeWithName(Y_MAX)).getValue();
	}
	
	public Color getLineGraphColor(){
		return ((ColorAttribute) getAttributeWithName(LINE_GRAPH_COLOR)).getValue();
	}
	
	public void addExpression(String s) throws AttributeException{
		getListWithName(EXPRESSIONS).addValueWithString(s);
	}

	public void setLineGraphColor(Color c) throws AttributeException{
		setAttributeValue(LINE_GRAPH_COLOR, c);
	}
	
	public void setxMin(double d) throws AttributeException{
		setAttributeValue(X_MIN, d);
	}
	
	public void setxStep(double d) throws AttributeException{
		setAttributeValue(X_STEP, d);
	}
	
	public void setyStep(double d) throws AttributeException{
		setAttributeValue(Y_STEP, d);
	}
	
	public void setyMin(double d) throws AttributeException{
		setAttributeValue(Y_MIN, d);
	}
	
	public void setxMax(double d) throws AttributeException{
		setAttributeValue(X_MAX, d);
	}
	
	public void setyMax(double d) throws AttributeException{
		setAttributeValue(Y_MAX, d);
	}

	public Vector<StringAttribute> getExpressions(){
		return (Vector<StringAttribute>) getListWithName(EXPRESSIONS).getValues();
	}
	
	public Vector<GridPointAttribute> getPoints(){
		return (Vector<GridPointAttribute>) getListWithName(POINTS).getValues();
	}
	
	public synchronized ListAttribute<GridPointAttribute> getLineGraphPoints(){
		return (ListAttribute<GridPointAttribute>) getListWithName(LINE_GRAPH);
	}
	
	public synchronized ListAttribute<DoubleAttribute> getBarGraphValues(){
		return (ListAttribute<DoubleAttribute>) getListWithName(BAR_GRAPH_VALUES);
	}
	
	public synchronized ListAttribute<StringAttribute> getBarGraphLabels(){
		return (ListAttribute<StringAttribute>) getListWithName(BAR_GRAPH_LABELS);
	}
	
	public int getBarGraphGroupSize(){
		return (Integer) getAttributeValue(BAR_GRAPH_GROUP_SIZE);
	}
	
	@Override
	public synchronized boolean setAttributeValue(String n, Object o) throws AttributeException{
		/*
		if (n.equals(X_MIN)){
			if (o instanceof Double){
				if (((Double)o) < (Double)getAttributeWithName(X_MAX).getValue()){
					getAttributeWithName(X_MIN).setValue(o);
				}
				else{
					throw new AttributeException( X_MIN + " must be less than " + X_MAX);
				}
			}
		}
		else if (n.equals(X_MAX)){
			if (o instanceof Double){
				if (((Double)o) > (Double)getAttributeWithName(X_MIN).getValue()){
					getAttributeWithName(X_MAX).setValue(o);
				}
				else{
					throw new AttributeException(X_MAX + " must be greater than " + X_MIN);
				}
			}
		}
		
		if (n.equals(Y_MIN)){
			if (o instanceof Double){
				if (((Double)o) < (Double)getAttributeWithName(Y_MAX).getValue()){
					getAttributeWithName(Y_MIN).setValue(o);
				}
				else{
					throw new AttributeException( Y_MIN + " must be greater than " + Y_MAX);
				}
			}
		}
		else if (n.equals(Y_MAX)){
			if (o instanceof Double){
				if (((Double)o) > (Double)getAttributeWithName(Y_MIN).getValue()){
					getAttributeWithName(Y_MAX).setValue(o);
				}
				else{
					throw new AttributeException(Y_MAX + " must be less than " + Y_MIN);
				}
			}
		}
		*/
		getAttributeWithName(n).setValue(o);
		return true;
	}

	@Override
	public void addDefaultAttributes() {
		
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return GRAPH_OBJ;
	}

	@Override
	public MathObject newInstance() {
		return new GraphObject();
	}
}
