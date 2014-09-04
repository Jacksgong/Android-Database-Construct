package cn.dreamtobe.library.db.sample.app;

import android.app.Application;
import android.content.Context;

/**
 * 
 * @author Jacks gong
 * @data 2014-9-4 上午10:53:43
 * @Describe
 */
public class SampleApplication extends Application {

	private static Context CONTEXT;

	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = this;
	}

	public static Context getContext() {
		return CONTEXT;
	}
}
