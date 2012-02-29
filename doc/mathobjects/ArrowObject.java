package doc.mathobjects;

import doc.GridPoint;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class ArrowObject extends PolygonObject {
	
	private static GridPoint[] vertices = {new GridPoint(0, .25), new GridPoint(.75, .25),
		new GridPoint(.75, 0), new GridPoint(1, .5), new GridPoint(.75, 1),
		new GridPoint(.75, .75), new GridPoint(0, .75) };

	public ArrowObject(MathObjectContainer p, int x, int y, int w, int h, int t) {
		super(p, x, y, w, h, t);
		addAction(FLIP_HORIZONTALLY);
	}
	
	public ArrowObject(MathObjectContainer p){
		super(p);
		addAction(FLIP_HORIZONTALLY);
	}

	public ArrowObject() {
		addAction(FLIP_HORIZONTALLY);
	}

	@Override
	public String getType() {
		return ARROW_OBJECT;
	}

	@Override
	public void addDefaultAttributes() {
		
	}

	@Override
	public GridPoint[] getVertices() {
		return vertices;
	}

	@Override
	public MathObject newInstance() {
		return new ArrowObject();
	}

}
