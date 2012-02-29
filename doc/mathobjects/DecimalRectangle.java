package doc.mathobjects;

public class DecimalRectangle {

	private double x, y, width, height;
	
	public DecimalRectangle(double x, double y, double width, double height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getHeight() {
		return height;
	}
	
	@Override
	public String toString(){
		return "( " + x + " , " + y + " ) [" + width + " , " + height + " ]";
	}
}
