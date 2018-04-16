package com.socket.server;

import java.io.Serializable;

import com.tangosol.util.UID;

@SuppressWarnings("serial")
public class IoConInfo implements Serializable {
	public UID nodeUID;
	public long iosessionId;

	public IoConInfo(UID nodeUID, long iosessionId) {
		super();
		this.nodeUID = nodeUID;
		this.iosessionId = iosessionId;
	}

	// public void setInfo(UID nodeUID, long iosessionId) {
	// this.nodeUID = nodeUID;
	// this.iosessionId = iosessionId;
	// }
}
