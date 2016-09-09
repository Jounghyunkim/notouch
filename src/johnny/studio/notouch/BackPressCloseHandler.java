package johnny.studio.notouch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;


public class BackPressCloseHandler {
	private long backKeyPressedTime = 0;
	private Toast toast;

	private Activity activity;

	public BackPressCloseHandler(Activity context) {
		this.activity = context;
	}

	public void onBackPressed() {
		if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
			backKeyPressedTime = System.currentTimeMillis();
			showGuide();
			return;
		}
		if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
			activity.finish();
			toast.cancel();
		}
	}

	public void showGuide() {
		toast = Toast.makeText(activity,
				"서비스를 실행하지 않고 종료하시겠습니까?\n\n\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
		toast.show();
	}
}
