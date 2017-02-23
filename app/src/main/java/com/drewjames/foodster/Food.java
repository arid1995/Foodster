package com.drewjames.foodster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
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


import com.drewjames.foodster.util.IabBroadcastReceiver;
import com.drewjames.foodster.util.IabBroadcastReceiver.IabBroadcastListener;
import com.drewjames.foodster.util.IabHelper;
import com.drewjames.foodster.util.IabHelper.IabAsyncInProgressException;
import com.drewjames.foodster.util.IabResult;
import com.drewjames.foodster.util.Inventory;
import com.drewjames.foodster.util.Purchase;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

public class Food extends AppCompatActivity {
    IabHelper mHelper;
    Food _main;
    int boughtItem = 0;
    int subsItemId = 0;
    ImageView _imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        GridLayout grid = (GridLayout) findViewById(R.id.grid);
        final R.drawable drawableResources = new R.drawable();
        final Class<R.drawable> c = R.drawable.class;
        final Field[] fields = c.getDeclaredFields();

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3ZMBNs8cKWXt536+FsoqMyo+rOC4F/d1+UuK+KLB6/soEcai0rMAC5DFLPy9lV6Yxu2nybZaWY4T8vEBatJvTTtMNlqvPXUm1IXZGq9DOQX2xSLNGcFoOHONPVT9wi74hQjUPSVBuDak5HYS2AX2grcidLPwC5r5sZ9eD7xV97y6BNQtnyr5s60anuaLv/NFz8O13PrvVAVrttkcFgj+QwioyYQgEr8EB12D5g+WfYbyBezi+nnI8ZrhvDod17PyC+suj62HOulafC1J7b4TL5U9Oi0R/9dpF6qOWdoyWuNrqy+yk5bKUP7Yg31Xw8/hPuG5C/cmYOgdu9fd3U0hHwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                }
            }
        });

        for (int i = 0, max = fields.length; i < max; i++) {
            final int resourceId;
            try {
                resourceId = fields[i].getInt(drawableResources);
            } catch (Exception e) {
                continue;
            }

            if (i < 81 || i > 132) continue;//workaround default drawables
            //subscription
            final ImageView image = new ImageView(this);
            image.setImageResource(resourceId);
            image.setTag(resourceId);

            if(i == 132) subsItemId = resourceId;

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x / 2 - 50;

            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(width, width));

            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boughtItem = (int) v.getTag();
                    _imageView = image;
                    purchaseItem(resourceId, subsItemId);
                }
            });
            grid.addView(image);
        }
    }


    private void purchaseItem(int resourceId, int subsId) {
        try {
            if(resourceId == subsId) {
                List<String> oldskus = null;
                mHelper.launchPurchaseFlow(this, "chicken", IabHelper.ITEM_TYPE_SUBS, oldskus, 10,
                        mPurchaseFinishedListener, "");
            } else {
                mHelper.launchPurchaseFlow(this, "water", 10,
                        mPurchaseFinishedListener, "");
            }
            _main = this;
        } catch (Exception e) {
            Log.d("MEEEEEEEEEE", e.getMessage());
        }
        //Intent intent = new Intent(_main, EmailActivity.class);
        //intent.putExtra("itemId", boughtItem);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //((ViewGroup) _imageView.getParent()).removeView(_imageView);
        //startActivity(intent);
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                return;
            }
            try {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } catch (IabAsyncInProgressException e) {
                Log.d("MEEEEEEEEEE", e.getMessage());
            }
            Intent intent = new Intent(_main, EmailActivity.class);
            intent.putExtra("itemId", boughtItem);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ((ViewGroup) _imageView.getParent()).removeView(_imageView);
            startActivity(intent);
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d("", "onActivityResult handled by IABUtil.");
        }
    }

}