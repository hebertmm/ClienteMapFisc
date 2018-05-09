package hebertmm.github.io.clientemapfisc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ALARM: ", "tick");
        Intent i = new Intent(context, LocationService.class);
        context.startService(i);
    }
}
