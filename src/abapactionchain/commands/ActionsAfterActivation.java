package abapactionchain.commands;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Button;

import com.sap.adt.activation.ui.IActivationSuccessListener;
import com.sap.adt.tools.core.IAdtObjectReference;

import abapactionchain.views.View;


@SuppressWarnings("restriction")
public class ActionsAfterActivation implements IActivationSuccessListener {
boolean debug = true;
	@Override
	public void onActivationSuccess(List<IAdtObjectReference> adts, IProject arg1) {
		try {
			for(Button btn : View.ButtonsList) {
				if (!btn.getSelection()) {
					continue;
				}
				
				if (debug) {
					System.out.println("adts: "+ adts );
				}
				if("Run Test" == btn.getText()) {
					ActionsBeforeActivation.runTest();	
				}

				if("Run Test with coverage" == btn.getText()) {
					ActionsBeforeActivation.runTestWithCoverage();
					
				}

				if("Run Test with adt checks" == btn.getText()) {
					ActionsBeforeActivation.runTestWithAdt();	
				}
				
			}

		} catch (Exception e) {
				System.out.println( e );

		}
		
	}

}
