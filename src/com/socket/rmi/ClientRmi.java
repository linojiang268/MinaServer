package com.socket.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRmi extends Remote {
	public void doClientTask(String key, byte[] valueBytes) throws RemoteException;
}
