package com.samknows.ska.activity;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.achartengine.GraphicalView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.samknows.libcore.SKCommon;
import com.samknows.libcore.SKLogger;
import com.samknows.measurement.MainService.ContinuousState;
import com.samknows.measurement.MainService.MainServiceBinder;
import com.samknows.measurement.SK2AppSettings;
import com.samknows.measurement.CachingStorage;
import com.samknows.measurement.DeviceDescription;
import com.samknows.measurement.MainService;
import com.samknows.measurement.SKApplication;
import com.samknows.measurement.SKApplication.eNetworkTypeResults;
import com.samknows.libcore.R;
import com.samknows.measurement.SamKnowsLoginService;
import com.samknows.measurement.SamKnowsResponseHandler;
import com.samknows.measurement.Storage;
import com.samknows.measurement.activity.BaseLogoutActivity;
import com.samknows.measurement.activity.components.ResizeAnimation;
import com.samknows.measurement.activity.components.SKGraphForResults;
import com.samknows.measurement.activity.components.SKGraphForResults.DATERANGE_1w1m3m1y;
import com.samknows.measurement.activity.components.StatModel;
import com.samknows.measurement.activity.components.StatRecord;
import com.samknows.measurement.activity.components.UpdatedTextView;
import com.samknows.measurement.activity.components.Util;
import com.samknows.measurement.schedule.ScheduleConfig;
import com.samknows.measurement.schedule.TestDescription;
import com.samknows.measurement.storage.DBHelper;
import com.samknows.measurement.storage.ExportFile;
import com.samknows.measurement.storage.TestResult;
import com.samknows.measurement.util.OtherUtils;
import com.samknows.measurement.util.SKDateFormat;
import com.samknows.ska.activity.components.StatView;



public class SKAMainResultsActivity extends BaseLogoutActivity implements OnClickListener {

	// use to decide when to show the state_machine_status_failure

	int TIMEOUT_MILLISEC = 10000;
	int bandera = 0;
	int botoncito=0;
	private String operador_red ="", imei_red ="", so_red = "", tipo_red = "", hora_red = "", fecha_red = "", MAC_red = "", potencia_red ="";
	private String lat="", lon="";
	private String valor_subida="", unidad_subida="", valor_bajada="", unidad_bajada="", bajada ="", subida="", latencia="", perdida_paquetes="";
	private String [] aux = new String[2];
	private int tp = 0;
	private LocationManager locManager;
	private LocationListener locListener;
	private String cadena_final="";
	
	TelephonyManager telephonyManager;
	AndroidPhoneStateListener phoneStateListener;
	SKGraphForResults graphHandlerDownload;
	SKGraphForResults graphHandlerUpload;
	SKGraphForResults graphHandlerLatency;
	SKGraphForResults graphHandlerPacketLoss;
	SKGraphForResults graphHandlerJitter;
	int download_page_index = 0;
	int upload_page_index = 0;
	int latency_page_index = 0;
	int packetloss_page_index = 0;
	int jitter_page_index = 0;

	// For mock testing...
	public SKGraphForResults getDownloadGraphHandler () {
        return graphHandlerDownload;
	}

	private static final String TAG = SKAMainResultsActivity.class
			.getSimpleName();
	public static final String SETTINGS = "SamKnows";
	private static final int PANEL_HEIGHT = 550;
	private final Context context = this;
	private SamKnowsLoginService service = new SamKnowsLoginService();

	private StatModel statModel = new StatModel();
	private String start_date;
	private JSONObject recentData;
	// private DeviceDescription device;
	// private boolean isCurrentDevice;


	public static final int RECENT = 0;
	public static final int WEEK = 1;
	public static final int MONTH = 2;
	public static final int THREE_MONTHS = 3;
	public static final int SIX_MONTHS = 4;
	public static final int YEAR = 5;

	private static final int ITEMS_PER_PAGE = 5;

	private boolean isDisplayingContent = false;

	private int[] latest;
	private int[] rows;
	private TableLayout table;
	private int[] buttons;

	private long start_dtime = 0;
	private long end_dtime = 0;

	private UpdatedTextView updated;

	View.OnTouchListener gestureListener;
	private GestureDetector gestureDetector;
	DeviceDescription currentDevice;
	private View subview;

	private int total_archive_records = 0;
	private int total_download_archive_records = 0;
	private int total_upload_archive_records = 0;
	private int total_latency_archive_records = 0;
	private int total_packetloss_archive_records = 0;
	private int total_jitter_archive_records = 0;

	private DBHelper dbHelper;
	// private DBHelper dbHelperAsync;

	private MyPagerAdapter adapter = null;
	private ViewPager viewPager;
	private View current_page_view;
	private int current_page_view_position = 0;

	private String last_run_test_formatted;
	private int target_height = 0;
	private boolean on_aggregate_page = true;

	Storage storage;
	ScheduleConfig config;

	List<TestDescription> testList;
	String array_spinner[];
	int array_spinner_int[];
	
	TextView tvHeader = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);  
		phoneStateListener = new AndroidPhoneStateListener (context);
		

		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		potencia_red = phoneStateListener.valor_potencia();
		
		SKLogger.d(this, "+++++DEBUG+++++ SamKnowsAggregateStatViewerActivity onCreate...");
		
		this.setTitle(getString(R.string.sk2_main_results_activity_title));

		setContentView(R.layout.ska_main_results_activity_main_page_views);

		dbHelper = new DBHelper(SKAMainResultsActivity.this);
		//dbHelperAsync = new DBHelper(SamKnowsAggregateStatViewerActivity.this);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
	
		adapter = new MyPagerAdapter(this);
		viewPager.setAdapter(adapter);
		 viewPager.setOffscreenPageLimit(3);

		tvHeader = (TextView) findViewById(R.id.textViewHeader);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int page) {
             	handleOnPageSelected(page);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
//				if (state == ViewPager.SCROLL_STATE_SETTLING) {
//
//				}
			}
		});
		

		Util.initializeFonts(this);
		Util.overrideFonts(this, findViewById(android.R.id.content));

	}
	
	void handleOnPageSelected(int page) {

		tvHeader.setText(getString(R.string.page) + " " + (page + 1));

		if (page == 0) {
			on_aggregate_page = true;
			//boolean db_refresh = false;
			setContinuousTestingButton();
			SKAMainResultsActivity.this.setTitle(getString(R.string.sk2_main_results_activity_title));

			View v = viewPager.findViewWithTag(page);

			if (v == null) {
				// ... we should trap this where possible in the debugger...
				SKLogger.sAssert(getClass(), false);
			} else {
			}

		} else {
			View v = viewPager.findViewWithTag(page);
			if (v == null) {
				// ... we should trap this where possible in the debugger...
				SKLogger.sAssert(getClass(), false);
			} else {
				on_aggregate_page = false;
				SKAMainResultsActivity.this.setTitle(getString(R.string.archive_result));
				
				
			//	String caption = getString(R.string.archive_result) + " " + page + " " + getString(R.string.archive_result_of) + " " + total_archive_records;
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		SKLogger.d(this, "+++++DEBUG+++++ SamKnowsAggregateStatViewerActivity onDestroy...");
	}
	
	boolean mbHandlingOnActivityResult = false;

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		
        SKLogger.d(this, "+++++DEBUG+++++ SamKnowsAggregateStatViewerActivity onResume...");

        if (mbHandlingOnActivityResult == true) {
        	// Already handled on the activity result... which has taken us to the archived results screen.
            mbHandlingOnActivityResult = false;
        } else {
        	// Resuming from e.g. T&C screen, or About screen.
        	if (setTotalArchiveRecords()) {
        		//adapter = new MyPagerAdapter(SKAMainResultsActivity.this);
        		//viewPager = (ViewPager) findViewById(R.id.viewPager);
				SKLogger.sAssert(getClass(), viewPager == (ViewPager) findViewById(R.id.viewPager));
        		//viewPager.setAdapter(adapter);
        	}
        }
        
        setContinuousTestingButton();
	}
	
	private void setContinuousTestingButton(){
		if (SKApplication.getAppInstance().supportOneDayResultView() == false) {
			return;
		}
		}
	
	@Override
	public void onPause(){
		super.onPause();
		
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == RESULT_OK) {
			mbHandlingOnActivityResult = true;

			// The returned result might be a mobile result, or a network result.
			// We must update the filter to match, and then show that result screen...!
			String activeNetworkType = data.getStringExtra("activeneworktype");

			if (activeNetworkType == null) {
				SKLogger.sAssert(getClass(), false);
			} else if (activeNetworkType.equals("mobile")) {
				if (SKApplication.getNetworkTypeResults() == eNetworkTypeResults.eNetworkTypeResults_WiFi) {
    				SKApplication.setNetworkTypeResults(eNetworkTypeResults.eNetworkTypeResults_Any);
				}
			} else if (activeNetworkType.equals("WiFi")) {
				if (SKApplication.getNetworkTypeResults() == eNetworkTypeResults.eNetworkTypeResults_Mobile) {
    				SKApplication.setNetworkTypeResults(eNetworkTypeResults.eNetworkTypeResults_Any);
				}
			} else {
				SKLogger.sAssert(getClass(), false);
			}
			setNetworkTypeToggleButton();

			// refresh data
			adapter = new MyPagerAdapter(SKAMainResultsActivity.this);
			setTotalArchiveRecords();
			viewPager = (ViewPager) findViewById(R.id.viewPager);
			SKLogger.sAssert(getClass(), viewPager == (ViewPager) findViewById(R.id.viewPager));
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(1, true); // true means - perform a smooth scroll!
			overridePendingTransition(0, 0);
		}
	}
	
	private void lookBackwardInTime(Calendar fromCal) {
		switch (mDateRange) {
		case DATERANGE_1w1m3m1y_ONE_DAY:
    		fromCal.add(Calendar.DAY_OF_YEAR, -1);
    		break;
		case DATERANGE_1w1m3m1y_ONE_WEEK:
    		fromCal.add(Calendar.WEEK_OF_YEAR, -1);
    		break;
		case DATERANGE_1w1m3m1y_ONE_MONTH:
    		fromCal.add(Calendar.WEEK_OF_YEAR, -4);
    		break;
		case DATERANGE_1w1m3m1y_THREE_MONTHS:
    		fromCal.add(Calendar.WEEK_OF_YEAR, -12);
    		break;
		case DATERANGE_1w1m3m1y_ONE_YEAR:
    		fromCal.add(Calendar.WEEK_OF_YEAR, -52);
    		break;
		}
	}

	private void loadAverage() {
		Calendar fromCalNow = Calendar.getInstance();
		long current_dtime = fromCalNow.getTimeInMillis();

		lookBackwardInTime(fromCalNow);
		long starting_dtime = fromCalNow.getTimeInMillis();

		JSONArray jsonResult = dbHelper.getAverageResults(starting_dtime, current_dtime);
		//String result = "";

		for (int i = 0; i < jsonResult.length(); i++) {
			try {
				JSONObject json_data = jsonResult.getJSONObject(i);
				//String value = json_data.getString("value");
				String type = json_data.getString("type");

				if (type.equals("" + TestResult.DOWNLOAD_TEST_ID)) {
				}
				if (type.equals("" + TestResult.UPLOAD_TEST_ID)) {
				}
				if (type.equals("" + TestResult.LATENCY_TEST_ID)) {
				}
				if (type.equals("" + TestResult.PACKETLOSS_TEST_ID)) {
				}
				if (type.equals("" + TestResult.JITTER_TEST_ID)) {
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public boolean setTotalArchiveRecords() {
		boolean result = false;
		
		total_archive_records = 0;
		total_download_archive_records = 0;
		total_upload_archive_records = 0;
		total_latency_archive_records = 0;
		total_packetloss_archive_records = 0;
		total_jitter_archive_records = 0;

		JSONObject summary = dbHelper.getArchiveDataSummary();

		JSONObject results = null;

		try {
			results = summary.getJSONObject("test_counter");

		} catch (JSONException e) {

			SKLogger.sAssert(getClass(),  false);
			
			return false;
		}
		

		try {
			if (results.has("" + TestResult.DOWNLOAD_TEST_ID)) {
				total_download_archive_records = results.getInt(""
						+ TestResult.DOWNLOAD_TEST_ID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (mShowArchivedResultsButton != null) {
			mShowArchivedResultsButton.setVisibility((total_download_archive_records > 0) ? View.VISIBLE :
				View.INVISIBLE);
		}

		try {
			if (results.has("" + TestResult.UPLOAD_TEST_ID)) {
				total_upload_archive_records = results.getInt(""
						+ TestResult.UPLOAD_TEST_ID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (results.has("" + TestResult.LATENCY_TEST_ID)) {
				total_latency_archive_records = results.getInt(""
						+ TestResult.LATENCY_TEST_ID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (results.has("" + TestResult.JITTER_TEST_ID)) {
				total_jitter_archive_records = results.getInt(""
						+ TestResult.JITTER_TEST_ID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (results.has("" + TestResult.PACKETLOSS_TEST_ID)) {
				total_packetloss_archive_records = results.getInt(""
						+ TestResult.PACKETLOSS_TEST_ID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			int records = summary.getInt("counter");
			if (total_archive_records != records) {
				total_archive_records = records;
				result = true;
			}

			String last_run_test = summary.getString("enddate");
			long last_run_test_l = Long.parseLong(last_run_test);
			if (last_run_test_l != 0) {
				last_run_test_formatted = new SKDateFormat(context)
						.UIDate(last_run_test_l);
			} else {
             	last_run_test_formatted = this.getString(R.string.last_run_never);
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	private void loadDownloadGrid(int testnumber, int grid, int offset,
			int limit) {

		double a1 = (double) total_archive_records;
		double a2 = (double) limit;
		int pages = (int) Math.ceil(a1 / a2);
		//int page_number = (offset + limit) / limit;

		//TextView tv;

		switch (testnumber) {
		case TestResult.DOWNLOAD_TEST_ID:
			a1 = (double) total_download_archive_records;
			pages = (int) Math.ceil(a1 / a2);
			if (pages == 0) pages = 1;
			break;

		case TestResult.UPLOAD_TEST_ID:
			a1 = (double) total_upload_archive_records;
			pages = (int) Math.ceil(a1 / a2);
			if (pages == 0) pages = 1;
			break;

		case TestResult.LATENCY_TEST_ID:
			a1 = (double) total_latency_archive_records;
			pages = (int) Math.ceil(a1 / a2);
			if (pages == 0) pages = 1;
			break;

		case TestResult.PACKETLOSS_TEST_ID:
			a1 = (double) total_packetloss_archive_records;
			pages = (int) Math.ceil(a1 / a2);
			if (pages == 0) pages = 1;
			break;

		case TestResult.JITTER_TEST_ID:
			a1 = (double) total_jitter_archive_records;
			pages = (int) Math.ceil(a1 / a2);
			if (pages == 0) pages = 1;			
			break;

		default:

		}

		JSONObject jsonObject;

		jsonObject = dbHelper.getGridData(testnumber, offset, limit);
		// jsonObject=dbHelper.getGridData(1, offset, limit);

		JSONArray results = null;
		try {
			results = jsonObject.getJSONArray("results");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String location;
		String dtime;
		String dtime_formatted = null;
		String result;
		String success;

		int addedRowCount = 0;
		
		if (results != null) {

			for (int i = 0; i < results.length(); i++) {
				location = "";
				dtime = "";
				result = "";
				success = "";
				String networkType = "";
				JSONObject user = null;
				try {
					user = results.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if (user != null) {
					try {
						success = user.getString(DBHelper.GRIDDATA_RESULTS_SUCCESS);
						location = user.getString(DBHelper.GRIDDATA_RESULTS_LOCATION);
						dtime = user.getString(DBHelper.GRIDDATA_RESULTS_DTIME);
						networkType = user.getString(DBHelper.GRIDDATA_RESULTS_NETWORK_TYPE);
						if (dtime != "") {

							long datelong = Long.parseLong(dtime);
							if (datelong != 0) {
								dtime_formatted = new SKDateFormat(context).UIShortDateTime(datelong);
							}

						} else {
							dtime_formatted = "";
						}
						result = user.getString("hrresult");

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			
				if (addedRowCount == 0) {
					// Add the header, just before adding the first row!
					addGridItemHeader(getString(R.string.results_table_header_datetime),
                            getString(R.string.results_table_header_location),
              							getString(R.string.results_table_header_result),
                            grid);
				}

				if (success.equals("1")) {
					addGridItem(dtime_formatted, location, result, networkType, grid);
				} else {
					result = getString(R.string.failed);
					addGridItemFailed(dtime_formatted, location, result, networkType, grid);
				}
			
				addedRowCount++;
			}
		}
		
		TableLayout table = (TableLayout) findViewById(grid);
		if (addedRowCount > 0) {
			table.setVisibility(View.VISIBLE);
		} else {
			table.setVisibility(View.GONE);
			table.getLayoutParams().height = 0;
		}
		table.getParent().requestLayout();

		Util.overrideFonts(this, findViewById(android.R.id.content));
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}

	Button mNetworkTypeToggleButton = null;
	Button       mShowArchivedResultsButton = null;
	
	void setNetworkTypeToggleButton() {
		if (mNetworkTypeToggleButton == null) {
			SKLogger.sAssert(getClass(), false);
		} else {
			if (SKApplication.getNetworkTypeResults() == eNetworkTypeResults.eNetworkTypeResults_WiFi) {
				mNetworkTypeToggleButton.setText(R.string.network_type_wifi_results);
			} else if (SKApplication.getNetworkTypeResults() == eNetworkTypeResults.eNetworkTypeResults_Mobile) {
				mNetworkTypeToggleButton.setText(R.string.network_type_mobile_results);
			} else {
				mNetworkTypeToggleButton.setText(R.string.network_type_all_results);
			}
		}
	}
	
	private void buttonSetup() {

		Button run_now_choice_button;
		run_now_choice_button = (Button) findViewById(R.id.btnRunTest);

		run_now_choice_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			
				botoncito = 1;
				RunChoice();
				

			}
		});
		

     	setNetworkTypeToggleButton();
			}		
		Button download_grid_right_button;

		Button download_grid_left_button;
		//download_grid_left_button = (Button) subview.findViewById(R.id.btn_download_grid_left);


		Button upload_grid_right_button;
		//upload_grid_right_button = (Button) subview.findViewById(R.id.btn_upload_grid_right);


		Button upload_grid_left_button;
		//upload_grid_left_button = (Button) subview.findViewById(R.id.btn_upload_grid_left);
	

		Button latency_grid_right_button;
		//latency_grid_right_button = (Button) subview.findViewById(R.id.btn_latency_grid_right);


		Button latency_grid_left_button;
		//latency_grid_left_button = (Button) subview.findViewById(R.id.btn_latency_grid_left);


		Button packetloss_grid_right_button;
		//packetloss_grid_right_button = (Button) subview.findViewById(R.id.btn_packetloss_grid_right);


		Button packetloss_grid_left_button;
		//packetloss_grid_left_button = (Button) subview.findViewById(R.id.btn_packetloss_grid_left);

		Button jitter_grid_right_button;
		//jitter_grid_right_button = (Button) subview.findViewById(R.id.btn_jitter_grid_right);
		

		Button jitter_grid_left_button;
		//jitter_grid_left_button = (Button) subview.findViewById(R.id.btn_jitter_grid_left);
		
		// toggle buttons

		TableLayout button;
		

	//}
		public void carga_base(){
			try {
				JSONObject json = new JSONObject();
				json.put("dir_mac", MAC_red);
				json.put("imei", imei_red);
				json.put("proveedor", operador_red);				
				json.put("fecha_hora", fecha_red);
				json.put("latitud", lat);
				json.put("longitud", lon);
				json.put("tipo_red", tipo_red);
				json.put("potencia_senial", potencia_red);
				json.put("valor_subida", valor_subida);
				json.put("unidad_subida", unidad_subida);
				json.put("valor_bajada", valor_bajada);
				json.put("unidad_bajada", unidad_bajada);
				json.put("subida", subida);
				json.put("bajada", bajada);
				json.put("latencia", latencia);
				json.put("perdida", perdida_paquetes);
				
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,
						TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpClient client = new DefaultHttpClient(httpParams);
				//
				//String url = "http://10.0.2.2:8080/sample1/webservice2.php?json={\"UserName\":1,\"FullName\":2}";
				String url = "http://54.191.193.31:80/ws.php";

				HttpPost request = new HttpPost(url);
				request.setEntity(new ByteArrayEntity(json.toString().getBytes(
						"UTF8")));
				request.setHeader("json", json.toString());
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				// If the response does not enclose an entity, there is no need
				if (entity != null) {
					InputStream instream = entity.getContent();

					String result = RestClient.convertStreamToString(instream);
					Log.i("Read from server", result);
					Toast.makeText(this,  result,
							Toast.LENGTH_LONG).show();
				}


		} catch (Throwable t) {
			Toast.makeText(this, "Request failed: " + t.toString(),
					Toast.LENGTH_LONG).show();
		}
		}
		
		public class Data {
			// private List<User> users;
			public List<User> users;

			// +getters/setters
		}

		static class User {
			String idusers;
			String UserName;
			String FullName;

			public String getUserName() {
				return UserName;
			}

			public String getidusers() {
				return idusers;
			}

			public String getFullName() {
				return FullName;
			}

			public void setUserName(String value) {
				UserName = value;
			}

			public void setidusers(String value) {
				idusers = value;
			}

			public void setFullName(String value) {
				FullName = value;
			}
		}
		
		private void comenzarLocalizacion()
		{
			
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			potencia_red = phoneStateListener.valor_potencia();
			System.out.println("dos: " + potencia_red);
			//Obtenemos una referencia al LocationManager
			locManager = 
				(LocationManager)getSystemService(Context.LOCATION_SERVICE);
			
			//Obtenemos la última posición conocida
			Location loc = 
				locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			//Mostramos la última posición conocida
			datos_red();
			mostrarPosicion(loc);
			//cargar señal
			
			//Nos registramos para recibir actualizaciones de la posición
			locListener = new LocationListener() {
		    	public void onLocationChanged(Location location) {
		    		datos_red();
		    		mostrarPosicion(location);
		    	}
		    	public void onProviderDisabled(String provider){
		    		//lblEstado.setText("Provider OFF");
		    	}
		    	public void onProviderEnabled(String provider){
		    		//lblEstado.setText("Provider ON ");
		    	}
		    	public void onStatusChanged(String provider, int status, Bundle extras){
		    		Log.i("", "Provider Status: " + status);
		    		//lblEstado.setText("Provider Status: " + status);
		    	}
			};
			
			locManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 300000, 0, locListener);
		}
		
		private void datos_red(){
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			TipoRed tipofinal = new TipoRed();
			Wifi wifi = new Wifi(context);
			
			
			operador_red = tm.getNetworkOperatorName();
			//Toast.makeText(context, operador, Toast.LENGTH_SHORT).show();

			imei_red = tm.getDeviceId();
			//Toast.makeText(context, imei, Toast.LENGTH_SHORT).show();
			
			so_red = tm.getDeviceSoftwareVersion();
			//Toast.makeText(context, so, Toast.LENGTH_SHORT).show();
			
			tp = tm.getNetworkType();
			tipo_red = tipofinal.tipo_red(tp);
			//Toast.makeText(context, tipo, Toast.LENGTH_SHORT).show();
			
			Date horaActual=new Date();

			if((horaActual.getMonth()+1)<=9)
				fecha_red =(horaActual.getYear()+1900)+ "-0" +(horaActual.getMonth()+1)+ "-" +horaActual.getDate();
			else
				fecha_red =(horaActual.getYear()+1900)+ "-" +(horaActual.getMonth()+1)+ "-" +horaActual.getDate();
			hora_red = horaActual.getHours() + ":" + horaActual.getMinutes() + ":" +horaActual.getSeconds();
			//Toast.makeText(context, fecha, Toast.LENGTH_SHORT).show();
			//Toast.makeText(context, hora, Toast.LENGTH_SHORT).show();
			 
			MAC_red = wifi.estado_wifi();
			//Toast.makeText(context,MAC, Toast.LENGTH_SHORT).show();*/
			
			  
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			potencia_red = phoneStateListener.valor_potencia();
			System.out.println("tres: " + potencia_red);
		}

		private void mostrarPosicion(Location loc) {
			
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			potencia_red = phoneStateListener.valor_potencia();
			System.out.println("cuatro: " + potencia_red);
			if(loc != null)
			{
				System.out.println("IMEI: " + imei_red);
				System.out.println("MAC: " + MAC_red);
				System.out.println("OPERADOR: " + operador_red);
				System.out.println("FECHA: " + fecha_red);
				System.out.println("HORA: " + hora_red);
				System.out.println("TIPO DE RED: " + tipo_red);
				System.out.println("POTENCIA: " + potencia_red);
				
				System.out.println("LATITUD: " + String.valueOf(loc.getLatitude()));
				lat = String.valueOf(loc.getLatitude());
				//lblPrecision.setText("Precision: " + String.valueOf(loc.getAccuracy()));
				System.out.println("LONGITUD: " + String.valueOf(loc.getLongitude()));
				lon = String.valueOf(loc.getLongitude());
				carga_base();
				//conexion.insertar(MAC_red, imei_red, operador_red, fecha_red, hora_red, String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()), "0", tipo_red, "0");
				
				Log.i("", String.valueOf(loc.getLatitude() + " - " + String.valueOf(loc.getLongitude())));
			}
			else
			{
				System.out.println("IMEI: " + imei_red);
				System.out.println("MAC: " + MAC_red);
				System.out.println("OPERADOR: " + operador_red);
				System.out.println("FECHA: " + fecha_red);
				System.out.println("HORA: " + hora_red);
				System.out.println("TIPO DE RED: " + tipo_red);
				System.out.println("POTENCIA: " + potencia_red);
				
				lat = "0";
				lon = "0";
				System.out.println("LATITUD: (sin_datos)");
				System.out.println("LONGITUD: (sin_datos)");
				carga_base();
				//carga_base();
				//lblPrecision.setText("Precision: (sin_datos)");
			}
		}
		
	private void addGridItemHeader(String timestamp, String location,
			String result, int grid) {
		TableLayout table = (TableLayout) findViewById(grid);
		TableLayout row = (TableLayout) LayoutInflater.from(
				SKAMainResultsActivity.this).inflate(R.layout.ska_main_results_activity_stat_grid_header, null);

		((TextView) row.findViewById(R.id.stat_grid_timestamp))
				.setText(timestamp);
		((TextView) row.findViewById(R.id.stat_grid_location))
				.setText(location);
		((TextView) row.findViewById(R.id.stat_grid_result)).setText(result);

		table.addView(row);
	}

	private void addGridItem(String timestamp, String location, String result, String networkType,
			int grid) {
		TableLayout table = (TableLayout) findViewById(grid);
		TableLayout row = (TableLayout) LayoutInflater.from(
				SKAMainResultsActivity.this).inflate(
				R.layout.ska_main_results_activity_stat_grid, null);

		((TextView) row.findViewById(R.id.stat_grid_timestamp))
				.setText(timestamp);
		((TextView) row.findViewById(R.id.stat_grid_location))
				.setText(location);
		((TextView) row.findViewById(R.id.stat_grid_result)).setText(result);
		
		if (networkType.equals("mobile")) {
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setImageResource(R.drawable.cell_phone_icon);
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setVisibility(View.VISIBLE);
		} else if (networkType.equals("WiFi")) {
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setImageResource(R.drawable.wifiservice);
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setVisibility(View.VISIBLE);
		} else {
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setVisibility(View.INVISIBLE);
		}

		table.addView(row);
	}

	private void addGridItemFailed(String timestamp, String location,
			String result, String networkType, int grid) {
		TableLayout table = (TableLayout) findViewById(grid);
		TableLayout row = (TableLayout) LayoutInflater.from(
				SKAMainResultsActivity.this).inflate(
				R.layout.ska_main_results_activity_stat_grid_fail, null);

		((TextView) row.findViewById(R.id.stat_grid_timestamp))
				.setText(timestamp);
		((TextView) row.findViewById(R.id.stat_grid_location))
				.setText(location);
		((TextView) row.findViewById(R.id.stat_grid_result)).setText(result);
		
		if (networkType.equals("mobile")) {
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setImageResource(R.drawable.cell_phone_icon);
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setVisibility(View.VISIBLE);
		} else if (networkType.equals("WiFi")) {
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setImageResource(R.drawable.wifiservice);
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setVisibility(View.VISIBLE);
		} else {
    		((ImageView) row.findViewById(R.id.networkTypeImage)).setVisibility(View.INVISIBLE);
		}

		table.addView(row);
	}

	private void clearGrid(int grid) {
		TableLayout table = (TableLayout) findViewById(grid);
		int count = table.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = table.getChildAt(i);
			((ViewGroup) child).removeAllViews();
		}
	}

	private void graphsSetup() {

	

	}

	/**
	 * Create the options menu that displays the refresh and about options
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ska_main_results_activity_menu, menu);
		//menu.findItem(R.id.menu_export_file).setVisible(SKApplication.getAppInstance().isExportMenuItemSupported());
		return true;
	}

	private static Intent getFileIntent(Context context, File file) {

		final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		
		Uri uri = Uri.fromFile(file);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setType("application/zip");
		//intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipDestinationString));

		return intent;
	}

	/**
	 * Handle menu options
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		boolean ret = false;
		int itemId = item.getItemId();
		if (R.id.menu_about == itemId) {
			Intent intent = new Intent(this, SKAAboutActivity.class);
			startActivity(intent);
			ret = true;
		} else if (R.id.menu_settings == itemId) {
			startActivity(new Intent(this, SKAPreferenceActivity.class));
			ret = true;
		} else if(R.id.menu_resultados == itemId){
			startActivity(new Intent(this, HistoricoResultados.class));
			ret = true;
		}else{
			
			ret = super.onOptionsItemSelected(item);
		}
		return ret;
	}

	/**
	 * Returns a response handler that displays a loading message
	 * 
	 * @return SamKnowsResponseHandler
	 */
	private SamKnowsResponseHandler getLoadingResponseHandler(String message) {
		final ProgressDialog dialog = getProgressDialog(message);
		return new SamKnowsResponseHandler() {
			public void onSuccess(JSONObject result, Date date,
					String start_date) {
				dialog.dismiss();
				// setStartDate(start_date);
				// setData(result);
				// setDate(date);
			}

			public void onFailure(Throwable error) {
				SKLogger.e(SKAMainResultsActivity.class,
						"failed to get data", error);
				dialog.dismiss();
			}
		};
	}

	private ProgressDialog getProgressDialog(String message) {
		return ProgressDialog.show(SKAMainResultsActivity.this,
				"", message, true, true);
	}

	/**
	 * Returns a response handler that displays a loading message for the RECENT
	 * api
	 * 
	 * @return SamKnowsResponseHandler
	 */
	private SamKnowsResponseHandler getRecentLoadingResponseHandler(String message) {
		final ProgressDialog dialog = getProgressDialog(message);
		return new SamKnowsResponseHandler() {
			public void onSuccess(JSONObject result, Date date,
					String start_date) {
				dialog.dismiss();
				// setRecentData(result);
			}

			public void onFailure(Throwable error) {
				dialog.dismiss();
			}
		};
	}

	private class MyPagerAdapter extends PagerAdapter {

		private ArrayList<StatRecord> statRecords;

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			current_page_view = (View) object;
			current_page_view_position = position;
		}
		
		SKAMainResultsActivity mMainResultsActivity;

		public MyPagerAdapter(SKAMainResultsActivity inMainResultsActivity) {
			
			mMainResultsActivity = inMainResultsActivity;
			
			statRecords = new ArrayList<StatRecord>();
			statRecords.add(new StatRecord());
			// views.get(0) is the aggregate view

			JSONObject summary = dbHelper.getArchiveDataSummary();

			try {
				total_archive_records = summary.getInt("counter");
				String last_run_test = summary.getString("enddate");

				if (Long.parseLong(last_run_test) != 0) {
					last_run_test_formatted = new SKDateFormat(context)
							.UITime(Long.parseLong(last_run_test));

				} else {
					last_run_test_formatted = "";
				}
			} catch (JSONException e1) {
				SKLogger.e(this, "Error in reading from JSONObject.", e1);
			}

			for (int i = 0; i < total_archive_records; i++) {
				statRecords.add(new StatRecord());
				// load blank records ready for populating
			}
		}

		public void readArchiveItem(int archiveItemIndex) {
			JSONObject archive;
			try {

				archive = dbHelper.getArchiveData(archiveItemIndex);

			} catch (Exception e) {
				SKLogger.e(this, "Error in reading archive item " + archiveItemIndex, e);
				SKLogger.sAssert(getClass(), false);
				return;
			}
			
			if (archive == null) {
				SKLogger.sAssert(getClass(), false);
				return;
			}

			// read headers of json
			String datetime = "";
			String dtime_formatted;
			try {
				datetime = archive.getString("dtime");

				dtime_formatted = new SKDateFormat(context).UITime(Long
						.parseLong(datetime));
				statRecords.get(archiveItemIndex + 1).time_stamp = dtime_formatted;

			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			// unpack activemetrics
			JSONArray results = null;

			try {
				results = archive.getJSONArray("activemetrics");
			} catch (JSONException je) {
				SKLogger.e(this, "Exception in reading active metrics array: "
						+ je.getMessage());
			}

			if (results != null) {
				int itemcount = 0;

				for (itemcount = 0; itemcount < results.length(); itemcount++) {

					JSONObject user = null;
					try {
						user = results.getJSONObject(itemcount);
					} catch (JSONException je) {
						SKLogger.e(
								this,
								"Exception in reading JSONObject: "
										+ je.getMessage());
					}

					if (user != null) {
						try {
							String testnumber = user.getString("test");
							String location = user.getString("location");
							String success = user.getString("success");
							String hrresult = user.getString("hrresult");

							if (success.equals("0")) {
								hrresult = getString(R.string.failed);
							}

							if (testnumber.equals("" + TestResult.UPLOAD_TEST_ID)) {
								statRecords.get(archiveItemIndex + 1).tests_location = location;
								statRecords.get(archiveItemIndex + 1).upload_location = location;
								statRecords.get(archiveItemIndex + 1).upload_result = hrresult;

							}
							if (testnumber.equals("" + TestResult.DOWNLOAD_TEST_ID)) {
								statRecords.get(archiveItemIndex + 1).tests_location = location;
								statRecords.get(archiveItemIndex + 1).download_location = location;
								statRecords.get(archiveItemIndex + 1).download_result = hrresult;
							}

							if (testnumber.equals("" + TestResult.LATENCY_TEST_ID)) {
								statRecords.get(archiveItemIndex + 1).tests_location = location;
								statRecords.get(archiveItemIndex + 1).latency_location = location;
								statRecords.get(archiveItemIndex + 1).latency_result = hrresult;
							}

							if (testnumber.equals("" + TestResult.PACKETLOSS_TEST_ID)) {
								statRecords.get(archiveItemIndex + 1).tests_location = location;
								statRecords.get(archiveItemIndex + 1).packetloss_location = location;
								statRecords.get(archiveItemIndex + 1).packetloss_result = hrresult;
							}

							if (testnumber.equals("" + TestResult.JITTER_TEST_ID)) {
								statRecords.get(archiveItemIndex + 1).tests_location = location;
								statRecords.get(archiveItemIndex + 1).jitter_location = location;
								statRecords.get(archiveItemIndex + 1).jitter_result = hrresult;
							}

						} catch (JSONException je) {
							SKLogger.e(
									this,
									"Exception in reading JSONObject: "
											+ je.getMessage());
						}
					}
				}
			}

			// Log.d(this.getClass().toString(), "*** SamKnowsAggregateStatViewerActivity:UNPACK PASSIVE METRICS!!");
			
			// unpack passivemetrics
			results = null;
			try {
				results = archive.getJSONArray("passivemetrics");
			} catch (JSONException je) {
				SKLogger.e(this,
						"Exception in reading JSONObject: " + je.getMessage());
			}

			if (results != null) {
				int itemcount = 0;
				
				for (itemcount = 0; itemcount < results.length(); itemcount++) {
					JSONObject user = null;
					try {
						user = results.getJSONObject(itemcount);
					} catch (JSONException je) {
						SKLogger.e(
								this,
								"Exception in reading JSONObject: "
										+ je.getMessage());
						user = null;
					}
				
					if (user == null) {
					  continue;
					}
						
					captureUserMetricAtArchiveItemIndex(archiveItemIndex, user);
				}
			}

		}

		private void captureUserMetricAtArchiveItemIndex(int archiveItemIndex,
				JSONObject user) {
			if (user == null) {
				Log.e(this.getClass().toString(), "captureUserMetricAtArchiveItemIndex - user == null");
				return;
			}

			
			try {
				String metric = user.getString("metric");
				String value = user.getString("value");
				String type = user.getString("type");
				
				//Log.d("*** SamKnowsAggregateStatViewerActivity:MyPagerAdapter", "INFO - metric (" + metric +"), type=(" + type + "), value=(" + value + ")");

				// There is a completed disconnect between the integer metric value
				// as considered by the PassiveMetric class, and the layout "passive metric"
				// identifiers, such as R.id.passivemetric20.
				// The only safe thing to do, is to use as String value to determine
				// which resource id to use.

				if (metric.equals("connected")) { // connected
					statRecords.get(archiveItemIndex + 1).passivemetric1 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric1_type = type;
				} else if (metric.equals("connectivitytype")) { // connectivity
					// type
					statRecords.get(archiveItemIndex + 1).passivemetric2 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric2_type = type;
				} else if (metric.equals("gsmcelltowerid")) { // cell tower id
					// TODO - Giancarlo says this isn't displayed in SamKnowsAggregateStatViewerActivity - "Archived Result" (archive_result)
					// METRIC_TYPE.GSMCID("gsmcelltowerid")
					statRecords.get(archiveItemIndex + 1).passivemetric3 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric3_type = type;
				} else if (metric.equals("gsmlocationareacode")) { // cell tower
					// location area
					statRecords.get(archiveItemIndex + 1).passivemetric4 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric4_type = type;
				} else if (metric.equals("gsmsignalstrength")) { // signal strength
					statRecords.get(archiveItemIndex + 1).passivemetric5 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric5_type = type;
				} else if (metric.equals("networktype")) { // bearer
					statRecords.get(archiveItemIndex + 1).passivemetric6 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric6_type = type;
				} else if (metric.equals("networkoperatorname")) { // network
					// operator
					statRecords.get(archiveItemIndex + 1).passivemetric7 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric7_type = type;
				} else if (metric.equals("latitude")) { // latitude
					statRecords.get(archiveItemIndex + 1).passivemetric8 = 
							SKCommon.sGetDecimalStringAnyLocaleAs1Pt5LocalisedString(value);
					statRecords.get(archiveItemIndex + 1).passivemetric8_type = type;
				} else if (metric.equals("longitude")) { // longitude
					statRecords.get(archiveItemIndex + 1).passivemetric9 = 
							SKCommon.sGetDecimalStringAnyLocaleAs1Pt5LocalisedString(value);
					statRecords.get(archiveItemIndex + 1).passivemetric9_type = type;
				} else if (metric.equals("accuracy")) { // accuracy
					statRecords.get(archiveItemIndex + 1).passivemetric10 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric10_type = type;
				} else if (metric.equals("locationprovider")) { // location
					// provider
					statRecords.get(archiveItemIndex + 1).passivemetric11 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric11_type = type;
				} else if (metric.equals("simoperatorcode")) { // sim operator code
					statRecords.get(archiveItemIndex + 1).passivemetric12 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric12_type = type;
				} else if (metric.equals("simoperatorname")) { // sim operator name
					statRecords.get(archiveItemIndex + 1).passivemetric13 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric13_type = type;
				} else if (metric.equals("imei")) { // imei
					statRecords.get(archiveItemIndex + 1).passivemetric14 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric14_type = type;
				} else if (metric.equals("imsi")) { // imsi
					statRecords.get(archiveItemIndex + 1).passivemetric15 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric15_type = type;
				} else if (metric.equals("manufactor")) { // manufacturer
					statRecords.get(archiveItemIndex + 1).passivemetric16 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric16_type = type;
				} else if (metric.equals("model")) { // model
					statRecords.get(archiveItemIndex + 1).passivemetric17 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric17_type = type;
				} else if (metric.equals("ostype")) { // os type
					statRecords.get(archiveItemIndex + 1).passivemetric18 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric18_type = type;
				} else if (metric.equals("osversion")) { // os version
					statRecords.get(archiveItemIndex + 1).passivemetric19 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric19_type = type;
				} else if (metric.equals("gsmbiterrorrate")) { // gsmbiterrorrate
					statRecords.get(archiveItemIndex + 1).passivemetric20 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric20_type = type;
				} else if (metric.equals("cdmaecio")) { // cdmaecio
					statRecords.get(archiveItemIndex + 1).passivemetric21 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric21_type = type;
				} else if (metric.equals("phonetype")) { // phone type
					statRecords.get(archiveItemIndex + 1).passivemetric22 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric22_type = type;
				} else if (metric.equals("activenetworktype")) { // active network
					// type
					if(value.length() > 0 ){
						String new_value = value.substring(0, 1).toUpperCase() + value.substring(1);
						statRecords.get(archiveItemIndex + 1).active_network_type = "("+new_value+")";
					}

					//views.get(i + 1).passivemetric23 = value;
					//views.get(i + 1).passivemetric23_type = type;
				} else if (metric.equals("connectionstatus")) { // connection
					// status
					statRecords.get(archiveItemIndex + 1).passivemetric24 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric24_type = type;
				} else if (metric.equals("roamingstatus")) { // roaming status
					statRecords.get(archiveItemIndex + 1).passivemetric25 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric25_type = type;
				} else if (metric.equals("networkoperatorcode")) { // network
					// operator code
					statRecords.get(archiveItemIndex + 1).passivemetric26 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric26_type = type;
				} else if (metric.equals("cdmasignalstrength")) { // cdmasignalstrength
					statRecords.get(archiveItemIndex + 1).passivemetric27 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric27_type = type;
				} else if (metric.equals("cdmabasestationid")) { // cdmabasestationid
					statRecords.get(archiveItemIndex + 1).passivemetric28 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric28_type = type;
				} else if (metric.equals("cdmabasestationlatitude")) { // cdmabasestationlatitude
					statRecords.get(archiveItemIndex + 1).passivemetric29 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric29_type = type;
				} else if (metric.equals("cdmabasestationlongitude")) { // cdmabasestationlongitude
					statRecords.get(archiveItemIndex + 1).passivemetric30 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric30_type = type;
				} else if (metric.equals("cdmanetworkid")) { // cdmanetworkid
					statRecords.get(archiveItemIndex + 1).passivemetric31 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric31_type = type;
				} else if (metric.equals("cdmasystemid")) { // cdmasystemid
					statRecords.get(archiveItemIndex + 1).passivemetric32 = value;
					statRecords.get(archiveItemIndex + 1).passivemetric32_type = type;
				} else {
					Log.d("SamKnowsAggregateStatViewerActivity:MyPagerAdapter", "WARNING - unsupported metric (" + metric +")");
				}

			} catch (JSONException je) {
				Log.d("SamKnowsAggregateStatViewerActivity:MyPagerAdapter", "ERROR - exception reading JSON object (" + je.getMessage() +")");
				
				SKLogger.e(
						this,
						"Exception in reading JSONObject: "
								+ je.getMessage());
			}
		}

		@Override
		public void destroyItem(View view, int arg1, Object object) {
			((ViewPager) view).removeView((StatView) object);
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return statRecords.size();
		}

		@SuppressLint("InflateParams")
		@Override
		public Object instantiateItem(View view, int position) {
			bandera=0;
			StatView sc = new StatView(SKAMainResultsActivity.this);
			sc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			sc.setFillViewport(true);

			LayoutInflater inflater = (LayoutInflater) SKAMainResultsActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



			View subview_archive;

			// If position is zero take care of the visibility of the messages
			if(position == 0) {
				subview = inflater.inflate(R.layout.ska_main_results_activity_runnow_and_graphs, null);
				subview.setTag(position);

        if (SKApplication.getAppInstance().hideJitter() == true) {
          // Hide some elements!
				//  subview.findViewById(R.id.jitter_panel).setVisibility(View.GONE);
        }

        if (SKApplication.getAppInstance().hideJitterLatencyAndPacketLoss() == true) {
          // Hide some elements!

        }

				sc.addView(subview);

				((ViewPager) view).addView(sc);
				//if there is a problem with with the state machine display the 
				//appropriate message

				setContinuousTestingButton();
				
				if (!SK2AppSettings.getSK2AppSettingsInstance().stateMachineStatus()) {
					// We are NOT activated!
				} else {
					// We are activated!

					if (total_archive_records == 0) {
					} else {
					}
				}

				//in case there are results to display load it
				//no matter if the state machine status
				if(total_archive_records > 0){

					adapter.readArchiveItem(0);
				}

				//String caption = getString(R.string.last_run) + " " + last_run_test_formatted;
				
        if (SKApplication.getAppInstance().hideJitter() == true) {
        }

        if (SKApplication.getAppInstance().hideJitterLatencyAndPacketLoss() == true) {
	
        }

				buttonSetup();

				loadAverage();
				graphsSetup();

				loadGrids();
			}
			
			subview_archive = inflater.inflate(R.layout.ska_main_results_activity_single_result, null);
			
      if (SKApplication.getAppInstance().hideJitter() == true) {
				// Hide some elements!
        subview_archive.findViewById(R.id.jitter_archive_panel).setVisibility(View.GONE);
      }

      if (SKApplication.getAppInstance().hideJitterLatencyAndPacketLoss() == true) {
			  // Hide some elements!
			  subview_archive.findViewById(R.id.packetloss_archive_panel).setVisibility(View.GONE);
  			subview_archive.findViewById(R.id.latency_archive_panel).setVisibility(View.GONE);
  			subview_archive.findViewById(R.id.jitter_archive_panel).setVisibility(View.GONE);
      }

			//
			// Show or hide the passive results, depending on whether
			// we're looking at mobile (show) or WiFi (hide!)
			//
			if (SKApplication.getNetworkTypeResults() == eNetworkTypeResults.eNetworkTypeResults_WiFi) {
			} else {
			}
			if (position == total_archive_records) {
			} else {
			}
			


			if (position > 0 && position <2) {


				subview_archive.setTag(position);
				sc.addView(subview_archive);

				StatRecord sr = statRecords.get(position);								
				
				
				if (bandera==0){
					
					telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
					potencia_red = phoneStateListener.valor_potencia();
					

					System.out.println("HHHHHHHHHOOOOOOOOOLLLLLLLLLAAAAAAAA");
					
				
						System.out.println("Upload Result:" + sr.upload_result);
						subida= sr.upload_result;
					
						System.out.println("Download Result: " + sr.download_result);					
						bajada = sr.download_result;

					

					
					System.out.println("Packet loss: " + sr.packetloss_result);
					perdida_paquetes = sr.packetloss_result;
					System.out.println("Latencia:" + sr.latency_result);
					latencia = sr.latency_result;
					
					System.out.println("uno: " + potencia_red);
					comenzarLocalizacion();	
					datos_red();										
					bandera++;															
				}
				
				sc.setData(sr);
				
				
				cadena_final = fecha_red + "," + hora_red + "," + bajada + "," + subida + "," + potencia_red + "," + botoncito;
				botoncito=0;
				
				
				
				
				try
				{
				    File ruta_sd = Environment.getExternalStorageDirectory();
				    //File x = new File();
				    
				    
				    File f = new File(ruta_sd.getAbsolutePath(), "registro_datos.txt");	
				    
				    if(f.exists()){
				    	FileWriter fw = new FileWriter(f,true);
				    	fw.write(cadena_final + "\n");
				    	fw.flush();
				    	fw.close();
				    }
				    else{
				    	FileWriter fw = new FileWriter(f);
				    	fw.write(cadena_final + "\n");
				    	fw.flush();
				    	fw.close();
				    }			    		 
				}
				catch (Exception ex)
				{
				    Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
				    System.out.println("valio");
				}		
				
				
				
				

				((ViewPager) view).addView(sc);

				if (position == total_archive_records) {
					sc.setRightPageIndicator(false);
				}

				if (position < total_archive_records) {

					adapter.readArchiveItem(position);
				}
			}

			Util.overrideFonts(SKAMainResultsActivity.this, sc);
			return sc;

		}

		public void loadGrids() {

		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

	//private int mWeeks = 1;
	private DATERANGE_1w1m3m1y mDateRange = DATERANGE_1w1m3m1y.DATERANGE_1w1m3m1y_ONE_WEEK;


	
	// Query average data, and update charts - this might make them invisible, if there is no data!
	void queryAverageDataAndUpdateTheCharts() {
		loadAverage();

		// Update charts - this might make them invisible, if there is no data!
		setGraphDataForColumnIdAndHideIfNoResultsFound(TestResult.DOWNLOAD_TEST_ID);
		setGraphDataForColumnIdAndHideIfNoResultsFound(TestResult.UPLOAD_TEST_ID);
		setGraphDataForColumnIdAndHideIfNoResultsFound(TestResult.LATENCY_TEST_ID);
		setGraphDataForColumnIdAndHideIfNoResultsFound(TestResult.PACKETLOSS_TEST_ID);
		setGraphDataForColumnIdAndHideIfNoResultsFound(TestResult.JITTER_TEST_ID);
	}

	private void RunChoice() {

		storage = CachingStorage.getInstance();
		config = storage.loadScheduleConfig();
		// if config == null the app is not been activate and
		// no test can be run
		if (config == null) {
			// TODO Add an alert that the app has not been init yet
			config = new ScheduleConfig();
		}

		testList = config.manual_tests;
		array_spinner = new String[testList.size() + 1];
		//array_spinner_int = -1;
		int spinner_int = -1;
		Builder builder = new AlertDialog.Builder(this);


				Intent intent = new Intent(SKAMainResultsActivity.this,SKARunningTestActivity.class);
				Bundle b = new Bundle();
				b.putInt("testID", spinner_int);
				intent.putExtras(b);
				startActivityForResult(intent, 1);
				overridePendingTransition(R.anim.transition_in,R.anim.transition_out);

		AlertDialog alert = builder.create();
		alert.show();
  }
	
	
	void startContinuousTestAfterCheckingForDataCap() {

		//Button b = (Button) findViewById(R.id.btnRunContinuousTests);
		
		mContinuousHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				mContinuousState = (ContinuousState) msg.obj;
				setContinuousTestingButton();
				if(mContinuousState == ContinuousState.STOPPED){
					adapter = new MyPagerAdapter(SKAMainResultsActivity.this);
					setTotalArchiveRecords();
					viewPager = (ViewPager) findViewById(R.id.viewPager);
					SKLogger.sAssert(getClass(), viewPager == (ViewPager) findViewById(R.id.viewPager));
					viewPager.setAdapter(adapter);
				}
			}
		};
		MainService.registerContinuousHandler(this ,mContinuousHandler);
		mContinuousState = ContinuousState.STARTING;

		//b.setText(R.string.starting_continuous);
	}
	
	private ContinuousState mContinuousState = ContinuousState.STOPPED;
	private Handler mContinuousHandler;
	private void ContinuousToggle(View v){
		
		//CONTINUOUS TESTING

		if (mContinuousState == ContinuousState.STOPPED) {
		
			if ( 	(SK2AppSettings.getSK2AppSettingsInstance().isDataCapReached()) 
					// Data cap exceeded - but only ask the user if they want to continue, if the app is configured
					// to work like that...
					&& (SKApplication.getAppInstance().getIsDataCapEnabled() == true)
					) {

				SKLogger.d(SKARunningTestActivity.class, "Data cap exceeded");
				new AlertDialog.Builder(this)
				.setMessage(getString(R.string.data_cap_exceeded))
				.setPositiveButton(R.string.ok_dialog,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						startContinuousTestAfterCheckingForDataCap();
					}
				})
				.setNegativeButton(R.string.no_dialog,
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
					}
				}).show();
				return;
			} else {
				startContinuousTestAfterCheckingForDataCap();
			}


		}else if(mContinuousState == ContinuousState.EXECUTING){	
			MainService.stopContinuousExecution();

			((Button) v).setText(R.string.stopping_continuous);
		}

	}
	

	
	private boolean mContinuousRequested = false;
	private MainService mMainService = null;
	private ServiceConnection mConnection;
	
	public boolean forceBackToAllowClose() {
		if (on_aggregate_page) {
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {

		if (on_aggregate_page) {
			// The app can be closed from this page - we expect to be the task root.
			if (this.wouldBackButtonReturnMeToTheHomeScreen()) {
				super.onBackPressed();
				return;
			}
		} else {
			// The app must NOT be closed from this page - change "viewed page" instead.
			viewPager = (ViewPager) findViewById(R.id.viewPager);
			 //viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(0, true); // The true means to perform a smooth scroll!
			// overridePendingTransition(0, 0);
			on_aggregate_page = true;
		}
	}

	// This method will query the data synchronously, for the specified column.

	private JSONObject fetchGraphDataForColumnId(int PColumnId) {
		Calendar fromCal = Calendar.getInstance();
		
		lookBackwardInTime(fromCal);
		
		long startTime = fromCal.getTimeInMillis();

		Calendar upToCal = Calendar.getInstance();
		long upToTime = upToCal.getTimeInMillis();
		
		if (!(startTime < upToTime)) {
			Log.e(this.getClass().toString(), "getDataForColumnId - startTime/upToTime out of range mis-matched");
		}
		
		JSONObject data = dbHelper.fetchGraphData(PColumnId, startTime, upToTime, mDateRange);	
		return data;
	}
	

    // This method will set the graph data and update, based on the specified column;
	// and will make the container layout invisible if there are no results in that data.
	//
	// GIVEN: the response data has been queried synchronously from the local database for the specified column
	// WHEN: there are NO results in the response data
	// THEN: the graph layout will be made invisible (GONE)
	
	private boolean setGraphDataForColumnIdAndHideIfNoResultsFound(int PColumnId) {
		boolean buttonFound = false;
		
		JSONObject data = null;
		try {
			data = fetchGraphDataForColumnId(PColumnId);
			if (data.getJSONArray("results").length() > 0) {
				buttonFound = true;
			}
		} catch (JSONException e1) {
		}
		
		LinearLayout l = null;

		switch (PColumnId) {
		case TestResult.DOWNLOAD_TEST_ID:
		//	l = (LinearLayout) findViewById(R.id.download_content);
			graphHandlerDownload.updateGraphWithTheseResults(data, mDateRange);
			break;
		case TestResult.UPLOAD_TEST_ID:
			//l = (LinearLayout) findViewById(R.id.upload_content);
			graphHandlerUpload.updateGraphWithTheseResults(data, mDateRange);
			break;
		case TestResult.LATENCY_TEST_ID:
			//l = (LinearLayout) findViewById(R.id.latency_content);
			graphHandlerLatency.updateGraphWithTheseResults(data, mDateRange);
			break;
		case TestResult.PACKETLOSS_TEST_ID:
			//l = (LinearLayout) findViewById(R.id.packetloss_content);
			graphHandlerPacketLoss.updateGraphWithTheseResults(data, mDateRange);
			break;
		case TestResult.JITTER_TEST_ID:
			//l = (LinearLayout) findViewById(R.id.jitter_content);
			graphHandlerJitter.updateGraphWithTheseResults(data, mDateRange);
			break;
		default:
			Log.e(this.getClass().toString(), "setGraphDataForColumnIdAndHideIfNoResultsFound - unexpected result");
			return buttonFound;
		}
		
		if (l == null) {
			Log.e(this.getClass().toString(), "setGraphDataForColumnIdAndHideIfNoResultsFound - l == null");
		}


		
		return buttonFound;
	}

	@Override
	public void onClick(View v) {
		// Toast.makeText(this,"clicked ..."+v.getId(),3000).show();

		int grid = 0;
		boolean buttonfound = false;
		ImageView button = null;
		LinearLayout l = null;
		int testid = 0;

		int id = v.getId();

	

		// actions

		if (l.getVisibility() != View.VISIBLE) {

			button.setBackgroundResource(R.drawable.btn_open_selector);
			button.setContentDescription(getString(R.string.close_panel));
			// graphHandler1.update();
			clearGrid(grid);
			loadDownloadGrid(testid, grid, 0, ITEMS_PER_PAGE);

			l.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
			l.setVisibility(View.VISIBLE);

		} else {

			l.setVisibility(View.GONE);

			button.setBackgroundResource(R.drawable.btn_closed_selector);
			button.setContentDescription(getString(R.string.open_panel));
		}

	}

	public class MyAnimationListener implements AnimationListener {
		View view;

		public void setView(View view) {
			this.view = view;
		}

		public void onAnimationEnd(Animation animation) {
			view.setVisibility(View.INVISIBLE);
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public void onAnimationStart(Animation animation) {
		}
	}

}
