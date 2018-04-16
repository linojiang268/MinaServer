package com.socket.server;

/**
 * 
 * @ClassName CmdUtil
 * @Description cmd公共类
 * @author ouArea
 * @date 2013-11-13 下午4:29:29
 * 
 */
public class CmdUtil {
	/**
	 * 配置芯片wifi时搜索指令
	 */
	public final static byte SET_DEV_WIFI_SEARCH = 0x20;
	/**
	 * 服务端初始化
	 */
	public final static byte WAN_INI = 0x21;
	/**
	 * 呼叫节点
	 */
	public final static byte CALL = 0x23;
	/**
	 * 学习
	 */
	public final static byte LEARN = 0x11;
	/**
	 * 返回学习编码
	 */
	public final static byte LEARN_BACK_SUCCESS = 0x30;
	/**
	 * 学习失败
	 */
	public final static byte LEARN_BACK_FAIL = 0x31;
	/**
	 * 控制
	 */
	public final static byte CONTROL = 0x12;
	/**
	 * 接收成功
	 */
	public final static byte CONTROL_BACK_SUCCESS = 0x32;
	/**
	 * 接收失败
	 */
	public final static byte CONTROL_BACK_FAIL = 0x33;

	/**
	 * 请求获取温度
	 */
	public final static byte TEMPERATURE = 0x13;
	/**
	 * 温度成功返回
	 */
	public final static byte TEMPERATURE_BACK_SUCCESS = 0x34;

	// ==========================================
	/**
	 * 射频315和射频433学习失败、控制成功、控制失败都是用的是使用红外对应的指令
	 */

	/**
	 * 射频315学习
	 */
	public final static byte RADIO315_LEARN = 0x41;

	/**
	 * 射频315学习成功返回
	 */
	public final static byte RADIO315_LEARN_BACK_SUCCESS = 0x43;

	/**
	 * 射频315控制指令
	 */
	public final static byte RADIO315_CONTROL = 0x42;

	/**
	 * 射频433学习
	 */
	public final static byte RADIO433_LEARN = 0x44;

	/**
	 * 射频315学习成功返回
	 */
	public final static byte RADIO433_LEARN_BACK_SUCCESS = 0x46;
	/**
	 * 射频315控制指令
	 */
	public final static byte RADIO433_CONTROL = 0x45;

	/**
	 * 获取温度曲线数据
	 */
	public final static byte TEMPERATUREPOLYLINE = 0x47;

	/**
	 * 温度曲线数据返回
	 */
	public final static byte TEMPERATUREPOLYLINE_BACK_SUCCESS = 0x48;

	/**
	 * 定时功能
	 */
	public final static byte TIMMING = 0x49;

	/**
	 * 定时返回成功
	 */
	public final static byte TIMMING_BACK = 0x4A;

	/**
	 * 
	 * @Title check
	 * @Description 检测此cmd命令是否合法
	 * @author ouArea
	 * @date 2013-11-13 下午4:29:09
	 * @param cmd
	 * @return
	 */
	public static boolean check(byte cmd) {
		switch (cmd) {
		case SET_DEV_WIFI_SEARCH:
		case WAN_INI:
		case CALL:
		case LEARN:
		case LEARN_BACK_SUCCESS:
		case LEARN_BACK_FAIL:
		case CONTROL:
		case CONTROL_BACK_SUCCESS:
		case CONTROL_BACK_FAIL:
		case TEMPERATURE:
		case TEMPERATURE_BACK_SUCCESS:
			// =================================
		case RADIO315_LEARN:
		case RADIO315_LEARN_BACK_SUCCESS:
		case RADIO315_CONTROL:
		case RADIO433_LEARN:
		case RADIO433_LEARN_BACK_SUCCESS:
		case RADIO433_CONTROL:

		case TEMPERATUREPOLYLINE:
		case TEMPERATUREPOLYLINE_BACK_SUCCESS:
		case TIMMING:
		case TIMMING_BACK:
			return true;
		default:
			return false;
		}
	}
}
