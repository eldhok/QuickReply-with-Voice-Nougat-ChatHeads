/*
 * Copyright (C) 2018 Screenfly Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.notifications.screen.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.widget.Toast;

import com.notifications.screen.R;
import com.notifications.screen.admin.AdminReceiver;
import com.notifications.screen.util.Mlog;

import static com.notifications.screen.R.string.device_admin_too_fast;
import static com.notifications.screen.ui.WelcomeActivity.toastLayout;
import static com.notifications.screen.ui.WelcomeActivity.tv;

public class DeviceAdminEnabler extends Activity {
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(Html.fromHtml("<font color='#FFFFFF'><b> Enable Device Admin</b></font>"));


        if (((DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE)).isAdminActive(AdminReceiver.getComponentName(this))) {
           // Toast.makeText(this, getString(R.string.device_admin_alredy_on), Toast.LENGTH_SHORT).show();

            tv.setText(getString(R.string.device_admin_alredy_on));
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.show();

            startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
            finish();
            return;
        }

        showDialog();
    }

    private void showDialog() {
        time = System.currentTimeMillis();
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_admin_enable_title)
                .setMessage(R.string.device_admin_enable_explanation)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (System.currentTimeMillis()-time > 5000) {
                            enableAdmin();
                            finish();
                        } else {
                           // Toast.makeText(getApplicationContext(), R.string.device_admin_too_fast, Toast.LENGTH_SHORT).show();
                            tv.setText(getString(device_admin_too_fast));
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(toastLayout);
                            toast.show();

                            showDialog();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
    }

    private void enableAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                AdminReceiver.getComponentName(getApplicationContext()));
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.device_admin_enable_explanation));

        if (intent.resolveActivity(getPackageManager()) != null) {
            Mlog.d("DeviceAdminEnabler", "Is resolved");
            startActivity(intent);
        } else {
           // Toast.makeText(this, getString(R.string.device_admin_not_available), Toast.LENGTH_SHORT).show();
            tv.setText(getString(R.string.device_admin_not_available));
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.show();
            Mlog.w("DeviceAdminEnabler", "Not possible");
        }
    }

}
