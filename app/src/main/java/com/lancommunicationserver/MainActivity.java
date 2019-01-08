package com.lancommunicationserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text = findViewById(R.id.openServerView);

        TextView ipView = findViewById(R.id.ipView);

        String ipAddress = GetLocalIPUtil.getIPAddress(this);
        ipView.setText(ipAddress);

        new CommunicationServerUtil(new CommunicationServerUtil.mIOnAcceptMessListener() {
            @Override
            public void onMess(String mess) {
                text.setText(text.getText() + "\n" + mess);
            }
        }).openSocket();

    }
}
