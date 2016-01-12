/**
 * The MIT License
 *
 * Copyright (c) 2016 Waldo J. Urribarri. http://www.waldou.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.waldou.example.httprequestsample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static HttpConn connection;

    private TextView tvEmail;
    private TextView tvLocation;
    private TextView tvOccupation;
    private TextView tvURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference to the screen TextViews
        tvEmail = (TextView)this.findViewById(R.id.email);
        tvLocation = (TextView)this.findViewById(R.id.location);
        tvOccupation = (TextView)this.findViewById(R.id.occupation);
        tvURL = (TextView)this.findViewById(R.id.url);

        // Call to the AsyncTask which does all the work.
        // We'll pass references to the TextViews so it updates
        // the values in the UI Thread at the end of it's execution.
        new HttpRequestTask(this).execute("www.colourlovers.com");
    }

    /*
     * This just updates the UI fields. It should be called from the UI thread.
     */
    public void updateValues(String email, String location, String occupation, String url) {
        tvEmail.setText(email);
        tvLocation.setText(location);
        tvOccupation.setText(occupation);
        tvURL.setText(url);
    }

}
