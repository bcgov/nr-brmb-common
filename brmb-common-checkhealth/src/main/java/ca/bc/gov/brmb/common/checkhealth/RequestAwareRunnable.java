package ca.bc.gov.brmb.common.checkhealth;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Eugene Kuleshov
 */
public abstract class RequestAwareRunnable implements Runnable {
	
	private final RequestAttributes requestAttributes;
	private Thread thread;

	public RequestAwareRunnable() {
		this.requestAttributes = RequestContextHolder.getRequestAttributes();
		this.thread = Thread.currentThread();
	}

	@Override
	public final void run() {
		try {
			RequestContextHolder.setRequestAttributes(requestAttributes);
			onRun();
		} finally {
			if (Thread.currentThread() != thread) {
				RequestContextHolder.resetRequestAttributes();
			}
			thread = null;
		}
	}

	protected abstract void onRun();
}