package com.socket.server;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.tangosol.coherence.component.net.Member;
import com.tangosol.net.AbstractInvocable;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.InvocationService;
import com.tangosol.net.NamedCache;

/**
 * <b>function:</b> 处理服务器端消息
 * 
 * @author hoojo
 * @createDate 2012-6-26 下午01:12:34
 * @file ServerMessageHandler.java
 * @package com.hoo.mina.server.message
 * @project ApacheMiNa
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class ServerMessageHandler implements IoHandler {

	// private final static SessionMap sessionMap = new SessionMap();

	// private final static Logger log =
	// LoggerFactory.getLogger(ServerMessageHandler.class);
	// private byte[] macBytes = new byte[12];
	// private byte[] markBytes = new byte[8];

	// @Override
	// public void exceptionCaught(IoSession session, Throwable cause) throws
	// Exception {
	// // log.info("服务器发生异常： {}", cause.getMessage());
	// }

	// @Override
	// public void messageReceived(IoSession session, Object message) throws
	// Exception {
	// // log.info("服务器接收到数据： {}", message);
	// String content = message.toString();
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	// String datetime = sdf.format(new Date());
	//
	// // log.info("转发 messageReceived: " + datetime + "\t" + content);
	//
	// // 拿到所有的客户端Session
	// Collection<IoSession> sessions =
	// session.getService().getManagedSessions().values();
	// // 向所有客户端发送数据
	// for (IoSession sess : sessions) {
	// sess.write(datetime + "\t" + content);
	// }
	// }

	// @Override
	// public void messageSent(IoSession session, Object message) throws
	// Exception {
	// // log.info("服务器发送消息： {}", message);
	// }

	// @Override
	// public void sessionClosed(IoSession session) throws Exception {
	// // log.info("关闭当前session：{}#{}", session.getId(),
	// // session.getRemoteAddress());
	//
	// CloseFuture closeFuture = session.close(true);
	// closeFuture.addListener(new IoFutureListener<IoFuture>() {
	// public void operationComplete(IoFuture future) {
	// if (future instanceof CloseFuture) {
	// ((CloseFuture) future).setClosed();
	// // log.info("sessionClosed CloseFuture setClosed-->{},",
	// // future.getSession().getId());
	// System.out.println("sessionClosed CloseFuture setClosed-->{}," +
	// future.getSession().getId());
	// }
	// }
	// });
	// }

	// @Override
	// public void sessionCreated(IoSession session) throws Exception {
	// System.out.println("创建一个新连接：{}" + session.getRemoteAddress());
	// // log.info("创建一个新连接：{}", session.getRemoteAddress());
	// // session.write("welcome to the chat room !");
	// }

	// @Override
	// public void sessionIdle(IoSession session, IdleStatus status) throws
	// Exception {
	// // log.info("当前连接{}处于空闲状态：{}", session.getRemoteAddress(), status);
	// System.out.println("当前连接{}处于空闲状态：{}" + session.getRemoteAddress() +
	// status);
	// }

	// @Override
	// public void sessionOpened(IoSession session) throws Exception {
	// // log.info("打开一个session：{}#{}", session.getId(),
	// // session.getBothIdleCount());
	// System.out.println("打开一个session：{}#{}" + session.getId() +
	// session.getBothIdleCount());
	// }

	// ============================================================================
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// System.out.println("**read--");
		final byte[] commandBytes = (byte[]) message;
		// System.out.println(HexTool.bytes2HexString(commandBytes, 0,
		// commandBytes.length));
		// 数据不为空，且总长度不少于29位，第一位为0x0A，最后一位为0x55
		int commandLength = commandBytes.length;
		if (commandLength < 29 || 0x0A != commandBytes[0] || 0x55 != commandBytes[commandLength - 1]) {
			if (commandLength > 0) {
				byte[] heartBytes = new byte[1];
				heartBytes[0] = commandBytes[0];
				session.write(heartBytes);
			}
			return;
		}
		// if (true) {
		// return;
		// }
		// 网络类型不是外网，不处理
		if (0x02 != commandBytes[5]) {
			return;
		}
		// 相当于登录指令，配置连接标识
		if (0x21 == commandBytes[1]) {
			// 第五位为0x01芯片发出，0x02为客户端发出
			if (0x01 == commandBytes[4]) {
				byte[] macBytes = new byte[12];
				System.arraycopy(commandBytes, 6, macBytes, 0, 12);
				String mac = new String(macBytes);
				session.setAttribute("key", mac);
				session.setAttribute("type", "1");
				// ====================================（rmi集群方案===========================================
				// String localIp = InetAddress.getLocalHost().getHostAddress();
				// CacheFactory.getCache("YunduoCache").put(mac, localIp);
				// ====================================rmi集群方案）===========================================
				// ====================================（invocation集群方案===========================================
				NamedCache cache = CacheFactory.getCache("YunduoCache");
				if (null != cache) {
					// 序列化后无法引用更改
					// if (cache.containsKey(mac)) {
					// ((IoConInfo)
					// cache.get(mac)).setInfo(cache.getCacheService().getCluster().getLocalMember().getUid(),
					// session.getId());
					// } else {
					cache.put(mac, new IoConInfo(cache.getCacheService().getCluster().getLocalMember().getUid(), session.getId()));
					// }
				}
				// ====================================invocation集群方案)===========================================
				// 原样返回
				// commandBytes[4] = 0x03;
				byte[] sendBytes = new byte[43];
				System.arraycopy(commandBytes, 0, sendBytes, 0, 26);
				System.arraycopy(commandBytes, 26, sendBytes, 40, 3);
				sendBytes[4] = 0x03;
				sendBytes[2] = 0x00;
				sendBytes[3] = 0x24;
				String timeStr = TimeUtil.getDataBaseString(Calendar.getInstance().getTime());
				byte[] timeBytes = timeStr.getBytes();
				System.arraycopy(timeBytes, 0, sendBytes, 26, 14);
				// session.write(commandBytes);
				session.write(sendBytes);
			} else if (0x02 == commandBytes[4]) {
				byte[] markBytes = new byte[8];
				System.arraycopy(commandBytes, 18, markBytes, 0, 8);
				String mark = new String(markBytes);
				session.setAttribute("key", mark);
				session.setAttribute("type", "2");
				// ====================================（rmi集群方案）===========================================
				// String localIp = InetAddress.getLocalHost().getHostAddress();
				// CacheFactory.getCache("YunduoCache").put(mark, localIp);
				// ====================================rmi集群方案）===========================================
				// ====================================（invocation集群方案===========================================
				NamedCache cache = CacheFactory.getCache("YunduoCache");
				if (null != cache) {
					// 序列化后无法引用更改
					// if (cache.containsKey(mark)) {
					// ((IoConInfo)
					// cache.get(mark)).setInfo(cache.getCacheService().getCluster().getLocalMember().getUid(),
					// session.getId());
					// } else {
					cache.put(mark, new IoConInfo(cache.getCacheService().getCluster().getLocalMember().getUid(), session.getId()));
					// }
				}
				// ====================================invocation集群方案)===========================================
				// 原样返回
				commandBytes[4] = 0x03;
				session.write(commandBytes);
			}
		} else {
			if (0x01 == commandBytes[4]) {
				byte[] markBytes = new byte[8];
				System.arraycopy(commandBytes, 18, markBytes, 0, 8);
				String mark = new String(markBytes);
				// ====================================（rmi集群方案）===========================================
				// byte[] macBytes = new byte[12];
				// System.arraycopy(commandBytes, 6, macBytes, 0, 12);
				// String mac = new String(macBytes);
				// session.setAttribute("key", mac);
				// String localIp = InetAddress.getLocalHost().getHostAddress();
				// CacheFactory.getCache("YunduoCache").put(mac, localIp);
				// ====================================rmi集群方案）===========================================
				// ====================================（invocation集群方案===========================================
				// byte[] macBytes = new byte[12];
				// System.arraycopy(commandBytes, 6, macBytes, 0, 12);
				// String mac = new String(macBytes);
				// session.setAttribute("key", mac);
				NamedCache cache = CacheFactory.getCache("YunduoCache");
				// if (null != cache) {
				// // 序列化后无法引用更改
				// // if (cache.containsKey(mac)) {
				// // ((IoConInfo)
				// //
				// cache.get(mac)).setInfo(cache.getCacheService().getCluster().getLocalMember().getUid(),
				// // session.getId());
				// // } else {
				// cache.put(mac, new
				// IoConInfo(cache.getCacheService().getCluster().getLocalMember().getUid(),
				// session.getId()));
				// // }
				// }
				// ====================================invocation集群方案)===========================================
				// ====================================（rmi集群方案===========================================
				// boolean isWrite = false;
				// Set<Entry<Long, IoSession>> ioSessionEntries =
				// session.getService().getManagedSessions().entrySet();
				// if (null != ioSessionEntries) {
				// for (Entry<Long, IoSession> entry : ioSessionEntries) {
				// IoSession ioSession = entry.getValue();
				// if (null != ioSession && ioSession.containsAttribute("key")
				// && ioSession.getAttribute("key").equals(mark)) {
				// if (ioSession.isConnected()) {
				// ioSession.write(commandBytes);
				// isWrite = true;
				// break;
				// }
				// }
				// }
				// }
				// // System.out.println(mark + ":发往设备" + (isWrite ? "本地" :
				// "集群"));
				// if (!isWrite) {
				// // 调用远程支持ip＋rmi版本
				// NamedCache cache = CacheFactory.getCache("YunduoCache");
				// if (null != cache) {
				// Object value = cache.get(mark);
				// if (null != value) {
				// String clientIp = String.valueOf(value);
				// String address = "rmi://" + clientIp + ":4126/RClient";
				// // System.out.println(mark + ":发往设备集群" + address);
				// ClientRmi clientRmi = (ClientRmi) Naming.lookup(address);
				// if (null != clientRmi) {
				// clientRmi.doClientTask(mark, commandBytes);
				// }
				// }
				// }
				// }
				// ====================================rmi集群方案）===========================================
				// ====================================（invocation集群方案===========================================
				if (null != cache) {
					IoConInfo ioConInfo = (IoConInfo) cache.get(mark);
					if (null != ioConInfo) {
						if (Arrays.equals(cache.getCacheService().getCluster().getLocalMember().getUid().toByteArray(), ioConInfo.nodeUID.toByteArray())) {
							IoSession localIoSession = session.getService().getManagedSessions().get(ioConInfo.iosessionId);
							if (null != localIoSession && localIoSession.isConnected()) {
								localIoSession.write(commandBytes);
							}
						} else {
							@SuppressWarnings("unchecked")
							Set<Member> setMembers = cache.getCacheService().getInfo().getServiceMembers();
							for (Iterator<Member> it = setMembers.iterator(); it.hasNext();) {
								Member member = (Member) it.next();
								if (!Arrays.equals(member.getUid().toByteArray(), ioConInfo.nodeUID.toByteArray())) {
									it.remove();
								}
							}
							if (!setMembers.isEmpty()) {
								InvocationService invocationService = (com.tangosol.net.InvocationService) CacheFactory.getService("InvocationService");
								if (null != invocationService) {
									invocationService.execute(new ClusterTask(ioConInfo, commandBytes), setMembers, null);
								}
							}
						}
					}
				}
				// ====================================invocation集群方案）===========================================
			} else if (0x02 == commandBytes[4]) {
				byte[] macBytes = new byte[12];
				System.arraycopy(commandBytes, 6, macBytes, 0, 12);
				String mac = new String(macBytes);
				// ====================================（rmi集群方案）===========================================
				// byte[] markBytes = new byte[8];
				// System.arraycopy(commandBytes, 18, markBytes, 0, 8);
				// String mark = new String(markBytes);
				// session.setAttribute("key", mark);
				// String localIp = InetAddress.getLocalHost().getHostAddress();
				// CacheFactory.getCache("YunduoCache").put(mark, localIp);
				// ====================================rmi集群方案）===========================================
				// ====================================（invocation集群方案===========================================
				// byte[] markBytes = new byte[8];
				// System.arraycopy(commandBytes, 18, markBytes, 0, 8);
				// String mark = new String(markBytes);
				// session.setAttribute("key", mark);
				NamedCache cache = CacheFactory.getCache("YunduoCache");
				// if (null != cache) {
				// // 序列化后无法引用更改
				// // if (cache.containsKey(mark)) {
				// // ((IoConInfo)
				// //
				// cache.get(mark)).setInfo(cache.getCacheService().getCluster().getLocalMember().getUid(),
				// // session.getId());
				// // } else {
				// cache.put(mark, new
				// IoConInfo(cache.getCacheService().getCluster().getLocalMember().getUid(),
				// session.getId()));
				// // }
				// }
				// ====================================invocation集群方案)===========================================
				// ====================================（rmi集群方案===========================================
				// boolean isWrite = false;
				// Set<Entry<Long, IoSession>> ioSessionEntries =
				// session.getService().getManagedSessions().entrySet();
				// if (null != ioSessionEntries) {
				// for (Entry<Long, IoSession> entry : ioSessionEntries) {
				// IoSession ioSession = entry.getValue();
				// if (null != ioSession && ioSession.containsAttribute("key")
				// && ioSession.getAttribute("key").equals(mac)) {
				// if (ioSession.isConnected()) {
				// ioSession.write(commandBytes);
				// isWrite = true;
				// break;
				// }
				// }
				// }
				// }
				// // System.out.println(mac + ":发往用户" + (isWrite ? "本地" :
				// "集群"));
				// if (!isWrite) {
				// // 调用远程支持
				// NamedCache cache = CacheFactory.getCache("YunduoCache");
				// if (null != cache) {
				// Object value = cache.get(mac);
				// if (null != value) {
				// String clientIp = String.valueOf(value);
				// String address = "rmi://" + clientIp + ":4126/RClient";
				// // System.out.println(mac + ":发往用户集群" + address);
				// ClientRmi clientRmi = (ClientRmi) Naming.lookup(address);
				// if (null != clientRmi) {
				// clientRmi.doClientTask(mac, commandBytes);
				// }
				// }
				// }
				// }
				// ====================================rmi集群方案）===========================================
				// ====================================（invocation集群方案===========================================
				try {
					if (null != cache) {
						IoConInfo ioConInfo = (IoConInfo) cache.get(mac);
						if (null != ioConInfo) {
							if (Arrays.equals(cache.getCacheService().getCluster().getLocalMember().getUid().toByteArray(), ioConInfo.nodeUID.toByteArray())) {
								IoSession localIoSession = session.getService().getManagedSessions().get(ioConInfo.iosessionId);
								if (null != localIoSession && localIoSession.isConnected()) {
									localIoSession.write(commandBytes);
								}
							} else {
								@SuppressWarnings("unchecked")
								Set<Member> setMembers = cache.getCacheService().getInfo().getServiceMembers();
								for (Iterator<Member> it = setMembers.iterator(); it.hasNext();) {
									Member member = (Member) it.next();
									if (!Arrays.equals(member.getUid().toByteArray(), ioConInfo.nodeUID.toByteArray())) {
										it.remove();
									}
								}
								if (!setMembers.isEmpty()) {
									InvocationService invocationService = (com.tangosol.net.InvocationService) CacheFactory.getService("InvocationService");
									if (null != invocationService) {
										invocationService.execute(new ClusterTask(ioConInfo, commandBytes), setMembers, null);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// ====================================invocation集群方案）===========================================
			}
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// System.out.println("_____________________________");
		// System.out.println("**create:" + session.getRemoteAddress());
		session.setAttribute("key", "key");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// System.out.println("___");
		// System.out.println("**open:" + session.getRemoteAddress());
		session.write(new byte[] { 0x0A, 0x22, 0x00, 0x16, 0x03, 0x02, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x55 });
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		// System.out.println("**send--");
	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable t) throws Exception {
		// if (session.containsAttribute("key")) {
		// // 远程支持取消
		// NamedCache cache = CacheFactory.getCache("YunduoCache");
		// if (null != cache) {
		// cache.remove(session.getAttribute("key"));
		// }
		// }
		session.close(true);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// if (session.containsAttribute("key")) {
		// // 远程支持取消
		// NamedCache cache = CacheFactory.getCache("YunduoCache");
		// if (null != cache) {
		// cache.remove(session.getAttribute("key"));
		// }
		// }
		if (session.containsAttribute("type") && "2".equals(session.getAttribute("type")) && session.containsAttribute("key")) {
			// 远程支持取消
			NamedCache cache = CacheFactory.getCache("YunduoCache");
			if (null != cache) {
				cache.remove(session.getAttribute("key"));
			}
		}
	}
}

@SuppressWarnings("serial")
class ClusterTask extends AbstractInvocable {
	private IoConInfo ioConInfo;
	private byte[] sendBytes;

	public ClusterTask(IoConInfo ioConInfo, byte[] sendBytes) {
		super();
		this.ioConInfo = ioConInfo;
		this.sendBytes = sendBytes;
	}

	@Override
	public void run() {
		IoSession invoIoSession = MinaServer.instance().getManagedSessions().get(ioConInfo.iosessionId);
		if (null != invoIoSession && invoIoSession.isConnected()) {
			invoIoSession.write(sendBytes);
		}
	}

}