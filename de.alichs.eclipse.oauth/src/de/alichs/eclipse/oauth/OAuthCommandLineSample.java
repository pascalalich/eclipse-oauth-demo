package de.alichs.eclipse.oauth;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class OAuthCommandLineSample {

	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
			.build();
	private static JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private static final Collection<String> TASKS_SCOPE = Arrays
			.asList("https://www.googleapis.com/auth/tasks");
	private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	// private static final String REDIRECT_URI = "http://localhost";
	private static final String USER_NAME = "pascal";

	private static final File CLIENT_SECRETS_FILE = new File(
			"D:/NAK/Google API/client_secret.json");
	private static final File DATA_STORE_DIRECTORY = new File(
			"D:/NAK/Google API/store");

	public static void doit() {
		try {
			Credential credential = authorize();
			listAll(credential);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Credential authorize() throws IOException {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY,
				new FileReader(CLIENT_SECRETS_FILE));
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
		System.out.println("Go to the following address:");
		System.out.println(url);

		System.out.println("Please enter the code:");
		String authorizationCode;
		try (Scanner in = new Scanner(System.in)) {
			authorizationCode = in.nextLine();
		}
		GoogleAuthorizationCodeTokenRequest tokenRequest = authorizationFlow
				.newTokenRequest(authorizationCode);
		tokenRequest.setRedirectUri(REDIRECT_URI);
		TokenResponse tokenResponse = tokenRequest.execute();

		credential = authorizationFlow.createAndStoreCredential(tokenResponse,
				USER_NAME);
		System.out.println("Access token retrieved");
		System.out.println("-------------------------------------------------");
		return credential;
	}

	private static void listAll(Credential credential)
			throws GeneralSecurityException, IOException {
		Tasks tasksService = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				credential).setApplicationName("NAK Task Aggregator").build();
		TaskLists lists = tasksService.tasklists().list().execute();

		System.out.println("All task lists:");
		for (TaskList list : lists.getItems()) {
			System.out.println("- " + list.getTitle());
		}
	}

}
