package de.alichs.eclipse.tasks.google;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import com.google.api.client.auth.oauth2.Credential;

import de.alichs.eclipse.oauth.ErrorDialogHelper;
import de.alichs.eclipse.oauth.TokenHandler;

public class GoogleTaskView extends ViewPart {

	public static final String PART_ID = "de.alichs.eclipse.tasks.google";

	private static final URL CLIENT_SECRETS = Activator.getDefault()
			.getBundle().getEntry("client_secret.json");

	private TreeViewer treeViewer;

	public GoogleTaskView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);

		treeViewer = new TreeViewer(composite, SWT.BORDER);
		treeViewer.setContentProvider(new GoogleTaskContentProvider());
		treeViewer.setLabelProvider(new GoogleTaskLabelProvider());
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		refreshTree();
	}

	private void refreshTree() {
		// Job job = new Job("Loading Google tasks...") {
		// @Override
		// protected IStatus run(IProgressMonitor monitor) {
		Credential credential;
		try {
			credential = getCredentials();
			if (credential != null) {
				GoogleTaskRepository taskRepository = new GoogleTaskRepository(
						credential);
				List<GoogleTaskList> lists = taskRepository.findAllTaskLists();
				updateTree(lists);

			}
		} catch (IOException e) {
			// return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
			// "Failed to load Google tasks", e);
			ErrorDialogHelper.openErrorDialog("Google Tasks",
					"Failed to load Google tasks", e);
		}

		// return Status.OK_STATUS;
		// }
		// };
		// job.addJobChangeListener(new JobChangeAdapter() {
		//
		// });
		// job.schedule();
	}

	private void updateTree(final List<GoogleTaskList> lists) {
		getViewSite().getWorkbenchWindow().getShell().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						treeViewer.setInput(lists);
					}
				});
	}

	@Override
	public void setFocus() {
		treeViewer.getTree().setFocus();
	}

	private Credential getCredentials() throws IOException {
		TokenHandler tokenHandler = TokenHandler.getInstance();
		return tokenHandler.getCredentials(CLIENT_SECRETS);
	}
}
