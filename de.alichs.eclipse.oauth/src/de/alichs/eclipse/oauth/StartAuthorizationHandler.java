package de.alichs.eclipse.oauth;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class StartAuthorizationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		OAuthCommandLineSample.doit();
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
