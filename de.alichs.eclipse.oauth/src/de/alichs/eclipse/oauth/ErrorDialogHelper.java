package de.alichs.eclipse.oauth;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.alichs.eclipse.oauth.internal.Activator;

public class ErrorDialogHelper {

	public static void openErrorDialog(String title, String message, Exception e) {

		Shell parent = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		IStatus status = buildStatus(message, e);
		ErrorDialog.openError(parent, title, null, status);
	}

	private static Status buildStatus(String message, Exception e) {
		if (e == null) {
			return buildNormalStatus(message);
		} else {
			return buildStatusForException(message, e);
		}
	}

	private static Status buildNormalStatus(String message) {
		return new Status(Status.ERROR, Activator.PLUGIN_ID, message);
	}

	private static Status buildStatusForException(String message, Exception e) {
		ArrayList<IStatus> children = new ArrayList<>();
		for (String element : toString(e)) {
			children.add(buildNormalStatus(element));
		}

		return new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
				children.toArray(new IStatus[children.size()]), message, null);
	}

	private static String[] toString(Exception e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.getBuffer().toString().split("\\n");
	}
}
