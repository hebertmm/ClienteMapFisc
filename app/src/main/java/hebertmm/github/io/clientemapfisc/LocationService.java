package hebertmm.github.io.clientemapfisc;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationService extends IntentService {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private final String TAG = "SERVICE: ";

    public LocationService() {
        super("Location Service");

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Iniciou serviço");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLastLocation = task.getResult();
                    Log.e(TAG,"Latitude: " + String.valueOf(mLastLocation.getLatitude()));
                    Log.e(TAG,"Longitude" + String.valueOf(mLastLocation.getLongitude()));
                } else {
                    Log.i(TAG, "Inside getLocation function. Error while getting location");
                    System.out.println(TAG+task.getException());
                }
            }
        });
        stopSelf();
    }
}