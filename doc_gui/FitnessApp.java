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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

import org.xml.sax.XMLReader;

import com.sun.swing.internal.plaf.basic.resources.basic;

import doc.Document;
import doc.GridPoint;
import doc.attributes.AttributeException;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.DoubleAttribute;
import doc.attributes.EnumeratedAttribute;
import doc.attributes.GridPointAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc.mathobjects.GraphObject;
import doc.mathobjects.MathObject;
import doc.xml.DocReader;
import doc_gui.attribute_panels.AdjustmentPanel;
import doc_gui.attribute_panels.BooleanAdjustmentPanel;
import doc_gui.attribute_panels.ColorAdjustmentPanel;
import doc_gui.attribute_panels.EnumeratedAdjuster;
import doc_gui.attribute_panels.GenericAdjustmentPanel;
import doc_gui.attribute_panels.StringAdjustmentPanel;
import doc_gui.mathobject_gui.GraphObjectGUI;
import expression.Expression;
import expression.Node;
import expression.NodeException;
import expression.Number;

public class FitnessApp {

	ImageIcon image;
	JPanel heartRateGraph;
	JPanel skinConductanceGraph;
	JPanel skinTemperatureGraph;
	JPanel compositeGraph;
	JPanel signalGraph;
	JPanel summaryGraph;

	GraphObject heartRateGraphData;
	GraphObject skinConductanceGraphData;
	GraphObject skinTemperatureGraphData;
	GraphObject compositeGraphData;
	GraphObject signalGraphData;
	GraphObject summaryGraphData;

	JFrame frame;

	public static final String COM_PORT = "com port", DATA_RATE = "Data rate",
			SKIN_FUNC = "Skin conductance func (c)", HEART_FUNC = "Heart rate function (h)",
			SKIN_TEMP = "Skin temp func (t)";

	GraphObjectGUI graphGui;
	JScrollBar scrollbar;
	int randMax = 100;

	long timeAtSignalStart = 0;
	long ellapsedTime = 0;
	long lastFrameTime = 0;
	Random rand = new Random();
	int counter = 0;
	public Timer timer;
	JDialog problemDialog;
	final SurveyQuestions survey = new SurveyQuestions();
	final FitnessAppProperties appProps = new FitnessAppProperties();
	final SerialTest usb = new SerialTest(FitnessApp.this);
	Timer signalRefresh;

	private double mouseX;
	boolean refPoint, dragSelection, justFinishedPic, isTimeToRedraw, movingSelectionEnd,
	draggingGraph, dragDiskShowing;

	public FitnessApp(){
		heartRateGraphData = new GraphObject();

		try {
			heartRateGraphData.setAttributeValue(GraphObject.X_MIN, 0.0);
			heartRateGraphData.setAttributeValue(GraphObject.X_MAX, 20.0);
			heartRateGraphData.setAttributeValue(GraphObject.Y_MIN, 0.0);
			heartRateGraphData.getLineGraphPoints().removeAll();

			skinConductanceGraphData = (GraphObject) heartRateGraphData.clone();
			skinConductanceGraphData.setLineGraphColor(Color.RED);
			skinTemperatureGraphData = (GraphObject) heartRateGraphData.clone();
			skinTemperatureGraphData.setLineGraphColor(Color.GREEN.darker());
			compositeGraphData = (GraphObject) heartRateGraphData.clone();
			compositeGraphData.setLineGraphColor(Color.MAGENTA.darker());

			signalGraphData = (GraphObject) heartRateGraphData.clone();
			signalGraphData.setAttributeValue(GraphObject.X_MAX, 5.0);
			signalGraphData.setLineGraphColor(Color.RED);

			summaryGraphData = (GraphObject) heartRateGraphData.clone();

		} catch (AttributeException e) {
			e.printStackTrace();
		}

		timer = new Timer(30, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//synchronized(this){
					ellapsedTime += new Date().getTime() - lastFrameTime;
					lastFrameTime = new Date().getTime();
					autoScale(heartRateGraphData, skinTemperatureGraphData, skinConductanceGraphData, compositeGraphData);
					generateRandomData();
				//}
			}

		});
		
		signalRefresh = new Timer(30, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				double currTime =  (new Date().getTime() - timeAtSignalStart)/1000.0;
				if ( currTime > 3.5){
					signalGraphData.getAttributeWithName(GraphObject.X_MAX).setValue(currTime + 1.5);
					signalGraphData.getAttributeWithName(GraphObject.X_MIN).setValue(currTime - 3.5);
				}
				if ( signalGraphData.getListWithName(
						GraphObject.LINE_GRAPH).getValues().size() != 0){
					autoScale(signalGraphData); // do not resize if there is no data recorded
				}
				signalGraph.repaint();
			}

		});

		scrollbar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100, 0, 100);

		graphGui = new GraphObjectGUI();
	}

	public static class FitnessAppProperties extends MathObject{

		public FitnessAppProperties() {
			addDefaultAttributes();
		}

		protected void addDefaultAttributes(){
			addAttribute(new IntegerAttribute(DATA_RATE, 115200, 0, 1000000, true));
			addAttribute(new StringAttribute(COM_PORT, "/dev/ttyACM0", true));
			addAttribute(new StringAttribute(SKIN_FUNC, "5c", true));
			addAttribute(new StringAttribute(HEART_FUNC, "3h", true));
			addAttribute(new StringAttribute(SKIN_TEMP, "4t", true));
		}

		@Override
		public String getType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MathObject newInstance() {
			// TODO Auto-generated method stub
			return null;
		}


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

		public static final String[] possibleValues = {
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
			return new SurveyQuestions();
		}
	}

	private MouseListener makeMouseListener(){
		return new MouseListener(){

			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {
				graphGui.mouseClicked(heartRateGraphData, e.getX(), e.getY(), 1);
				graphGui.mouseClicked(skinTemperatureGraphData, e.getX(), e.getY(), 1);
				graphGui.mouseClicked(skinConductanceGraphData, e.getX(), e.getY(), 1);
				graphGui.mouseClicked(compositeGraphData, e.getX(), e.getY(), 1);
				repaintAll();
			}
		};
	}

	private MouseMotionListener makeMouseMotionListener(){
		return new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {}

			@Override
			public void mouseDragged(MouseEvent e) {
				graphGui.mouseClicked(heartRateGraphData, e.getX(), e.getY(), 1);
				graphGui.mouseClicked(skinTemperatureGraphData, e.getX(), e.getY(), 1);
				graphGui.mouseClicked(skinConductanceGraphData, e.getX(), e.getY(), 1);
				graphGui.mouseClicked(compositeGraphData, e.getX(), e.getY(), 1);
				repaintAll();
			}
		};
	}

	public  void makeErrorDialog(String s){
		JOptionPane.showMessageDialog(null,
				s,
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public void save() {
		BufferedWriter f = null;
		JFileChooser fileChooser = new JFileChooser();
		try {
			int value = fileChooser.showSaveDialog(heartRateGraph);
			if (value == JFileChooser.APPROVE_OPTION) {
				java.io.File file = fileChooser.getSelectedFile();

				file = new File(file.getParentFile() + File.separator + file.getName());

				f = new BufferedWriter(new FileWriter(file));
				
				Document saveDoc = new Document(file.getName());
				saveDoc.addBlankPage();
				saveDoc.getPage(0).addObject(heartRateGraphData);
				saveDoc.getPage(0).addObject(skinTemperatureGraphData);
				saveDoc.getPage(0).addObject(skinConductanceGraphData);
				saveDoc.getPage(0).addObject(compositeGraphData);
				f.write(saveDoc.exportToXML());

				f.flush();
				f.close();
			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				f.flush();
				f.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Error saving file", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void open() {
		JFileChooser fileChooser = new JFileChooser();
		try {
			int value = fileChooser.showOpenDialog(heartRateGraph);
			if (value == JFileChooser.APPROVE_OPTION) {
				FileReader fileReader = new FileReader(fileChooser
						.getSelectedFile());
				DocReader reader = new DocReader();
				Document readIn = reader.readDoc(fileReader, fileChooser.getSelectedFile().getName());
				heartRateGraphData = (GraphObject) readIn.getPage(0).getObjects().get(0);
				skinTemperatureGraphData = (GraphObject) readIn.getPage(0).getObjects().get(1);
				skinConductanceGraphData = (GraphObject) readIn.getPage(0).getObjects().get(2);
				compositeGraphData = (GraphObject) readIn.getPage(0).getObjects().get(3);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error opening file, please send it to the lead developer at\n"
							+ "dev@open-math.com to help with debugging",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void createGUI() {

		frame = new JFrame("Stress Application");
		frame.addWindowListener(new WindowListener() {
			@Override public void windowActivated(WindowEvent arg0) {}
			@Override public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				usb.close();
				frame.dispose();
			}
			@Override public void windowDeactivated(WindowEvent arg0) {}
			@Override public void windowDeiconified(WindowEvent arg0) {}
			@Override public void windowIconified(WindowEvent arg0) {}
			@Override public void windowOpened(WindowEvent arg0) {}
		});
		Dimension frameDim = new Dimension(500, 700);
		frame.setPreferredSize(frameDim);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addContents(frame.getContentPane());

		frame.pack();

	}

	private void addContents(Container container){

		Font f = new Font("Arial",Font.BOLD,20);

		container.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				//synchronized(this){
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
				//}
			}
		});

		container.setLayout(new GridBagLayout());
		GridBagConstraints bCon = new GridBagConstraints();

		bCon.gridx = 0;
		bCon.gridwidth = 3;
		bCon.gridy = 0;
		bCon.weightx = .5;
		bCon.insets = new Insets(5, 5, 5, 5);
		bCon.fill = GridBagConstraints.HORIZONTAL;

		JLabel logoLabel = new JLabel(getIcon("img/Final Logo_hoizontal_no_background.png"));
		logoLabel.setMaximumSize(new Dimension(300, 100));
		logoLabel.setPreferredSize(new Dimension(300, 100));
		container.add(logoLabel, bCon);

		JLabel heartRateLabel = new JLabel("Heart Rate");
		heartRateLabel.setForeground(Color.BLUE);
		heartRateLabel.setFont(f);

		bCon.gridy++;
		container.add(heartRateLabel, bCon);

		heartRateGraph = new JPanel(){
			public void paint(Graphics g){
				//synchronized(this){
					paintGraph(g, heartRateGraphData, heartRateGraph);
				//}
			}
		};

		heartRateGraph.addMouseListener(makeMouseListener());
		heartRateGraph.addMouseMotionListener(makeMouseMotionListener());

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
				//synchronized(this){
					paintGraph(g, skinConductanceGraphData, skinConductanceGraph);
				//}
			}
		};

		skinConductanceGraph.addMouseListener(makeMouseListener());
		skinConductanceGraph.addMouseMotionListener(makeMouseMotionListener());

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
				//synchronized(this){
					paintGraph(g, skinTemperatureGraphData, skinTemperatureGraph);
				//}
			}
		};

		skinTemperatureGraph.addMouseListener(makeMouseListener());
		skinTemperatureGraph.addMouseMotionListener(makeMouseMotionListener());

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
				//synchronized(this){
					paintGraph(g, compositeGraphData, compositeGraph);
				//}
			}
		};

		compositeGraph.addMouseListener(makeMouseListener());
		compositeGraph.addMouseMotionListener(makeMouseMotionListener());

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
				//synchronized(this){
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
				//}
			}
		});

		container.add(scrollbar, bCon);

		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//synchronized(this){
					if ( timer.isRunning()){
						return;
					}
					lastFrameTime = new Date().getTime();
					scrollbar.setEnabled(false);

					heartRateGraphData.getAttributeWithName(GraphObject.X_STEP).setValue(2.0);
					
					if ( (heartRateGraphData.getListWithName(
							GraphObject.LINE_GRAPH).getValues().size() == 0) ||
							heartRateGraphData.getLineGraphPoints().getLastValue().getValue().getx() < 15){
						heartRateGraphData.getAttributeWithName(GraphObject.X_MAX).setValue(20.0);
						heartRateGraphData.getAttributeWithName(GraphObject.X_MIN).setValue(0.0);
					}
					else{
						heartRateGraphData.getAttributeWithName(GraphObject.X_MAX).setValue(
								heartRateGraphData.getLineGraphPoints().getLastValue().getValue().getx() + 5.0);
						heartRateGraphData.getAttributeWithName(GraphObject.X_MIN).setValue(
								heartRateGraphData.getLineGraphPoints().getLastValue().getValue().getx() - 15.0);
					}
					syncGridProperties(heartRateGraphData, skinConductanceGraphData,
							skinTemperatureGraphData, compositeGraphData);

					autoScale(heartRateGraphData, skinConductanceGraphData, skinTemperatureGraphData, compositeGraphData);

					timer.start();
				//}
			}
		});
		bCon.gridwidth = 1;
		bCon.gridy++;
		container.add(startButton, bCon);


		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//synchronized(this){
					if ( ! timer.isRunning()){
						return;
					}
					timer.stop();

					// need to think about the best way to fix this, I'm close, but I'm not quite sure what I want to do

					if ( ! (heartRateGraphData.getListWithName(
							GraphObject.LINE_GRAPH).getValues().size() == 0)){
						int maxX = (int) (100 * heartRateGraphData.getLineGraphPoints().getLastValue().getValue().getx());
						System.out.println( (maxX - 150) + " " + 350 + " " + 0 + " " + maxX);
						scrollbar.setValues( 0, maxX, 0, maxX);

						try {
							heartRateGraphData.setxMin(0);
							heartRateGraphData.setxMax(maxX / 100.0);
						} catch (AttributeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					heartRateGraphData.autoAdjustGrid();

					syncGridProperties(heartRateGraphData, skinConductanceGraphData,
							skinTemperatureGraphData, compositeGraphData);

					autoScale(heartRateGraphData, skinConductanceGraphData, skinTemperatureGraphData, compositeGraphData);

					repaintAll();

					scrollbar.setEnabled(true);
				//}
			}
		});
		bCon.gridx = 1;
		container.add(stopButton, bCon);
		
		bCon.gridx++;
		JButton openButton = new JButton("Open");
		openButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				open();
				ellapsedTime = (long) (((GridPoint)heartRateGraphData.getListWithName(
						GraphObject.LINE_GRAPH).getLastValue().getValue()).getx() * 1000);
				repaintAll();
			}
			
		});
		container.add(openButton, bCon);
		
		bCon.gridx++;
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
			
		});
		container.add(saveButton, bCon);

		bCon.gridy++;
		bCon.gridx = 0;
		JButton summaryButton = new JButton("Summary");
		summaryButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//synchronized(this){
					createSummaryDialog();
				//}
			}

		});

		container.add(summaryButton, bCon);

		bCon.gridx = 1;
		JButton signalButton = new JButton("Check Signal");
		signalButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//synchronized(this){
					createSignalDialog();
				//}
			}

		});

		container.add(signalButton, bCon);

		bCon.gridx = 2;
		JButton propsButton = new JButton("Properties");
		propsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//synchronized(this){
					createPropertiesDialog();
				//}
			}

		});

		container.add(propsButton, bCon);

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

	private void generateRandomData() {
		counter++;
		randMax++;
		double currTime =  ellapsedTime / 1000.0;
		try {
			String newPt;
			if (counter % 15 == 0){
				
				newPt = "(" + currTime + "," + (rand.nextInt(randMax) - 100) + ")";
				heartRateGraphData.getLineGraphPoints().addValueWithString(newPt);
				newPt = "(" + currTime + "," + (rand.nextInt(randMax + 1000) + 400) + ")";

				skinTemperatureGraphData.getLineGraphPoints().addValueWithString(newPt);

				newPt = "(" + currTime + "," + (rand.nextInt(randMax + 400) + 200) + ")";

				skinConductanceGraphData.getLineGraphPoints().addValueWithString(newPt);
				/*
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
				*/
				
			}
			
			if (counter % 30 == 0){
				String formula = appProps.getAttributeValue(HEART_FUNC) +  "+" +
						appProps.getAttributeValue(SKIN_FUNC) + "+" +
						appProps.getAttributeValue(SKIN_TEMP);
				System.out.println(formula);
				Node ex = Expression.parseNode(formula);
				ex = ex.replace("h", new Number(heartRateGraphData.getLineGraphPoints()
								.getLastValue().getValue().gety()));
				ex = ex.replace("c", new Number(skinConductanceGraphData.getLineGraphPoints()
						.getLastValue().getValue().gety()));
				ex = ex.replace("t", new Number(skinTemperatureGraphData.getLineGraphPoints()
						.getLastValue().getValue().gety()));

				newPt = "(" + currTime + "," + ex.numericSimplify().toStringRepresentation() + ")";
				System.out.println(ex.smartNumericSimplify().toStringRepresentation());
				compositeGraphData.getLineGraphPoints().addValueWithString(newPt);
			}
			
			if ( currTime > 15.0){
				heartRateGraphData.setAttributeValue(GraphObject.X_MAX, currTime + 5.0);
				heartRateGraphData.setAttributeValue(GraphObject.X_MIN, currTime - 15.0);

				syncGridProperties(heartRateGraphData, skinConductanceGraphData,
						skinTemperatureGraphData, compositeGraphData);
			}

			repaintAll();

		} catch (AttributeException e1) {
			e1.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createSummaryDialog(){

		summaryGraph = new JPanel(){
			public void paint(Graphics g){
				paintGraph(g, summaryGraphData, summaryGraph);
			}
		};
		summaryGraph.setMinimumSize(new Dimension(300,300));
		summaryGraph.setPreferredSize(new Dimension(800,300));
		
		try{
			summaryGraphData.setAttributeValue(GraphObject.BAR_GRAPH_GROUP_SIZE, 3);
			summaryGraphData.getBarGraphLabels().removeAll();
			summaryGraphData.getBarGraphLabels().addValueWithString("Heart Rate");
			summaryGraphData.getBarGraphLabels().addValueWithString("Skin Temp.");
			summaryGraphData.getBarGraphLabels().addValueWithString("Skin Cond.");
			summaryGraphData.getBarGraphLabels().addValueWithString("Stress");
			summaryGraphData.setyMax(100.0);
			summaryGraphData.setyMin(-10.0);
			summaryGraphData.autoAdjustGrid();
			summaryGraphData.getBarGraphValues().removeAll();

			for ( Double d : findAverage(heartRateGraphData)){
				summaryGraphData.getBarGraphValues().addValueWithString(d.toString());
			}
			for ( Double d : findAverage(skinTemperatureGraphData)){
				summaryGraphData.getBarGraphValues().addValueWithString(d.toString());
			}
			for ( Double d : findAverage(skinConductanceGraphData)){
				summaryGraphData.getBarGraphValues().addValueWithString(d.toString());
			}
			for ( Double d : findAverage(compositeGraphData)){
				summaryGraphData.getBarGraphValues().addValueWithString(d.toString());
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}

		final JComponent[] inputs = new JComponent[] {
				summaryGraph,
				new JLabel("Overall Average, Average Before Mark, Average After Mark"),
				/*new JLabel("Heart Rate: " + averages(heartRateGraphData)),
				new JLabel("Skin Temperature: " + averages(skinTemperatureGraphData)),
				new JLabel("Skin Conductance: " + averages(skinConductanceGraphData)),
				new JLabel("Overall Stress: " + averages(compositeGraphData)),*/
				new JLabel("Starting Stress Value: need to calculate this"),

		};
		int input = JOptionPane.showConfirmDialog(null, 
				inputs, "Workout Summary", JOptionPane.PLAIN_MESSAGE);
	}
	
	public String averages(GraphObject graphData){
		return String.format("%.2f", findAverage(graphData)[1]) +
				", " + String.format("%.2f", findAverage(graphData)[2]) +
				", " + String.format("%.2f", findAverage(graphData)[0]);
	}

	public void createSignalDialog(){

		signalGraph = new JPanel(){
			public void paint(Graphics g){
				paintGraph(g, signalGraphData, signalGraph);
			}
		};
		signalGraph.setMinimumSize(new Dimension(300,300));
		signalGraph.setPreferredSize(new Dimension(300,300));

		final JComponent[] inputs = new JComponent[] {
				new JLabel("Heart Rate Signal Check"),
				signalGraph
		};

		timeAtSignalStart = new Date().getTime();
		signalRefresh.start();
		signalGraphData.getLineGraphPoints().removeAll();
		int input = JOptionPane.showConfirmDialog(null, 
				inputs, "Signal Check", JOptionPane.PLAIN_MESSAGE);
		signalRefresh.stop();

	}

	public void syncGridProperties(GraphObject prototype, GraphObject... graphs){
		for (GraphObject graph : graphs){
			graph.syncGridWithOtherGraph(prototype);
		}
	}

	public void createPropertiesDialog() {
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

		for (MathObjectAttribute mAtt : appProps.getAttributes()){
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

		JButton confirmButton = new JButton("Refresh");

		confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				usb.initialize();
			}
		});
		con.fill = GridBagConstraints.NONE;
		problemDialog.getContentPane().add(confirmButton, con);
		problemDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		problemDialog.pack();
		problemDialog.validate();

		problemDialog.setVisible(true);
	}

	public void createSurveyDialog() {
		if (problemDialog != null){
			problemDialog.dispose();
		}
		problemDialog = new JDialog(frame);
		problemDialog.addWindowListener(new WindowListener() {
			@Override public void windowActivated(WindowEvent arg0) {}
			@Override public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				usb.close();
				problemDialog.dispose();
			}
			@Override public void windowDeactivated(WindowEvent arg0) {}
			@Override public void windowDeiconified(WindowEvent arg0) {}
			@Override public void windowIconified(WindowEvent arg0) {}
			@Override public void windowOpened(WindowEvent arg0) {}
		});
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
				problemDialog.dispose();
				frame.setVisible(true);
			}
		});
		con.fill = GridBagConstraints.NONE;
		problemDialog.getContentPane().add(confirmButton, con);
		problemDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		problemDialog.pack();
		problemDialog.validate();

		problemDialog.setVisible(true);
	}

	public double[] findAverage(GraphObject graph){
		double[] ret = new double[3];
		double total = 0, totalBefore = 0, totalAfter = 0;
		int counter = 0, counterBefore = 0, counterAfter = 0;
		Vector<GridPointAttribute> values = (Vector<GridPointAttribute>)
				graph.getLineGraphPoints().getValues();
		GridPointAttribute d;
		for ( int i = 0; i < values.size(); i++){
			d = values.get(i);
			total += d.getValue().gety();
			counter++;
			if ( d.getValue().getx() < graph.getSelection().getStart()){
				counterBefore++;
				totalBefore +=  d.getValue().gety(); 
			}
			else{
				counterAfter++;
				totalAfter +=  d.getValue().gety();
			}
		}
		ret[0] = total / counter;
		ret[1] = totalBefore / counterBefore;
		ret[2] = totalAfter / counterAfter;
		return ret;
	}

	public void autoScale(GraphObject... graphs){

		double min, max;
		int counter = 0;
		try{
			for ( GraphObject graph : graphs){
				min = Double.MAX_VALUE;
				max = Double.MIN_VALUE;
				counter = 0;
				Vector<GridPointAttribute> values = (Vector<GridPointAttribute>)
						graph.getLineGraphPoints().getValues();
				GridPointAttribute d;
				for ( int i = 0; i < values.size(); i++){
					d = values.get(i);
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
		//else if (mAtt instanceof StringAttribute)
		//p = new StringAdjustmentPanel((StringAttribute)mAtt, notebookPanel, panel);
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
