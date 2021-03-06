package hebertmm.github.io.clientemapfisc;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Random;

import hebertmm.github.io.clientemapfisc.domain.Message;
import hebertmm.github.io.clientemapfisc.domain.MessageRepository;
import hebertmm.github.io.clientemapfisc.settings.SettingsActivity;
import hebertmm.github.io.clientemapfisc.util.Constants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private String TAG = "MainActivity: ";
    private String SENDER_ID = "1055377163465";
    private Random random = new Random();

    private ImageButton btnSend;
    private ToggleButton btnDeslocamento;
    private ToggleButton btnAction;
    private ToggleButton btnEncerra;
    private EditText txtMsgSend;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 111;

    private MessageViewModel messageViewModel;
    private MessageRepository messageRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final MessageListAdapter adapter = new MessageListAdapter(this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.getAllMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(@Nullable List<Message> messages) {
                adapter.setmMessages(messages);
                recyclerView.smoothScrollToPosition(messages.size());
            }
        });
        messageRepository = new MessageRepository(getApplication());
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnDeslocamento = (ToggleButton) findViewById(R.id.btnDesloca);
        btnAction = (ToggleButton) findViewById(R.id.btnAcao);
        btnEncerra = (ToggleButton) findViewById(R.id.btnEncerra);
        btnSend.setOnClickListener(this);
        btnDeslocamento.setOnCheckedChangeListener(this);
        btnAction.setOnCheckedChangeListener(this);
        btnEncerra.setOnCheckedChangeListener(this);
        txtMsgSend = (EditText) findViewById(R.id.txtMsgToSend);
        String tel = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("phone_ID", "0");
        Integer id = Integer.parseInt(tel);
        if(id == 0){
            //System.out.println("não contem");
            InitialConfigDialog i = new InitialConfigDialog();
            i.setCancelable(false);
            i.show(getSupportFragmentManager(), "A");

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            Log.i(TAG, "Inside onStart function; requesting permission when permission is not available");
            requestPermissions();
        } else {
            Log.i(TAG, "Inside onStart function; getting location when permission is already available");
            startSendLocationService(10000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
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
                startSendLocationService(10000);
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
                    .addData("remote_id","1")
                    .build());
            Message m = new Message();
            m.setText(txtMsgSend.getText().toString());
            m.setTimestamp(System.currentTimeMillis());
            m.setType(0);
            messageRepository.insert(m);
            txtMsgSend.setText("");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuConfig){
           showSettings();
           return true;
        }
        else if(item.getItemId() ==R.id.menuDeleteAll){

            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);

    }
    private void startSendLocationService(Integer interval){
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),10000,pendingIntent);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.isChecked()) {
            SharedPreferences preferences = this.getSharedPreferences(Constants.PROPERTIES_NAME, Context.MODE_PRIVATE);
            if (buttonView.getId() == R.id.btnDesloca) {
                Toast.makeText(this, "Equipe em Deslocamento", Toast.LENGTH_SHORT).show();
                preferences.edit().putString(Constants.STATUS_KEY, "DESLOCAMENTO").commit();
                if (btnEncerra.isChecked())
                    btnEncerra.setChecked(false);
                if (btnAction.isChecked())
                    btnAction.setChecked(false);

            }
            if (buttonView.getId() == R.id.btnAcao) {
                Toast.makeText(this, "Equipe em ação", Toast.LENGTH_SHORT).show();
                preferences.edit().putString(Constants.STATUS_KEY, "AÇÃO").commit();
                if (btnEncerra.isChecked())
                    btnEncerra.setChecked(false);
                if (btnDeslocamento.isChecked())
                    btnDeslocamento.setChecked(false);

            }
            if (buttonView.getId() == R.id.btnEncerra) {
                Toast.makeText(this, "Atividade Encerrada", Toast.LENGTH_SHORT).show();
                preferences.edit().putString(Constants.STATUS_KEY, "ENCERRADA").commit();
                if (btnAction.isChecked())
                    btnAction.setChecked(false);
                if (btnDeslocamento.isChecked())
                    btnDeslocamento.setChecked(false);

            }
        }
    }

}

