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

import doc.mathobjects.AnswerBoxObject;
import doc.mathobjects.ArrowObject;
import doc.mathobjects.ConeObject;
import doc.mathobjects.ConnectedDrawing;
import doc.mathobjects.CubeObject;
import doc.mathobjects.CylinderObject;
import doc.mathobjects.ExpressionObject;
import doc.mathobjects.GeneratedProblem;
import doc.mathobjects.GraphObject;
import doc.mathobjects.Grouping;
import doc.mathobjects.LineObject;
import doc.mathobjects.MathObject;
import doc.mathobjects.NumberLineObject;
import doc.mathobjects.OvalObject;
import doc.mathobjects.ParallelogramObject;
import doc.mathobjects.PolygonObject;
import doc.mathobjects.ProblemGenerator;
import doc.mathobjects.ProblemNumberObject;
import doc.mathobjects.RectangleObject;
import doc.mathobjects.RegularPolygonObject;
import doc.mathobjects.TextObject;
import doc.mathobjects.TrapezoidObject;
import doc.mathobjects.TriangleObject;
import doc.mathobjects.VariableValueInsertionProblem;

public class MathObjectVisitorImpl<RET, STATE, EXP extends Exception> implements MathObjectVisitor<RET, STATE, EXP> {
  
  public RET defaultAction(MathObject obj, STATE state) throws EXP {
    throw new RuntimeException(
        "No action defined for type: "
        + obj.getClass().getSimpleName());
  }
  
  @Override
  public RET visitArrowObject(ArrowObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitArrowObject(AnswerBoxObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitConeObject(ConeObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitConnectedDrawing(ConnectedDrawing obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitCubeObject(CubeObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitCylinderObject(CylinderObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitExpressionObject(ExpressionObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitGeneratedProblem(GeneratedProblem obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitGraphObject(GraphObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitGrouping(Grouping obj, STATE state) throws EXP {
    for (MathObject mObj : obj.getObjects()) {
      defaultAction(mObj, state);
    }
    // TODO - think more about this, try moving some of the existing code to
    // this visitor pattern to see what is the best default here
    // will we need multiple base/abstrat classes to extend from?
    return null;
  }

  @Override
  public RET visitLineObject(LineObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitMathObject(MathObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitNumberLineObject(NumberLineObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitOvalObject(OvalObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitParallelogramObject(ParallelogramObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitPolygonObject(PolygonObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitProblemGenerator(ProblemGenerator obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitProblemNumberObject(ProblemNumberObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitRectangleObject(RectangleObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitRegularPolygonObject(RegularPolygonObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitTextObject(TextObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitTrapezoidObject(TrapezoidObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitTriangleObject(TriangleObject obj, STATE state) throws EXP {
    return defaultAction(obj, state);
  }

  @Override
  public RET visitVariableValueInsertionProblem(VariableValueInsertionProblem obj, STATE state) throws EXP {
    for (MathObject mObj : obj.getObjects()) {
      defaultAction(mObj, state);
    }
    // TODO - think more about this, try moving some of the existing code to
    // this visitor pattern to see what is the best default here
    // will we need multiple base/abstrat classes to extend from?
    return null;
  }
}
