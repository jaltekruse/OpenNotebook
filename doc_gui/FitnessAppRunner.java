package doc_gui;

public class FitnessAppRunner {

	private static FitnessApp app;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				app = new FitnessApp();
				app.usb.initialize();
				app.createSurveyDialog();
			}
		});
	}
}
