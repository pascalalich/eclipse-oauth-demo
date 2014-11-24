package de.alichs.eclipse.dropbox;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class DropboxFilesLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof AbstractDropboxFile) {
			return ((AbstractDropboxFile) element).getName();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		ImageRegistry registry = Activator.getDefault().getImageRegistry();

		if (element instanceof DropboxDirectory) {
			return registry.get("dir");
		} else if (element instanceof DropboxFile) {
			return registry.get("file");
		}
		return super.getImage(element);
	}
}
