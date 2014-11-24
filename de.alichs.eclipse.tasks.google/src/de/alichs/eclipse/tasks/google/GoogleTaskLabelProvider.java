package de.alichs.eclipse.tasks.google;

import org.eclipse.jface.viewers.LabelProvider;

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

}
