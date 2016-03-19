package com.ucas.jomoo.tools;



import android.content.Context;
import android.content.SharedPreferences;

/**
 * SP存储工具类
 * 
 * @author 浩
 *
 */
public final  class SPUtils {
	
	public static final String LOST_NAME = "lost_name";
	public static final String LOST_PWD = "lost_pwd";
	public static final String CONFIGURE = "configure";
	public static final String SIM_NUMBER = "sim_number";
	public static final String SAFE_NUMBER = "safe_number";
	public static final String PROTECT = "protect";
	public static final String STYLE_INDEX = "style_index";
	public static final String UP_LEFT = "up_left";
	public static final String UP_TOP = "up_top";

	private static SPUtils instance;
	private static SharedPreferences sp;

	private SPUtils() {
	};

	public static SPUtils getInstance(Context context) {
		if (instance == null) {
			instance = new SPUtils();
			sp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
		}
		return instance;
	}

	/**
	 * 1. 保存数据
	 */
	public void save(String name, Object value) {
		if (value instanceof String) {
			sp.edit().putString(name, (String) value).commit();
		} else if (value instanceof Boolean) {
			sp.edit().putBoolean(name, (Boolean) value).commit();
		} else if (value instanceof Integer) {
			sp.edit().putInt(name, (Integer) value).commit();
		}
	}

	/**
	 * 2. 读取数据 String
	 * 
	 */
	public String getString(String name, String defValue) {
		return sp.getString(name, defValue);
	}

	/**
	 * 2. 读取数据 boolean
	 * 
	 */
	public Boolean getBoolean(String name, boolean defValue) {
		return sp.getBoolean(name, defValue);
	}

	/**
	 * 2. 读取数据 int
	 * 
	 */
	public int getInt(String name, int defValue) {
		return sp.getInt(name, defValue);
	}

	/**
	 * 3. 移除数据
	 */
	public void remove(String name) {
		sp.edit().remove(name).commit();
	}
}
