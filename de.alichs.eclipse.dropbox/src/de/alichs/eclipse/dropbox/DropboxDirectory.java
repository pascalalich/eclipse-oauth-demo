package de.alichs.eclipse.dropbox;

import java.util.ArrayList;
import java.util.List;

public class DropboxDirectory extends AbstractDropboxFile {


	private List<AbstractDropboxFile> files = new ArrayList<>(0);

	public List<AbstractDropboxFile> getFiles() {
		return files;
	}

	public void setFiles(List<AbstractDropboxFile> files) {
		this.files = files;
	}

}
