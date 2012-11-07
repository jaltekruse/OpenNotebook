package doc_gui.graph;

import java.awt.Color;
import java.awt.Graphics;

import tree.EvalException;
import tree.ParseException;

public class PointOnGrid extends GraphComponent {
	
	private static final int radius = 4;
	
	private double x, y;
	
	public PointOnGrid(Graph g, double x, double y) {
		super(g);
		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(Graphics g) throws EvalException, ParseException {
		if (x >= graph.X_MIN && x <= graph.X_MAX && y >= graph.Y_MIN && y <= graph.Y_MAX){
			int currRadius = (int) (graph.DOC_ZOOM_LEVEL * radius);
			g.setColor(Color.BLACK);
			g.fillOval(gridxToScreen(x) - currRadius, gridyToScreen(y) - currRadius, currRadius * 2, currRadius * 2);
		}
	}

}
