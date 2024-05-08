package abapactionchain.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;

import abapactionchain.utils.ProjectUtility;
import abapactionchain.views.ILinkedWithEditorView;
import abapactionchain.views.LinkWithEditorPartListener;
import abapactionchain.views.LinkedObject;
import abapactionchain.views.CommandListener;
import abapactionchain.views.View;

import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;





@SuppressWarnings({ "restriction", "unused" })
public class View extends ViewPart implements ILinkedWithEditorView  {
	static boolean debug = true;
	public GridLayout layout;
	private Label myLabelInView;
	public static ArrayList<Button> ButtonsList = new ArrayList<>();
	public static LinkedObject linkedObject = new LinkedObject(null, null, null);
	public static List<LinkedObject> linkedObjects = new ArrayList<>();
	private CommandListener commandListener = new CommandListener();	
	protected IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	
	
	public static View view;
	private IEditorPart editor;
	public Composite parent;	
	
	private static Composite container;
	
	public static ArrayList<String> btn_str_list = new ArrayList<>() {
		public static final long serialVersionUID = 12L;
		{
			add("Use Abap Cleaner");
			add("Save current file");
			add("Save all files");
			add("Activate current File");
			add("Activate all files");
			add("Run Test");
			add("Run Test with coverage");
			add("Run Test with adt checks");
		}
	};
	

	
	@PostConstruct
	public void createPartControl(Composite parent) {
		System.out.println("Enter in SampleE4View postConstruct");
		this.parent = parent;
		view = this;
		
		addCommandListener();
		
		GridLayout toplayout = new GridLayout();		
		toplayout.numColumns = 1;
		parent.setLayout(toplayout);

		
		myLabelInView = new Label(parent, SWT.None);
		myLabelInView.setText("Enable your Chain Actions. This Actions starts on saving a file");
		parent.layout();
		
		
		

		container = new Composite(parent, SWT.None);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginLeft = 10;
		container.setLayout(layout);
		createButtons(container);
		container.layout();

		
		
		setLinkingWithEditor();
		linkedObject = ProjectUtility.getObjectFromEditor();
		if(linkedObject == null) {
			reload();
		}
	}

	@Focus
	public void setFocus() {
		myLabelInView.setFocus();
		refreshObject();

	}

	/**
	 * This method is kept for E3 compatiblity. You can remove it if you do not
	 * mix E3 and E4 code. <br/>
	 * With E4 code you will set directly the selection in ESelectionService and
	 * you do not receive a ISelection
	 * 
	 * @param s
	 *            the selection received from JFace (E3 mode)
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection s) {
		if (s==null || s.isEmpty())
			return;

		if (s instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) s;
			if (iss.size() == 1)
				setSelection(iss.getFirstElement());
			else
				setSelection(iss.toArray());
		}
	}

	/**
	 * This method manages the selection of your current object. In this example
	 * we listen to a single Object (even the ISelection already captured in E3
	 * mode). <br/>
	 * You should change the parameter type of your received Object to manage
	 * your specific selection
	 * 
	 * @param o
	 *            : the current object received
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {

		// Remove the 2 following lines in pure E4 mode, keep them in mixed mode
		if (o instanceof ISelection) // Already captured
			return;

		// Test if label exists (inject methods are called before PostConstruct)
		if (myLabelInView != null)
			myLabelInView.setText("Current single selection class is : " + o.getClass());
	}

	/**
	 * This method manages the multiple selection of your current objects. <br/>
	 * You should change the parameter type of your array of Objects to manage
	 * your specific selection
	 * 
	 * @param o
	 *            : the current array of objects received in case of multiple selection
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {

		// Test if label exists (inject methods are called before PostConstruct)
		if (myLabelInView != null)
			myLabelInView.setText("This is a multiple selection of " + selectedObjects.length + " objects");
	}
	
	
	private void addCommandListener() {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		commandService.addExecutionListener(commandListener);
	}
		
	
	public static IAbapSourcePage getSourcPage() {
		IAdtFormEditor edit = linkedObject.getLinkedEditor();
		IAbapSourcePage sourcePage = edit.getAdapter(AbapSourcePage.class);
		return sourcePage;
	}

	public static IAdtFormEditor getEditor() {
		IAdtFormEditor edit = linkedObject.getLinkedEditor();
		return edit;
	}
	
	private void setLinkingWithEditor() {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		workbenchWindow.getPartService().addPartListener(this.linkWithEditorPartListener);
	}
	
	public void createButtons(Composite container) {
    	for (String text : btn_str_list ) {
				Button mybtn = new Button(container, SWT.CHECK);
				 mybtn.setText(text);
//				 mybtn.setLocation(0, 20);
//				 mybtn.setBounds(0, 0, 10, 10);
				 mybtn.addSelectionListener(new SelectionAdapter() {
				        @Override
				        public void widgetSelected(SelectionEvent event) {
				            Button btn = (Button) event.getSource();
				            System.out.println("Listener: "+btn+btn.getSelection());
				        }
				    });
				 ButtonsList.add(mybtn);
 		    }
	}


	@Override
	public void editorActivated(IEditorPart activeEditor) {
		if (isLinkingActive()) {
			IProject LinkedProject = ProjectUtility.getActiveAdtProject();
			if (LinkedProject == null) {
				update();
				return;
			}
			linkedObject = ProjectUtility.getObjectFromEditor(activeEditor);
			if (linkedObject == null || linkedObject.isEmpty()) {
				// if (activeEditor.)
				update();
				return;
			}

		}
	}

	private void update() {
		container.layout();
		container.update();
	}
	

	
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(this.linkWithEditorPartListener);
		removeCommandListener();
		for (Control ctrl : container.getChildren()) {
			System.out.println( "Dispose" );
			ctrl.dispose();
		}
		container = null;
		super.dispose();
	}

	private void removeCommandListener() {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		commandService.removeExecutionListener(commandListener);
	}

	public boolean isLinkingActive() {
		return true;
	}

	public void callEditorActivationWhenNeeded(final boolean linkingActive) {
		if (linkingActive) {
			editorActivated(getSite().getPage().getActiveEditor());
		}
	}

	
	
	
	public void reload() {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					linkedObject = ProjectUtility.getObjectFromEditor();
					update();
				}

			});
	}

	
	
	private void refreshObject() {
		if ((linkedObject != null && linkedObject.isEmpty() && (isLinkingActive()))) {
			IProject LinkedProject = ProjectUtility.getActiveAdtProject();

			linkedObject = ProjectUtility.getObjectFromEditor();
			if (linkedObject == null || linkedObject.isEmpty()) {
				return;
			}
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					update();
				}
			});
		}
	}
	
	
	
	
	
	
	
	
	
}
