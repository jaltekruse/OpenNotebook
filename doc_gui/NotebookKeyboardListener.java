package doc_gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

public class NotebookKeyboardListener implements KeyListener{
	
	private Vector<Integer> keysPressed;
	
	private NotebookPanel notebook;										
	
	public NotebookKeyboardListener(NotebookPanel notebook){
		this.notebook = notebook;
		keysPressed = new Vector<Integer>();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(KeyEvent.getKeyText(e.getKeyCode()) + " presssed");
		keysPressed.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		keysPressed.remove(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean shiftPressed(){
		return keysPressed.contains(KeyEvent.SHIFT_DOWN_MASK);
	}
}
