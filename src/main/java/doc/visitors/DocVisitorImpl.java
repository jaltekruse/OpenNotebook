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
package doc.visitors;

import doc.Document;
import doc.Page;
import doc.visitors.DocVisitor;

public class DocVisitorImpl<RET, STATE, EXP extends Exception> implements DocVisitor<RET, STATE, EXP> {

  public DocVisitorImpl() {

  }

  @Override
  public RET visitDoc(Document doc, STATE state) throws EXP {
    for (Page p : doc.getPages()) {
      visitPage(p, state);
    }
    return null;
  }

  @Override
  public RET visitPage(Page page, STATE state) throws EXP {
    return null;
  }
}
