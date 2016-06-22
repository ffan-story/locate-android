package com.libs.ui.activities.model;

import android.content.Intent;

/**
 * Created by mengmeng on 15/11/23.
 */
public class ActivityResultModel {
    private int requestCode;
    private int resultCode;
    private Intent intent;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
