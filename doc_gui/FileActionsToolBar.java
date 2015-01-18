package doc_gui;


import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

/*
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
*/

public class FileActionsToolBar extends JToolBar {

	private NotebookPanel notebookPanel;

	public FileActionsToolBar(NotebookPanel p){
		this.setFloatable(false);
		notebookPanel = p;
		Icon icon; 
		
		icon = notebookPanel.getIcon("img/save.png");

		new OCButton(icon, "Save [ctrl+s]", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.save();
			}
		};

		icon = notebookPanel.getIcon("img/open.png");

		new OCButton(icon, "Open [ctrl+o]", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.open();
			}
		};
		/*
		icon = notebookPanel.getIcon("img/save_cloud.png");

		new OCButton(icon, "Save To Open-Math Cloud", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
				
				// open dialog for user to enter a name, later add fields for tags and such
				File temp = null, tempDir = null;
				try {

				    tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));

				    if(!(tempDir.delete()))
				    {
				        throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
				    }

				    if(!(tempDir.mkdir()))
				    {
				        throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
				    }

				    temp = new File(tempDir.getAbsolutePath() + File.separator +
				    		NotebookPanel.addFileExtension(notebookPanel.getCurrentDocViewer().getDoc().getName()));

				    // Write to temp file
				    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
				    String doc = notebookPanel.getCurrentDocViewer().getDoc().exportToXML();
				    System.out.println(doc);
				    out.write(doc);
				    out.close();
				} catch (IOException e) {
				}
				
				HttpClient client = new DefaultHttpClient();
				HttpParams params = new BasicHttpParams();
				params.setParameter("http.protocol.handle-redirects", true);
				HttpPost post = new HttpPost("http://localhost/index.php/user/upload_doc");
				post.setParams(params);
				post.setHeader("Cookie", OpenNotebook.getCookie());

				FileBody fileBody = new FileBody(temp);
				System.out.println(fileBody.getContentLength());
				MultipartEntity entity = new MultipartEntity();
				entity.addPart("userfile", fileBody);
				try {
					entity.addPart("doc_tags", new StringBody("random tags"));
					entity.addPart("material_type", new StringBody("worksheet"));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					System.out.println("unsupp encoding");
					e1.printStackTrace();
				}

				post.setEntity(entity);

				try {

					HttpResponse response = client.execute(post);
					
					HttpEntity responseEntity = response.getEntity();
					DataInputStream dataInput = new DataInputStream (responseEntity.getContent());
					String str;
					while (null != ((str = dataInput.readLine())))
					{
						System.out.println (str);
					}
				    temp.delete();
				    tempDir.delete();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					System.out.println("client protocol");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("io ex");
					e.printStackTrace();
				}
			}
		};

		icon = notebookPanel.getIcon("img/open_cloud.png");

		new OCButton(icon, "Open From Open-Math Cloud", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
			}
		};
*/
		
//		new OCButton("frame", "open frame", 1, 1, 2, 0, this){
//
//			@Override
//			public void associatedAction(){
//				notebookPanel.createWorkspace(null);
//			}
//		};
//		
		
		if ( ! notebookPanel.isInStudentMode()){
			ImageIcon addPageIcon = notebookPanel.getIcon("img/newPage.png");

			OCButton addPageButton = new OCButton(addPageIcon,
					"Add Page [ctrl+n](before selected, or at the end if none selected)", 1, 1, 2, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.addPage();
				}
			};

			//			ImageIcon pagePropsIcon = notebookPanel.getIcon("img/pageProperties.png");
			//			
			//			OCButton pagePropsButton = new OCButton(pagePropsIcon, "Doc Properties", 1, 1, 2, 0, this){
			//				
			//				@Override
			//				public void associatedAction(){
			//					notebookPanel.showDocProperties();
			//				}
			//			};
		}

		icon = notebookPanel.getIcon("img/zoomIn.png");

		new OCButton(icon, "Zoom In [ctrl and '+']", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.zoomIn();
			}
		};

		icon = notebookPanel.getIcon("img/zoomOut.png");

		new OCButton(icon, "Zoom Out [ctrl and '-']", 1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.getCurrentDocViewer().zoomOut();
			}
		};
		icon = notebookPanel.getIcon("img/undo.png");

		new OCButton(icon, "Undo", 1, 1, 3, 0, this){

			public void associatedAction(){
				notebookPanel.undo();
			}
		};

		icon = notebookPanel.getIcon("img/redo.png");

		OCButton redoButton = new OCButton(icon, "Redo", 1, 1, 3, 0, this){

			public void associatedAction(){
				notebookPanel.redo();
			}
		};

		icon = notebookPanel.getIcon("img/print.png");

		new OCButton(icon, "Print [ctrl+p]", 1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.print();
			}
		};

		if ( ! notebookPanel.isInStudentMode()){
			icon = notebookPanel.getIcon("img/generateWorksheet.png");

			new OCButton(icon, "Generate Worksheet", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.generateWorksheet();
				}
			};

			icon = notebookPanel.getIcon("img/answerKey.png");

			new OCButton(icon, "Show Answer Key", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.addDoc(notebookPanel.getCurrentDocViewer().getDoc().generateAnswerKey());
				}
			};
		}
		icon = notebookPanel.getIcon("img/keyboard.png");

		new OCButton(icon, "Show On-Screen Keyboard", 1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.setKeyboardDialogVisible(true);
			}
		};

		new OCButton("<html><font size=-1>Tutorials/<br>Samples</font><html>", "Sample Documents",
				1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.setSampleDialogVisible(true);
			}
		};
	}

}
