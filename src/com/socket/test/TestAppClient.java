package com.socket.test;

import java.net.InetSocketAddress;
import java.util.Random;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.socket.server.CmdUtil;
import com.socket.server.HexTool;

public class TestAppClient {

	private boolean isQuit = true;
	private SocketConnector connector;
	private ConnectFuture future;
	private IoSession session;

	/**
	 * 
	 * @Title isQuit
	 * @Description 是否已退出
	 * @author ouArea
	 * @date 2013-11-12 下午5:08:11
	 * @return
	 */
	public boolean isQuit() {
		return isQuit;
	}

	/**
	 * 
	 * @Title init
	 * @Description 公网通信初始化
	 * @author ouArea
	 * @date 2013-11-12 下午5:37:12
	 * @param host
	 * @param port
	 * @param wanCallBack
	 * @return
	 */
	public void init(String host, int port) {
		connect(host, port);
	}

	/**
	 * 
	 * @Title connect
	 * @Description 连接
	 * @author ouArea
	 * @date 2013-11-12 下午5:41:53
	 * @param host
	 * @param port
	 */
	private void connect(final String host, final int port) {
		isQuit = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 创建一个socket连接
					connector = new NioSocketConnector();
					// 设置链接超时时间
					connector.setConnectTimeoutMillis(3000);
					connector.getSessionConfig().setReadBufferSize(1024);
					// 获取过滤器链
					DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
					// 添加编码过滤器 处理乱码、编码问题
					filterChain.addLast("codec", new ProtocolCodecFilter(new TCharsetCodecFactory()));

					/*
					 * // 日志 LoggingFilter loggingFilter = new LoggingFilter();
					 * loggingFilter.setMessageReceivedLogLevel(LogLevel.INFO);
					 * loggingFilter.setMessageSentLogLevel(LogLevel.INFO);
					 * filterChain.addLast("loger", loggingFilter);
					 */

					// 消息核心处理器
					connector.setHandler(new ClientMessageHandlerAdapter());

					// 连接服务器，知道端口、地址
					future = connector.connect(new InetSocketAddress(host, port));
					// 等待连接创建完成
					future.awaitUninterruptibly();
					// 获取当前session
					session = future.getSession();
				} catch (Exception e) {
					e.printStackTrace();
					isQuit = true;
				}
			}
		}).start();

	}

	/**
	 * 
	 * @Title quit
	 * @Description 退出公网通信
	 * @author ouArea
	 * @date 2013-11-12 下午5:38:51
	 */
	public void quit() {
		if (null != session) {
			CloseFuture future = session.getCloseFuture();
			future.awaitUninterruptibly(1000);
		}
		if (null != connector) {
			connector.dispose();
		}
		isQuit = true;
	}

	/**
	 * 
	 * @Title send
	 * @Description 公网通信发送数据
	 * @author ouArea
	 * @date 2013-11-12 下午5:25:42
	 * @param sendBytes
	 */
	public void send(byte[] sendBytes) {
		if (isQuit()) {
			return;
		}
		if (null != session) {
			session.write(sendBytes);
		}
		// System.out.println("  wan send  :" +
		// HexTool.bytes2HexString(sendBytes, 0, sendBytes.length));
	}

	// public SocketConnector getConnector() {
	// return connector;
	// }
	//
	// public IoSession getSession() {
	// return session;
	// }

	class ClientMessageHandlerAdapter extends IoHandlerAdapter {

		// private final static Logger log =
		// LoggerFactory.getLogger(ClientMessageHandlerAdapter.class);

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			isQuit = true;
			super.sessionClosed(session);
			System.out.println("wan delete");
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			super.sessionOpened(session);
			System.out.println("wan connect success");
			// String macStr = "macmacmacmac", markStr = "markmark";
			// byte[] macBytes = macStr.getBytes(), markBytes =
			// markStr.getBytes();
			// byte[] sendBytes = new byte[] { 0x0A, 0x21, 0x00, 0x16, 0x01,
			// 0x02, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12,
			// 0x12, 0x12, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x01, 0x02,
			// 0x55 };
			// System.arraycopy(macBytes, 0, sendBytes, 6, 12);
			// System.arraycopy(markBytes, 0, sendBytes, 18, 8);
			// send(sendBytes);
			// 客户端检测在线用
			// String macStr = "macmacmacmac", markStr = "markmark";
			String macStr = "macmacmacmac", markStr = getRandomString(8);
			// String macStr = "021204A0147C", markStr = getRandomString(8);
			final byte[] macBytes = macStr.getBytes();
			final byte[] markBytes = markStr.getBytes();
			byte[] sendBytes = new byte[] { 0x0A, 0x21, 0x00, 0x16, 0x02, 0x02, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x6, 0x6, 0x3, 0x3, 0x3, 0x3, 0x6, 0x6, 0x01, 0x02, 0x55 };
			System.arraycopy(macBytes, 0, sendBytes, 6, 12);
			System.arraycopy(markBytes, 0, sendBytes, 18, 8);
			send(sendBytes);
			final byte[] sendBytes2 = new byte[sendBytes.length];
			System.arraycopy(sendBytes, 0, sendBytes2, 0, sendBytes.length);
			sendBytes2[1] = CmdUtil.CONTROL;
			sendBytes2[1] = CmdUtil.CALL;
			// send(sendBytes2);jhhiy;o;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						// String sendBytes3Str =
						// "0A12001602026D61636D61636D61636D61633730343443353330010255";
						String sendBytes3Str = "0A1201E602026D61636D61636D61636D616337303434433533300606030303030606060303030306060603030303060606030303030606060303030306060603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606030306030303030606060303060303030306060603030603030303060606010255";
						byte[] sendBytes3 = HexTool.hexStringToBytes(sendBytes3Str);
						System.arraycopy(macBytes, 0, sendBytes3, 6, 12);
						System.arraycopy(markBytes, 0, sendBytes3, 18, 8);
						// send(sendBytes3);
						send(new byte[] { 0x0b, 0x0d });
						break;
						// send(sendBytes2);
						// try {
						// Thread.sleep(2000);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
					}
				}
			}).start();
		}

		int i, j;

		public void messageReceived(IoSession session, Object message) throws Exception {
			byte[] recBytes = (byte[]) message;
			System.out.println("wan received:" + HexTool.bytes2HexString(recBytes, 0, recBytes.length));
			// switch (recBytes[1]) {
			// case CmdUtil.LEARN:
			// recBytes[4] = 0x01;
			// byte[] macBytes = "macmacmacmac".getBytes();
			// System.arraycopy(macBytes, 0, recBytes, 6, 12);
			// byte[] sendBytes1 = new byte[recBytes.length];
			// System.arraycopy(recBytes, 0, sendBytes1, 0, recBytes.length);
			// send(sendBytes1);
			// final byte[] markBytes = new byte[8];
			// System.arraycopy(recBytes, 18, markBytes, 0, 8);
			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			// try {
			// Thread.sleep(5000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// byte[] sendBytes = new byte[29 + 8];
			// sendBytes[0] = 0x0A;
			// sendBytes[1] = CmdUtil.LEARN_BACK_SUCCESS;
			// System.arraycopy(HexTool.shortToByteArray((short) (37 - 7)), 0,
			// sendBytes, 2, 2);
			// sendBytes[4] = 0x01;
			// sendBytes[5] = 0x02;
			// byte[] macBytes = "macmacmacmac".getBytes();
			// System.arraycopy(macBytes, 0, sendBytes, 6, 12);
			// System.arraycopy(markBytes, 0, sendBytes, 18, 8);
			// byte[] commandBytes = new byte[] { 0x6, 0x6, 0x3, 0x3, 0x3, 0x3,
			// 0x6, 0x6 };
			// System.arraycopy(commandBytes, 0, sendBytes, 26, 8);
			// sendBytes[sendBytes.length - 3] = 0x01;
			// sendBytes[sendBytes.length - 2] = 0x02;
			// sendBytes[sendBytes.length - 1] = 0x55;
			// send(sendBytes);
			// }
			// }).start();
			// break;
			// case CmdUtil.CALL:
			// recBytes[4] = 0x01;
			// byte[] macBytes3 = "macmacmacmac".getBytes();
			// System.arraycopy(macBytes3, 0, recBytes, 6, 12);
			// recBytes[1] = CmdUtil.CALL;
			// byte[] sendBytes3 = new byte[29];
			// System.arraycopy(recBytes, 0, sendBytes3, 0, 26);
			// System.arraycopy(recBytes, recBytes.length - 3, sendBytes3, 26,
			// 3);
			// send(sendBytes3);
			// break;
			// case CmdUtil.CONTROL:
			// recBytes[4] = 0x01;
			// byte[] macBytes2 = "macmacmacmac".getBytes();
			// System.arraycopy(macBytes2, 0, recBytes, 6, 12);
			// recBytes[1] = CmdUtil.CONTROL_BACK_SUCCESS;
			// byte[] sendBytes = new byte[29];
			// System.arraycopy(recBytes, 0, sendBytes, 0, 26);
			// System.arraycopy(recBytes, recBytes.length - 3, sendBytes, 26,
			// 3);
			// send(sendBytes);
			// break;
			// default:
			// break;
			// }

			if (recBytes[1] == CmdUtil.CALL) {
				System.out.println("收到 " + (++j) + " 次");
			}
			// if (recBytes[1] == CmdUtil.CONTROL_BACK_SUCCESS) {
			// System.out.println("收到 " + (++j) + " 次");
			// }
		}

		public void messageSent(IoSession session, Object message) throws Exception {
			byte[] recBytes = (byte[]) message;
			System.out.println("wan send:" + HexTool.bytes2HexString(recBytes, 0, recBytes.length));
			// if (CmdUtil.CONTROL == recBytes[1]) {
			// System.out.println("发出 " + (++i) + " 次");
			// }
			if (CmdUtil.CALL == recBytes[1]) {
				System.out.println("发出 " + (++i) + " 次");
			}
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			System.out.println("caught" + cause.getMessage());
		}
	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println("模拟 app端 公网部分功能：");
		TestAppClient testLikeDeviceWan = new TestAppClient();
		// testLikeDeviceWan.init("transitserver.ouarea.cc", 4123);
		testLikeDeviceWan.init("swapcloud.indeo.cn", 4123);
		// testLikeDeviceWan.init("192.168.0.116", 4123);
		// testLikeDeviceWan.init("127.0.0.1", 4123);
		// testLikeDeviceWan.init("211.149.186.232", 4123);
		// testLikeDeviceWan.init("211.149.213.97", 4123);
		// testLikeDeviceWan.init("211.149.206.172", 4123);
	}

	public static String markInstance;

	public static String instanceMark() {
		if (null == markInstance) {
			Random random = new Random();
			byte[] bytes = new byte[6];
			random.nextBytes(bytes);
			markInstance = HexTool.bytes2HexString(bytes, 0, bytes.length);
		}
		return markInstance;
	}
}
