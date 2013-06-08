
package com.marakana.android.yamba;

import android.os.Bundle;
import android.content.res.Resources;

public class StatusActivity extends BaseActivity {
    private int colorOk;
    private int colorWarn;
    private int colorError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources rez = getResources();
        colorOk = rez.getColor(R.color.ok_color);
        colorWarn = rez.getColor(R.color.warn_color);
        colorError = rez.getColor(R.color.error_color);

        setContentView(R.layout.activity_status);
    }

    public int getColor(int id) {
        switch (id) {
            case R.color.error_color:
                return colorError;
            case R.color.warn_color:
                return colorWarn;
            default:
                return colorOk;
        }
    }
}
