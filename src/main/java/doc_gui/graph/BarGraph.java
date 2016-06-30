package doc_gui.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import expression.NodeException;

public class BarGraph extends GraphComponent {
	
	public Vector<Double> values;
	
	public Vector<String> labels;
	
	public int groupSize = 3;
	
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
		values = new Vector<>();
		labels = new Vector<>();
	}

	@Override
	public void draw(Graphics g) throws NodeException {
		if (values.size() == 0) {
			return;
		}
		int space = 20;
		int groupW = (graph.X_SIZE - (int) (Math.ceil( (double)values.size() / groupSize) + 1) * space) 
				/ (int) Math.ceil( (double)values.size() / groupSize);
		int groupX;
		for (int i = 0; i < values.size(); i += groupSize){
			groupX = gridxToScreen(0) + (i / groupSize) * (groupW + space) + space;
			drawBars(g, 0, groupX, groupW, i);
			
			g.setColor(Color.WHITE);
			System.out.println(labels.size());
			String ptText = labels.get(i / groupSize);
			int width = g.getFontMetrics().stringWidth(ptText);
			int numberInset = 1;
			int height = g.getFontMetrics().getHeight();
			int numberAndAxisSpace = 3;
			
			g.fillRect( groupX + groupW/2 - (width/2) - numberInset,
					gridyToScreen(0.0) + numberAndAxisSpace
					, width + numberInset * 2, height + 2 * numberInset);
			g.setColor(Color.black);
			g.drawString(ptText, groupX + groupW/2  - (width/2)
					, gridyToScreen(0.0) + height + numberAndAxisSpace);
		}
	}
	
	public void drawBars(Graphics g, int space, int startX, int width, int valueIndex){;
		int index = 0;
		int barW = (width - (groupSize + 1) * space) / groupSize;
		int barX, barY, barH;
		for (int i = 0; i + valueIndex < values.size() && i < groupSize; i++){ 
			g.setColor(colors[index]);
			barX = startX + index * (barW + space) + space;
			barY = Math.min(this.gridyToScreen(values.get(valueIndex + i)), gridyToScreen(0));
			barH = (int) Math.abs(gridyToScreen(0) - this.gridyToScreen(values.get(valueIndex + i)));
			g.fillRect(barX, barY, barW, barH);
			g.setColor(Color.BLACK);
			g.drawRect(barX, barY, barW, barH);
			
			
			g.setColor(Color.WHITE);
			System.out.println(labels.size());
			String ptText = doubleToString(values.get(valueIndex + i), .01);
			int txtWidth = g.getFontMetrics().stringWidth(ptText);
			int numberInset = 1;
			int height = g.getFontMetrics().getHeight();
			int numberAndAxisSpace = 3;
			
			g.fillRect( barX + barW/2 - (txtWidth/2) - numberInset,
					gridyToScreen(0.0) - height - numberAndAxisSpace
					, txtWidth + numberInset * 2, height + 2 * numberInset);
			g.setColor(Color.black);
			g.drawString(ptText, barX + barW/2  - (txtWidth/2)
					, gridyToScreen(0.0) - numberAndAxisSpace - 2);
			
			
			index++;
		}
	}

}
