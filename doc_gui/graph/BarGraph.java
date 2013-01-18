package doc_gui.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import tree.EvalException;
import tree.ParseException;
import expression.NodeException;

public class BarGraph extends GraphComponent {
	
	public Vector<Double> values;
	Color[] colors = {
			Color.BLUE,
			Color.RED,
			Color.GREEN.darker(),
			Color.CYAN.darker(),
			Color.ORANGE,
			Color.yellow.darker()
	};

	public BarGraph(Graph g) {
		super(g);
		values = new Vector<Double>();
	}

	@Override
	public void draw(Graphics g) throws EvalException, ParseException,
			NodeException {
		int space = 4;
		int width = (graph.X_SIZE - (values.size() + 1) * space) / values.size();
		int index = 0;
		for (Double d : values){
			g.setColor(colors[index]);
			g.fillRect( gridxToScreen(0) + index * (width + space) + space, 
					this.gridyToScreen(d) + gridyToScreen(0), width, 
					graph.Y_SIZE - this.gridyToScreen(d));
			index++;
		}
	}

}
