package com.socket.server;

import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

@SuppressWarnings("serial")
public class SessionMap extends HashMap<String, IoSession> {
	public void addIoSession(String key, IoSession ioSession) {
		if (this.containsKey(key)) {
			this.put(key, ioSession);
		}
	}

	public IoSession findIoSession(String key) {
		if (this.containsKey(key)) {
			return this.get(key);
		}
		return null;
	}

	public void delIoSession(String key) {
		if (this.containsKey(key)) {
			this.remove(key);
		}
	}
	// private static HashMap<Integer, ArrayList<IoSession>> cliLocation;

	// public SessionMap() {
	// cliLocation = new HashMap<Integer, ArrayList<IoSession>>();
	// }

	// public void addCli(IoSession session, String s) {
	// String[] sa = s.split(" ");
	// ArrayList<IoSession> tempList = new ArrayList<IoSession>();
	//
	// for (int i = 0; i < sa.length; i++) {
	// int key = Integer.parseInt(sa[i]);
	// tempList.clear();
	// if (cliLocation.containsKey(key)) {
	// if (cliLocation.get(key) != null)
	// tempList = cliLocation.get(key);
	// } else {
	// tempList.add(session);
	// cliLocation.put(key, tempList);
	// continue;
	// }
	// tempList.add(session);
	// cliLocation.put(key, tempList);
	// }
	// }

	// public ArrayList<IoSession> getCli(int receiver) {
	// try {
	// if (cliLocation.containsKey(receiver)) {
	// return cliLocation.get(receiver);
	// } else {
	// return null;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	// public void delCli(IoSession session) {
	// ArrayList<IoSession> tempList = new ArrayList<IoSession>();
	// for (Integer inte : cliLocation.keySet()) {
	// tempList = cliLocation.get(inte);
	// if (tempList.contains(session)) {
	// tempList.remove(session);
	// }
	// }
	// }

}
