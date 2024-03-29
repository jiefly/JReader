package com.gao.jiefly.jieflysbooks.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class AndroidUtilities {

	public static float density = 1;
	public static Point displaySize = new Point();

	static {
		density = ApplicationLoader.applicationContext.getResources()
				.getDisplayMetrics().density;
		checkDisplaySize();
	}
	public static boolean isServiceRunning(String serviceName) {
		ActivityManager manager = (ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//			Log.e(serviceName,service.service.getClassName());
			if (serviceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	public static int dp(float value) {
		return (int) Math.ceil(density * value);
	}

	public static float dpf2(float value) {
		return density * value;
	}

	public static void checkDisplaySize() {
		try {
			WindowManager manager = (WindowManager) ApplicationLoader.applicationContext
					.getSystemService(Context.WINDOW_SERVICE);
			if (manager != null) {
				Display display = manager.getDefaultDisplay();
				if (display != null) {
					if (android.os.Build.VERSION.SDK_INT < 13) {
						displaySize
								.set(display.getWidth(), display.getHeight());
					} else {
						display.getSize(displaySize);
					}
					Log.e("tmessages", "display size = " + displaySize.x+ " " + displaySize.y);
				}
			}
		} catch (Exception e) {
			Log.e("tmessages", e.toString());
		}
	}

}
