package de.alichs.eclipse.oauth.internal;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;

public class OAuthBrowserDialog extends BrowserDialog implements
		LocationListener, ProgressListener, TitleListener {

	private volatile String token;

	public OAuthBrowserDialog(String url) {
		super(800, 600, "Authorization / Authentication", url, false);
	}

	@Override
	protected void initBrowser(Browser browser) {
		browser.addProgressListener(this);
		browser.addLocationListener(this);
		browser.addTitleListener(this);
	}

	@Override
	public void changing(LocationEvent event) {
		System.out.println("Changing: " + event);
	}

	@Override
	public void changed(LocationEvent event) {
		System.out.println("Changed: " + event);
	}

	@Override
	public void changed(ProgressEvent event) {
		System.out.println("Changed: " + event);
	}

	@Override
	public void completed(ProgressEvent event) {
		System.out.println("Completed: " + event);

	}

	@Override
	public void changed(TitleEvent event) {
		System.out.println("Changed: " + event);
		closeIfTitleContainsToken(event.title);
		closeIfTitleContainsFailure(event.title);
	}

	private void closeIfTitleContainsToken(String title) {
		String prefix = "Success code=";
		if (title.startsWith(prefix)) {
			this.token = title.substring(prefix.length());
			setReturnCode(OK);
			close();
		}
	}

	private void closeIfTitleContainsFailure(String title) {
		if (title.equals("Denied error=access_denied")) {
			setReturnCode(CANCEL);
			close();
		}
	}

	public String getToken() {
		return token;
	}
}
