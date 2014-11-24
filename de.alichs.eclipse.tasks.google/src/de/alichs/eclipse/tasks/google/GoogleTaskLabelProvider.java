package de.alichs.eclipse.tasks.google;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class GoogleTaskLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof GoogleTaskList) {
			return ((GoogleTaskList) element).getTitle();
		} else if (element instanceof GoogleTask) {
			return ((GoogleTask) element).getTitle();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		ImageRegistry registry = Activator.getDefault().getImageRegistry();

		if (element instanceof GoogleTask) {
			GoogleTask task = (GoogleTask) element;
			if (task.isDone()) {
				return registry.get("done");
			} else {
				return registry.get("todo");
			}
		}
		return super.getImage(element);
	}
}
