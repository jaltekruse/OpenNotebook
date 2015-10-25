package doc;

import doc.mathobjects.MathObject;

import java.awt.*;
import java.util.UUID;

/**
 * Created by Scott on 10/24/2015.
 */
public class StudentAnswersAndWork extends Page{
    public StudentAnswersAndWork () {
        super(new Document(""));
    }
    public int getWidth() {
        return 100;
    }
    public int getHeight() {
        return 100;
    }
    public boolean addObject(MathObject mObj){

        //check to make sure the object will fit on the page, inside of the margins
        if ( getObjects().contains(mObj)){
            return true;
        }
        Rectangle printablePage = new Rectangle(0, 0, getWidth(),
                getHeight());

        //		Rectangle objRect = new Rectangle(mObj.getxPos(), mObj.getyPos(), mObj.getWidth(), mObj.getHeight());
        //		if (printablePage.contains(objRect)){
        //		objects.add(mObj);
        //			return true;
        //		}


        if ( ! objects.contains(mObj)){
            objects.add(mObj);
            mObj.setParentContainer(this);
            return true;
        }

        return false;
        //throw error? the object would not fit within the printable page with the current position and dimensions
    }

}
