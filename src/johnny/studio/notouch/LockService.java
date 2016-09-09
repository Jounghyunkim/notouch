package johnny.studio.notouch;


import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams") public class LockService extends Service {

	private MyApplication myApp; 

	//TIMER_S
	private TimerTask mTask;
	private Timer mTimer;
	//TIMER_E

	// Debugging
	private static final String TAG = "TSL";
	private static final boolean D = false;

	/* 노티 */
	private NotificationManager mNM;
	private Notification mNoti;

	private View mView;
	private ImageView lockedImg, mainScreenImg, dragGuideImg;
	private TextView dragGuideText;
	private WindowManager mManager;
	private WindowManager.LayoutParams mParams;

	private float mTouchX, mTouchY;
	private float mFirstTouchX, mFirstTouchY, mSecondTouchDeltaX, mSecondTouchDeltaY;
	private int mViewX, mViewY;

	private boolean isMove = false;

	private boolean isTimerEnd = true;

	private boolean isDoubleTouchTimerEnd = true;

	private boolean changeScreenImg = true;

	private int currentApiVersion;

	private int threeTouchCount = 0;

	Toast nowToast = null;

	private OnTouchListener mViewTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isMove = false;

				mTouchX = event.getRawX();
				mTouchY = event.getRawY();
				mViewX = mParams.x;
				mViewY = mParams.y;

				if(isTimerEnd){
					isTimerEnd = false;
					mTransferScreen(v);
				}

				if(myApp.get_Service_continue_touch_unlock()){

					if(isDoubleTouchTimerEnd){
						isDoubleTouchTimerEnd = false;
						mFirstTouchX = mTouchX;
						mFirstTouchY = mTouchY;
						Double_Touch_Check();
					}else {
						mSecondTouchDeltaX = mFirstTouchX - mTouchX;
						mSecondTouchDeltaY = mFirstTouchY - mTouchY;
						if(mSecondTouchDeltaX < 70 && mSecondTouchDeltaY < 70)
						{
							threeTouchCount++;
							if(threeTouchCount > 3){
								if(D) Toast.makeText(getApplicationContext(), "Double Touched!!", Toast.LENGTH_SHORT).show();
								Change_to_UnlockService();
							}
						}
					}
				}

				break;

			case MotionEvent.ACTION_UP:
				if(isMove){
					if(D) Toast.makeText(getApplicationContext(), "드레그", Toast.LENGTH_SHORT).show();
				} else {
					if(D) Toast.makeText(getApplicationContext(), "노무브", Toast.LENGTH_SHORT).show();
				}	
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

				if(myApp.get_Service_Mode() == 1){
					if(x>600){
						mView.setBackgroundResource(R.drawable.black_img2);
						dragGuideImg.setImageResource(R.drawable.left_drag);
						dragGuideImg.setVisibility(View.INVISIBLE);
						dragGuideText.setVisibility(View.INVISIBLE);
					}else if(x<-600) {
						mView.setBackgroundResource(R.drawable.black_img);
						dragGuideImg.setImageResource(R.drawable.right_drag);
						dragGuideImg.setVisibility(View.INVISIBLE);
						dragGuideText.setVisibility(View.INVISIBLE);
					}
				} else if(myApp.get_Service_Mode() == 2){
					if(x>600){
						mView.setBackgroundResource(R.drawable.black_img);
						dragGuideImg.setImageResource(R.drawable.left_drag);
						dragGuideImg.setVisibility(View.INVISIBLE);
						dragGuideText.setVisibility(View.INVISIBLE);
					}else if(x<-600) {
						mView.setBackgroundResource(R.drawable.main_screen);
						dragGuideImg.setImageResource(R.drawable.right_drag);
						dragGuideImg.setVisibility(View.INVISIBLE);
						//						dragGuideText.setVisibility(View.INVISIBLE);
					}
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

				//				mManager.updateViewLayout(mView, mParams);

				break;
			}

			return true;
		}
	};


	public void onClickLockButton(View v) {
		stopService(new Intent(this, LockService.class));
		startService(new Intent(this, unLockService.class));
		myApp.set_Service_Status(false);
	}

	public void mOnlyLesteningMode(){
		if(myApp.get_Service_Mode() == 1){
			//mainScreenImg.setImageResource(R.drawable.black_img);
			mView.setBackgroundResource(R.drawable.black_img);
			dragGuideImg.setVisibility(View.VISIBLE);
			dragGuideText.setVisibility(View.VISIBLE);
			mManager.updateViewLayout(mView, mParams);
		} else if(myApp.get_Service_Mode() == 2){
			//mView.setBackgroundResource(R.drawable.black_img);
			dragGuideImg.setVisibility(View.VISIBLE);
			//dragGuideText.setVisibility(View.VISIBLE);
			mManager.updateViewLayout(mView, mParams);
		} else {
			//mainScreenImg.setImageResource(R.drawable.main_screen);
			mView.setBackgroundResource(R.drawable.main_screen);
			mManager.updateViewLayout(mView, mParams);
			dragGuideImg.setVisibility(View.INVISIBLE);
			dragGuideText.setVisibility(View.INVISIBLE);
		}
	}


	public void mTransferScreen(View v) {

		if(myApp.get_Service_Unlock_Noti_setting()){
			if(nowToast == null) {
				nowToast = Toast.makeText(getApplicationContext(), "잠금버튼을 이용하여 잠금해제 하세요!!", Toast.LENGTH_SHORT);
			} else {
				nowToast.setText("잠금버튼을 이용하여 잠금해제 하세요!!");
			}
			nowToast.show();
		}

		lockedImg.setVisibility(View.VISIBLE);

		if(myApp.get_Service_Mode() == 1){
			dragGuideImg.setVisibility(View.VISIBLE);
			dragGuideText.setVisibility(View.VISIBLE);
		} else if(myApp.get_Service_Mode() == 2){
			dragGuideImg.setVisibility(View.VISIBLE);
		}

		Disappear_lock();

	}

	void Change_to_UnlockService(){
		stopSelf();
		startService(new Intent(this, unLockService.class));
	}

	void Double_Touch_Check(){
		/* Service UI. */
		final Context ctx = this;
		Handler mHandler = new Handler();

		Runnable
		makeToast = new Runnable() {
			public void run() {
				//		        Toast.makeText(ctx, "msg", Toast.LENGTH_LONG).show();
				isDoubleTouchTimerEnd = true;
				threeTouchCount = 0;
			}
		};
		mHandler.postDelayed(makeToast, 1000);
	}

	void Disappear_lock(){
		/* Service UI. */
		final Context ctx = this;
		Handler mHandler = new Handler();

		Runnable
		makeToast = new Runnable() {
			public void run() {
				//		        Toast.makeText(ctx, "msg", Toast.LENGTH_LONG).show();
				lockedImg.setVisibility(View.GONE);
				if(myApp.get_Service_Mode() == 1){
					dragGuideImg.setVisibility(View.INVISIBLE);
					dragGuideText.setVisibility(View.INVISIBLE);
				}else if(myApp.get_Service_Mode() == 2){
					dragGuideImg.setVisibility(View.INVISIBLE);
				}
				isTimerEnd = true;
			}
		};
		mHandler.postDelayed(makeToast, 2000);
	}


	@Override
	public void onCreate() {
		super.onCreate();

		myApp = (MyApplication)getApplicationContext();

		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.service_locked, null);

		lockedImg = (ImageView) mView.findViewById(R.id.lock_button);
		mainScreenImg = (ImageView) mView.findViewById(R.id.main_screen);

		dragGuideImg = (ImageView) mView.findViewById(R.id.drag_imageView);
		dragGuideImg.setVisibility(View.INVISIBLE);

		dragGuideText = (TextView) mView.findViewById(R.id.drag_textView);
		dragGuideText.setVisibility(View.INVISIBLE);

		
		Resources res = getResources();
    	Drawable thumb = res.getDrawable(R.drawable.locked);
    	BitmapDrawable bmpOrg = ((BitmapDrawable)thumb);
    	Bitmap sizeChange = bmpOrg.getBitmap();
    	int w = ((int) convertDpToPixel(myApp.get_Service_Lock_Icon_Size()));
    	int h = ((int) convertDpToPixel(myApp.get_Service_Lock_Icon_Size()));   			
    	Bitmap bmpScaled = Bitmap.createScaledBitmap(sizeChange, w, h, true);
    	
    	lockedImg.setImageBitmap(bmpScaled);
		
		
		mView.setOnTouchListener(mViewTouchListener);		

		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
				|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.TOP | Gravity.LEFT;


		final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

		mView.setSystemUiVisibility(flags);

		mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mManager.addView(mView, mParams);
		myApp.set_Service_Status(true);	

		buttonNoti();

		if (myApp.get_Service_Status() == true) {
			mNM.notify(7777, mNoti);
		}else {
			mNM.cancelAll();
		}

		/* Service UI. */
		Disappear_lock();

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


		mNoti = new Notification(R.drawable.locked, "NO TOUCH Service Start!!", System.currentTimeMillis());
		mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
		mNoti.flags |= Notification.FLAG_ONGOING_EVENT;

		mNoti.contentView = remoteViews;
		mNoti.contentIntent = content;
	}


	@Override
	public void onDestroy() {
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

	// onStartcommand
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		String action = null;
		if (intent != null)
		{
			action = intent.getAction();
		}

		if ("android.intent.action.LockService.switch".equals(action))
		{
			stopSelf();
			startService(new Intent(this, unLockService.class));
		}

		if ("android.intent.action.LockService.finish".equals(action))
		{
			myApp.set_Service_Status(false);
			stopSelf();
		}

		if("android.intent.action.LockService.youtube".equals(action))
		{
			Intent StartYoutube_intent;
			StartYoutube_intent = this.getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
			this.startActivity(StartYoutube_intent);
		}

		mOnlyLesteningMode();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;


	}

}
