package hebertmm.github.io.clientemapfisc;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import hebertmm.github.io.clientemapfisc.util.Constants;

public class LocationService extends IntentService {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private final String TAG = "SERVICE: ";
    private SharedPreferences preferences;

    public LocationService() {
        super("Location Service");

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Iniciou serviço");
        preferences = this.getSharedPreferences(Constants.PROPERTIES_NAME, Context.MODE_PRIVATE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLastLocation = task.getResult();
                    String lat = String.valueOf(mLastLocation.getLatitude());
                    String lng = String.valueOf(mLastLocation.getLongitude());
                    Log.e(TAG,"Latitude: " + lat);
                    Log.e(TAG,"Longitude" + lng);
                    preferences.edit()
                            .putString(Constants.LAT_KEY, lat)
                            .putString(Constants.LNG_KEY, lng)
                            .commit();

                } else {
                    Log.i(TAG, "Inside getLocation function. Error while getting location");
                    System.out.println(TAG+task.getException());
                }
            }
        });
        stopSelf();
    }
}