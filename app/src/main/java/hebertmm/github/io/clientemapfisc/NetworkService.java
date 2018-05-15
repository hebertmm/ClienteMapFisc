package hebertmm.github.io.clientemapfisc;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_ID = "hebertmm.github.io.clientemapfisc.action.GET_ID";
    private static final String ACTION_SEND_UPDATE = "hebertmm.github.io.clientemapfisc.action.SEND_UPDATE";

    // TODO: Rename parameters
    private static final String EXTRA_NUMBER = "hebertmm.github.io.clientemapfisc.extra.NUMBER";
    private static final String EXTRA_LAT = "hebertmm.github.io.clientemapfisc.extra.LAT";
    private static final String EXTRA_LNG = "hebertmm.github.io.clientemapfisc.extra.LNG";
    private static final String EXTRA_FCM = "hebertmm.github.io.clientemapfisc.extra.FCM";
    private static final String EXTRA_STATUS = "hebertmm.github.io.clientemapfisc.extra.STATUS";

    SharedPreferences preferences;
    RequestQueue queue;


    public NetworkService() {
        super("NetworkService");

    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetId(Context context, String number) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_GET_ID);
        intent.putExtra(EXTRA_NUMBER, number);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSendUpdate(Context context, String lat, String lng, String fcmId, String status) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_SEND_UPDATE);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        intent.putExtra(EXTRA_FCM, fcmId);
        intent.putExtra(EXTRA_STATUS, status);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            queue = Volley.newRequestQueue(this);
            final String action = intent.getAction();
            if (ACTION_GET_ID.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_NUMBER);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionGetId(param1);
            } else if (ACTION_SEND_UPDATE.equals(action)) {
                final String lat = intent.getStringExtra(EXTRA_LAT);
                final String lng = intent.getStringExtra(EXTRA_LNG);
                final String fcmId = intent.getStringExtra(EXTRA_FCM);
                final String status = intent.getStringExtra(EXTRA_STATUS);
                handleActionSendUpdate(lat, lng, fcmId, status);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetId(String number) {

        String url ="http://10.62.10.52:8080/idByNumber?number=" + number;


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String id = response.toString();
                        preferences.edit().putString("phone_ID", id).commit();
                        Log.i("NET: ","Response is: "+ response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("NET: ","That didn't work!");
            }
        });


// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendUpdate(final String lat, final String lng, final String fcmId, final String status) {
        String url = "http://10.62.10.52:8080/updateRemoteDevice/" + preferences.getString("phone_ID", "0");
        Log.i("NET: ", url);
        JSONObject params = new JSONObject();
        try {
            params.put("id", JSONObject.NULL);
            params.put("validationCode", null);
            params.put("description", null);
            params.put("number", null);
            params.put("messagingId", fcmId);
            params.put("lat", lat);
            params.put("lng", lng);
            params.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url,params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", "response");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getLocalizedMessage());
                    }
                }
        );
        queue.add(putRequest);
    }
}
