package com.samknows.ska.activity;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.samknows.libcore.R;
import com.samknows.measurement.SKApplication;
import com.samknows.measurement.activity.BaseLogoutActivity;
import com.samknows.measurement.activity.components.Util;
import com.samknows.libcore.SKLogger;

public class SKAAboutActivity extends BaseLogoutActivity {

	@Override
	public void onStart(){
		super.onStart();
		this.setTitle(SKApplication.getAppInstance().getAboutScreenTitle());
		setContentView(R.layout.ska_about_activity);
		String versionName="";
		try {
			versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0 ).versionName;
		} catch (NameNotFoundException e) {
			SKLogger.sAssert(getClass(), false);
		}
		
		TextView tv=(TextView) findViewById(R.id.version);		
		Util.initializeFonts(this);
		Util.overrideFonts(this, findViewById(android.R.id.content));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:			
			NavUtils.navigateUpTo(this,
					new Intent(this, SKApplication.getAppInstance().getTheMainActivityClass()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
