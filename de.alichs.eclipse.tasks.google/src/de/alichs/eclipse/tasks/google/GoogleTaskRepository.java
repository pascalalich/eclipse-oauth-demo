package de.alichs.eclipse.tasks.google;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class GoogleTaskRepository {

	private final Credential credential;

	public GoogleTaskRepository(Credential credential) {
		this.credential = credential;
	}

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
			.build();
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	public List<GoogleTaskList> findAllTaskLists() throws IOException {
		Tasks tasksService = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				credential).setApplicationName("NAK Task Aggregator").build();
		TaskLists gLists = tasksService.tasklists().list().execute();

		List<GoogleTaskList> lists = new LinkedList<>();
		for (TaskList gList : gLists.getItems()) {
			GoogleTaskList list = new GoogleTaskList();
			list.setTitle(gList.getTitle());
			list.setTasks(findTasksForList(gList.getId()));
			lists.add(list);
		}
		return lists;
	}

	private List<GoogleTask> findTasksForList(String listId) throws IOException {
		Tasks tasksService = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				credential).setApplicationName("NAK Task Aggregator").build();
		com.google.api.services.tasks.model.Tasks gTasks = tasksService.tasks()
				.list(listId).execute();
		List<GoogleTask> tasks = new LinkedList<>();
		for (Task gTask : gTasks.getItems()) {
			String title = gTask.getTitle();
			if (!title.trim().isEmpty()) {
				GoogleTask task = new GoogleTask();
				task.setTitle(title);
				tasks.add(task);
			}
		}
		return tasks;
	}
}
