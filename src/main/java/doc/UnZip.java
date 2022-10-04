package doc;

import doc.attributes.AttributeException;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.mathobjects.*;
import doc_gui.NotebookPanel;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

// http://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
public class UnZip {

	// http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
	public static File createTempDirectory()
			throws IOException {
		final File temp;

		temp = Files.createTempDirectory("temp" + Long.toString(System.nanoTime())).toFile();

		return (temp);
	}

	/**
	 * Unzip it
	 * @param zipFileName input zip file
	 */
	public void unZipIt(String zipFileName, NotebookPanel notebookPanel, Document key) throws IOException {

		ZipFile zipFile;
		//get the zip file content
		zipFile = new ZipFile(zipFileName);
		//get the zipped file list entry
		Enumeration<? extends ZipEntry> zes = zipFile.entries();
		ZipEntry ze = null;
		List<String> studentDocNames = new ArrayList<>();

		List<Document> docs = new ArrayList<>();
		while(zes.hasMoreElements()){
			ze = zes.nextElement();
			String fileName = ze.getName();
			// ignore failures to read individual files out of zip archive
			// TODO - look back at this, seemed lik there were more
			// ZipEntries than files
			try{
				InputStream inputStream = zipFile.getInputStream(ze);
				if (inputStream.available() == 0) {
					break;
				}
				studentDocNames.add(fileName);
				docs.add(notebookPanel.openDoc(inputStream, fileName));
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		Document resultDoc = new Document("Student Summary");
		resultDoc.addBlankPage();

		List<List<MathObject>> incorrectWork = new ArrayList<>();
		List<List<MathObject>> allStudentWork = new ArrayList<>();

		List<List<String>> answers = getAnswers(key);
		for (int i = 0; i < docs.get(0).getPages().size(); i++) {
			List<MathObject> allStudentWorkForOneProblem = new ArrayList<>();
			List<MathObject> allIncorrectStudentWorkForOneProblem = new ArrayList<>();
			for (Document doc : docs) {
				if (doc == null) continue;
				Page p = doc.getPage(i);
				List<String> studentAnswers = getStudentAnswers(p);
				boolean answerCorrect = false;
				// answers were correct, hide from teacher view during grading
				// if there are no answers the problem must be manually graded
				if (!answers.get(i).isEmpty() && studentAnswers.size() == answers.get(i).size() && studentAnswers.containsAll(answers.get(i))) {
					answerCorrect = true;
				}
				// combine together all of the content of one page into a group, it will
				// all be shown to the teacher if the
				Grouping group = new Grouping(p);
				for (MathObject mObj : p.getObjects()) {
					group.addObject(mObj);
				}
				GridPoint gp = Document.getFirstWhiteSpaceOnPage(p);
				int numPoints = 3;
				int scoreLabelWidth = 40;
				int scoreLabelHeight = 20;
				TextObject score = new TextObject(p, (int)gp.getx(), (int)gp.gety(), scoreLabelWidth, scoreLabelHeight, 12, "Score");
				AnswerBoxObject scoreInput = new AnswerBoxObject(p, (int)gp.getx() + scoreLabelWidth, (int)gp.gety(), scoreLabelWidth, scoreLabelHeight);
				TextObject points = new TextObject(p,
						(int)gp.getx() + 2 * scoreLabelWidth + 10,
						(int)gp.gety(), scoreLabelWidth, scoreLabelHeight, 12,
						"of " + numPoints);
				try {
					if (answerCorrect) {
						scoreInput.setAttributeValue(AnswerBoxObject.STUDENT_ANSWER, numPoints + "");
					}
				} catch (AttributeException e) {
					// should not happen
					e.printStackTrace();
				}
				TextObject feedback = new TextObject(p, (int)gp.getx(),
						(int)gp.gety() + scoreLabelHeight + 5,
						scoreLabelWidth * 2, scoreLabelHeight, 12, "Feedback");
				AnswerBoxObject feedbackInput = new AnswerBoxObject(p, (int)gp.getx(),
						(int)gp.gety() + 2 * (scoreLabelHeight + 5),
						scoreLabelWidth * 3, scoreLabelHeight * 3);

				group.addObject(score);
				group.addObject(scoreInput);
				group.addObject(points);
				group.addObject(feedback);
				group.addObject(feedbackInput);
				group.adjustSizeToFitChildren();
				if (! answerCorrect) {
//					group.setHidden(true);
					allIncorrectStudentWorkForOneProblem.add(group);
				}
				allStudentWorkForOneProblem.add(group);
			}
			incorrectWork.add(allIncorrectStudentWorkForOneProblem);
			allStudentWork.add(allStudentWorkForOneProblem);
		}
		StringBuffer allAnswers = new StringBuffer();
		for (List<String> problemAnswers : answers) {
			for (String answer : problemAnswers) {
				allAnswers.append(answer).append(",");
			}
			allAnswers.append("::");
		}

//		JOptionPane.showMessageDialog(null,
//				allAnswers,
//				"Error", JOptionPane.ERROR_MESSAGE);

		// TODO - let the teachers give us a list
		// of problem numbers in the assignment
		// the lists of objects will not be sparse
		int[] problemNumbers = {5,7,9};
		int problemNumber = 0;
		for (List<MathObject> problems : incorrectWork) {
			Document.layoutProblems(problems, "Problem " + problemNumber, resultDoc, false);
			problemNumber++;
		}
		notebookPanel.addDoc(resultDoc);
		notebookPanel.getCurrentDocViewer().gradePage = true;
		notebookPanel.getCurrentDocViewer().studentFeedbackDocNames = studentDocNames;
		notebookPanel.getCurrentDocViewer().allStudentWork = allStudentWork;
		zipFile.close();
	}

	/**
	 * Generates a list of documents to give back to individual students based on
	 * a composite grading summary document.
	 *
	 * @param
	 * @return
	 */
	public static List<Document> generateStudentFeedbackDocs(List<String> studentFeedbackDocNames,
																													 List<List<MathObject>> allStudentWorkAndFeedback) {
		int studentIndex;
		List<Document> studentDocs = new ArrayList<>();
		for (String s : studentFeedbackDocNames) {
			studentDocs.add(new Document(s));
		}
		for (List<MathObject> allWorkOnOneProblem : allStudentWorkAndFeedback) {
			studentIndex = 0;
			for (MathObject mObj : allWorkOnOneProblem) {
				// to skip the problem numbers
				if (!(mObj instanceof Grouping)) continue;
				studentDocs.get(studentIndex).addBlankPage();
				// this should be a group that contains that feedback elements
				studentDocs.get(studentIndex).getLastPage().addObject(mObj);
				studentIndex++;
			}
		}
		return studentDocs;
	}

	private List<List<String>> getAnswers(Document doc) {
		List<List<String>> answers = new ArrayList<>();
		for (Page p : doc.getPages()) {
			for (MathObject mObj : p.getObjects()) {
				// TODO - options for, answer must be one of many, or some subset of a list of answers
				if (mObj instanceof AnswerBoxObject) {
					// TODO - shouldn't call this student answer if I'm going to reuse it
					// here for the answer key written by the teacher, for now it will work okay
					answers.add(convertToBareStringArray(mObj.getListWithName(AnswerBoxObject.CORRECT_ANSWERS).getValues()));
				} else if (mObj instanceof ExpressionObject) {
					answers.add(convertToBareStringArray(mObj.getListWithName(ExpressionObject.CORRECT_ANSWERS).getValues()));
				}
			}
		}
		return answers;
	}

	private List<String> getStudentAnswers(Page p) {
		List<String> answers = new ArrayList<>();
		// pull answers out of expression based work, unless there is an answer box
		List<String> expressionAnswers = new ArrayList<>();
		for (MathObject mObj : p.getObjects()) {
			getStudentAnswers(mObj, expressionAnswers, answers);
		}
		if (answers.isEmpty()) {
			return expressionAnswers;
		} else {
			return answers;
		}
	}

	private void getStudentAnswers(MathObject mObj, List<String> expressionAnswers, List<String> answers) {
		if (mObj instanceof Grouping) {
			for (MathObject innerObj : ((Grouping)mObj).getObjects()) {
				getStudentAnswers(innerObj, expressionAnswers, answers);
			}
		}
		// TODO - options for, answer must be one of many, or some subset of a list of answers
		if (mObj instanceof AnswerBoxObject) {
			// TODO - shouldn't call this student answer if I'm going to reuse it
			// here for the answer key written by the teacher, for now it will work okay
			answers.add((String) mObj.getAttributeWithName(AnswerBoxObject.STUDENT_ANSWER).getValue());
		} else if (mObj instanceof ExpressionObject) {
			ListAttribute<?> steps = mObj.getListWithName(ExpressionObject.STEPS);
			if (steps.getLastValue() != null) {
				expressionAnswers.add((String) steps.getLastValue().getValue());
			}
		}
	}

	private List<String> convertToBareStringArray(List<? extends MathObjectAttribute> values) {
		List<String> outVals = new ArrayList<>();
		for ( MathObjectAttribute mAtt : values) {
			// TODO type safety
			outVals.add((String)mAtt.getValue());
		}
		return outVals;
	}
}

