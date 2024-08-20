package abapactionchain.views;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import abapactionchain.commands.ActionsBeforeActivation;



public class CommandListener implements IExecutionListener {
boolean debug = false;
	@Override
	public void notHandled(String commandId, NotHandledException exception) {
		// TODO Auto-generated method stub
		
		if (debug) {
			System.out.println(this.getClass().getSimpleName());
			System.out.println( "nothandled commandId:" + commandId + "return:" + exception);
		}
	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {

		switch (commandId) {
		
		case "org.eclipse.ui.file.save": {
			
			ActionsBeforeActivation.runActionChain(commandId);	
			}
		}	

	}

	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		// TODO Auto-generated method stub
		boolean debug = true;
		if (debug) {
			System.out.println(this.getClass().getSimpleName());
			System.out.println("preExecute");
			System.out.println( event );
			System.out.println(  );
		}
	
		

	
	}

}
