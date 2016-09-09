package johnny.studio.notouch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MybroadcastReceiverService extends BroadcastReceiver {

	private static final boolean D = true;
	
	public MybroadcastReceiverService() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.



		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){

			ComponentName cName = new ComponentName(context.getPackageName(), NotouchBootingStartService.class.getName());
			ComponentName svcName = context.startService(new Intent().setComponent(cName));		

			if(svcName == null) {
				//½ÇÇà ¾ÈµÊ.
				if(D) Toast.makeText(context, "Booting Start Fail!", 1).show();
			}  else {
				//½ÇÇà µÊ.
				if(D) Toast.makeText(context, "Booting Start Success!", 1).show();
			}


		}



		//throw new UnsupportedOperationException("Not yet implemented");
	}
}
