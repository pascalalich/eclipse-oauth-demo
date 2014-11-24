package de.alichs.eclipse.tasks.google;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowGoogleTasksHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(GoogleTaskView.PART_ID);
		} catch (PartInitException e) {
			throw new ExecutionException("Could not open Google task view", e);
		}
		return null;
	}
}