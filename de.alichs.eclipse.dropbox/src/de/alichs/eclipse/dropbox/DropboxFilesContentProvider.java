package de.alichs.eclipse.dropbox;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DropboxFilesContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (hasChildren(inputElement)) {
			return getChildren(inputElement);
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DropboxDirectory) {
			DropboxDirectory dir = (DropboxDirectory) parentElement;
			return dir.getFiles().toArray(
					new AbstractDropboxFile[dir.getFiles().size()]);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof DropboxDirectory) {
			DropboxDirectory dir = (DropboxDirectory) element;
			return dir.getFiles() != null && !dir.getFiles().isEmpty();
		}
		return false;
	}

}
