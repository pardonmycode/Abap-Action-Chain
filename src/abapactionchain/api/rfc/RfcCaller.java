package abapactionchain.api.rfc;

import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import abapactionchain.Activator;

import abapactionchain.utils.AbapRelease;
import abapactionchain.utils.BackendComponentVersion;
import abapactionchain.utils.ProjectUtility;
import abapactionchain.views.LinkedObject;
import abapactionchain.views.View;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoClassMetaData;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.adt.communication.message.IResponse;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.IRestResourceFactory;
import com.sap.adt.communication.resources.ResourceNotFoundException;
import com.sap.adt.compatibility.discovery.AdtDiscoveryFactory;
import com.sap.adt.compatibility.discovery.IAdtDiscovery;
import com.sap.adt.compatibility.discovery.IAdtDiscoveryCollectionMember;
import com.sap.adt.tools.abapsource.ui.AbapSourceUi;
import com.sap.adt.tools.abapsource.ui.IAbapSourceUi; 

import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference; 
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import com.sap.adt.activation.IActivationServiceFactory;
import com.sap.adt.activation.AdtActivationPlugin;
import com.sap.adt.activation.IActivationService;
import com.sap.adt.activation.IActivationServiceFactory;
import com.sap.adt.activation.model.inactiveObjects.IInactiveCtsObject;
import com.sap.adt.activation.model.inactiveObjects.IInactiveCtsObjectList;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.tools.core.ui.*;
public class RfcCaller  {
	

static boolean debug = true;
	static IEditorPart activeEditor;

	public static void setEditorInRfc(IEditorPart activeEditor2) {

		RfcCaller.activeEditor =  activeEditor2;
	}
	public static void test2() {
	
	IFile file = View.getSourcPage().getFile(); 
	URI uri = URI.create(file.getFullPath().toString());

	String destinationId = ProjectUtility.getDestinationID(View.linkedObject.getProject());
	IAdtFormEditor  formEditor = View.getEditor();
	IAdtObject model = formEditor.getModel();
	IAdtObjectReference ref = model.getContainerRef();
	
	uri = URI.create(ref.getUri());
	uri = URI.create(file.getFullPath().toString());
	
	IRestResourceFactory restResourceFactory = AdtRestResourceFactory.createRestResourceFactory();
	IRestResource flightResource = restResourceFactory.createResourceWithStatelessSession(uri, destinationId);

	
	try {
		JCoDestination destination 	= JCoDestinationManager.getDestination(destinationId);
		destination.getRepository().getRequest(destinationId);
	} catch (JCoException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	
	try {
		// Trigger GET request on resource data
		IResponse response = flightResource.get(null,IResponse.class);

		// confirm that flight exists
		System.out.println("Flight exists! HTTP-status: "+String.valueOf(response.getStatus()) );
		} catch (ResourceNotFoundException e) {
			System.out.println("No flight data found");
		}
	if (debug) {
		System.out.println( "start" );
		System.out.println( destinationId );
		System.out.println( formEditor );
		System.out.println( ref );
		System.out.println( uri );
		System.out.println( restResourceFactory );
		System.out.println( model );

		System.out.println( "end" );
	}

	}

	
	
	static final String DISCOVERY_URI = "/ztransportutils/discovery";
	static final String TOC_RESOURCE_SCHEME = "http://www.drumm.de/ztransportutils/toc";
	static final String TOC_TERM = "create_toc";

	static final String QUERY_PARAMETER_TRANSPORT_REQUEST = "Z-ToC-RequestID";
	static final String QUERY_PARAMETER_RELEASE_IMMEDIATELY = "Z-ToC-ReleaseImmediately";
	
	
	private URI getResourceUri(String destination) {
		IAdtDiscovery discovery = AdtDiscoveryFactory.createDiscovery(
				destination,
				URI.create(DISCOVERY_URI));

		IAdtDiscoveryCollectionMember collectionMember = discovery
				.getCollectionMember(
						TOC_RESOURCE_SCHEME,
						TOC_TERM,
						new NullProgressMonitor());

		if (collectionMember == null) {
			return null;
		} else {
			return collectionMember.getUri();
		}
	}
	
	
	
	
	
	public static void test() {
		String destinationId = ProjectUtility.getDestinationID(View.linkedObject.getProject());
		JCoDestination destination;
		JCoFunction function = null;
		
		try {
			destination 	= JCoDestinationManager.getDestination(destinationId);
			function = destination.getRepository().getFunction("BAPI_COMPANYCODE_GETLIST");
			JCoClassMetaData cls = destination.getRepository().getClassMetaData("zcl_fibonacci");
			 destination.getRepository();
			if (debug) {
				System.out.println( "start" );
				System.out.println( destinationId );
				System.out.println( destination );
				System.out.println(destination.getRepository());
				System.out.println( cls );
				System.out.println( function );
				System.out.println( "end" );
			}
		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
//		IFile file = sourcePage.getFile();
//		URI uri = URI.create(file.getFullPath().toString());
	//
//		String dest = getDestinationID(project);
//		IAdtObjectReference ref = formEditor.getModel().getContainerRef();
	//
//		IRestResourceFactory restResourceFactory = AdtRestResourceFactory.createRestResourceFactory();
//		IRestResource flightResource = restResourceFactory.createResourceWithStatelessSession(uri, dest);
	//
	//
	//
//		try {
//			// Trigger GET request on resource data
//			IResponse response = flightResource.get(null,IResponse.class);
	//
//			// confirm that flight exists
//			System.out.println("Flight exists! HTTP-status: "+String.valueOf(response.getStatus()) );
//			} catch (ResourceNotFoundException e) {
//				System.out.println("No flight data found");
//			}
		
		
//		JCoDestination destination;
//		JCoFunction function = null;
//		try {
//			destination 	= JCoDestinationManager.getDestination(destinationId);
//			function = destination.getRepository().getFunction("Z_ADTCO_GET_OBJECT_TREE");
//			JCoTable objectTree = function.getExportParameterList().getTable("TREE");
//
//			return RfcObjectTreeContentHandler.deserialize2(linkedObject, objectTree);
//		} catch (JCoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	

//		 sourcePage = (IAbapSourcePage) ed.getAdapter(AbapSourcePage.class);
//		IDocument document = sourcePage.getDocument();
		
		
	}
//		try {
//			String destinationId = ProjectUtility.getDestinationID(linkedObject.getProject());
//			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
//
//			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_OBJECT_TREE");
//			String text = "Kein Token";
//			if (function == null) {
//				ObjectTree newObjectTree = new ObjectTree(linkedObject);
//				SourceNode sourceNode = new SourceNode(1);
//				
//				
//			//TODO getting nodes	
//				IAbapSourceUi sourceUi = AbapSourceUi.getInstance();
//				IAbapSourceScannerServices scannerServices = sourceUi.getSourceScannerServices();
//				int offset = 0;
//				
//				if ( PlatformUI.getWorkbench() != null ) {
//				System.out.println(PlatformUI.getWorkbench());
//				Display.getDefault().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						
//					try {
//						IWorkbenchWindow iw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//						activeEditor = iw.getActivePage().getActiveEditor();
//					} catch (Exception e) {
//						System.out.println("RfcCaller " + e  );
//					}	
//						
//					}
//
//				});
////				activeEditor = ProjectUtility.getActiveEditor();
////				 activeEditor = (IAdtFormEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
////						.getActiveEditor();
//				System.out.println("active Editor " + activeEditor);
//				IProject activePrj = ProjectUtility.getActiveAdtProject();
//				System.out.println(activePrj);
//				if(activeEditor != null) {
//					IAbapSourcePage sourcePage = (IAbapSourcePage) activeEditor.getAdapter(AbapSourcePage.class);
//					IDocument document = sourcePage.getDocument();
//					
//					int beginOfStatement = scannerServices.goBackToDot(document,
//							offset ) + 1;
//					int endOfStatement = scannerServices.goForwardToDot(document, offset);
//					List<Token> statementTokens = scannerServices.getStatementTokens(document, beginOfStatement);
//					
//							if (statementTokens.size() > 0) {
//								text = "calling tokens";
//								text +=  String.valueOf(statementTokens.get(0).offset);
//							}	
//				}
//				 
//	


//	@Override
//	public ObjectTree getObjectTree(LinkedObject linkedObject) {
//		int count = 0;
//		while (objectsList.size() > count) {
//			ObjectTree rfcObjectTree = objectsList.get(count);
//			if (rfcObjectTree.getLinkedObject().getName() == linkedObject.getName()
//					&& rfcObjectTree.getLinkedObject().getProject().getName() == linkedObject.getProject().getName()
//					&& rfcObjectTree.getLinkedObject().getType() == linkedObject.getType()) {
//				return rfcObjectTree;
//			}
//			count++;
//		}
//		return null;
//	}
//
//	@Override
//	public String getUriForTreeNode(TreeNode selectedNode) {
//		// TODO Auto-generated method stub
//		return null;
//	}



}