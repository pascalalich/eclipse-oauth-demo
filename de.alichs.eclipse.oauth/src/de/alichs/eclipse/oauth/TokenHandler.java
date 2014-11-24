package de.alichs.eclipse.oauth;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import de.alichs.eclipse.oauth.internal.Activator;
import de.alichs.eclipse.oauth.internal.OAuthBrowserDialog;

public class TokenHandler {

	private static final TokenHandler INSTANCE = new TokenHandler();
	private static final String GOOGLE_REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob:auto";

	private final HttpTransport httpTransport = new NetHttpTransport.Builder()
			.build();
	private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private final DataStoreFactory dataStoreFactory;

	private TokenHandler() {
		try {
			File dataStoreDirectory = new File(Activator.getDefault()
					.getStateLocation().toFile(), "store");
			dataStoreFactory = new FileDataStoreFactory(dataStoreDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static TokenHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * @param clientId
	 * @param clientSecrets
	 * @return the credentials containing the access token or <code>null</code>
	 *         if it the user canceled the operation
	 * @throws IOException
	 */
	public Credential getCredentials(String clientId, URL clientSecrets)
			throws IOException {
		return getCredentials(clientId, clientSecrets, null);
	}

	/**
	 * @param clientId
	 * @param clientSecrets
	 * @param scopes
	 *            (optional)
	 * @return the credentials containing the access token or <code>null</code>
	 *         if it the user canceled the operation
	 * @throws IOException
	 */
	public Credential getCredentials(String clientId, URL clientSecrets,
			Collection<String> scopes) throws IOException {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(jsonFactory,
				new InputStreamReader(clientSecrets.openStream()));

		AuthorizationCodeFlow authorizationFlow = buildAuthorizationFlow(
				secrets, scopes);

		// TODO currently one account per client possible
		String userName = clientId;
		Credential credential = authorizationFlow.loadCredential(userName);
		if (credential != null) {
			System.out.println("Credentials found for " + userName);
			return credential;
		}

		AuthorizationCodeRequestUrl url = authorizationFlow
				.newAuthorizationUrl();
		url.setRedirectUri(buildRedirectUri(secrets));

		OAuthBrowserDialog browserDialog = new OAuthBrowserDialog(
				url.toString());
		int result = browserDialog.open();
		if (result == Window.OK) {
			AuthorizationCodeTokenRequest tokenRequest = authorizationFlow
					.newTokenRequest(browserDialog.getToken());
			tokenRequest.setRedirectUri(buildRedirectUri(secrets));
			tokenRequest.setScopes(scopes);
			TokenResponse tokenResponse = tokenRequest.execute();

			System.out.println("Storing credentials for " + userName);
			credential = authorizationFlow.createAndStoreCredential(
					tokenResponse, userName);
		}
		return credential;
	}

	private AuthorizationCodeFlow buildAuthorizationFlow(
			GoogleClientSecrets secrets, Collection<String> scopes)
			throws IOException {
		if (isGoogle(secrets)) {
			return buildAuthorizationFlowGoogle(secrets, scopes);
		} else {
			return buildAuthorizationFlowNonGoogle(secrets);
		}
	}

	private AuthorizationCodeFlow buildAuthorizationFlowGoogle(
			GoogleClientSecrets secrets, Collection<String> scopes)
			throws IOException {
		return new GoogleAuthorizationCodeFlow.Builder(httpTransport,
				jsonFactory, secrets, scopes).setDataStoreFactory(
				dataStoreFactory).build();
	}

	private AuthorizationCodeFlow buildAuthorizationFlowNonGoogle(
			GoogleClientSecrets secrets) throws IOException {
		Details secretDetails = secrets.getDetails();
		GenericUrl tokenServerUrl = new GenericUrl(secretDetails.getTokenUri());
		String clientId = secretDetails.getClientId();
		String clientSecret = secretDetails.getClientSecret();
		HttpExecuteInterceptor clientAuthentication = new ClientParametersAuthentication(
				clientId, clientSecret);
		String authorizationUrl = secretDetails.getAuthUri();
		return new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(), httpTransport,
				jsonFactory, tokenServerUrl, clientAuthentication, clientId,
				authorizationUrl).setDataStoreFactory(dataStoreFactory).build();

	}

	private String buildRedirectUri(GoogleClientSecrets secrets) {
		if (isGoogle(secrets)) {
			return buildRedirectUriGoogle(secrets);
		} else {
			return buildRedirectUriNonGoogle(secrets);
		}
	}

	private String buildRedirectUriGoogle(GoogleClientSecrets secrets) {
		return GOOGLE_REDIRECT_URI;
	}

	private String buildRedirectUriNonGoogle(GoogleClientSecrets secrets) {
		return secrets.getDetails().getRedirectUris().get(0);
	}

	private boolean isGoogle(GoogleClientSecrets secrets) {
		return secrets.getDetails().getAuthUri()
				.equals("https://accounts.google.com/o/oauth2/auth");
	}

}
