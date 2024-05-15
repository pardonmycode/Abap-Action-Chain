package abapactionchain.views;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;

import abapactionchain.commands.ActionsBeforeActivation;

public class CommandListener implements IExecutionListener {

	@Override
	public void notHandled(String commandId, NotHandledException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {

		

		switch (commandId) {
		case "org.eclipse.ui.file.saveAll": {
			}
		
		case "org.eclipse.ui.file.save": {
				ActionsBeforeActivation.runActionChain(commandId);	
			}
		}
	}


	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		// TODO Auto-generated method stub

	}

}
