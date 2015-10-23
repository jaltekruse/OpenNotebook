/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

public class Cursor {
	
	private NodeGraphic nodeGraphic;
	private int pos;
	
	public Cursor(){}
	
	public Cursor(NodeGraphic vg, int p){
		setValueGraphic(vg);
		setPos(p);
	}

	public void setValueGraphic(NodeGraphic nodeGraphic) {
		this.nodeGraphic = nodeGraphic;
	}

	public NodeGraphic getValueGraphic() {
		return nodeGraphic;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}
}
