package com.samknows.fcc;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TabHost;


import com.samknows.libcore.SKLogger;
import com.samknows.measurement.SK2AppSettings;
import com.samknows.measurement.MainService;
import com.samknows.fcc.R;

import com.samknows.measurement.activity.BaseLogoutActivity;
import com.samknows.measurement.activity.SamKnowsBaseActivity;

import com.samknows.measurement.activity.components.Util;
import com.samknows.measurement.util.LoginHelper;
import com.samknows.measurement.util.OtherUtils;
import com.samknows.ska.activity.SKAActivationActivity;
import com.samknows.ska.activity.SKAMainResultsActivity;

import com.samknows.measurement.util.LoginHelper;


import com.samknows.measurement.activity.SamKnowsBaseActivity;

import android.app.Activity;
import android.app.TabActivity;

@SuppressLint("InlinedApi")
public class SKAMainAndTermsAndConditionsActivity extends SamKnowsBaseActivity  {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		//TabActivity x = new TabActivity();
		System.out.println("333333333333333333333333");
		Log.d(this.getClass().toString(), "*** onCreate ***");
		final Activity ctx = this;	
		
		/*Resources res = getResources(); // Objeto base para poner todos los objetos de tipo Drawable
	    TabHost tabHost = getTabHost();  // Menú TabHost
	    TabHost.TabSpec spec;  // Cada tab del menú
	    Intent intent;  // se utiliza para agregar cada Activity Class para cada tab del menú
	    
	    intent = new Intent().setClass(ctx, SKAActivationActivity.class);	    
	    spec = tabHost.newTabSpec("Activacion").setIndicator("",res.getDrawable(R.drawable.play1)).setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(ctx, SKAActivationActivity.class);
	    spec = tabHost.newTabSpec("Acercade").setIndicator("",res.getDrawable(R.drawable.information1)).setContent(intent);
	    tabHost.addTab(spec);*/
		
		
		
		startActivity(new Intent(ctx, SKAActivationActivity.class));
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}
