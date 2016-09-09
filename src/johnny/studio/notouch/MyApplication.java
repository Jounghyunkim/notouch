package johnny.studio.notouch;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {

	// Debugging
	private static final String TAG = "TSL";
	private static final boolean D = false;

/* Example */	
//	public static final String KEY_EXAMPLE_STRING = "";
//	public static final String KEY_EXAMPLE_BOOL ="true";
//	public static final String KEY_EXAMPLE_INT ="0";
	
	public static final String KEY_SERVICE_STATUS = "false";
	public static final String KEY_SERVICE_BOOTING_START = "false";
	public static final String KEY_SERVICE_PROXI = "false";
	public static final String KEY_SERVICE_UNLOCK_BUTTON_HIDE = "false";
	//public static final String KEY_SERVICE_TEXT_GUIDE = "treu";
	public static final String KEY_SERVICE_CONTINUE_TOUCH_UNLOCK = "true";
	
	
	public static final String KEY_SERVICE_MODE = "0";
	
	public static final String KEY_SERVICE_VISIBLE = "false";
	public static final String KEY_SERVICE_UPDATE_TIP = "0";
	
	public static final String KEY_SERVICE_UNLOCK_NOTI = "true";
	
	public static final String KEY_SERVICE_LOCK_ICON_SIZE = "60";
	

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	//KEY_SERVICE_STATUS
	public void set_Service_Status(boolean value){
		SharedPreferences prefs = getSharedPreferences("Service_Status", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KEY_SERVICE_STATUS, value);
		editor.commit();
	}
	public boolean get_Service_Status(){
		SharedPreferences prefs = getSharedPreferences("Service_Status", MODE_PRIVATE);
		return prefs.getBoolean(KEY_SERVICE_STATUS, false);
	}
	
	//KEY_SERVICE_MODE
	public void set_Service_Mode(int value){
		SharedPreferences prefs = getSharedPreferences("Service_Mode", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(KEY_SERVICE_MODE, value);
		editor.commit();
	}
	public int get_Service_Mode(){
		SharedPreferences prefs = getSharedPreferences("Service_Mode", MODE_PRIVATE);
		return prefs.getInt(KEY_SERVICE_MODE, 0);
	}
	
	//KEY_SERVICE_UPDATE_TIP
	public void set_Service_Update_Tip_ver(int value){
		SharedPreferences prefs = getSharedPreferences("Service_Update_Tip_ver", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(KEY_SERVICE_UPDATE_TIP, value);
		editor.commit();
	}
	public int get_Service_Update_Tip_ver(){
		SharedPreferences prefs = getSharedPreferences("Service_Update_Tip_ver", MODE_PRIVATE);
		return prefs.getInt(KEY_SERVICE_UPDATE_TIP, 0);
	}
	
	//KEY_SERVICE_BOOTING_START
	public void set_Service_Booting_Start(boolean value){
		SharedPreferences prefs = getSharedPreferences("Service_Booting_Start", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KEY_SERVICE_BOOTING_START, value);
		editor.commit();
	}
	public boolean get_Service_Booting_Start(){
		SharedPreferences prefs = getSharedPreferences("Service_Booting_Start", MODE_PRIVATE);
		return prefs.getBoolean(KEY_SERVICE_BOOTING_START, false);
	}
	
	//KEY_SERVICE_PROXI
	public void set_Service_Proxi(boolean value){
		SharedPreferences prefs = getSharedPreferences("Service_Proxi", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KEY_SERVICE_PROXI, value);
		editor.commit();
	}
	public boolean get_Service_Proxi(){
		SharedPreferences prefs = getSharedPreferences("Service_Proxi", MODE_PRIVATE);
		return prefs.getBoolean(KEY_SERVICE_PROXI, false);
	}
	
	//KEY_SERVICE_UNLOCK_BUTTON_HIDE
		public void set_Service_unlock_button_hide(boolean value){
			SharedPreferences prefs = getSharedPreferences("Service_unlock_button_hide", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(KEY_SERVICE_UNLOCK_BUTTON_HIDE, value);
			editor.commit();
		}
		public boolean get_Service_unlock_button_hide(){
			SharedPreferences prefs = getSharedPreferences("Service_unlock_button_hide", MODE_PRIVATE);
			return prefs.getBoolean(KEY_SERVICE_UNLOCK_BUTTON_HIDE, false);
		}

/*
		//KEY_SERVICE_TEXT_GUIDE
		public void set_Service_text_guide(boolean value){
			SharedPreferences prefs = getSharedPreferences("Service_text_guide", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(KEY_SERVICE_TEXT_GUIDE, value);
			editor.commit();
		}
		public boolean get_Service_text_guide(){
			SharedPreferences prefs = getSharedPreferences("Service_text_guide", MODE_PRIVATE);
			return prefs.getBoolean(KEY_SERVICE_TEXT_GUIDE, false);
		}
*/		
		
	//KEY_SERVICE_CONTINUE_TOUCH_UNLOCK
		public void set_Service_continue_touch_unlock(boolean value){
			SharedPreferences prefs = getSharedPreferences("Service_continue_touch_unlock", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(KEY_SERVICE_CONTINUE_TOUCH_UNLOCK, value);
			editor.commit();
		}
		public boolean get_Service_continue_touch_unlock(){
			SharedPreferences prefs = getSharedPreferences("Service_continue_touch_unlock", MODE_PRIVATE);
			return prefs.getBoolean(KEY_SERVICE_CONTINUE_TOUCH_UNLOCK, false);
		}
		
	//KEY_SERVICE_VISIBLE
	public void set_Service_Update_Info_Visible(boolean value){
		SharedPreferences prefs = getSharedPreferences("Service_Visible", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KEY_SERVICE_VISIBLE, value);
		editor.commit();
	}
	public boolean get_Service_Update_Info_Visible(){
		SharedPreferences prefs = getSharedPreferences("Service_Visible", MODE_PRIVATE);
		return prefs.getBoolean(KEY_SERVICE_VISIBLE, true);
	}
	
	
	//KEY_SERVICE_UNLOCK_NOTI
	public void set_Service_Unlock_Noti_setting(boolean value){
		SharedPreferences prefs = getSharedPreferences("Service_Unlock_Noti_Setting", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KEY_SERVICE_UNLOCK_NOTI, value);
		editor.commit();
	}
	public boolean get_Service_Unlock_Noti_setting(){
		SharedPreferences prefs = getSharedPreferences("Service_Unlock_Noti_Setting", MODE_PRIVATE);
		return prefs.getBoolean(KEY_SERVICE_UNLOCK_NOTI, true);
	}
	
	//KEY_SERVICE_LOCK_ICON_SIZE
	public void set_Service_Lock_Icon_Size(int value){
		SharedPreferences prefs = getSharedPreferences("Service_Lock_Icon_Size", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(KEY_SERVICE_LOCK_ICON_SIZE, value);
		editor.commit();
	}
	public int get_Service_Lock_Icon_Size(){
		SharedPreferences prefs = getSharedPreferences("Service_Lock_Icon_Size", MODE_PRIVATE);
		return prefs.getInt(KEY_SERVICE_LOCK_ICON_SIZE, 60);
	}
	
}



//	/* SharedPreferences */
//	//KEY_EXAMPLE_STRING
//	public void setServiceState(String value){
//		SharedPreferences prefs = getSharedPreferences("Example_Stirng", MODE_PRIVATE);
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putString(KEY_EXAMPLE_STRING, value);
//		editor.commit();
//	}
//	public String getServiceState(){
//		SharedPreferences prefs = getSharedPreferences("Example_Stirng", MODE_PRIVATE);
//		return prefs.getString(KEY_EXAMPLE_STRING, "Example_Stirng");
//	}
//
//	
//
//	//KEY_EXAMPLE_BOOL
//	public void set_Is_EarPhone_Detection(boolean value){
//		SharedPreferences prefs = getSharedPreferences("Example_bool", MODE_PRIVATE);
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putBoolean(KEY_EXAMPLE_BOOL, value);
//		editor.commit();
//	}
//	public boolean get_Is_EarPhone_Detection(){
//		SharedPreferences prefs = getSharedPreferences("Example_bool", MODE_PRIVATE);
//		return prefs.getBoolean(KEY_EXAMPLE_BOOL, true);
//	}
//
//
//
//	//KEY_EXAMPLE_INT
//	public void setBackgroundImage(int value){
//		SharedPreferences prefs = getSharedPreferences("Example_int", MODE_PRIVATE);
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putInt(KEY_EXAMPLE_INT, value);
//		editor.commit();
//	}
//	public int getBackgroundImage(){
//		SharedPreferences prefs = getSharedPreferences("Example_int", MODE_PRIVATE);
//		return prefs.getInt(KEY_EXAMPLE_INT, 0);
//	}	


