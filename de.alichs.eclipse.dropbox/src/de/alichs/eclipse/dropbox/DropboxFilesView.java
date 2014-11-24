package de.alichs.eclipse.dropbox;

import java.io.IOException;

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

public class DropboxFilesView extends ViewPart {

	public static final String PART_ID = "de.alichs.eclipse.dropbox";

	private TreeViewer treeViewer;

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
		treeViewer.setContentProvider(new DropboxFilesContentProvider());
		treeViewer.setLabelProvider(new DropboxFilesLabelProvider());
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		refreshTree();
	}

	private void refreshTree() {
		TokenHandler tokenHandler = TokenHandler.getInstance();
		try {
			Credential credential = tokenHandler.getCredentialsForDropbox();
			DropboxFilesRepository repository = new DropboxFilesRepository(
					credential);
			DropboxDirectory rootDir = (DropboxDirectory) repository
					.getFile("/");
			updateTree(rootDir);
		} catch (IOException e) {
			ErrorDialogHelper.openErrorDialog("Dropbox Files",
					"Could not access Dropbox", e);
		}

	}

	private void updateTree(final DropboxDirectory dir) {
		getViewSite().getWorkbenchWindow().getShell().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						treeViewer.setInput(dir);
					}
				});
	}

	@Override
	public void setFocus() {
		treeViewer.getTree().setFocus();
	}

}
