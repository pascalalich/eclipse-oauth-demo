package de.alichs.eclipse.dropbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import de.alichs.eclipse.oauth.TokenHandler;

public class ShowDropboxFilesHandler extends AbstractHandler {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
			.build();
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		TokenHandler tokenHandler = TokenHandler.getInstance();
		try {
			Credential credential = tokenHandler.getCredentialsForDropbox();
			System.out.println("Retrieved credentials: " + credential);

			HttpRequest listRequest = HTTP_TRANSPORT
					.createRequestFactory()
					.buildGetRequest(
							new GenericUrl(
									"https://api.dropbox.com/1/metadata/auto/"));

			listRequest.setInterceptor(credential);

			HttpResponse response = listRequest.execute();
			InputStream contentIn = response.getContent();

			JsonParser jsonParser = JSON_FACTORY.createJsonParser(contentIn);
			LinkedHashMap responseDoc = new LinkedHashMap();
			jsonParser.parseAndClose(responseDoc);

			// {hash=fa0d0ad70caa9855bc54c528289a6d2c, thumb_exists=false,
			// bytes=0, path=/, is_dir=true, icon=folder, root=app_folder,
			// contents=[{bytes=0, rev=12d7934e6, revision=1, icon=folder,
			// path=/Test, is_dir=true, thumb_exists=false, root=app_folder,
			// modified=Mon, 24 Nov 2014 17:11:15 +0000, size=0 bytes}], size=0
			// bytes}
			System.out.println("Response" + responseDoc);

		} catch (IOException e) {
			throw new ExecutionException("Could not access Dropbox", e);
		}

		return null;
	}

}
