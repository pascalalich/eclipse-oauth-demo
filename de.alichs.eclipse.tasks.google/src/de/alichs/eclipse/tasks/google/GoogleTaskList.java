package de.alichs.eclipse.tasks.google;

import java.util.List;

public class GoogleTaskList {

	private String title;

	private List<GoogleTask> tasks;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<GoogleTask> getTasks() {
		return tasks;
	}

	public void setTasks(List<GoogleTask> tasks) {
		this.tasks = tasks;
	}

	@Override
	public String toString() {
		return title + " " + tasks;
	}

}
