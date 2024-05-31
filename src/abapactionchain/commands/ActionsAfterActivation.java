package abapactionchain.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.internal.handlers.LegacyHandlerService;

import com.sap.adt.activation.ui.IActivationSuccessListener;
import com.sap.adt.tools.core.IAdtObjectReference;

import abapactionchain.utils.ProjectUtility;
import abapactionchain.views.View;


import com.sap.adt.activation.ui.PostActivationNotifier ;

@SuppressWarnings("restriction")
public class ActionsAfterActivation implements IActivationSuccessListener {
boolean debug = true;
	@Override
	public void onActivationSuccess(List<IAdtObjectReference> adts, IProject arg1) {
		View.view.reload();  
		try {
			for(Button btn : View.ButtonsList) {
				if (!btn.getSelection()) {
					continue;
				}
				
				if("Run Test" == btn.getText()) {
					runTest();	
				}

				if("Run Test with coverage" == btn.getText()) {
					runTestWithCoverage();
					
					
				}

				if("Run Test with ATC checks" == btn.getText()) {
					runTestWithAtc();	
				}
				
			}

		} catch (Exception e) {
				System.out.println( e );

		}
		
	}

	
	
	
	
	
	
	
	
	public static void runTest() {
		System.out.println( "Run tests" );

		try {	
			LegacyHandlerService srv = (LegacyHandlerService) ProjectUtility.service.executeCommand("com.sap.adt.tool.abap.unit.launchShortcut.run", null);
			

		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
		ActionsBeforeActivation.sleep(100);

	}
	public static void runTestWithCoverage() {
		System.out.println( "Run tests with coverage" );

		try {
			ProjectUtility.service.executeCommand("com.sap.adt.tool.abap.unit.launchShortcutWithCoverage.coverage", null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
		ActionsBeforeActivation.sleep(100);

	}		
	
	public static void runTestWithAtc() {
		System.out.println( "Run tests with ATC checks" );

		try {
			ProjectUtility.service.executeCommand("com.sap.adt.atc.ui.launchShortcut.run", null);
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			e.printStackTrace();
		}
		ActionsBeforeActivation.sleep(100);

	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
