package com.example.saubhagyam.wifi_chat_;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeviceClickListener,Handler.Callback,WifiP2pManager.ConnectionInfoListener {

    ListView other_devices;
    ImageView edit;
    TextView profile_name;
    EditText profile_name_edittext;

    int name_changed;


    public static final String TAG = "wifidirectdemo";
    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_wifidemotest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    private WifiP2pManager manager;
    static final int SERVER_PORT = 4545;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private Handler handler = new Handler(this);
    private TextView statusTxtView;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name_changed = 0;

        other_devices = (ListView) findViewById(R.id.other_devices);
        edit = (ImageView) findViewById(R.id.edit);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_name_edittext = (EditText) findViewById(R.id.profile_name_edittext);
    /*    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    @Override
    protected void onResume() {
        super.onResume();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_changed == 0) {
                    profile_name.setVisibility(View.GONE);
                    profile_name_edittext.setVisibility(View.VISIBLE);
                    edit.setImageDrawable(getResources().getDrawable(R.mipmap.ic_save));
                    name_changed = 1;
                } else {
                    edit.setImageDrawable(getResources().getDrawable(R.mipmap.ic_edit));
                    profile_name.setVisibility(View.VISIBLE);
                    profile_name_edittext.setVisibility(View.GONE);
                    name_changed = 0;
                }
            }
        });

        List<String> otherList = new ArrayList();
        for (int i = 0; i < new ArrayList<WiFiP2pService>().size(); i++) {

            Log.e("connection"+i,new ArrayList<WiFiP2pService>().get(i).toString());

        }

        WiFiDevicesAdapter wiFiDevicesAdapter=new WiFiDevicesAdapter(getApplicationContext(),R.layout.custom_spinner_textview, android.R.id.text1,
                new ArrayList<WiFiP2pService>());
//        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner_textview, otherList);
        other_devices.setAdapter(wiFiDevicesAdapter);




        other_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                ((DeviceClickListener) getApplicationContext()).connectP2p((WiFiP2pService) adapterView
                        .getItemAtPosition(i));
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(false);
    }

    @Override
    public void connectP2p(WiFiP2pService wifiP2pService) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pService.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onFailure(int arg0) {
                        }
                    });
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                appendStatus("Connecting to service");
                Toast.makeText(MainActivity.this, "Connectiong to service", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int errorCode) {
//                appendStatus("Failed connecting to service");
                Toast.makeText(MainActivity.this, "Failed Connectiong to service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, readMessage);
//                new ChatActivity().pushMessage("Buddy: " + readMessage);
                break;
            case MY_HANDLE:
                Object obj = msg.obj;
//                new ChatActivity().setChatManager((ChatManager) obj);
        }
        return true;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

    }


    public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {
        private List<WiFiP2pService> items;

        public WiFiDevicesAdapter(Context context, int resource,
                                  int textViewResourceId, List<WiFiP2pService> items) {
            super(context, resource, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_2, null);
            }
            WiFiP2pService service = items.get(position);
            if (service != null) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);
                if (nameText != null) {
                    nameText.setText(service.device.deviceName + " - " + service.instanceName);
                }
                TextView statusText = (TextView) v
                        .findViewById(android.R.id.text2);
                statusText.setText(getDeviceStatus(service.device.status));
            }
            return v;
        }
    }

    public static String getDeviceStatus(int statusCode) {
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }


}
