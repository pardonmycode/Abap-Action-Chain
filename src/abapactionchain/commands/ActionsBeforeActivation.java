package abapactionchain.commands;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.progress.UIJob;

import com.sap.adt.activation.CheckException;
import com.sap.adt.activation.checklist.MessageList;
import com.sap.adt.activation.internal.AdtActivationServiceFactory;
import com.sap.adt.communication.content.AdtContentHandlingFactory;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.ContentHandlerException;
import com.sap.adt.communication.content.IContentHandlingService;
import com.sap.adt.communication.message.AdtRequestFactory;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.communication.message.IRequest;
import com.sap.adt.communication.message.IResponse;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.IRestResourceFactory;
import com.sap.adt.communication.resources.QueryParameter;
import com.sap.adt.communication.resources.ResourceNotFoundException;
import com.sap.adt.communication.session.AdtSystemSessionFactory;
import com.sap.adt.communication.session.IEnqueueSystemSession;
import com.sap.adt.communication.session.IStatelessSystemSession;
import com.sap.adt.communication.session.ISystemSessionFactory;
import com.sap.adt.compatibility.discovery.AdtDiscoveryFactory;
import com.sap.adt.project.AdtCoreProjectServiceFactory;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.IAdtObjectReference;
import com.sap.adt.tools.core.content.AdtStaxContentHandlerUtility;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoRequest;

import abapactionchain.utils.ProjectUtility;
import abapactionchain.views.View;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;

@SuppressWarnings({ "restriction", })
public class ActionsBeforeActivation {

	final boolean debug = true;

	public static void runActionChain(String after_commandId) {
		UIJob uiJob = new UIJob("actions chain uiJob") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					for (Button btn : View.ButtonsList) {
						if (!btn.getSelection()) {
							continue;
						}

						if (checkSyntax(View.getSourcPage(),monitor) != null) {
							break; 
						}

						if ("Save current file" == btn.getText()) {
							saveCurrentFile(View.getSourcPage(), monitor);
							continue;
						}
						if ("Save all files" == btn.getText()) {
							saveAllFile();
							continue;
						}


						if ("Use Abap Cleaner" == btn.getText()) {
							useAbapCleaner();
							continue;
						}

						if ("Activate current file" == btn.getText()) {
							activatFile(monitor);
							continue;

						}
						if ("Activate all files" == btn.getText()) {
							activateAllFiles(monitor);
							continue;
						}

					}

				} catch (Exception e) {
					System.out.println(e);

				}
				return Status.OK_STATUS;
			}
		};
		uiJob.setSystem(true);
		uiJob.schedule();
	}

	public static MessageList checkSyntax(IAbapSourcePage sourcepage, IProgressMonitor monitor) {
		MessageList msg = null;
		try {
			IAdtFormEditor editor = View.view.getEditor();
			IAdtObjectReference adt = ProjectUtility.getAdtObjectReference(editor); // editor.getAdapter(IAdtObjectReference.class);

			List<IAdtObjectReference> adts = new ArrayList<IAdtObjectReference>();
			adts.add(adt);
			msg = ProjectUtility.activationService.check(adts, monitor);
			

		} catch (CheckException e) {
			e.printStackTrace();
		}
		return msg;
	}

	public static void useAbapCleaner() {

		try {
			ProjectUtility.service.executeCommand("com.sap.adt.abapcleaner.cleanup.automatic", null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
		sleep(10);
	}

	public static void saveCurrentFile(IAbapSourcePage sourcepage, IProgressMonitor monitor) {

		System.out.println("Start save currentFile");
		sourcepage.doSave(monitor);
		sleep(50);
	}

	public static void saveAllFile() {
		System.out.println("org.eclipse.ui.file.saveAll");

		try {
			ProjectUtility.service.executeCommand("org.eclipse.ui.file.saveAll", null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
		sleep(100);
	}

	public static void activatFile(IProgressMonitor monitor) {

		try {
			ProjectUtility.service.executeCommand("com.sap.adt.activation.ui.command.singleActivation", null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
		sleep(200);

	}

	public static void activateAllFiles(IProgressMonitor monitor) {
		MessageList msg = null;
		try {
			IAdtFormEditor editor = View.view.getEditor();
			IAdtObjectReference adt = ProjectUtility.getAdtObjectReference(editor); // editor.getAdapter(IAdtObjectReference.class);

			List<IAdtObjectReference> adts = new ArrayList<IAdtObjectReference>();
			adts.add(adt);
			msg = ProjectUtility.activationService.check(adts, monitor);

		} catch (CheckException e) {
			e.printStackTrace();
		}
		

	}




	
	public static void sleep(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void print(Object obj) {
		
			if (obj != null) {
				if (obj.getClass() != null) {
					if (obj.getClass().getSimpleName() != null) {
						System.out.print(obj.getClass().getSimpleName());

						if (obj.getClass().getEnclosingMethod() != null) {
							System.out.print("   method: ");
							System.out.print(obj.getClass().getEnclosingMethod().getName());
							System.out.print("   result: ");

						}
					}
				}

				System.out.println(obj);

			}else {
				System.out.println("obj is null");
			}

		

	}
	
	public static void print(String obj) {
		System.out.print(obj);
	}
	
}
