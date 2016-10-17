package com.mcardoso.channelsphoenixclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.ISocketOpenCallback;
import org.phoenixframework.channels.Socket;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private Socket socket;
    private static Integer RECONNECT_IN_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.connect();

//        final Handler h = new Handler();
//        final int delay = 5000;
//
//        h.postDelayed(new Runnable(){
//            public void run(){
//                Log.d(TAG, "Checking socket connection...");
//                if( (socket == null || !socket.isConnected()) && !JOINING) {
//                    JOINING = true;
//                    connect();
//                    JOINING = false;
//                }
//                h.postDelayed(this, delay);
//            }
//        }, delay);
    }

    private void connect() {
        final Activity activity = this;

        try {
            socket = new Socket("ws://10.1.209.10:4000/socket/websocket", RECONNECT_IN_MS);
            socket.onOpen(new ISocketOpenCallback() {
                @Override
                public void onOpen() {
                    Channel channel = socket.chan("room:lobby", null);
                    try {
                        channel.join().receive("ignore", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                Log.d(TAG, "IGNORE");
                            }
                        }).receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, envelope.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } catch (IOException ioe) {
                        Log.e(TAG, ioe.getMessage());
                    }

                    channel.on("new:msg", new IMessageCallback() {
                        @Override
                        public void onMessage(final Envelope envelope) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, envelope.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            });

            socket.connect();
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }
    }
}
