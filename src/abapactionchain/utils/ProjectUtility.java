package abapactionchain.utils;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.sap.adt.activation.AdtActivationPlugin;
import com.sap.adt.activation.IActivationService;
import com.sap.adt.activation.IActivationServiceFactory;
import com.sap.adt.activation.internal.AdtActivationServiceDiscovery;
import com.sap.adt.activation.model.inactiveObjects.IInactiveCtsObject;
import com.sap.adt.activation.model.inactiveObjects.IInactiveCtsObjectList;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.model.IAuthenticationToken;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.internal.AuthenticationToken;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.project.AdtCoreProjectServiceFactory;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.tools.abapsource.AbapSource;
import com.sap.adt.tools.abapsource.IAdtObjectLoader;
import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourceMultiPageEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;
//import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;
import com.sap.adt.tools.core.spi.sfs.util.IAdtSpiSfsUtil;
import com.sap.adt.tools.core.spi.sfs.util.AdtSpiSfsUtilFactory;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import abapactionchain.views.LinkedObject;
import abapactionchain.views.View;




import			com.sap.adt.communication.AdtCommunicationFrameworkPlugin;
@SuppressWarnings({ "restriction" })
public class ProjectUtility {
	public static IProgressMonitor pgmoni;
	public static IHandlerService service;
	public static IActivationServiceFactory activationServiceFactory;
	public static IActivationService activationService;
	public static IInactiveCtsObjectList newInactiveCtsObjectList;
	

	public static IProject getActiveAdtProject() {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkbenchWindow window = page.getWorkbenchWindow();
			ISelection adtSelection = window.getSelectionService().getSelection();
			IProject project = ProjectUtil.getActiveAdtCoreProject(adtSelection, null, null, null);
			service = window.getService(IHandlerService.class);
		
			
			//Activate stuff
			
			

			activationServiceFactory = AdtActivationPlugin.getDefault().getActivationServiceFactory();
			activationService = activationServiceFactory.createActivationService(project.getName());
			newInactiveCtsObjectList = activationService.getInactiveCtsObjects(pgmoni);
			
//			IInactiveCtsObject neg = newInactiveCtsObjectList.getEntry().get(1);

			return project;
		} catch (Exception e) {
			if (true) {
				System.out.println("ProjectUtility " + e);
			}
			return null;
		}
	}

	public static String getDestinationID(IProject project) {

		String destinationId = AdtCoreProjectServiceFactory.createCoreProjectService().getDestinationId(project);
		return destinationId;
	}

	public static List<IFile> getAllFilesFromEditor(IEditorPart editor) {
		List<IFile> files = null;
		if (editor instanceof IAdtFormEditor) {

			IAdtFormEditor formEditor = (IAdtFormEditor) editor;
			files = formEditor.getAllFiles();

		}
		return files;
	}

	public static LinkedObject getObjectFromEditor(IEditorPart editor) {
		if (editor instanceof IAdtFormEditor) {

			IAdtFormEditor formEditor = (IAdtFormEditor) editor;
			IAdtObject model = formEditor.getModel();
			
			if (model != null) {
				int count = 0;
				IProject project = getActiveAdtProject();

				while (View.linkedObjects.size() > count) {
					LinkedObject currentlyLinkedObject = View.linkedObjects.get(count);

					if (currentlyLinkedObject.equals(model, project)) {
						return currentlyLinkedObject;
					}
					count++;
				}
				IAbapSourcePage sourcePage = editor.getAdapter(AbapSourcePage.class);

				LinkedObject linkedObject = new LinkedObject(formEditor, project, sourcePage);

				View.linkedObjects.add(linkedObject);
				return linkedObject;
			}
		}
		return null;
	}

	public static void ensureLoggedOn(IProject project) {
		try {
			IAbapProject abapProject = project.getAdapter(IAbapProject.class);
			
			AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(abapProject.getDestinationData(),
					PlatformUI.getWorkbench().getProgressService());
			

		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	public static IEditorPart getActiveEditor() {
		try {

			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			System.out.println("  getActiveEditor in ProjectUtility.java : " + activeEditor);

			return activeEditor;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static LinkedObject getObjectFromEditor() {
		return getObjectFromEditor(getActiveEditor());
	}

	public static IAbapSourcePage getAbapSourcePage(IEditorPart editor) {
		try {
			MultiPageEditorPart multiPartEditor = (MultiPageEditorPart) editor;
			IAbapSourcePage sourcePage = (IAbapSourcePage) multiPartEditor.getSelectedPage();
			return sourcePage;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static IAdtObjectReference getAdtObject(IEditorPart editor) {
		if (editor instanceof IAbapSourceMultiPageEditor) {
//			IAbapSourcePage abapSourcePage = editor.getAdapter(IAbapSourcePage.class);
			IAdtObjectReference adtobj = editor.getAdapter(IAdtObjectReference.class);

			if (adtobj != null) {
				return adtobj;
			}

//			return abapSourcePage.getMultiPageEditor().getModel();
		}
		return null;
	}

	public static IFile getFile(IEditorPart editor) {
		IEditorInput editorInput = editor.getEditorInput();
		IFile file = ((IFileEditorInput) editorInput).getFile();
		return file;
	}

	public static IAdtObjectReference getAdtObjectReference(IEditorPart editor) {
		IFile file = getFile(editor);
		return Adapters.adapt((Object) file, IAdtObjectReference.class);
	}

	public static IAdtObject getAdtObject2(IEditorPart editor) {
		IFile file = getFile(editor);
		final IAdtObjectLoader loader = AbapSource.getInstance().getAdtObjectLoader(file);
		final List<IAdtObject> adtObjects = loader.getAdtComponentObjects(file);
		if (adtObjects != null && adtObjects.size() > 0) {
			return adtObjects.get(0);
		}
		return Adapters.adapt((Object) file, IAdtObject.class);
	}

	public static IProject getProjectByName(String projectName) {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			return project;
		} catch (Exception e) {
			return null;
		}
	}

	public static IProject getProjectFromProjectChooserDialog() {
		return AbapProjectSelectionDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
	}

	public static void logonWithPassword(IProject project, String password) {
		IAdtCoreProject adtProject = project.getAdapter(IAdtCoreProject.class);
		IDestinationData destinationData = adtProject.getDestinationData();
		IStatus status = null;
		try {
			if (!password.isEmpty()) {
				if (pgmoni == null) {
					pgmoni = new NullProgressMonitor();
				}

				IAuthenticationToken authenticationToken = new AuthenticationToken();
				authenticationToken.setPassword(password); 

				
				 status = AdtLogonServiceFactory.createLogonService().ensureLoggedOn(destinationData, authenticationToken,
						pgmoni);
				
//				 jenkinsConc(String baseUrl, String username, password,  authenticationToken);
					
					
				
			}
		} catch (Exception e) {
			ensureLoggedOn(project);
		}
		

	}

//	public static void openObject(final IProject project, final IAdtObjectReference adtObjectRef) {
//		AdtNavigationServiceFactory.createNavigationService().navigate(project, adtObjectRef, true);
//
//	}
//
//	public static void runObject(final IProject project, final IAdtObjectReference adtObjectRef) {
//		AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility().openEditorForObject(project, adtObjectRef, true,
//				WorkbenchAction.EXECUTE.toString(), null, Collections.<String, String>emptyMap());
//
//	}

}
