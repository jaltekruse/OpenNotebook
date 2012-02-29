/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc;

/**
 * A wrapper class for storing a single point in the user space of a document,
 * along with a page number. Used to store a position that is translated from
 * a single point on the entire displayed document. Helps mediate the different
 * grids that exist on the screen and in the document files/printing space.
 * 
 * @author jason
 *
 */
public class PointInDocument implements Comparable<PointInDocument>{

	private int page, xPos, yPos;

	private boolean outSidePage, belowPage;

	public PointInDocument(int page, int xPos, int yPos){
		this.page = page;
		this.yPos = yPos;
		this.xPos = xPos;
	}

	public PointInDocument() {
		// TODO Auto-generated constructor stub
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getxPos() {
		return xPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setOutSidePage(boolean outSidePage) {
		this.outSidePage = outSidePage;
	}

	public boolean isOutSidePage() {
		return outSidePage;
	}

	public void setBelowPage(boolean belowPage) {
		this.belowPage = belowPage;
	}

	public boolean isBelowPage() {
		return belowPage;
	}

	@Override
	public int compareTo(PointInDocument other) {
		// had problems with columns being off by small amounts, causing the
		// number to turn out bizarrely. this function is used to order
		// the numbers, so I added a small buffer to prevent a problem
		// that is slightly higher, but on the right column from being ordered
		// after another on the left that is slightly lower
		int columnBuffer = 4;
		if ( page < other.getPage()){
			return -1;
		}
		else if ( page > other.getPage()){
			return 1;
		}
		else{
			if ( yPos < other.getyPos() - 3){
				return -1;
			}
			else if ( yPos > other.getyPos() + 3){
				return 1;
			}
			else{
				if ( xPos < other.getxPos()){
					return -1;
				}
				else{
					return 1;
				}
			}
		}
	}
}
