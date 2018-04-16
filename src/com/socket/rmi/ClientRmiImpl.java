package com.socket.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketAcceptor;

@SuppressWarnings("serial")
public class ClientRmiImpl extends UnicastRemoteObject implements ClientRmi {
	public SocketAcceptor socketAcceptor;

	public ClientRmiImpl(SocketAcceptor socketAcceptor) throws RemoteException {
		this.socketAcceptor = socketAcceptor;
	}

	@Override
	public void doClientTask(String key, byte[] valueBytes) throws RemoteException {
		Set<Entry<Long, IoSession>> ioSessionEntries = this.socketAcceptor.getManagedSessions().entrySet();
		if (null != ioSessionEntries) {
			for (Entry<Long, IoSession> entry : ioSessionEntries) {
				IoSession ioSession = entry.getValue();
				if (null != ioSession && ioSession.containsAttribute("key") && ioSession.getAttribute("key").equals(key)) {
					if (ioSession.isConnected()) {
						ioSession.write(valueBytes);
						break;
					}
				}
			}
		}
	}

}
