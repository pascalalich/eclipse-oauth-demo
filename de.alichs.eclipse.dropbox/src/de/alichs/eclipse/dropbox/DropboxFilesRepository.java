package de.alichs.eclipse.dropbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;

public class DropboxFilesRepository {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
			.build();
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private final Credential credential;

	public DropboxFilesRepository(Credential credential) {
		this.credential = credential;
	}

	public AbstractDropboxFile getFile(String path) throws IOException {
		HttpRequest listRequest = HTTP_TRANSPORT.createRequestFactory()
				.buildGetRequest(
						new GenericUrl(
								"https://api.dropbox.com/1/metadata/auto/"));

		listRequest.setInterceptor(credential);

		HttpResponse response = listRequest.execute();
		InputStream contentIn = response.getContent();

		JsonParser jsonParser = JSON_FACTORY.createJsonParser(contentIn);
		Map<?, ?> responseDoc = new LinkedHashMap<>();
		jsonParser.parseAndClose(responseDoc);

		return convert(responseDoc);
	}

	private AbstractDropboxFile convert(Map<?, ?> responseDoc) {
		// {hash=fa0d0ad70caa9855bc54c528289a6d2c, thumb_exists=false,
		// bytes=0, path=/, is_dir=true, icon=folder, root=app_folder,
		// contents=[{bytes=0, rev=12d7934e6, revision=1, icon=folder,
		// path=/Test, is_dir=true, thumb_exists=false, root=app_folder,
		// modified=Mon, 24 Nov 2014 17:11:15 +0000, size=0 bytes}], size=0
		// bytes}
		System.out.println("Response" + responseDoc);

		String path = (String) responseDoc.get("path");
		String name = getName(path);
		boolean isDir = (Boolean) responseDoc.get("is_dir");

		if (isDir) {
			DropboxDirectory directory = new DropboxDirectory();
			directory.setName(name);
			directory.setPath(path);

			List<AbstractDropboxFile> files = new LinkedList<>();
			List<Map<?, ?>> contents = (List<Map<?, ?>>) responseDoc
					.get("contents");
			if (contents != null && contents.size() > 0) {
				for (Map<?, ?> content : contents) {
					files.add(convert(content));
				}
			}
			directory.setFiles(files);
			return directory;
		} else {
			DropboxFile file = new DropboxFile();
			file.setName(name);
			file.setPath(path);
			return file;
		}
	}

	private String getName(String path) {
		int lastSlashPos = path.lastIndexOf('/');
		if (lastSlashPos + 1 < path.length()) {
			return path.substring(lastSlashPos + 1);
		}
		return "";
	}

}
