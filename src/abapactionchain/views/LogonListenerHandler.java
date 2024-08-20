package abapactionchain.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.sap.adt.destinations.logon.notification.ILoggedOnEvent;
import com.sap.adt.destinations.logon.notification.ILogonListener;

import abapactionchain.utils.ProjectUtility;

public class LogonListenerHandler implements ILogonListener {
	private static final List<String> destinationListenerInfo = new ArrayList<>();

	@Override
	public void loggedOn(ILoggedOnEvent loggedOnEvent, IProgressMonitor progress) {
		String destId = loggedOnEvent.getDestinationData().getId();
		if (destinationListenerInfo.indexOf(destId) == -1) {
			System.out.println("LoggedOnEvent: " + destId);
			ProjectUtility.pgmoni = progress;
			destinationListenerInfo.add(destId);
			AbapPageLoadListener.addListener(new AbapPageLoadListenerHandler(destId));
		
		}

	}

}
