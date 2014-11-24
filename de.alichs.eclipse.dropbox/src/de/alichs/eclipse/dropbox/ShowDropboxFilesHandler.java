package de.alichs.eclipse.dropbox;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.google.api.client.auth.oauth2.Credential;

import de.alichs.eclipse.oauth.TokenHandler;

public class ShowDropboxFilesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		TokenHandler tokenHandler = TokenHandler.getInstance();
		try {
			Credential credential = tokenHandler.getCredentialsForDropbox();
			System.out.println("Retrieved credentials: "+credential);
			
		} catch (IOException e) {
			throw new ExecutionException("Could not access Dropbox", e);
		}

		return null;
	}

}
