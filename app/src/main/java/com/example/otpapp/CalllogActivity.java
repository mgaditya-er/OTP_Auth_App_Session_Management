package com.example.otpapp;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;

public class CalllogActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION_READ_CALL_LOG = 123;
    private Button btnGetMissedCalls;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calllog);

        btnGetMissedCalls = findViewById(R.id.btnGetMissedCalls);
        listView = findViewById(R.id.listView);

        btnGetMissedCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    showMissedCalls();
                } else {
                    requestPermission();
                }
            }
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CODE_PERMISSION_READ_CALL_LOG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMissedCalls();
            } else {
                Toast.makeText(this, "Permission denied. Cannot retrieve missed calls.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMissedCalls() {
        String[] projection = {CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE};
        String selection = CallLog.Calls.TYPE + " = ? AND " + CallLog.Calls.DATE + " > ?";
        String[] selectionArgs = {String.valueOf(CallLog.Calls.MISSED_TYPE), String.valueOf(System.currentTimeMillis() - 3600000)}; // Past hour

        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0
            );

            listView.setAdapter(adapter);
        }


    }
}