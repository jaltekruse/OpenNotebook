package doc.mathobjects;

import doc.GridPoint;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class ParallelogramObject extends PolygonObject {

	private static final GridPoint[] vertices = {new GridPoint(.25, 0), new GridPoint(1, 0),
		new GridPoint(.75, 1), new GridPoint(0, 1)};
	
	public ParallelogramObject(MathObjectContainer p, int x, int y, int w, int h, int t) {
		super(p, x, y, w, h, t);
		addAction(FLIP_VERTICALLY);
		addAction(FLIP_HORIZONTALLY);
	}
	
	public ParallelogramObject(MathObjectContainer p){
		super(p);
		addAction(FLIP_VERTICALLY);
		addAction(FLIP_HORIZONTALLY);
	}

	public ParallelogramObject() {
		addAction(FLIP_VERTICALLY);
		addAction(FLIP_HORIZONTALLY);
	}
	
	@Override
	public void addDefaultAttributes() {}

	@Override
	public String getType() {
		return PARALLELOGRAM_OBJ;
	}

	@Override
	public GridPoint[] getVertices() {
		return vertices;
	}

	@Override
	public MathObject newInstance() {
		return new ParallelogramObject();
	}
}
