
package com.marakana.android.yamba;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.app.Activity;
import android.content.res.Resources;

import com.marakana.android.yamba.svc.YambaService;

public class StatusActivity extends Activity {
    private static final String TAG = "STATUS";

    public static final int MAX_STATUS_LEN = 140;
    public static final int WARN_CHAR_CNT = 10;
    public static final int ERROR_CHAR_CNT = 0;


    private int colorOk;
    private int colorWarn;
    private int colorError;

    private EditText statusText;
    private TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources rez = getResources();
        colorOk = rez.getColor(R.color.ok_color);
        colorWarn = rez.getColor(R.color.warn_color);
        colorError = rez.getColor(R.color.error_color);

        setContentView(R.layout.activity_status);

        count = (TextView) findViewById(R.id.status_count);

        ((Button) findViewById(R.id.status_submit)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { submit(); }
                } );

        statusText = (EditText) findViewById(R.id.status_message);
        statusText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { updateCount(); }

            @Override
            public void onTextChanged(CharSequence s, int strt, int pre, int n) { }

            @Override
            public void beforeTextChanged(CharSequence s, int strt, int pre, int post) { }
        } );
   }

    void  submit() {
        String status = statusText.getText().toString();
        if (TextUtils.isEmpty(status)) { return; }

        statusText.setText("");

        if (BuildConfig.DEBUG) { Log.d(TAG, "Submit status: " + status); }
        YambaService.post(this, status);
    }

    void updateCount() {
        int n = MAX_STATUS_LEN - statusText.getText().toString().length();

        int textColor = colorOk;
        if (ERROR_CHAR_CNT >= n) { textColor = colorError; }
        else if (WARN_CHAR_CNT >= n) { textColor = colorWarn; }
        count.setTextColor(textColor);
        count.setText(String.valueOf(n));
    }
}
