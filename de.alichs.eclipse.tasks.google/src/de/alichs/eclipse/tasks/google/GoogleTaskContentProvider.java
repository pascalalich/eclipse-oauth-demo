package de.alichs.eclipse.tasks.google;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class GoogleTaskContentProvider implements ITreeContentProvider {

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
		if (inputElement instanceof List) {
			List<?> list = (List<?>) inputElement;
			return list.toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof GoogleTaskList) {
			GoogleTaskList list = (GoogleTaskList) parentElement;
			return getElements(list.getTasks());
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof GoogleTaskList) {
			GoogleTaskList list = (GoogleTaskList) element;
			return list.getTasks() != null && !list.getTasks().isEmpty();
		}
		return false;
	}
}
