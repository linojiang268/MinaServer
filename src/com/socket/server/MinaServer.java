package com.socket.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.tangosol.net.CacheFactory;

/**
 * <b>function:</b> 服务器启动类
 * 
 * @author hoojo
 * @createDate 2012-6-29 下午07:11:00
 * @file MinaServer.java
 * @package com.hoo.mina.server
 * @project ApacheMiNa
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class MinaServer {

	public static SocketAcceptor acceptor;
	private DefaultIoFilterChainBuilder defaultIoFilterChainBuilder;
	private ProtocolCodecFilter protocolCodecFilter;
	private CharsetCodecFactory charsetCodecFactory;
	private ServerMessageHandler serverMessageHandler;

	// private ClientRmi clientRmi;

	public MinaServer() {
		// 创建非阻塞的server端的Socket连接
		acceptor = new NioSocketAcceptor();
	}

	public static SocketAcceptor instance() {
		return acceptor;
	}

	public boolean start() {
		defaultIoFilterChainBuilder = acceptor.getFilterChain();
		charsetCodecFactory = new CharsetCodecFactory();
		protocolCodecFilter = new ProtocolCodecFilter(charsetCodecFactory);
		// 添加编码过滤器 处理乱码、编码问题
		defaultIoFilterChainBuilder.addLast("codec", protocolCodecFilter);

		/*
		 * LoggingFilter loggingFilter = new LoggingFilter();
		 * loggingFilter.setMessageReceivedLogLevel(LogLevel.INFO);
		 * loggingFilter.setMessageSentLogLevel(LogLevel.INFO); // 添加日志过滤器
		 * filterChain.addLast("loger", loggingFilter);
		 */

		// 设置核心消息业务处理器
		serverMessageHandler = new ServerMessageHandler();
		acceptor.setHandler(serverMessageHandler);
		// acceptor.setSessionRecycler(new ExpiringSessionRecycler(120));
		// 设置session配置，30秒内无操作进入空闲状态
		// acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 0);
		acceptor.getSessionConfig().setReadBufferSize(1024);

		try {
			// 初始化
			CacheFactory.getCache("YunduoCache");
			CacheFactory.getService("InvocationService");
			// 绑定端口3456
			acceptor.bind(new InetSocketAddress(4123));
			// ====================================(rmi集群方案===========================================
			// 绑定端口4126
			// RMISocketFactory.setSocketFactory(new MyRMISocketFactory());
			// clientRmi = new ClientRmiImpl(acceptor);
			// LocateRegistry.createRegistry(4126);
			// // String localIp = InetAddress.getLocalHost().getHostAddress();
			// Naming.bind("rmi://localhost:4126/RClient", clientRmi);
			// ====================================rmi集群方案）===========================================
		} catch (IOException e) {
			System.out.println("IOE异常！");
			e.printStackTrace();
			return false;
		}
		// catch (AlreadyBoundException e) {
		// System.out.println("发生重复绑定对象异常！");
		// e.printStackTrace();
		// }
		return true;
	}

	public static void main(String[] args) {
		MinaServer server = new MinaServer();
		if (server.start()) {
			System.out.println("MinaServer服务开启成功");
		} else {
			System.out.println("MinaServer服务开启失败");
		}
	}
}