package hebertmm.github.io.clientemapfisc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import hebertmm.github.io.clientemapfisc.util.Constants;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ALARM: ", "tick");
        SharedPreferences preferences = context.getSharedPreferences(Constants.PROPERTIES_NAME, Context.MODE_PRIVATE);
        Intent i = new Intent(context, LocationService.class);
        context.startService(i);
        NetworkService.startActionSendUpdate(context,
                preferences.getString(Constants.LAT_KEY,""),
                preferences.getString(Constants.LNG_KEY,""),
                preferences.getString(Constants.FCM_KEY,""),
                preferences.getString(Constants.STATUS_KEY,""));
    }
}
