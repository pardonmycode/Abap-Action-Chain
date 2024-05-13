package abapactionchain.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.internal.handlers.LegacyHandlerService;
import org.eclipse.ui.progress.UIJob;

import com.sap.adt.activation.AdtActivationPlugin;
import com.sap.adt.activation.IActivationService;
import com.sap.adt.activation.IActivationServiceFactory;
import com.sap.adt.activation.model.inactiveObjects.IInactiveCtsObjectList;
import com.sap.adt.activation.ui.IActivationSuccessListener;
import com.sap.adt.sapgui.ui.internal.GuiURLStreamHandlerService;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.IAdtObjectReference;

import abapactionchain.api.rfc.RfcCaller;
import abapactionchain.utils.ProjectUtility;
import abapactionchain.views.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@SuppressWarnings({ "restriction",  })
public class ActionsBeforeActivation extends AbstractHandler   {


	
	public static ArrayList<String> commands = new ArrayList<>() {
		public static final long serialVersionUID = 12L;
		{
			add("org.eclipse.ui.file.save");
			add("org.eclipse.ui.file.saveAll");
			add("com.sap.adt.abapcleaner.cleanup.automatic");
			add("com.sap.adt.activation.ui.command.singleActivation");
			add("com.sap.adt.activation.ui.command.multiActivation");
			add("com.sap.adt.tool.abap.unit.launchShortcut.run");
			add("com.sap.adt.tool.abap.unit.launchShortcutWithCoverage.coverage");
			add("com.sap.adt.atc.ui.launchShortcut.run");
		}
	};

	
	static Map<String, List<String> > map_func_afterCommands
	= new HashMap<String, List<String> >(){
		private static final long serialVersionUID = 13L;
		{

			for(int k = View.btn_str_list.size()-1; k > -1; k--) {
					put(View.btn_str_list.get(k), commands.subList(0,k+1) );
			}
		}
	};
	

	
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
						
//						if ( !( map_func_afterCommands.get(btn.getText()).contains(after_commandId)) ){
//							continue;
//						}
						
						if("Use Abap Cleaner" == btn.getText()) {
							useAbapCleaner();
//							RfcCaller.test();
							RfcCaller.test2();
							
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
//
//						if("Run Test" == btn.getText()) {
//							runTest();	
//						}
//
//						if("Run Test with coverage" == btn.getText()) {
//							runTestWithCoverage();
//							
//						}
//
//						if("Run Test with adt checks" == btn.getText()) {
//							runTestWithAdt();	
//						}

						
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
			System.out.println( "Start activatFile" );


//			final IActivationServiceFactory activationServiceFactory = AdtActivationPlugin.getDefault().getActivationServiceFactory();
//			final IActivationService activationService = activationServiceFactory
//					.createActivationService(ProjectUtility.getActiveAdtProject().getName());
//			final IInactiveCtsObjectList newInactiveCtsObjectList = activationService
//					.getInactiveCtsObjects(monitor);
//			
			
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
