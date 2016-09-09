package johnny.studio.notouch;

import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity implements android.widget.RadioGroup.OnCheckedChangeListener {

	private MyApplication myApp; 

	ComponentName mService;

	private NotificationManager mNM;
	private Notification mNoti;

	private Switch mySwitch;
	private Switch mySwitch_booting_start, mySwitch_proxi, mySwitch_lockbutton_hide, mySwitch_text_guide, mySwitch_continue_touch_unlock;

	// Debugging
	private static final String TAG = "TSL";
	private static final boolean D = false;

	RadioGroup radioGroup;
	RadioButton rb1;
	RadioButton rb2;
	RadioButton rb3;

	// Seek Bar
	SeekBar changeIconSize_seekbar;
	TextView sizeSeekbar;
	
	Drawable SizeChangedLockButton;
	
	private ImageView guideImg;

	private float UPDATE_TIP_VER = 0; //version 
	
	private BackPressCloseHandler backPressCloseHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);


		myApp = (MyApplication)getApplicationContext();
		
		final Intent Service_Intent = new Intent(this, unLockService.class);

		/* 투명 Activity */
		//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		//                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		//		buttonNoti();

		mySwitch = (Switch) findViewById(R.id.mySwitch);
		mySwitch.setChecked(myApp.get_Service_Status());

		mySwitch_booting_start = (Switch) findViewById(R.id.mySwitch2);
		mySwitch_booting_start.setChecked(myApp.get_Service_Booting_Start());

		//mySwitch_proxi
		mySwitch_proxi = (Switch) findViewById(R.id.mySwitch3);
		mySwitch_proxi.setChecked(myApp.get_Service_Proxi());

		mySwitch_lockbutton_hide = (Switch) findViewById(R.id.mySwitch4);
		mySwitch_lockbutton_hide.setChecked(myApp.get_Service_unlock_button_hide());

		//mySwitch_text_guide
		mySwitch_text_guide = (Switch) findViewById(R.id.mySwitch5);
		mySwitch_text_guide.setChecked(myApp.get_Service_Unlock_Noti_setting());

		//mySwitch_continue_touch_unlock
		mySwitch_continue_touch_unlock = (Switch) findViewById(R.id.mySwitch6);
		mySwitch_continue_touch_unlock.setChecked(myApp.get_Service_continue_touch_unlock());

		changeIconSize_seekbar  = (SeekBar) findViewById(R.id.seekBar1);
		changeIconSize_seekbar.setMax(90);
		sizeSeekbar = (TextView)findViewById(R.id.subtext_size_seekbar);		

		//attach a listener to check for changes in state
		mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(D) Toast.makeText(getApplicationContext(), "ON", 1).show();
					myApp.set_Service_Status(true);
					startService(Service_Intent);
					//					mNM.notify(7777, mNoti);
					finish();
				} else {
					if(D) Toast.makeText(getApplicationContext(), "OFF", 1).show();
					stopService(Service_Intent);
					myApp.set_Service_Status(false);
					//					mNM.cancelAll();
				}

			}
		});

		//mySwitch_booting_start
		mySwitch_booting_start.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(D) Toast.makeText(getApplicationContext(), "Booting Auto Start!", 1).show();
					myApp.set_Service_Booting_Start(true);
				} else {
					if(D) Toast.makeText(getApplicationContext(), "Booting Manual Start!", 1).show();
					myApp.set_Service_Booting_Start(false);
				}

			}
		});

		//mySwitch_proxi
		mySwitch_proxi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(D) Toast.makeText(getApplicationContext(), "Proximity On!", 1).show();
					myApp.set_Service_Proxi(true);
					if(myApp.get_Service_Status()){
						if(D) Log.e(TAG, "Proximity SW ON");
						Intent Service_Proxi_On_intent = new Intent("android.intent.action.unLockService.proximity_on");
						PendingIntent svProxiOn = PendingIntent.getService(getApplicationContext(), 0, Service_Proxi_On_intent, 0);
						try {
							svProxiOn.send();
						} catch (CanceledException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					if(D) Toast.makeText(getApplicationContext(), "Proximity OFF!", 1).show();
					myApp.set_Service_Proxi(false);
					if(myApp.get_Service_Status()){
						Intent Service_Proxi_Off_intent = new Intent("android.intent.action.unLockService.proximity_off");
						PendingIntent svProxiOff = PendingIntent.getService(getApplicationContext(), 0, Service_Proxi_Off_intent, 0);
						try {
							svProxiOff.send();
						} catch (CanceledException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		});


		//mySwitch_lockbutton_hide
		mySwitch_lockbutton_hide.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(D) Toast.makeText(getApplicationContext(), "Button hide On!", 1).show();
					myApp.set_Service_unlock_button_hide(true);
					Toast.makeText(getApplicationContext(), "Service 시작 후 노티 메뉴에서 화면 잠금 기능 사용할 수 있습니다.", 1).show(); 

					Intent Service_unlock_button_hide_on_intent = new Intent("android.intent.action.unLockService.unlock_button_hide_on");
					PendingIntent svUnlockButtonHideOn = PendingIntent.getService(getApplicationContext(), 0, Service_unlock_button_hide_on_intent, 0);
					try {
						svUnlockButtonHideOn.send();
					} catch (CanceledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					if(D) Toast.makeText(getApplicationContext(), "Button hide OFF!", 1).show();
					myApp.set_Service_unlock_button_hide(false);

					Intent Service_unlock_button_hide_off_intent = new Intent("android.intent.action.unLockService.unlock_button_hide_off");
					PendingIntent svUnlockButtonHideOff = PendingIntent.getService(getApplicationContext(), 0, Service_unlock_button_hide_off_intent, 0);
					try {
						svUnlockButtonHideOff.send();
					} catch (CanceledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		});

		//mySwitch_text_guide
		mySwitch_text_guide.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(D) Toast.makeText(getApplicationContext(), "Text Guide On!", 1).show();
					//myApp.set_Service_text_guide(true);
					myApp.set_Service_Unlock_Noti_setting(true);
				} else {
					if(D) Toast.makeText(getApplicationContext(), "Text Guide OFF!", 1).show();
					//myApp.set_Service_text_guide(false);
					myApp.set_Service_Unlock_Noti_setting(false);
				}

			}
		});

		//mySwitch_continue_touch_unlock
		mySwitch_continue_touch_unlock.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(D) Toast.makeText(getApplicationContext(), "Continue touch unlock On!", 1).show();
					myApp.set_Service_continue_touch_unlock(true);
				} else {
					if(D) Toast.makeText(getApplicationContext(), "Continue touch unlock OFF!", 1).show();
					myApp.set_Service_continue_touch_unlock(false);
				}

			}
		});


		//		/*****************************/
		//		currentApiVersion = android.os.Build.VERSION.SDK_INT;
		//
		//	    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		//	        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		//	        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		//	        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		//	        | View.SYSTEM_UI_FLAG_FULLSCREEN
		//	        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		//
		//	    // This work only for android 4.4+
		//	    if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
		//	    {
		//
		//	        getWindow().getDecorView().setSystemUiVisibility(flags);
		//
		//	        // Code below is to handle presses of Volume up or Volume down.
		//	        // Without this, after pressing volume buttons, the navigation bar will
		//	        // show up and won't hide
		//	        final View decorView = getWindow().getDecorView();
		//	        decorView
		//	            .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
		//	            {
		//
		//	                @Override
		//	                public void onSystemUiVisibilityChange(int visibility)
		//	                {
		//	                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
		//	                    {
		//	                        decorView.setSystemUiVisibility(flags);
		//	                    }
		//	                }
		//	            });
		//	    }
		//		/*****************************/

		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		rb1 = (RadioButton)findViewById(R.id.rb1);
		rb2 = (RadioButton)findViewById(R.id.rb2);
		rb3 = (RadioButton)findViewById(R.id.rb3);
		radioGroup.setOnCheckedChangeListener(this);

		guideImg = (ImageView) findViewById(R.id.guideimg1);


		// UPDATE&TIP POPUP
		String version;
		try {
			PackageInfo i = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
			version = i.versionName;
			UPDATE_TIP_VER = ((int) Float.parseFloat(version));
		} catch(NameNotFoundException e) { }

		if(myApp.get_Service_Update_Tip_ver() < UPDATE_TIP_VER){
			myApp.set_Service_Update_Info_Visible(true);
		}
		if(myApp.get_Service_Update_Info_Visible()){
			UpdateNoti_DialogHtmlView();
			myApp.set_Service_Update_Tip_ver((int) UPDATE_TIP_VER);
		}
		//myApp.set_Service_Lock_Icon_Size(60);
		
		changeIconSize_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                //tv.setText("onStop TrackingTouch");
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) {
                //tv.setText("onStart TrackingTouch");
            }
 
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //tv.setText("onProgressChanged : " + progress);
            	
            	//changeIconSize_seekbar.setThumb(getResources().getDrawable(R.drawable.unlocked));
            	if(progress < 30){
            		progress = 30;
            	}
            	sizeSeekbar.setText("size : " + progress);
            	myApp.set_Service_Lock_Icon_Size(progress);
            	changeLockIconSize();
            	changeIconSize_seekbar.setThumb(SizeChangedLockButton);
            }
        });
		
		backPressCloseHandler = new BackPressCloseHandler(this);
		
	}// end of onCreate

	public float convertDpToPixel(float dp)
	{
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}
	
	public void changeLockIconSize(){
		Resources res = getResources();
    	Drawable thumb = res.getDrawable(R.drawable.locked);
    	BitmapDrawable bmpOrg = ((BitmapDrawable)thumb);
    	Bitmap sizeChange = bmpOrg.getBitmap();
    	int w = ((int) convertDpToPixel(myApp.get_Service_Lock_Icon_Size()));
    	int h = ((int) convertDpToPixel(myApp.get_Service_Lock_Icon_Size()));   			
    	Bitmap bmpScaled = Bitmap.createScaledBitmap(sizeChange, w, h, true);
    	
    	SizeChangedLockButton = new BitmapDrawable(res, bmpScaled);
	}
	
	/*****************************/
	//	@Override
	//	public void onWindowFocusChanged(boolean hasFocus)
	//	{
	//	    super.onWindowFocusChanged(hasFocus);
	//	    if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
	//	    {
	//	        getWindow().getDecorView().setSystemUiVisibility(
	//	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	//	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	//	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	//	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	//	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	//	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	//	    }
	//	}
	/*****************************/

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(D) Log.e(TAG, "+ onDestroy +");

		//		if (myApp.get_Service_Status() == true) {
		//			mNM.notify(7777, mNoti);
		//		}else {
		//			mNM.cancelAll();
		//		}
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//myApp.set_Service_Status(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(D) Log.e(TAG, "+ onPause +");

		if(isMyServiceRunning(this, "johnny.studio.notouch.unLockService")){
			if(D) Toast.makeText(getApplicationContext(), "unLockService run", 1).show();
			unLockServiceNoti();
		} else {
			if(D) Toast.makeText(getApplicationContext(), "LockService run", 1).show();
			LockServiceNoti();
		}

		if (myApp.get_Service_Status() == true) {
			mNM.notify(7777, mNoti);
		}else {
			mNM.cancelAll();
		}
	}

	private boolean isMyServiceRunning(Context ctx, String s_service_name) {
		ActivityManager manager = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (s_service_name.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;

	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		backPressCloseHandler.onBackPressed();
		//backbutton_DialogHtmlView();
		
	}
	
	@Override
	public synchronized void onResume() {
		super.onResume();
		if(D) Log.e(TAG, "+ ON RESUME +");

		/* UI복구 */
		mySwitch.setChecked(myApp.get_Service_Status());
		mySwitch_booting_start.setChecked(myApp.get_Service_Booting_Start());
		mySwitch_proxi.setChecked(myApp.get_Service_Proxi());
		mySwitch_lockbutton_hide.setChecked(myApp.get_Service_unlock_button_hide());
		//mySwitch_text_guide.setChecked(myApp.get_Service_text_guide());
		mySwitch_continue_touch_unlock.setChecked(myApp.get_Service_continue_touch_unlock());

		switch(myApp.get_Service_Mode()){
		case 0:
			rb1.setChecked(true);
			guideImg.setImageResource(R.drawable.guide_img1);
			break;
		case 1:
			rb2.setChecked(true);
			guideImg.setImageResource(R.drawable.guide_img2);
			break;
		case 2:
			rb3.setChecked(true);
			guideImg.setImageResource(R.drawable.guide_img3);
			break;
		}
		
		//Stop the Service at Setting Activity.
		stopService(new Intent(this, unLockService.class));
		myApp.set_Service_Status(false);
		mySwitch.setChecked(myApp.get_Service_Status());
		changeLockIconSize();
		changeIconSize_seekbar.setThumb(SizeChangedLockButton);
		changeIconSize_seekbar.setProgress(myApp.get_Service_Lock_Icon_Size());
		if(D) Toast.makeText(getApplicationContext(), "progress : " + myApp.get_Service_Lock_Icon_Size(), 1).show();
		
		
	}

	/* 노티 */
	//	private void buttonNoti(){
	//
	//		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	//
	//		Intent MMPService_intent = new Intent("johnny.studio.mmp.MMP_Service");
	//		Intent MUSIC_intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
	//
	//		Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
	//		activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
	//				| Intent.FLAG_ACTIVITY_CLEAR_TOP 
	//				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
	//
	//		PendingIntent content = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);
	//		PendingIntent svContent = PendingIntent.getService(getApplicationContext(), 0, MMPService_intent, 0);
	//		PendingIntent bdContent = PendingIntent.getActivity(getApplicationContext(), 0, MUSIC_intent, 0);
	//
	//		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notifiview);
	//		//Update Next version//remoteViews.setOnClickPendingIntent(R.id.btn_service, svContent);
	//		remoteViews.setOnClickPendingIntent(R.id.btn_servicesFinish, bdContent);
	//
	//		mNoti = new Notification(R.drawable.unlocked, "Touch Screen Lock", System.currentTimeMillis());
	//		mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
	//		mNoti.flags |= Notification.FLAG_ONGOING_EVENT;
	//
	//		mNoti.contentView = remoteViews;
	//		mNoti.contentIntent = content;
	//	}

	private void unLockServiceNoti(){

		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Intent Service_Switch_intent = new Intent("android.intent.action.unLockService.switch");
		Intent Service_Youtube_intent = new Intent("android.intent.action.unLockService.youtube");
		Intent Service_Finish_intent = new Intent("android.intent.action.unLockService.finish");

		Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
		activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
				| Intent.FLAG_ACTIVITY_CLEAR_TOP 
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent content = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);
		PendingIntent svSwitch = PendingIntent.getService(getApplicationContext(), 0, Service_Switch_intent, 0);
		PendingIntent svYoutube = PendingIntent.getService(getApplicationContext(), 0, Service_Youtube_intent, 0);
		PendingIntent svFinish = PendingIntent.getService(getApplicationContext(), 0, Service_Finish_intent, 0);

		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notifiview_unlocked);
		remoteViews.setOnClickPendingIntent(R.id.btn_servicesSwitch, svSwitch);
		remoteViews.setOnClickPendingIntent(R.id.btn_youtube, svYoutube);
		remoteViews.setOnClickPendingIntent(R.id.btn_servicesFinish, svFinish);


		mNoti = new Notification(R.drawable.unlocked, "Touch Screen Lock", System.currentTimeMillis());
		mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
		mNoti.flags |= Notification.FLAG_ONGOING_EVENT;

		mNoti.contentView = remoteViews;
		mNoti.contentIntent = content;

	}

	private void LockServiceNoti(){

		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Intent Service_Switch_intent = new Intent("android.intent.action.LockService.switch");
		Intent Service_Youtube_intent = new Intent("android.intent.action.LockService.youtube");
		Intent Service_Finish_intent = new Intent("android.intent.action.LockService.finish");

		Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
		activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
				| Intent.FLAG_ACTIVITY_CLEAR_TOP 
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent content = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);
		PendingIntent svSwitch = PendingIntent.getService(getApplicationContext(), 0, Service_Switch_intent, 0);
		PendingIntent svYoutube = PendingIntent.getService(getApplicationContext(), 0, Service_Youtube_intent, 0);
		PendingIntent svFinish = PendingIntent.getService(getApplicationContext(), 0, Service_Finish_intent, 0);

		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notifiview_locked);
		remoteViews.setOnClickPendingIntent(R.id.btn_servicesSwitch, svSwitch);
		remoteViews.setOnClickPendingIntent(R.id.btn_youtube, svYoutube);
		remoteViews.setOnClickPendingIntent(R.id.btn_servicesFinish, svFinish);


		/* 옵션 메뉴 */
		mNoti = new Notification(R.drawable.locked, "Touch Screen Lock", System.currentTimeMillis());
		mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
		mNoti.flags |= Notification.FLAG_ONGOING_EVENT;

		mNoti.contentView = remoteViews;
		mNoti.contentIntent = content;
	}

	private void backbutton_DialogHtmlView() {
		final Intent Service_Intent = new Intent(this, unLockService.class);
		Dialog dialog = null;
		CustomDialog.Builder customBuilder = new
				CustomDialog.Builder(MainActivity.this);
		customBuilder.setTitle(R.string.dialog_back_button)
		.setMessage(R.string.back_button_finish)
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		})
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				myApp.set_Service_Status(true);
				startService(Service_Intent);
				finish();
			}
		});
		dialog = customBuilder.create();
		dialog.show();
	}

	private void UpdateNoti_DialogHtmlView() {
		Dialog dialog = null;
		CustomDialog.Builder customBuilder = new
				CustomDialog.Builder(MainActivity.this);
		customBuilder.setTitle(R.string.dialog_update_info)
		.setMessage(R.string.update_info)
		.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.setPositiveButton(R.string.no_again, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				myApp.set_Service_Update_Info_Visible(false);
				dialog.dismiss();
			}
		});
		dialog = customBuilder.create();
		dialog.show();
	}


	private void AppInfo_DialogHtmlView() {
		Dialog dialog = null;
		CustomDialog.Builder customBuilder = new
				CustomDialog.Builder(MainActivity.this);
		customBuilder.setTitle(R.string.optionmenu_app_info)
		.setMessage(R.string.notouch_introduce)
		.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.setPositiveButton(R.string.more, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=johnny.studio.notouch"));
				startActivity(intent); 
				dialog.dismiss();
			}
		});
		dialog = customBuilder.create();
		dialog.show();
	}


	private void HowTo_DialogHtmlView() {
		Dialog dialog = null;
		CustomDialog.Builder customBuilder = new
				CustomDialog.Builder(MainActivity.this);
		customBuilder.setTitle(R.string.optionmenu_how_to)
		.setMessage(R.string.notouch_howto)
		.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.setPositiveButton(R.string.youtube_link, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://youtu.be/WoukcuPON7k"));
				startActivity(intent); 
				dialog.dismiss();
			}
		});
		dialog = customBuilder.create();
		dialog.show();
	}


	private void unlock_notification_DialogHtmlView() {
		Dialog dialog = null;
		CustomDialog.Builder customBuilder = new
				CustomDialog.Builder(MainActivity.this);
		customBuilder.setTitle(R.string.unlock_notification)
		.setMessage(R.string.set_unlock_notification)
		.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				myApp.set_Service_Unlock_Noti_setting(false);
				dialog.dismiss();
			}
		})
		.setPositiveButton(R.string.use, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				myApp.set_Service_Unlock_Noti_setting(true);
				dialog.dismiss();
			}
		});
		dialog = customBuilder.create();
		dialog.show();
	}






	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Locale systemLocale = getResources().getConfiguration().locale;

		switch(item.getItemId()){

		case R.id.app_info:
			if(D) Toast.makeText(getApplicationContext(), "app_info", 1).show();
			AppInfo_DialogHtmlView();
			break;
		case R.id.how_to:
			if(D) Toast.makeText(getApplicationContext(), "how_to", 1).show();
			HowTo_DialogHtmlView();
			break;
//		case R.id.unlock_notification:
//			if(D) Toast.makeText(getApplicationContext(), "unlock_notification", 1).show();
//			unlock_notification_DialogHtmlView();
//			break;
		case R.id.share:
			if(D) Toast.makeText(getApplicationContext(), "share", 1).show();
			Intent msg = new Intent(Intent.ACTION_SEND);
			msg.addCategory(Intent.CATEGORY_DEFAULT);	
			if(systemLocale.getLanguage().equals("ko")){
				msg.putExtra(Intent.EXTRA_SUBJECT, "NO TOUCH!! 앱을 소개 합니다.");
				msg.putExtra(Intent.EXTRA_TEXT, "\n여러분, 유투브 영상 보실 때 터치를 잘못했다가 끊기면 열받죠? 또는 유투브로 노래 소리만 듣고 싶은데 주머니에 넣지도 못하겠고..\n이런 분들을 위해 NO TOUCH!!(노타치!! 서비스)가 출시되었습니다.여러분의 많은 사랑 부탁 드립니다.\n https://play.google.com/store/apps/details?id=johnny.studio.notouch");
				msg.putExtra(Intent.EXTRA_TITLE, "제목");
			}else {
				msg.putExtra(Intent.EXTRA_SUBJECT, "★Release NO TOUCH!! service ★");
				msg.putExtra(Intent.EXTRA_TEXT, "\nNO TOUCH is released! Let me introduce NO TOUCH!! service.\n https://play.google.com/store/apps/details?id=johnny.studio.notouch");
				msg.putExtra(Intent.EXTRA_TITLE, "NO TOUCH!!");
			}
			msg.setType("text/plain");    
			startActivity(Intent.createChooser(msg, "공유"));
			break;
		case R.id.evaluation:
			if(D) Toast.makeText(getApplicationContext(), "evaluation", 1).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=johnny.studio.notouch"));
			startActivity(intent); 
			break;
			//		case R.id.etc:
			//			if(D) Toast.makeText(getApplicationContext(), "etc", 1).show();
			//			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (radioGroup.getCheckedRadioButtonId() == R.id.rb1) {
			if(D) Toast.makeText(getApplicationContext(), "타치 잠금 모드", 1).show();
			myApp.set_Service_Mode(0);
			guideImg.setImageResource(R.drawable.guide_img1);
		} else if (radioGroup.getCheckedRadioButtonId() == R.id.rb2) {
			if(D) Toast.makeText(getApplicationContext(), "음악 모드", 1).show();
			myApp.set_Service_Mode(1);
			guideImg.setImageResource(R.drawable.guide_img2);
		} else {
			if(D) Toast.makeText(getApplicationContext(), "복합 모드", 1).show();
			myApp.set_Service_Mode(2);
			guideImg.setImageResource(R.drawable.guide_img3);
		}
	}
}
