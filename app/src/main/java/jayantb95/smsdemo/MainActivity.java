package jayantb95.smsdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final String SMS_SENT = "sms sent successfully.";
    private final String SMS_DELIVERED = "sms delivered to the recipient(s).";
    private final int REQUEST_PERMISSION_CODE = 2;
    private final String[] contacts = {"contact1", "contact2"};
    private Button btnSendSms;
    private BroadcastReceiver receiver;
    private final String message = "jkasfksajdhflkasjh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        btnSendSms = findViewById(R.id.btn_send_sms);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = null;

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        message = "Message sent!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        message = "Error. Message not sent.";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        message = "Error: No service.";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        message = "Error: Null PDU.";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        message = "Error: Radio off.";
                        break;
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(receiver, new IntentFilter(SMS_SENT));  // SMS_SENT is a constant
        listener();
    }

    private void listener() {
        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    sendSms(contacts, message);
                } else {
                    requestPermission();
                }
            }
        });

    }

    private void sendSms(String[] contacts, String message) {
        SmsManager manager = SmsManager.getDefault();

        PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);


        for (String phoneNumber : contacts) {
            manager.sendTextMessage(phoneNumber, null, message, piSend, piDelivered);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        SEND_SMS,

                }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermission() {
        int firstPermission = ActivityCompat.checkSelfPermission(this, SEND_SMS);
        return firstPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean SmsPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (SmsPermission) {
                        Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                        sendSms(contacts, message);
                    }
                }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
