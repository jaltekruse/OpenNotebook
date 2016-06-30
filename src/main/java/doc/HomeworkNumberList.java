package doc;

/**
 * Created by Scott on 10/24/2015.
 */
import java.util.*;
public class HomeworkNumberList {
    private String problemNumbers;
    public HomeworkNumberList (String rawProblemNumberList) {
        problemNumbers = rawProblemNumberList;
    }// the object holds the number set as a raw input string

    public ArrayList<Integer> getNumberList() {
        problemNumbers.trim();
        //trim extra spaces from the homework input
        String[] problemNumberListv1 = problemNumbers.split(",");
        //separate each of the homework number blocks
        ArrayList<Integer> problemNumberListFinal = new ArrayList<Integer>();
        //build a list to house the final output
        for(int i = 0; i < problemNumberListv1.length; i++) {

            problemNumberListv1[i] = problemNumberListv1[i].trim();
            problemNumberListv1[i] = problemNumberListv1[i].toLowerCase();
            //keeps the String nice and manageable
            if(problemNumberListv1[i].indexOf("even") != -1) {

                int start = Integer.parseInt(problemNumberListv1[i].substring(0, problemNumberListv1[i].indexOf("-")));
                //makes the first number start ('1' from '1-25')
                int end = Integer.parseInt(problemNumberListv1[i].substring(problemNumberListv1[i].indexOf("-") + 1, problemNumberListv1[i].indexOf(" ")));
                //makes the last number end ('25' from '1-25')
                if (start%2 == 0) {
                    for(int i2 = start; i2 <= end ; i2 += 2) {

                        problemNumberListFinal.add(i2);
                    }//end for loop adding every other number (1-25 even)
                } else {
                    for(int i2 = start + 1; i2 <= end ; i2 += 2) {

                        problemNumberListFinal.add(i2);
                    }//end for loop adding every other number (1-25 even)
                }//checking if there is an 'even' in the number block
            } else if(problemNumberListv1[i].indexOf("odd") != -1) {

                int start = Integer.parseInt(problemNumberListv1[i].substring(0, problemNumberListv1[i].indexOf("-")));
                //makes the first number start ('1' from '1-25')
                int end = Integer.parseInt(problemNumberListv1[i].substring(problemNumberListv1[i].indexOf("-") + 1, problemNumberListv1[i].indexOf(" ")));
                //makes the last number end ('25' from '1-25')
                if (start%2 != 0) {
                    for(int i2 = start; i2 <= end ; i2 += 2) {

                        problemNumberListFinal.add(i2);
                    }//end for loop adding every other number (1-25 even)
                } else {
                    for(int i2 = start + 1; i2 <= end ; i2 += 2) {

                        problemNumberListFinal.add(i2);
                    }//end for loop adding every other number (1-25 even)
                }//checking if there is an 'even' in the number block
            } else if (problemNumberListv1[i].indexOf("-") != -1) {

                int start = Integer.parseInt(problemNumberListv1[i].substring(0, problemNumberListv1[i].indexOf("-")));
                //makes the first number start ('1' from '1-25')
                int end = Integer.parseInt(problemNumberListv1[i].substring(problemNumberListv1[i].indexOf("-") + 1));
                //makes the last number end ('25' from '1-25')
                for(int i2 = start; i2 <= end ; i2 ++) {

                    problemNumberListFinal.add(i2);
                }//end for loop adding every number (1-25)
            }
            else {

                problemNumberListFinal.add(Integer.parseInt(problemNumberListv1[i]));
            }//just adding the number to the list if its just a number on its own
        }//end for loop cycling through the array of number blocks (not the Array List)
        return problemNumberListFinal;
        /*
        String output;
        for(int i = 0; i < problemNumberListFinal.size(); i++) {
        output += (problemNumberListFinal.get(i) + " ");
        }
        return output;
        //different version of output
         */
    }//end get method
}//end class