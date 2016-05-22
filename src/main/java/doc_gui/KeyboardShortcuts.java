package doc_gui;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class KeyboardShortcuts {

	public static void addKeyboardShortcuts(final JPanel panel, final NotebookPanel notebookPanel){
		
		panel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.singleton(KeyStroke.getKeyStroke("TAB")));
		panel.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.singleton(KeyStroke.getKeyStroke("shift TAB")));
	    
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE")
				, "delete");
		panel.getActionMap().put("delete", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.delete();
			}
		});   

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control S")
				, "save");
		panel.getActionMap().put("save", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.save();
			}
		});
		
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control W")
				, "closeCurrent");
		panel.getActionMap().put("closeCurrent", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.closeCurrentViewer();
			}
		});
		
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control TAB")
				, "switchTab");
		panel.getActionMap().put("switchTab", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				notebookPanel.switchTab();
			}
		});

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control shift TAB")
				, "switchTabBackwards");
		panel.getActionMap().put("switchTabBackwards", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				notebookPanel.switchTabBackward();
			}
		});

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control O")
				, "open");
		panel.getActionMap().put("open", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.open();
			}
		});  

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control P")
				, "print");
		panel.getActionMap().put("print", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.print();
			}
		});
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control C")
				, "copy");
		panel.getActionMap().put("copy", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.copy();
			}
		});  
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control X")
				, "cut");
		panel.getActionMap().put("cut", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.cut();
			}
		});
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control V")
				, "paste");
		panel.getActionMap().put("paste", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.paste();
			}
		});
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control N")
				, "add page");
		panel.getActionMap().put("add page", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.addPage();
			}
		});
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z")
				, "undo");
		panel.getActionMap().put("undo", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.undo();
			}
		});

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y")
				, "redo");
		panel.getActionMap().put("redo", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.redo();
			}
		});
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control EQUALS")
				, "zoomIn");
		panel.getActionMap().put("zoomIn", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.zoomIn();
			}
		});
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control MINUS")
				, "zoomOut");
		panel.getActionMap().put("zoomOut", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {

				notebookPanel.zoomOut();
			}
		});
	}
}
