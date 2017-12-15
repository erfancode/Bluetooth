package com.example.bluetooth.bluetooth;

/**
 * Created by Erfan on 15/12/2017.
 */

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.AutoConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothStateListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class ListenerActivity extends AppCompatActivity {
    BluetoothSPP bt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);

        bt = new BluetoothSPP(this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setBluetoothStateListener(new BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if(state == BluetoothState.STATE_CONNECTED)
                    Log.i("Check", "State : Connected");
                else if(state == BluetoothState.STATE_CONNECTING)
                    Log.i("Check", "State : Connecting");
                else if(state == BluetoothState.STATE_LISTEN)
                    Log.i("Check", "State : Listen");
                else if(state == BluetoothState.STATE_NONE)
                    Log.i("Check", "State : None");
            }
        });

        bt.setOnDataReceivedListener(new OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.i("Check", "Message : " + message);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Log.i("Check", "Device Connected!!");
            }

            public void onDeviceDisconnected() {
                Log.i("Check", "Device Disconnected!!");
            }

            public void onDeviceConnectionFailed() {
                Log.i("Check", "Unable to Connected!!");
            }
        });

        bt.setAutoConnectionListener(new AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Log.i("Check", "New Connection - " + name + " - " + address);
            }

            public void onAutoConnectionStarted() {
                Log.i("Check", "Auto menu_connection started");
            }
        });

        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(ListenerActivity.this, DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void setup() { }
}
