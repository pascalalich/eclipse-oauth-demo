package de.alichs.eclipse.tasks.google;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

import com.google.api.client.auth.oauth2.Credential;

import de.alichs.eclipse.oauth.TokenHandler;

public class ShowGoogleTasksHandler extends AbstractHandler {

	private static final URL CLIENT_SECRETS = Activator.getDefault()
			.getBundle().getEntry("client_secret.json");

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Credential credential;
		try {
			credential = getCredentials();
			if (credential != null) {
				GoogleTaskRepository taskRepository = new GoogleTaskRepository(
						credential);
				showLists(taskRepository.findAllTaskLists());
			}
		} catch (IOException e) {
			throw new ExecutionException("Could not load tasks", e);
		}
		return null;
	}

	private void showLists(List<GoogleTaskList> lists) {
		MessageDialog.openInformation(null, "Tasks lists", lists.toString());
	}

	private Credential getCredentials() throws IOException {
		TokenHandler tokenHandler = TokenHandler.getInstance();
		return tokenHandler.getCredentials(CLIENT_SECRETS);
	}
}