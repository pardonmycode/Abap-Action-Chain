package abapactionchain.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.internal.handlers.LegacyHandlerService;
import org.eclipse.ui.progress.UIJob;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import abapactionchain.utils.ProjectUtility;
import abapactionchain.views.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@SuppressWarnings({ "restriction",  })
public class ActionsBeforeActivation extends AbstractHandler   {

	

	
	boolean  debug = true;
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		super.addHandlerListener(handlerListener);

	}

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		super.removeHandlerListener(handlerListener);

	}

	public static void runActionChain(String after_commandId) {
		UIJob uiJob = new UIJob("actions chain uiJob") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					for(Button btn : View.ButtonsList) {
						if (!btn.getSelection()) {
							continue;
						}
						
						
						if("Use Abap Cleaner" == btn.getText()) {
							useAbapCleaner();
							
						}
						if ("Save current File" == btn.getText() ) {
							saveCurrentFile(View.getSourcPage(), monitor);
						}
						if ("Save all files" == btn.getText()) {
							saveAllFile();
						}
						if ("Activate current File" == btn.getText()) {
							activatFile(monitor);
						}
						if("Activate all files" == btn.getText()) {
							activateAllFiles();
						}
						
					}



				} catch (Exception e) {
						System.out.println( e );

				}
				return Status.OK_STATUS;
			}
		};
		uiJob.setSystem(true);
		uiJob.schedule();
	}

	public static void useAbapCleaner() {
		try {
			ProjectUtility.service.executeCommand("com.sap.adt.abapcleaner.cleanup.automatic", null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
			sleep(100); 
	}

	public static void saveCurrentFile(IAbapSourcePage sourcepage, IProgressMonitor monitor) {
			System.out.println( "Start save currentFile" );
			sourcepage.doSave(monitor);      
			sleep(100);
	}
	

	public static void saveAllFile() {
			System.out.println( "org.eclipse.ui.file.saveAll" );

			try {
				ProjectUtility.service.executeCommand("org.eclipse.ui.file.saveAll", null);
			} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
				e.printStackTrace();
			}			
			sleep(100);
	}

	public static void activatFile(IProgressMonitor monitor ) {
			try {
				ProjectUtility.service.executeCommand("com.sap.adt.activation.ui.command.singleActivation", null);
			} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
				e.printStackTrace();
			}
			sleep(200);

	}
	
	
		public static void activateAllFiles() {
			System.out.println( "Start activate all files" );

			try {
				LegacyHandlerService srv =    (LegacyHandlerService) ProjectUtility.service.executeCommand("com.sap.adt.activation.ui.command.multiActivation", null);
				try {
					srv.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
				e.printStackTrace();
			}
			sleep(500);

		}
	
		
		

		
		public static void sleep(int milliseconds) {
			try {
				TimeUnit.MILLISECONDS.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}


		
}
