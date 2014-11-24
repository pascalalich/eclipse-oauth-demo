package de.alichs.eclipse.oauth;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jface.window.Window;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import de.alichs.eclipse.oauth.internal.Activator;
import de.alichs.eclipse.oauth.internal.OAuthBrowserDialog;

public class TokenHandler {

	private static final TokenHandler INSTANCE = new TokenHandler();

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
			.build();
	private static JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private static final File DATA_STORE_DIRECTORY = new File(Activator
			.getDefault().getStateLocation().toFile(), "store");

	private static final Collection<String> TASKS_SCOPE = Arrays
			.asList("https://www.googleapis.com/auth/tasks");
	private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob:auto";
	private static final String USER_NAME = "pascal";

	private TokenHandler() {
	}

	public static TokenHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * @param clientSecrets
	 * @return the credentials containing the access token or <code>null</code>
	 *         if it the user canceled the operation
	 * @throws IOException
	 */
	public Credential getCredentials(URL clientSecrets) throws IOException {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(clientSecrets.openStream()));
		GoogleAuthorizationCodeFlow authorizationFlow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, secrets, TASKS_SCOPE)
				.setDataStoreFactory(
						new FileDataStoreFactory(DATA_STORE_DIRECTORY)).build();

		Credential credential = authorizationFlow.loadCredential(USER_NAME);
		if (credential != null) {
			System.out.println("Credentials found.");
			return credential;
		}

		GoogleAuthorizationCodeRequestUrl url = authorizationFlow
				.newAuthorizationUrl();
		url.setRedirectUri(REDIRECT_URI);

		OAuthBrowserDialog browserDialog = new OAuthBrowserDialog(
				url.toString());
		int result = browserDialog.open();
		if (result == Window.OK) {
			GoogleAuthorizationCodeTokenRequest tokenRequest = authorizationFlow
					.newTokenRequest(browserDialog.getToken());
			tokenRequest.setRedirectUri(REDIRECT_URI);
			TokenResponse tokenResponse = tokenRequest.execute();

			credential = authorizationFlow.createAndStoreCredential(
					tokenResponse, USER_NAME);
		}
		return credential;
	}

	/**
	 * TODO abstract even further
	 * 
	 * @return the credentials containing the access token or <code>null</code>
	 *         if it the user canceled the operation
	 * @throws IOException
	 */
	public Credential getCredentialsForDropbox() throws IOException {
		GenericUrl tokenServerUrl = new GenericUrl("https://api.dropbox.com/1/oauth2/token");
		String clientId = "zq1o9ikyz89u6ug";
		String clientSecret = "lyuh8ao9uxzuzva";
		HttpExecuteInterceptor clientAuthentication = new ClientParametersAuthentication(clientId, clientSecret);
		String authorizationServerEncodedUrl = "https://www.dropbox.com/1/oauth2/authorize";
		AuthorizationCodeFlow authorizationFlow = new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(), HTTP_TRANSPORT,
				JSON_FACTORY, tokenServerUrl, clientAuthentication, clientId,
				authorizationServerEncodedUrl).setDataStoreFactory(
				new FileDataStoreFactory(DATA_STORE_DIRECTORY)).build();

		String user = "dropbox";
		Credential credential = authorizationFlow.loadCredential(user);
		if (credential != null) {
			System.out.println("Credentials found.");
			return credential;
		}

		AuthorizationCodeRequestUrl url = authorizationFlow
				.newAuthorizationUrl();
		String redirectUri = "http://localhost:7777/dropbox-redirect";
		url.setRedirectUri(redirectUri);

		OAuthBrowserDialog browserDialog = new OAuthBrowserDialog(
				url.toString());
		int result = browserDialog.open();
		if (result == Window.OK) {
			AuthorizationCodeTokenRequest tokenRequest = authorizationFlow
					.newTokenRequest(browserDialog.getToken());
			tokenRequest.setRedirectUri(redirectUri);
			tokenRequest.setScopes(null);
			TokenResponse tokenResponse = tokenRequest.execute();

			credential = authorizationFlow.createAndStoreCredential(
					tokenResponse, user);
		}
		return credential;
	}
}
