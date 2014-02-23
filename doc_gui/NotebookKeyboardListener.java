package doc_gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.util.Vector;

import doc.mathobjects.MathObject;

public class NotebookKeyboardListener implements KeyListener{
	
	private OpenNotebook notebook;										
	
	public NotebookKeyboardListener(OpenNotebook notebook){
		this.notebook = notebook;
	}

	@Override
	public void keyPressed(KeyEvent e) {

        try{
            MathObject foc = notebook.getNotebookPanel().getCurrentDocViewer().getFocusedObject();
            switch (e.getKeyCode()){
                case KeyEvent.VK_DOWN:
                    if (notebook.getNotebookPanel().getCurrentDocViewer().getPageGUI().getGUIForObj(foc).keyPressed(foc, PageGUI.DOWN)){
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                        return;
                    }
                    if (foc != null){
                        foc.setyPos(foc.getyPos() + 1);
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (notebook.getNotebookPanel().getCurrentDocViewer().getPageGUI().getGUIForObj(foc).keyPressed(foc, PageGUI.UP)){
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                        return;
                    }
                    if (foc != null){
                        foc.setyPos(foc.getyPos() - 1);
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (notebook.getNotebookPanel().getCurrentDocViewer().getPageGUI().getGUIForObj(foc).keyPressed(foc, PageGUI.LEFT)){
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                        return;
                    }
                    if (foc != null){
                        foc.setxPos(foc.getxPos() - 1);
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (notebook.getNotebookPanel().getCurrentDocViewer().getPageGUI().getGUIForObj(foc).keyPressed(foc, PageGUI.RIGHT)){
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                        return;
                    }
                    if (foc != null){
                        foc.setxPos(foc.getxPos() + 1);
                        notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                    }
                    break;
                default:
                    notebook.getNotebookPanel().getCurrentDocViewer().getPageGUI().getGUIForObj(foc).keyPressed(foc, e.getKeyChar());
                    notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
                    break;

            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}
