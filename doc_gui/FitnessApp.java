package doc_gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.sun.swing.internal.plaf.basic.resources.basic;

import doc.GridPoint;
import doc.attributes.AttributeException;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.DoubleAttribute;
import doc.attributes.EnumeratedAttribute;
import doc.attributes.GridPointAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc.mathobjects.GraphObject;
import doc.mathobjects.MathObject;
import doc_gui.attribute_panels.AdjustmentPanel;
import doc_gui.attribute_panels.BooleanAdjustmentPanel;
import doc_gui.attribute_panels.ColorAdjustmentPanel;
import doc_gui.attribute_panels.EnumeratedAdjuster;
import doc_gui.attribute_panels.GenericAdjustmentPanel;
import doc_gui.attribute_panels.StringAdjustmentPanel;
import doc_gui.mathobject_gui.GraphObjectGUI;

public class FitnessApp {

	ImageIcon image;
	JPanel heartRateGraph;
	JPanel skinConductanceGraph;
	JPanel skinTemperatureGraph;
	JPanel compositeGraph;
	GraphObject heartRateGraphData;
	GraphObject skinConductanceGraphData;
	GraphObject skinTemperatureGraphData;
	GraphObject compositeGraphData;
	GraphObjectGUI graphGui;
	JScrollBar scrollbar;
	int randMax = 18;
	long timeAtStart = 0;
	long lastStopTime = 0;
	Random rand = new Random();
	int counter = 0;
	public Timer timer;
	JDialog problemDialog;
	final SurveyQuestions survey = new SurveyQuestions();
	final SerialTest usb = new SerialTest(FitnessApp.this);


	private double mouseX;
	boolean refPoint, dragSelection, justFinishedPic, isTimeToRedraw, movingSelectionEnd,
	draggingGraph, dragDiskShowing;

	public FitnessApp(){
		heartRateGraphData = new GraphObject();
		System.setProperty("sun.java2d.opengl","True");

		try {
			heartRateGraphData.setAttributeValue(GraphObject.X_MIN, 0.0);
			heartRateGraphData.setAttributeValue(GraphObject.Y_MIN, 1.0);
			heartRateGraphData.setAttributeValue(GraphObject.Y_MAX, 5.0);
			heartRateGraphData.setAttributeValue(GraphObject.Y_STEP, 4.0);
			heartRateGraphData.getLineGraphPoints().removeAll();
			skinConductanceGraphData = (GraphObject) heartRateGraphData.clone();
			skinConductanceGraphData.setLineGraphColor(Color.RED);
			skinTemperatureGraphData = (GraphObject) heartRateGraphData.clone();
			skinTemperatureGraphData.setLineGraphColor(Color.GREEN.darker());
			compositeGraphData = (GraphObject) heartRateGraphData.clone();
			compositeGraphData.setLineGraphColor(Color.MAGENTA.darker());
		} catch (AttributeException e) {
			e.printStackTrace();
		}

		timer = new Timer(30, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				generateRandomData();
				autoScale(heartRateGraphData, skinTemperatureGraphData, skinConductanceGraphData, compositeGraphData);
			}

		});

		scrollbar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100, 0, 100);

		graphGui = new GraphObjectGUI();
	}

	public static class SurveyQuestions extends MathObject{

		private static String[] questions = {

			"In the last month, how often have you been upset because of something that happened unexpectedly?",
			"In the last month, how often have you felt that you were unable to control the important things in your life?",
			"In the last month, how often have you felt nervous and \"stressed\"?",
			"In the last month, how often have you dealt successfully with irritating life hassles?",
			"In the last month, how often have you felt that you were effectively coping with important changes that were occurring in your life?",
			"In the last month, how often have you felt confident about your ability to handle your personal problems?",
			"In the last month, how often have you felt that things were going your way?",
			"In the last month, how often have you found that you could not cope with all the things that you had to do?",
			"In the last month, how often have you been able to control irritations in your life?",
			"In the last month, how often have you felt that you were on top of things?",
			"In the last month, how often have you been angered because of things that happened that were outside of your control?",
			"In the last month, how often have you found yourself thinking about things that you have to accomplish?",
			"In the last month, how often have you been able to control the way you spend your time?",
			"In the last month, how often have you felt difficulties were piling up so high that you could not overcome them?",
		};

		public static final String SURVEY = "survey";

		public final String[] possibleValues = {
				"Never",
				"Sometimes",
				"Usually",
				"Almost Always",
				"Always"
		};

		public SurveyQuestions() {
			addDefaultAttributes();
		}

		@Override
		public void addDefaultAttributes() {
			String[] possibleValues = {
					"Never",
					"Almost Never",
					"Somtimes",
					"Faily Often",
					"Very Often"
			};
			for ( String q : questions){
				addAttribute(new EnumeratedAttribute(q, possibleValues));
			}
		}

		@Override
		public String getType() {
			return SURVEY;
		}

		@Override
		public MathObject newInstance() {
			return null;
		}
	}


	public  void makeErrorDialog(String s){
		JOptionPane.showMessageDialog(null,
				s,
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private  void createAndShowGUI() {

		FitnessApp fanApp = new FitnessApp();
		JFrame frame = new JFrame("Stress Application");
		Dimension frameDim = new Dimension(500, 700);
		frame.setPreferredSize(frameDim);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fanApp.addContents(frame.getContentPane());

		frame.pack();
		frame.setVisible(true);

		timeAtStart = new Date().getTime();

	}

	private void addContents(Container container){

		Font f = new Font("Arial",Font.BOLD,20);
		
		container.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ( heartRateGraphData.getListWithName(
						GraphObject.LINE_GRAPH).getValues().size() == 0){
					return; // do not allow scrolling if there is no data recorded
				}
				if (timer.isRunning())
					return;
				
				if (heartRateGraphData.getxMax() - heartRateGraphData.getxMin() < 2 && e.getWheelRotation() < 1){
					return;
				}

				mouseWheelEvent(e, heartRateGraphData, heartRateGraph);
				autoScale(heartRateGraphData);

				scrollbar.setValues( (int) (heartRateGraphData.getxMin() * 100.0),
						(int) (100.0 * (heartRateGraphData.getxMax() -  heartRateGraphData.getxMin()))
						, 0, (int) (100.0 * heartRateGraphData.getLineGraphPoints().getLastValue().getValue().getx()));

				syncGridProperties(heartRateGraphData, skinConductanceGraphData,
						skinTemperatureGraphData, compositeGraphData);
				
				autoScale(heartRateGraphData, skinConductanceGraphData, skinTemperatureGraphData, compositeGraphData);
				
				repaintAll();
			}
		});

		container.setLayout(new GridBagLayout());
		GridBagConstraints bCon = new GridBagConstraints();

		bCon.gridx = 0;
		bCon.gridwidth = 3;
		bCon.gridy++;
		bCon.weightx = .5;
		bCon.insets = new Insets(5, 5, 5, 5);
		bCon.fill = GridBagConstraints.HORIZONTAL;

		// this is the scrolling code that was used before the scrollbar was added
		/*
		container.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {}

			public void mouseEntered(MouseEvent e) {
				refPoint = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {

				draggingGraph = true;
				mouseX = e.getX();

				repaintAll();

				return;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				repaintAll();
				draggingGraph = false;
			}

		});

		container.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (timer.isRunning())
					return;
				if ( heartRateGraphData.getListWithName(
						GraphObject.LINE_GRAPH).getValues().size() == 0){
					return; // do not allow scrolling if there is no data recorded
				}

				mouseDragEvent(e, heartRateGraphData, heartRateGraph);
				syncGridProperties(heartRateGraphData, skinConductanceGraphData,
						skinTemperatureGraphData, compositeGraphData);

				repaintAll();
				mouseX = e.getX();
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {}

		});
		*/
		
		JLabel heartRateLabel = new JLabel("Heart Rate");
		heartRateLabel.setForeground(Color.BLUE);
		heartRateLabel.setFont(f);

		bCon.gridy++;
		container.add(heartRateLabel, bCon);

		heartRateGraph = new JPanel(){
			public void paint(Graphics g){
				paintGraph(g, heartRateGraphData, heartRateGraph);
			}
		};

		bCon.gridx = 0;
		bCon.gridy++;
		bCon.weightx = 1;
		bCon.weighty = 1;
		bCon.fill = GridBagConstraints.BOTH;
		container.add(heartRateGraph, bCon);

		bCon.gridy++;
		bCon.weighty = .1;
		bCon.fill = GridBagConstraints.HORIZONTAL;
		JLabel skinConductanceLabel = new JLabel("Skin Conductance");
		skinConductanceLabel.setForeground(Color.RED);
		skinConductanceLabel.setFont(f);
		container.add(skinConductanceLabel, bCon);

		skinConductanceGraph = new JPanel(){
			public void paint(Graphics g){
				paintGraph(g, skinConductanceGraphData, skinConductanceGraph);
			}
		};

		bCon.gridx = 0;
		bCon.gridy++;
		bCon.weightx = 1;
		bCon.weighty = 1;
		bCon.fill = GridBagConstraints.BOTH;
		container.add(skinConductanceGraph, bCon);

		bCon.gridy++;
		bCon.fill = GridBagConstraints.HORIZONTAL;
		bCon.weighty = .1;
		JLabel skinTemperatureLabel = new JLabel("Skin Temperature");
		skinTemperatureLabel.setForeground(Color.GREEN.darker());
		skinTemperatureLabel.setFont(f);
		container.add(skinTemperatureLabel, bCon);

		skinTemperatureGraph = new JPanel(){
			public void paint(Graphics g){
				paintGraph(g, skinTemperatureGraphData, skinTemperatureGraph);
			}
		};

		bCon.gridy++;
		bCon.weightx = 1;
		bCon.weighty = 1;
		bCon.fill = GridBagConstraints.BOTH;
		container.add(skinTemperatureGraph, bCon);

		bCon.gridy++;
		bCon.fill = GridBagConstraints.HORIZONTAL;
		bCon.weighty = .1;
		JLabel compositeLabel = new JLabel("Overall Stress");
		compositeLabel.setForeground(Color.MAGENTA.darker());
		compositeLabel.setFont(f);
		container.add(compositeLabel, bCon);

		compositeGraph = new JPanel(){
			public void paint(Graphics g){
				paintGraph(g, compositeGraphData, compositeGraph);
			}
		};

		bCon.gridy++;
		bCon.weightx = 1;
		bCon.weighty = 1;
		bCon.fill = GridBagConstraints.BOTH;
		container.add(compositeGraph, bCon);
		
		bCon.gridy++;
		bCon.fill = GridBagConstraints.HORIZONTAL;
		bCon.gridx = 0;
		bCon.weighty = .1;
		bCon.gridwidth = 3;

		scrollbar.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				try{
				if (timer.isRunning())
					return;
				if ( heartRateGraphData.getListWithName(
						GraphObject.LINE_GRAPH).getValues().size() == 0){
					return; // do not allow scrolling if there is no data recorded
				}
				double range = heartRateGraphData.getxMax() - heartRateGraphData.getxMin();
				heartRateGraphData.getAttributeWithName(GraphObject.X_MAX).setValue(scrollbar.getValue() / 100.0 + range);
				heartRateGraphData.getAttributeWithName(GraphObject.X_MIN).setValue(scrollbar.getValue() / 100.0);

				syncGridProperties(heartRateGraphData, skinConductanceGraphData,
						skinTemperatureGraphData, compositeGraphData);
				
				autoScale(heartRateGraphData, skinConductanceGraphData, skinTemperatureGraphData, compositeGraphData);
				repaintAll();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});

		container.add(scrollbar, bCon);
		
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ( timer.isRunning()){
					return;
				}
				if ( lastStopTime == 0){
					timeAtStart = new Date().getTime();
				}
				else {
					timeAtStart += new Date().getTime() - lastStopTime;
				}
				lastStopTime = 0;
				scrollbar.setEnabled(false);

				heartRateGraphData.getAttributeWithName(GraphObject.X_STEP).setValue(1.0);

				syncGridProperties(heartRateGraphData, skinConductanceGraphData,
						skinTemperatureGraphData, compositeGraphData);
				
				autoScale(heartRateGraphData, skinConductanceGraphData, skinTemperatureGraphData, compositeGraphData);
				
				timer.start();
			}
		});
		bCon.gridwidth = 1;
		bCon.gridy++;
		container.add(startButton, bCon);

		
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ( ! timer.isRunning()){
					return;
				}
				timer.stop();
				lastStopTime = new Date().getTime();

				// need to think about the best way to fix this, I'm close, but I'm not quite sure what I want to do
				int maxX = (int) (100 * heartRateGraphData.getLineGraphPoints().getLastValue().getValue().getx());
				System.out.println( (maxX - 150) + " " + 350 + " " + 0 + " " + maxX);
				scrollbar.setValues( 0, maxX, 0, maxX);

				try {
					heartRateGraphData.setxMax(maxX / 100.0);
					heartRateGraphData.setxMin(0);
				} catch (AttributeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				heartRateGraphData.autoAdjustGrid();

				syncGridProperties(heartRateGraphData, skinConductanceGraphData,
						skinTemperatureGraphData, compositeGraphData);
				
				autoScale(heartRateGraphData, skinConductanceGraphData, skinTemperatureGraphData, compositeGraphData);

				repaintAll();

				scrollbar.setEnabled(true);
			}
		});
		bCon.gridx = 1;
		container.add(stopButton, bCon);
		
		bCon.gridy++;
		bCon.gridx = 0;
		JButton summaryButton = new JButton("Summary");
		summaryButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				createSummaryDialog();
			}
			
		});
		
		container.add(summaryButton, bCon);
		
		
		/*
		JButton surveyButton = new JButton("Survey");
		surveyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				createSurveyDialog();
			}
		});
		bCon.gridx = 2;
		container.add(surveyButton);
		 */

	}

	public  void repaintAll(){
		heartRateGraph.repaint();
		skinConductanceGraph.repaint();
		skinTemperatureGraph.repaint();
		compositeGraph.repaint();
	}

	public void paintGraph(Graphics g, GraphObject graphData, JPanel panel){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphData.setWidth(panel.getWidth());
		graphData.setHeight(panel.getHeight());
		graphGui.drawMathObject(graphData, g, new Point(0,0), 1);	
	}

	public void mouseDragEvent(MouseEvent e, GraphObject graph, JPanel panel){
		double previousMin = graph.getxMin();
		double previousMax = graph.getxMax();
		if (draggingGraph){
			graph.shiftGraph((int)(mouseX - e.getX()), 0);
		}

		// do not allow the graph to move if either of the edges are hit

		double lastX = ((GridPoint)graph.getListWithName(
				GraphObject.LINE_GRAPH).getLastValue().getValue()).getx();

		if ( lastX < graph.getxMax()){
			graph.getAttributeWithName(GraphObject.X_MIN).setValue(lastX);
			graph.getAttributeWithName(GraphObject.X_MAX).setValue(previousMin);
		}
		else if (graph.getxMin() < 0){
			graph.getAttributeWithName(GraphObject.X_MIN).setValue(0);
			graph.getAttributeWithName(GraphObject.X_MAX).setValue(previousMax - previousMin);
		}
	}

	public void mouseWheelEvent(MouseWheelEvent e, GraphObject graph, JPanel panel){
		graph.zoom( 100 + 5 * -1 * e.getWheelRotation(), 0);

		autoFixGraph(graph);

		graph.autoAdjustGrid();
	}

	private  void generateRandomData() {
		counter++;
		randMax++;
		double currTime =  (new Date().getTime() - timeAtStart)/1000.0;
		try {
			if (counter % 2 == 0){
				String newPt = "(" + currTime + "," + (rand.nextInt(randMax) + 2) + ")";
				heartRateGraphData.getLineGraphPoints().addValueWithString(newPt);
				newPt = "(" + currTime + "," + (rand.nextInt(randMax + 1000) + 400) + ")";

				skinTemperatureGraphData.getLineGraphPoints().addValueWithString(newPt);

				newPt = "(" + currTime + "," + (rand.nextInt(randMax + 400) + 200) + ")";

				skinConductanceGraphData.getLineGraphPoints().addValueWithString(newPt);

				newPt = "(" + currTime + "," + 
						(	((GridPoint)heartRateGraphData.getLineGraphPoints()
								.getLastValue().getValue()).gety() + 
								((GridPoint)skinConductanceGraphData.getLineGraphPoints()
										.getLastValue().getValue()).gety() +
										((GridPoint)skinTemperatureGraphData.getLineGraphPoints()
												.getLastValue().getValue()).gety() 					

								) / 3.0
								+ ")";
				compositeGraphData.getLineGraphPoints().addValueWithString(newPt);
			}

			if ( currTime > 3.5){
				heartRateGraphData.setAttributeValue(GraphObject.X_MAX, currTime + 1.5);
				heartRateGraphData.setAttributeValue(GraphObject.X_MIN, currTime - 3.5);

				syncGridProperties(heartRateGraphData, skinConductanceGraphData,
						skinTemperatureGraphData, compositeGraphData);
			}

			repaintAll();

		} catch (AttributeException e1) {
			e1.printStackTrace();
		}
	}

	public void createSummaryDialog(){
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Average Heart Rate: " + String.format("%.2f", findAverage(heartRateGraphData))),
				new JLabel("Average Skin Temperature: " + String.format("%.2f", findAverage(skinTemperatureGraphData))),
				new JLabel("Average Skin Conductance: " + String.format("%.2f", findAverage(skinConductanceGraphData))),
				new JLabel("Average Overall Stress:" + String.format("%.2f", findAverage(compositeGraphData))),
		};
		int input = JOptionPane.showConfirmDialog(null, 
				inputs, "Workout Summary", JOptionPane.PLAIN_MESSAGE);
	}

	public void syncGridProperties(GraphObject prototype, GraphObject... graphs){
		for (GraphObject graph : graphs){
			graph.syncGridWithOtherGraph(prototype);
		}
	}

	public void createSurveyDialog() {
		if (problemDialog != null){
			problemDialog.dispose();
		}
		problemDialog = new JDialog();
		problemDialog.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.BOTH;
		con.weightx = 1;
		con.weighty = 1;
		con.insets = new Insets(2, 2, 2, 2);
		con.gridx = 0;
		con.gridy = 0;

		for (MathObjectAttribute mAtt : survey.getAttributes()){
			{// only show editing dialog if in teacher mode (not student)
				//or if the attribute has been left student editable
				if ( ! mAtt.isUserEditable() ) {
					continue;
				}
				if (mAtt instanceof StringAttribute){// make string panels stretch vertically
					con.weighty = 1;
					con.fill = GridBagConstraints.BOTH;
				}
				else{// make all others stretch very little vertically
					con.weighty = 0;
					con.fill = GridBagConstraints.HORIZONTAL;
				}
				problemDialog.getContentPane().add(getAdjuster(mAtt, null, (JPanel)problemDialog.getContentPane()), con);
				con.gridy++;
			}
		}

		JButton confirmButton = new JButton("Done");

		confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				problemDialog.setVisible(false);
				createAndShowGUI();
			}
		});
		con.fill = GridBagConstraints.NONE;
		problemDialog.getContentPane().add(confirmButton, con);
		problemDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		problemDialog.pack();
		problemDialog.validate();

		problemDialog.setVisible(true);
	}

	public double findAverage(GraphObject graph){
		double total = 0;
		int counter = 0;
		Vector<GridPointAttribute> values = (Vector<GridPointAttribute>)
				graph.getLineGraphPoints().getValues();
		for (GridPointAttribute d : values){
			total += d.getValue().gety();
		}
		return total / values.size();
	}

	public void autoScale(GraphObject... graphs){
		//return;
		
		double min, max;
		int counter = 0;
		try{
		for ( GraphObject graph : graphs){
			min = Double.MAX_VALUE;
			max = Double.MIN_VALUE;
			counter = 0;
			Vector<GridPointAttribute> values = (Vector<GridPointAttribute>)
					graph.getLineGraphPoints().getValues();
			for (GridPointAttribute d : values){
				if ( d.getValue().getx() < graph.getxMin() || d.getValue().getx() > graph.getxMax())
					continue; // skip values that are outside of the graph area
				if (d.getValue().gety() < min)
					min = d.getValue().gety();
				else if (d.getValue().gety() > max)
					max = d.getValue().gety();
				counter++;
			}

			if (counter < 3 || min == max || min == Double.MAX_VALUE || max == Double.MIN_VALUE){
				continue;
			}
			//System.out.println(min - max);
			double buffer = (max - min) / 15; 
			graph.getAttributeWithName(GraphObject.Y_MAX).setValue(max + buffer);
			graph.getAttributeWithName(GraphObject.Y_MIN).setValue(min - buffer);
			graph.autoAdjustGrid();
		}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		
	}

	public  AdjustmentPanel getAdjuster(MathObjectAttribute mAtt,
			NotebookPanel notebookPanel, JPanel panel){
		AdjustmentPanel p;
		if (mAtt instanceof BooleanAttribute)
			p = new BooleanAdjustmentPanel((BooleanAttribute)mAtt, notebookPanel, panel);
		else if (mAtt instanceof StringAttribute)
			p = new StringAdjustmentPanel((StringAttribute)mAtt, notebookPanel, panel);
		else if (mAtt instanceof ColorAttribute)
			p = new ColorAdjustmentPanel((ColorAttribute)mAtt, notebookPanel, panel );
		else if (mAtt instanceof EnumeratedAttribute)
			p = new EnumeratedAdjuster((EnumeratedAttribute)mAtt, notebookPanel, panel);
		else
			p = new GenericAdjustmentPanel(mAtt, notebookPanel, panel);
		return p;
	}

	public void autoFixGraph(GraphObject g){
		double lastX = ((GridPoint)heartRateGraphData.getListWithName(
				GraphObject.LINE_GRAPH).getLastValue().getValue()).getx();
		try {
			if ( lastX < g.getxMax())
				g.setxMax(lastX);

			if ( g.getxMin() < 0)
				g.setxMin(0);

		} catch (AttributeException e1) {
			e1.printStackTrace();
		}
	}

	public ImageIcon getIcon(String filename){
		try {
			BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
			return new ImageIcon(image);
		} catch (IOException e) {
			System.out.println("cannot find image " + filename);
		}
		return null;
	}
}
