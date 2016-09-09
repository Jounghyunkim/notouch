package johnny.studio.notouch;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotouchBootingStartService extends Service {
	
	private MyApplication myApp;

	@Override
	public void onCreate() {
		super.onCreate();

		myApp = (MyApplication)getApplicationContext();
		
		if(myApp.get_Service_Booting_Start()){
		startService(new Intent(this, unLockService.class));
		} else {
			stopSelf();
		}

	}

	
	// onStartcommand
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    super.onStartCommand(intent, flags, startId);

	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;


	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
