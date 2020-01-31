package org.tma.peer.thin;

import org.tma.util.Event;

public class NewMessageEvent implements Event {
	
	private SecureMessage secureMessage;

	public NewMessageEvent(SecureMessage secureMessage) {
		this.secureMessage = secureMessage;
	}

	public SecureMessage getSecureMessage() {
		return secureMessage;
	}

}
