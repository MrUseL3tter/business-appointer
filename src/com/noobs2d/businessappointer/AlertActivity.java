package com.noobs2d.businessappointer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertActivity extends Activity {

    public AlertActivity() {
	super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
	Builder builder = new AlertDialog.Builder(this);
	builder.setMessage(getResources().getString(R.string.alert_sms_notification));
	builder.setCancelable(true);
	builder.setTitle("Business Appointer");
	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		AlertActivity.this.finish();
	    }
	});
	AlertDialog dialog = builder.create();
	dialog.show();
	return super.onCreateDialog(id);
    }
}