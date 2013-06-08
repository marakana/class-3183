
package com.marakana.android.yamba;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Fragment;
import com.marakana.android.yamba.svc.YambaService;

public class StatusFragment extends Fragment {
    private static final String TAG = "STATUS";

    public static final int MAX_STATUS_LEN = 140;
    public static final int WARN_CHAR_CNT = 10;
    public static final int ERROR_CHAR_CNT = 0;


    private EditText statusText;
    private TextView count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_status, container, false);

        count = (TextView) view.findViewById(R.id.status_count);

        ((Button) view.findViewById(R.id.status_submit)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { submit(); }
                } );

        statusText = (EditText) view.findViewById(R.id.status_message);
        statusText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { updateCount(); }

            @Override
            public void onTextChanged(CharSequence s, int strt, int pre, int n) { }

            @Override
            public void beforeTextChanged(CharSequence s, int strt, int pre, int post) { }
        } );

        return view;
    }

    void  submit() {
        String status = statusText.getText().toString();
        if (TextUtils.isEmpty(status)) { return; }

        statusText.setText("");

        if (BuildConfig.DEBUG) { Log.d(TAG, "Submit status: " + status); }
        YambaService.post(getActivity(), status);
    }

    void updateCount() {
        int n = MAX_STATUS_LEN - statusText.getText().toString().length();


        StatusActivity act = (StatusActivity) getActivity();
        int textColor = act.getColor(R.color.ok_color);
        if (ERROR_CHAR_CNT >= n) { textColor = act.getColor(R.color.error_color); }
        else if (WARN_CHAR_CNT >= n) { textColor = act.getColor(R.color.warn_color); }
        count.setTextColor(textColor);
        count.setText(String.valueOf(n));
    }
}
