package com.samknows.ska.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samknows.libcore.SKLogger;
import com.samknows.measurement.MainService;
import com.samknows.measurement.SKApplication;
import com.samknows.libcore.R;
import com.samknows.measurement.activity.BaseLogoutActivity;
import com.samknows.measurement.activity.components.UIUpdate;
import com.samknows.measurement.activity.components.Util;
import com.samknows.measurement.util.LoginHelper;

public class SKAActivationActivity extends BaseLogoutActivity {

	public Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cargar_inicio);
		Util.initializeFonts(this);
		Util.overrideFonts(this, findViewById(android.R.id.content));
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				TextView tv;

				JSONObject message_json;
				if (msg.obj == null) {
					return;
				}
				message_json = (JSONObject) msg.obj;

				try {
					String type = message_json.getString(UIUpdate.JSON_TYPE);

					if (type == UIUpdate.JSON_MAINPROGRESS) {
						String value = message_json.getString(UIUpdate.JSON_VALUE);
					} else if (type == UIUpdate.JSON_ACTIVATED) {
					} else if (type == UIUpdate.JSON_DOWNLOADED) {
					} else if (type == UIUpdate.JSON_INITTESTS) {
						String total = message_json.getString(UIUpdate.JSON_TOTAL);
						String finished = message_json.getString(UIUpdate.JSON_FINISHED);
						String currentbest = message_json.getString(UIUpdate.JSON_CURRENTBEST);
						String besttime = message_json.getString(UIUpdate.JSON_BESTTIME);
					} else if (type == UIUpdate.JSON_COMPLETED) {
						LoginHelper.openMainScreen(SKAActivationActivity.this, SKApplication.getAppInstance().getTheMainActivityClass());
						SKAActivationActivity.this.finish();
					}
				} catch (JSONException e) {
					SKLogger.e(SKAActivationActivity.class,
							"Error in parsing JSONObject: " + e.getMessage());
				}
			}
		};
		if (MainService.registerActivationHandler(this, handler)) {
			SKLogger.d(this, "activation handler registered");
		} else {
			SKLogger.d(this, "MainService is not executing");
			LoginHelper.openMainScreen(SKAActivationActivity.this, SKApplication.getAppInstance().getTheMainActivityClass());
			SKAActivationActivity.this.finish();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MainService.unregisterActivationHandler();
	}
}
