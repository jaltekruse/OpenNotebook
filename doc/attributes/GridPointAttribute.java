package doc.attributes;

import java.util.StringTokenizer;

import doc.GridPoint;

public class GridPointAttribute extends MathObjectAttribute<GridPoint> {
	
	double xMin,xMax,yMin, yMax;
	
	public GridPointAttribute(String n) {
		super(n);
		xMin = 0;
		xMax = 1;
		yMin = 0;
		yMax = 1;
	}
	
	public GridPointAttribute(String n, boolean userEditable) {
		super(n, userEditable);
		xMin = 0;
		xMax = 1;
		yMin = 0;
		yMax = 1;
	}
	
	public GridPointAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		xMin = 0;
		xMax = 1;
		yMin = 0;
		yMax = 1;
	}
	
	public GridPointAttribute(String n, GridPoint pt) {
		super(n);
		xMin = 0;
		xMax = 1;
		yMin = 0;
		yMax = 1;
		setValue(pt);
	}
	
	public GridPointAttribute(String n, GridPoint pt, boolean userEditable) {
		super(n, userEditable);
		xMin = 0;
		xMax = 1;
		yMin = 0;
		yMax = 1;
		setValue(pt);
	}
	
	public GridPointAttribute(String n, GridPoint pt, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		xMin = 0;
		xMax = 1;
		yMin = 0;
		yMax = 1;
		setValue(pt);
	}

	public GridPointAttribute(String n, double xMin, double xMax, double yMin, double yMax) {
		super(n);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public GridPointAttribute(String n, double xMin, double xMax,
			double yMin, double yMax, boolean userEditable) {
		super(n, userEditable);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public GridPointAttribute(String n, double xMin, double xMax,
			double yMin, double yMax, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public GridPointAttribute(String n, GridPoint p, double xMin, double xMax,
			double yMin, double yMax) {
		super(n, p);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public GridPointAttribute(String n, GridPoint p, double xMin, double xMax,
			double yMin, double yMax, boolean userEditable) {
		super(n, p, userEditable);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public GridPointAttribute(String n, GridPoint p, double xMin, double xMax,
			double yMin, double yMax, boolean userEditable, boolean studentEditable) {
		super(n, p, userEditable, studentEditable);
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}



	@Override
	public String getType() {
		return GRID_POINT_ATTRIBUTE;
	}

	/* (non-Javadoc)
	 * @see doc.mathobjects.MathObjectAttribute#setValueWithString(java.lang.String)
	 * 
	 * Takes the value of the point in the format (x,y), where x and y are
	 * string representations of float values
	 */
	@Override
	public GridPoint readValueFromString(String s) throws AttributeException {
		StringTokenizer st = new StringTokenizer(s, ", ( )");
		double x, y;
		try{
			x = Double.parseDouble(st.nextToken());
			y = Double.parseDouble(st.nextToken());
			if (x < xMin || x > xMax){
				throw new AttributeException("x must be between " + xMin + " and " + xMax);
			}
			if (y < yMin || y > yMax){
				throw new AttributeException("y must be between " + yMin + " and " + yMax);
			}
		}catch(Exception e){
			throw new AttributeException("Error with ordered pair formatting.");
		}
		return new GridPoint(x, y);
	
	}

	@Override
	public void resetValue() {
		setValue(new GridPoint());
	}

	@Override
	public GridPointAttribute clone(){
		GridPointAttribute newGridPoint = new GridPointAttribute(
				getName(), new GridPoint(getValue().getx(), getValue().gety()),
					xMin, xMax, yMin, yMax);
		copyRootManagedFields(newGridPoint);
		return newGridPoint;
	}
}
