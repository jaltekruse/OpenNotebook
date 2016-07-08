package cz.natur.cuni.mirai.math.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import cz.natur.cuni.mirai.math.algebra.AlgebraSystem;
import cz.natur.cuni.mirai.math.editor.JArrayDialog;
import cz.natur.cuni.mirai.math.editor.JMathWorkbook;
import cz.natur.cuni.mirai.math.editor.JMatrixDialog;
import cz.natur.cuni.mirai.math.editor.JToolbarFolder;
import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.model.MathModel;

public class TestMath {

	private static String engine = "JMathLib";
	private static String command = null;

    public static void arrayDialog() {
        Frame frame = JOptionPane.getFrameForComponent(null);
        JArrayDialog dialog = new JArrayDialog(frame, "New Array ...");
    	dialog.pack();
    	dialog.setVisible(true);
		System.out.println("result code:" + dialog.getResult());
		System.out.println("columns:" + dialog.getColumns());
    }    

    public static void matrixDialog() {
        Frame frame = JOptionPane.getFrameForComponent(null);
        JMatrixDialog dialog = new JMatrixDialog(frame, "New Matrix ...");
    	dialog.pack();
    	dialog.setVisible(true);
		System.out.println("result code:" + dialog.getResult());
		System.out.println("columns:" + dialog.getColumns());
		System.out.println("rows:" + dialog.getRows());
    }

	public static JMathWorkbook newWorkbook(final MetaModel meta) {
		JMathWorkbook workbook = new JMathWorkbook();
		MathModel model = MathModel.newModel(meta);
		try {
			AlgebraSystem as = (AlgebraSystem) Class.forName(
					"cz.natur.cuni.mirai.math.algebra."+engine+"Plugin").newInstance();
			as.run(command);
			workbook.setAlgebraSystem(as);
			model.addElement(0, 
					"Welcome! Read tutorial at http://mirai.sf.net");

		} catch (Exception e) {
			model.addElement(0, 
					"Please, read install instructions http://mirai.sf.net");
			model.addElement(0, MessageFormat.format(
					"Error! {0} not found. Installation incomplete.", new Object[] {engine}));
		}
		model.setModified(false);
		workbook.attachModel(model);
		return workbook;
	}

	public static void main(String[] args) throws IOException {
		// create the frame
		JFrame frame = new JFrame("Mirai Test");

		// create tool-bars and their frames 
		MetaModel metaModel = new MetaModel(engine+".xml");
		JToolbarFolder.createInstance(frame, metaModel, JToolbarFolder.LARGE);

		// assign the frame a workbook
		JMathWorkbook workbook = newWorkbook(metaModel);

		// scroll pane
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setDoubleBuffered(false);
		scrollPane.setBackground(Color.white);
		scrollPane.setViewportView(workbook);
		scrollPane.setPreferredSize(new Dimension(550,450));
		workbook.setScrollPane(scrollPane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(50);
		scrollPane.getVerticalScrollBar().setBlockIncrement(250);

		frame.getContentPane().add(scrollPane);

		// display
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

}
