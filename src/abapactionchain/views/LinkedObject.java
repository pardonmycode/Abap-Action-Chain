package abapactionchain.views;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorPart;

import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;
import com.sap.adt.tools.core.ui.editors.IAdtEditor;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;

import abapactionchain.utils.ProjectUtility;

@SuppressWarnings({ "restriction", "unused" })
public class LinkedObject {
	private IEditorPart linkedEditor;
	private String name;
	private String type;
	private IProject project;
	private String parentName;
	private String parentType;
	private URI parentUri = null;

	public LinkedObject(IEditorPart linkedEditor, IProject project, IAbapSourcePage sourcePage) {
		this.linkedEditor 	= linkedEditor;
		setName();
		setType();
		setProject(project);
		
	}



	public String getName() {
		return name;
	}

	private void setName() {
		if (getLinkedEditor() != null) {
			IAdtObject model = getLinkedEditor().getModel();
			if (model != null) {
				this.name = model.getName();
			}

		}
	}

	public String getType() {
		return type;

	}

	private void setType() {
		if (getLinkedEditor() != null) {
			if (getLinkedEditor().getModel() != null) {
				this.type = getLinkedEditor().getModel().getType();
				if (this.type.equals("PROG/I")) { // || this.type.equals("FUGR/I")) {
					this.type = "REPS";
				}
			}

		}
	}

	public IProject getProject() {
		if (project == null) {
			project = ProjectUtility.getActiveAdtProject();
		}
		if (project == null) {
			project = ProjectUtility.getActiveEditor().getAdapter(IProject.class);
			if (project == null) {
				IResource resource = ProjectUtility.getActiveEditor().getAdapter(IResource.class);
				if (resource != null) {
					project = resource.getProject();
				}
			}

		}
		return project;
	}

	private void setProject(IProject project) {
		this.project = project;
	}

	public boolean isEmpty() {
		if (name == null || name.isEmpty() || type == null || type.isEmpty() || project == null) {
			return true;
		}
		return false;
	}

	public boolean equals(LinkedObject comparedObject) {
		if (comparedObject == null) {
			return false;
		}
		if (getName().equals(comparedObject.getName()) && getType().equals(comparedObject.getType())
				&& getProject().equals(comparedObject.getProject())) {
			return true;
		}
		return false;
	}

	public boolean equals(IAdtObject adtObject, IProject project) {

		if (adtObject == null) {
			return false;
		}

		if (getName().equals(adtObject.getName())
				&& (getType().equals(adtObject.getType())
						|| (getType().equals("REPS") && adtObject.getType().equals("PROG/I"))
						|| (getType().equals("CLAS/OC") && adtObject.getType().equals("PROG/I"))
						|| (getType().equals("REPS") && adtObject.getType().equals("FUGR/I"))
						|| (getType().equals("PROG/I") && adtObject.getType().equals("FUGR/I"))
						|| (getType().equals("FUGR/I") && adtObject.getType().equals("PROG/I"))
						|| (getType().equals("FUGR/I") && adtObject.getType().equals("REPS"))
						|| (getType().equals("PROG/I") && adtObject.getType().equals("REPS")))
				&& getProject().equals(project)) {
			return true;
		}
		
		return false;

	}

	public String getParentName() {
		return parentName;
	}



	public void setType(String masterType) {
		type = masterType;

	}

	public URI getParentUri() {
		return parentUri;
	}

	private void setParentUri(URI parentUri) {
		this.parentUri = parentUri;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public IAdtFormEditor getLinkedEditor() {
		return (IAdtFormEditor) linkedEditor;
	}

}
