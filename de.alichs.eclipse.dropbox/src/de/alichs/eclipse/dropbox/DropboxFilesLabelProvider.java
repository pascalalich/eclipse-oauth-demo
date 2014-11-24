package de.alichs.eclipse.dropbox;

import org.eclipse.jface.viewers.LabelProvider;

public class DropboxFilesLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof AbstractDropboxFile) {
			return ((AbstractDropboxFile) element).getName();
		}
		return super.getText(element);
	}

}
