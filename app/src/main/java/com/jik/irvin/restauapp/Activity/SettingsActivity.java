package com.jik.irvin.restauapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.jik.irvin.restauapp.DatabaseHelper;
import com.jik.irvin.restauapp.Model.PosModel;
import com.jik.irvin.restauapp.R;


public class SettingsActivity extends AppCompatActivity {

    EditText server, deviceKey , posId , receiptNumber;
    Button save;

    DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    void init() {
        server = findViewById(R.id.server);
        deviceKey = findViewById(R.id.deviceKey);
        save = findViewById(R.id.btn_save);
        posId = findViewById(R.id.posId);
        receiptNumber = findViewById(R.id.receiptNumber);

        server.setText(db.getBaseUrl());

        PosModel posModel = db.getAllSettings().get(0);
        posId.setText(posModel.getPos_id());
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceKey.setFocusable(false);
        deviceKey.setText(androidId);
        receiptNumber.setText(posModel.getLast_receipt_number());



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to save ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


                        db.updateBaseUrl(server.getText().toString());

                        db.updatePos(new PosModel(posId.getText().toString(),
                                receiptNumber.getText().toString(),
                                deviceKey.getText().toString()));

                        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });

    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to exit ?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
