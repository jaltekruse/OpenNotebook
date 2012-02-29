package doc.mathobjects;

import doc.GridPoint;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class ConnectedDrawing extends MathObject {

	protected GridPoint[] points;
	
	public ConnectedDrawing(MathObjectContainer p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
	}
	
	public ConnectedDrawing(MathObjectContainer p){
		super(p);
	}

	public ConnectedDrawing() {}

	@Override
	public void addDefaultAttributes() {
		
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public MathObject newInstance() {
		return new ConnectedDrawing();
	}

}
