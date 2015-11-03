/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
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
