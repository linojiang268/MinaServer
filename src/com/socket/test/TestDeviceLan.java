package com.socket.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.socket.server.CmdUtil;
import com.socket.server.HexTool;

public class TestDeviceLan {

	public static void main(String[] args) {
		System.out.println("模拟 设备端 局域网 部分功能：");
		// DatagramSocket sendDatagramSocket = null;
		// try {
		// sendDatagramSocket = new DatagramSocket();
		// } catch (SocketException e1) {
		// e1.printStackTrace();
		// }
		// ============================
		byte[] data = new byte[1024];
		DatagramSocket datagramSocket = null;
		DatagramPacket packet = null;
		try {
			datagramSocket = new DatagramSocket(6666);
			// packet = new DatagramPacket(data, data.length);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				packet = new DatagramPacket(data, data.length);
				datagramSocket.receive(packet);
				// =====
				System.out.println(packet.getAddress().getHostAddress() + packet.getPort() + "device:" + HexTool.bytes2HexString(packet.getData(), 0, packet.getLength()));
				// =====
				if (0x01 != packet.getData()[5]) {
					continue;
				}
				if (0x01 == packet.getData()[4]) {
					// 设备发出
					System.out.println("device:" + HexTool.bytes2HexString(packet.getData(), 0, packet.getLength()));
					if (CmdUtil.LEARN_BACK_SUCCESS != packet.getData()[1]) {
						System.out.println("------------------------------------------------------");
					}
				} else if (0x02 == packet.getData()[4]) {
					// app发出
					System.out.println("  app :" + HexTool.bytes2HexString(packet.getData(), 0, packet.getLength()));
					byte[] macBytes;
					if (CmdUtil.LEARN_BACK_SUCCESS == packet.getData()[1]) {
						System.out.println("------------------------------------------------------");
					}
					switch (packet.getData()[1]) {
					case CmdUtil.CALL:
						packet.getData()[4] = 0x01;
						macBytes = "macmacmacmac".getBytes();
						System.arraycopy(macBytes, 0, packet.getData(), 6, 12);
						// sendDatagramSocket.send(new
						// DatagramPacket(packet.getData(), 0,
						// packet.getLength(), new
						// InetSocketAddress("255.255.255.255", 6666)));
						sendDatagramSocket(packet.getAddress().getHostAddress(), packet.getData(), packet.getLength());
						break;
					case CmdUtil.LEARN:
					case CmdUtil.RADIO315_LEARN:
					case CmdUtil.RADIO433_LEARN:
						final byte[] learnBackSuccessByte = new byte[1];
						if (packet.getData()[1] == CmdUtil.RADIO315_LEARN) {
							learnBackSuccessByte[0] = CmdUtil.RADIO315_LEARN_BACK_SUCCESS;
						} else if (packet.getData()[1] == CmdUtil.RADIO433_LEARN) {
							learnBackSuccessByte[0] = CmdUtil.RADIO433_LEARN_BACK_SUCCESS;
						} else {
							learnBackSuccessByte[0] = CmdUtil.LEARN_BACK_SUCCESS;
						}
						packet.getData()[4] = 0x01;
						macBytes = "macmacmacmac".getBytes();
						System.arraycopy(macBytes, 0, packet.getData(), 6, 12);
						// sendDatagramSocket.send(new
						// DatagramPacket(packet.getData(), 0,
						// packet.getLength(), new
						// InetSocketAddress("255.255.255.255", 6666)));
						sendDatagramSocket(packet.getAddress().getHostAddress(), packet.getData(), packet.getLength());
						// final DatagramSocket tmpSendDatagramSocket =
						// sendDatagramSocket;
						final String ip = packet.getAddress().getHostAddress();
						final byte[] markBytes = new byte[8];
						System.arraycopy(packet.getData(), 18, markBytes, 0, 8);
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								// byte[] commandBytes = new byte[] { 0x6, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6 };
								// byte[] commandBytes = new byte[] { 0x6, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3,
								// 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
								// 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
								// 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3,
								// 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6,
								// 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6,
								// 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6,
								// 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
								// 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3,
								// 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3 };
								byte[] commandBytes = new byte[] { 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3,
										0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3,
										0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3,
										0x3, 0x6, 0x6, 0x6, 0x3, 0x3, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x6 };
								byte[] sendBytes = new byte[29 + commandBytes.length];
								sendBytes[0] = 0x0A;
								// sendBytes[1] = CmdUtil.LEARN_BACK_SUCCESS;
								sendBytes[1] = learnBackSuccessByte[0];
								System.arraycopy(HexTool.shortToByteArray((short) (37 - 7)), 0, sendBytes, 2, 2);
								sendBytes[4] = 0x01;
								sendBytes[5] = 0x01;
								byte[] macBytes = "macmacmacmac".getBytes();
								System.arraycopy(macBytes, 0, sendBytes, 6, 12);
								System.arraycopy(markBytes, 0, sendBytes, 18, 8);
								// byte[] commandBytes = new byte[] { 0x6, 0x6,
								// 0x3, 0x3, 0x3, 0x3, 0x6, 0x6 };
								System.arraycopy(commandBytes, 0, sendBytes, 26, commandBytes.length);
								sendBytes[sendBytes.length - 3] = 0x01;
								sendBytes[sendBytes.length - 2] = 0x02;
								sendBytes[sendBytes.length - 1] = 0x55;
								// tmpSendDatagramSocket.send(new
								// DatagramPacket(sendBytes, 0,
								// sendBytes.length, new
								// InetSocketAddress("255.255.255.255",
								// 6666)));
								TestDeviceLan.sendDatagramSocket(ip, sendBytes, sendBytes.length);
							}
						}).start();
						break;
					case CmdUtil.CONTROL:
					case CmdUtil.RADIO315_CONTROL:
					case CmdUtil.RADIO433_CONTROL:
						packet.getData()[4] = 0x01;
						macBytes = "macmacmacmac".getBytes();
						System.arraycopy(macBytes, 0, packet.getData(), 6, 12);
						packet.getData()[1] = CmdUtil.CONTROL_BACK_SUCCESS;
						byte[] sendBytes = new byte[29];
						System.arraycopy(packet.getData(), 0, sendBytes, 0, 26);
						System.arraycopy(packet.getData(), packet.getLength() - 3, sendBytes, 26, 3);
						// sendDatagramSocket.send(new DatagramPacket(sendBytes,
						// 0, sendBytes.length, new
						// InetSocketAddress("255.255.255.255", 6666)));
						sendBytes[2] = 0x00;
						sendBytes[3] = 0x16;
						sendDatagramSocket(packet.getAddress().getHostAddress(), sendBytes, sendBytes.length);
						break;
					case CmdUtil.TEMPERATURE:
						byte[] sendBytes1 = new byte[29 + 2];
						sendBytes1[0] = 0x0A;
						sendBytes1[1] = CmdUtil.TEMPERATURE_BACK_SUCCESS;
						System.arraycopy(HexTool.shortToByteArray((short) (31 - 7)), 0, sendBytes1, 2, 2);
						sendBytes1[4] = 0x01;
						sendBytes1[5] = 0x01;
						byte[] macBytes1 = "macmacmacmac".getBytes();
						System.arraycopy(macBytes1, 0, sendBytes1, 6, 12);
						System.arraycopy(packet.getData(), 18, sendBytes1, 18, 8);
						byte[] commandBytes = new byte[] { 0x00, 0x20 };
						System.arraycopy(commandBytes, 0, sendBytes1, 26, 2);
						sendBytes1[sendBytes1.length - 3] = 0x01;
						sendBytes1[sendBytes1.length - 2] = 0x02;
						sendBytes1[sendBytes1.length - 1] = 0x55;
						// tmpSendDatagramSocket.send(new
						// DatagramPacket(sendBytes, 0,
						// sendBytes.length, new
						// InetSocketAddress("255.255.255.255",
						// 6666)));
						TestDeviceLan.sendDatagramSocket(packet.getAddress().getHostAddress(), sendBytes1, sendBytes1.length);
						break;
					default:
						break;
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void sendDatagramSocket(String ip, byte[] sendBytes, int len) {
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		try {
			datagramSocket.send(new DatagramPacket(sendBytes, 0, len, new InetSocketAddress(ip, 6666)));
			if (len > 1) {
				System.out.println("device:" + HexTool.bytes2HexString(sendBytes, 0, len) + " " + ip);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != datagramSocket) {
				datagramSocket.close();
			}
		}
	}
}