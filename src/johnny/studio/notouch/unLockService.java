package johnny.studio.notouch;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RemoteViews;
import android.widget.Toast;

public class unLockService extends Service {

	private MyApplication myApp;

	// Debugging
	private static final String TAG = "TSL";
	private static final boolean D = false;

	//SENSOR_S
	private float p_value;
	private SensorManager sm;
	private SensorEventListener proxi;
	private Sensor proxiSensor;
	//SENSOR_E

	/* 노티 */
	private NotificationManager mNM;
	private Notification mNoti;

	private View mView;
	private ImageView unlockbutton/*, unlockbutton_hide*/;
	private WindowManager mManager;
	private WindowManager.LayoutParams mParams;

	private float mTouchX, mTouchY;
	private int mViewX, mViewY;

	private boolean isMove = false;
	private boolean isLongTouch = false;
	private boolean proximity_near = false;

	public static int LONG_PRESS_TIME = 500;
	
	Drawable SizeChangedLockButton;

	final Handler mHandler = new Handler();
	Runnable mLongPressed = new Runnable(){
		public void run(){
			if(D) Toast.makeText(getApplicationContext(), "롱롱 터치 롱롱 터치!!", Toast.LENGTH_SHORT).show();
			Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
					| Intent.FLAG_ACTIVITY_CLEAR_TOP 
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(activityIntent);
			isLongTouch = true;
		}
	};

	private OnTouchListener mViewTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isMove = false;
				isLongTouch = false;

				mTouchX = event.getRawX();
				mTouchY = event.getRawY();
				mViewX = mParams.x;
				mViewY = mParams.y;

				mHandler.postDelayed(mLongPressed, LONG_PRESS_TIME);

				break;

			case MotionEvent.ACTION_UP:
				if (!isMove && !isLongTouch && !proximity_near) {
					if(D) Toast.makeText(getApplicationContext(), "터치됨", Toast.LENGTH_SHORT).show();
					onLongClickUnLockButton();
				}

				mHandler.removeCallbacks(mLongPressed);

				break;

			case MotionEvent.ACTION_MOVE:
				isMove = true;

				int x = (int) (event.getRawX() - mTouchX);
				int y = (int) (event.getRawY() - mTouchY);

				final int num = 50;
				if ((x > -num && x < num) && (y > -num && y < num)) {
					isMove = false;
					break;
				}

				/**
				 * mParams.gravity에 따른 부호 변경
				 * 
				 * LEFT : x가 +
				 * 
				 * RIGHT : x가 -
				 * 
				 * TOP : y가 +
				 * 
				 * BOTTOM : y가 -
				 */
				mParams.x = mViewX + x;
				mParams.y = mViewY + y;

				mManager.updateViewLayout(mView, mParams);

				mHandler.removeCallbacks(mLongPressed);
				break;

				//ToDo			case MotionEvent.ACTION
			}

			return true;
		}
	};


	public void onLongClickUnLockButton() {
		stopService(new Intent(this, unLockService.class));
		startService(new Intent(this, LockService.class));		
	}

	@Override
	public void onCreate() {
		super.onCreate();

		myApp = (MyApplication)getApplicationContext();

		//Sensor
		sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		proxi = new proxiListener();
		proxiSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		if(myApp.get_Service_Proxi())
		{
			sm.registerListener(proxi,proxiSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			sm.unregisterListener(proxi);
		}

		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.service_unlocked, null);

		unlockbutton = (ImageView) mView.findViewById(R.id.unlocked_button);
		//unlockbutton_hide = (ImageView) mView.findViewById(R.id.unlocked_button_hide);
		//unlockbutton_hide.setVisibility(View.INVISIBLE);
		if(myApp.get_Service_unlock_button_hide()){
			unlockbutton.setVisibility(View.INVISIBLE);
			//unlockbutton_hide.setVisibility(View.VISIBLE);

			//popup????
		}
		
		Resources res = getResources();
    	Drawable thumb = res.getDrawable(R.drawable.unlocked);
    	BitmapDrawable bmpOrg = ((BitmapDrawable)thumb);
    	Bitmap sizeChange = bmpOrg.getBitmap();
    	int w = ((int) convertDpToPixel(myApp.get_Service_Lock_Icon_Size()));
    	int h = ((int) convertDpToPixel(myApp.get_Service_Lock_Icon_Size()));   			
    	Bitmap bmpScaled = Bitmap.createScaledBitmap(sizeChange, w, h, true);
    	
    	unlockbutton.setImageBitmap(bmpScaled);
		
		
		mView.setOnTouchListener(mViewTouchListener);
		myApp.set_Service_Status(true);


		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.TOP | Gravity.LEFT;	
		
		mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mManager.addView(mView, mParams);

		
		
		buttonNoti();

		if (myApp.get_Service_Status() == true) {
			mNM.notify(7777, mNoti);
		}else {
			mNM.cancelAll();
		}



	}

	
	public float convertDpToPixel(float dp)
	{
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	/* 노티 */
	private void buttonNoti(){

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

	// onStartcommand
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		String action = null;
		if (intent != null)
		{
			action = intent.getAction();
		}

		if ("android.intent.action.unLockService.switch".equals(action))
		{
			stopSelf();
			startService(new Intent(this, LockService.class));
			if(myApp.get_Service_Proxi())
			{
				sm.unregisterListener(proxi);
			}
		}

		if ("android.intent.action.unLockService.finish".equals(action))
		{
			//	        pause();
			//	        stopForeground(true);
			//	        this.mIsNotificationShowing = false;
			stopSelf();
			myApp.set_Service_Status(false);
		}

		if("android.intent.action.unLockService.youtube".equals(action))
		{
			Intent StartYoutube_intent;
			StartYoutube_intent = this.getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
			this.startActivity(StartYoutube_intent);
		}

		if("android.intent.action.unLockService.proximity_on".equals(action)){

			if(sm != null){
				sm.registerListener(proxi,proxiSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}else{
				//Sensor
				if(myApp.get_Service_Proxi())
				{
					sm.registerListener(proxi,proxiSensor, SensorManager.SENSOR_DELAY_NORMAL);
				} else {
					sm.unregisterListener(proxi);
				}
			}
		}

		if("android.intent.action.unLockService.proximity_off".equals(action)){

			unlockbutton.setVisibility(View.VISIBLE);
			//unlockbutton_hide.setVisibility(View.INVISIBLE);

			if(sm != null) sm.unregisterListener(proxi);
		}

		if("android.intent.action.unLockService.unlock_button_hide_on".equals(action)){
			unlockbutton.setVisibility(View.INVISIBLE);
			//unlockbutton_hide.setVisibility(View.VISIBLE);
		}
		if("android.intent.action.unLockService.unlock_button_hide_off".equals(action)){
			unlockbutton.setVisibility(View.VISIBLE);
		}


		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;


	}


	//Proximity Sensor
	private class proxiListener implements SensorEventListener{
		@Override
		public void onSensorChanged(SensorEvent event){
			p_value = event.values[0];
			Log.d("[SensorEvent2]", "p value : " + p_value);

			if(myApp.get_Service_Proxi())
			{

				if(p_value == 0.f){
					Log.d("[SensorEvent2]", "NEAR!!");
					proximity_near = true;
					if(myApp.get_Service_Unlock_Noti_setting()){
						Toast.makeText(getApplicationContext(), "근접센서에 의하여 화면잠금", Toast.LENGTH_SHORT).show();
					}
					mParams = new WindowManager.LayoutParams(
							WindowManager.LayoutParams.MATCH_PARENT,
							WindowManager.LayoutParams.MATCH_PARENT,
							WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
							PixelFormat.TRANSLUCENT);
					mParams.gravity = Gravity.TOP | Gravity.LEFT;

					mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
					mManager.updateViewLayout(mView, mParams);
				} else {
					Log.d("[SensorEvent2]", "FAR!!");
					proximity_near = false;
					mParams = new WindowManager.LayoutParams(
							WindowManager.LayoutParams.WRAP_CONTENT,
							WindowManager.LayoutParams.WRAP_CONTENT,
							WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
							PixelFormat.TRANSLUCENT);
					mParams.gravity = Gravity.TOP | Gravity.LEFT;

					mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
					mManager.updateViewLayout(mView, mParams);
				}
			} //Toast.makeText(getApplicationContext(), "근접센서에 의하여 화면잠금", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onDestroy() {

		if(sm != null) sm.unregisterListener(proxi);

		super.onDestroy();
		if (mView != null) {
			mManager.removeView(mView);
			mView = null;
		}
		mNM.cancelAll();
		myApp.set_Service_Status(false);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
