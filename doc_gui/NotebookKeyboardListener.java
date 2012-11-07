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
		// TODO Auto-generated method stub
		if ( e.getKeyCode() == KeyEvent.VK_DOWN){
			MathObject foc = notebook.getNotebookPanel().getCurrentDocViewer().getFocusedObject();
			if (foc != null){
				foc.setyPos(foc.getyPos() + 1);
				notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
			}
		}
		if ( e.getKeyCode() == KeyEvent.VK_UP){
			MathObject foc = notebook.getNotebookPanel().getCurrentDocViewer().getFocusedObject();
			if (foc != null){
				foc.setyPos(foc.getyPos() - 1);
				notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
			}
		}
		if ( e.getKeyCode() == KeyEvent.VK_LEFT){
			MathObject foc = notebook.getNotebookPanel().getCurrentDocViewer().getFocusedObject();
			if (foc != null){
				foc.setxPos(foc.getxPos() - 1);
				notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
			}
		}
		if ( e.getKeyCode() == KeyEvent.VK_RIGHT){
			MathObject foc = notebook.getNotebookPanel().getCurrentDocViewer().getFocusedObject();
			if (foc != null){
				foc.setxPos(foc.getxPos() + 1);
				notebook.getNotebookPanel().getCurrentDocViewer().repaintDoc();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}
