package hebertmm.github.io.clientemapfisc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "MainActivity: ";
    private String SENDER_ID = "1055377163465";
    private Random random = new Random();

    private ImageButton btnSend;
    private EditText txtMsgSend;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.e("Token: ", FirebaseInstanceId.getInstance().getToken());
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        txtMsgSend = (EditText) findViewById(R.id.txtMsgToSend);
        Intent i = new Intent(this, LocationService.class);
        startService(i);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),10000,pendingIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            Log.i(TAG, "Inside onStart function; requesting permission when permission is not available");
            requestPermissions();
        } else {
            Log.i(TAG, "Inside onStart function; getting location when permission is already available");
            //getLastLocation();
        }
    }
    private boolean checkPermissions() {
        int permissionState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //Request permission from user
    private void requestPermissions() {
        Log.i(TAG, "Inside requestPermissions function");
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        //Log an additional rationale to the user. This would happen if the user denied the
        //request previously, but didn't check the "Don't ask again" checkbox.
        // In case you want, you can also show snackbar. Here, we used Log just to clear the concept.
        if (shouldProvideRationale) {
            Log.i(TAG, "****Inside requestPermissions function when shouldProvideRationale = true");
            startLocationPermissionRequest();
        } else {
            Log.i(TAG, "****Inside requestPermissions function when shouldProvideRationale = false");
            startLocationPermissionRequest();
        }
    }

    //Start the permission request dialog
    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Callback to the following function is received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // user interaction is cancelled; in such case we will receive empty grantResults[]
                //In such case, just record/log it.
                Log.i(TAG, "User interaction has been cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted by the user.
                Log.i(TAG, "User permission has been given. Now getting location");
                //getLastLocation();
            } else {
                // Permission is denied by the user.
                Log.i(TAG, "User denied permission.");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSend){
            FirebaseMessaging fm = FirebaseMessaging.getInstance();
            fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                    .setMessageId(Integer.toString(random.nextInt(9999)))
                    .addData("message", txtMsgSend.getText().toString())
                    .addData("team_id","5")
                    .build());
            txtMsgSend.setText("");
        }
    }
}
