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
		// set default return code to CANCEL
		setReturnCode(CANCEL);
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
		closeIfURLContainsToken(event.location);
		closeIfURLContainsFailure(event.location);
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

	// for Google
	private void closeIfTitleContainsToken(String title) {
		String prefix = "Success code=";
		if (title.startsWith(prefix)) {
			this.token = title.substring(prefix.length());
			setReturnCode(OK);
			close();
		}
	}

	// for Google
	private void closeIfTitleContainsFailure(String title) {
		if (title.equals("Denied error=access_denied")) {
			setReturnCode(CANCEL);
			close();
		}
	}

	// for Dropbox
	private void closeIfURLContainsToken(String location) {
		String prefix = "http://localhost:7777/dropbox-redirect?code=";
		if (location.startsWith(prefix)) {
			this.token = location.substring(prefix.length());
			setReturnCode(OK);
			close();
		}
	}

	// for Dropbox
	private void closeIfURLContainsFailure(String location) {
		String prefix = "http://localhost:7777/dropbox-redirect?error_description=";
		if (location.startsWith(prefix)) {
			this.token = location.substring(prefix.length());
			setReturnCode(CANCEL);
			close();
		}
	}

	public String getToken() {
		return token;
	}
}
