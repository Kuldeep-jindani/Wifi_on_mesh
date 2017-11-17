package com.example.saubhagyam.wifi_chat_.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.saubhagyam.wifi_chat_.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuldeep_jindani on 11/15/2017.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {


    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mainActivity;
    private List<WifiP2pDevice> mPeers;
    private List<WifiP2pConfig> mConfig;

    public WiFiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainActivity applicationContext) {
        super();
        this.mChannel = mChannel;
        this.mManager = mManager;
        this.mainActivity = applicationContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Toast.makeText(context, "WIFI P2P enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Wi-Fi P2P is not enabled
                Toast.makeText(context, "WIFI P2P disabled", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            mPeers=new ArrayList<WifiP2pDevice>();
            mConfig=new ArrayList<WifiP2pConfig>();

            if (mManager!=null){
             WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
                 @Override
                 public void onPeersAvailable(WifiP2pDeviceList peerList) {
                    mPeers.clear();mPeers.addAll(peerList.getDeviceList());

                     mainActivity.displayPeers(peerList);
                     mPeers.addAll(peerList.getDeviceList());

                     for (int i=0;i<peerList.getDeviceList().size();i++){
                         WifiP2pConfig config=new WifiP2pConfig();
                         config.deviceAddress=mPeers.get(i).deviceAddress;
                         mConfig.add(config);
                     }
                 }
             };
            }
        }
    }
}
