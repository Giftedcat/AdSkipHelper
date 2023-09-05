package com.giftedcat.adskiphelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.giftedcat.adskiphelper.service.AdSkipService;

public class MainActivity extends AppCompatActivity {

    private Switch swSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
    }

    private void findViews(){
        swSkip = findViewById(R.id.sw_skip);
    }

    private void initViews() {
        swSkip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO controlling helper
                if (isChecked){
                    //打开跳过助手
                    if (isAccessibilitySettingsOn(MainActivity.this)){

                    }else {
                        showOpenAccessibilityDialog();
                    }
                }else {
                    //关闭跳过助手
                }
            }
        });
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + AdSkipService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }

        return false;
    }

    /**
     * 打开无障碍权限的提示
     * */
    private void showOpenAccessibilityDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("依次点击：无障碍->已下载应用->广告跳过助手")
                .setPositiveButton("取消", null)
                .setNeutralButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转至系统界面
                        Intent intent_abs = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        intent_abs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_abs);
                    }
                })
                .create();
        dialog.show();
    }

}