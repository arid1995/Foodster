package com.drewjames.foodster;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.view.Display;
import android.graphics.Point;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.drewjames.foodster.util.IabBroadcastReceiver;
import com.drewjames.foodster.util.IabBroadcastReceiver.IabBroadcastListener;
import com.drewjames.foodster.util.IabHelper;
import com.drewjames.foodster.util.IabHelper.IabAsyncInProgressException;
import com.drewjames.foodster.util.IabResult;
import com.drewjames.foodster.util.Inventory;
import com.drewjames.foodster.util.Purchase;

import java.util.ArrayList;
import java.util.List;
import com.drewjames.foodster.util.GMailSender;
import java.lang.reflect.Field;

public class EmailActivity extends AppCompatActivity {
    String _email;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Bundle extras = getIntent().getExtras();
        int resourceId = extras.getInt("itemId");

        final ImageView image = new ImageView(this);
        image.setImageResource(resourceId);
        image.setTag(resourceId);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x / 2 - 50;

        image.setLayoutParams(new android.view.ViewGroup.LayoutParams(width, width));

        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ViewGroup) image.getParent()).removeView(image);
                ((Button)findViewById(R.id.send_button)).setEnabled(true);
            }
        });
        ((LinearLayout)findViewById(R.id.send_layout)).addView(image);
    }

    public void sendEmail(View arg0) {
        EditText email = (EditText) findViewById(R.id.email_field);
        if (!email.getText().toString().equals("")) {
            _email = email.getText().toString();
            email.setText("");
            SendMailTask sender = new SendMailTask();
            sender.execute();
            intent = new Intent(this, Thanks.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
    };

    class SendMailTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        };

        @Override
        protected Void doInBackground(Void... params) {
            try {
                GMailSender sender = new GMailSender("proxy8925@gmail.com", "9293709B13");
                sender.sendMail("Someone bought something",
                        _email,
                        "proxy8925@gmail.com",
                        "proxy8925@gmail.com");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            startActivity(intent);
            return null;
        };

        @Override
        protected void onPostExecute(Void result) {
        };
    }
}
