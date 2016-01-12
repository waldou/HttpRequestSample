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

import android.os.AsyncTask;
import android.widget.TextView;
import java.net.URLEncoder;

public class HttpRequestTask extends AsyncTask<String, String, String[]> {

    private static final String URL_MAIN = "https://www.colourlovers.com/ajax/header-log-in-form?r=http%3A%2F%2Fwww.colourlovers.com%2F";
    private static final String URL_LOGIN = "https://www.colourlovers.com/op/log-in/1";
    private static final String URL_ACCOUNT = "http://www.colourlovers.com/account";

    private MainActivity activity;

    public HttpRequestTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(String... params) {

        String host = params[0];
        MainActivity.connection = new HttpConn(host);

        String page = "";

        // Do a GET to the main page
        try {
            page = MainActivity.connection.get(URL_MAIN);
        } catch (Exception e) {
            // You should implement some action but right now this should do for the example
            e.printStackTrace();
            return null;
        }

        /*
         * We'll not get from the first page the X and Y values for the parameters,
         * but if we needed to, then we'd have to extract them from the
         * raw HTML. There are libraries to do this, but usually I prefer
         * to do my own implementations so the APK doesn't get so
         * much bigger.
         */
        String postParams = "";
        try {
            postParams = "r=" + URLEncoder.encode("http%3A%2F%2Fwww.colourlovers.com%2F", "UTF-8") +
                    "&userName=" + URLEncoder.encode("THEUSERNAME", "UTF-8") +
                    "&userPassword=" + URLEncoder.encode("THEPASSWORD", "UTF-8") +
                    "&x=0&y=0";
        } catch (Exception e) {
            // You should implement some action but right now this should do for the example
            e.printStackTrace();
            return null;
        }

        // Do a POST to the login page
        try {
            page = MainActivity.connection.post(
                    URL_LOGIN, //URL
                    URL_MAIN, //REFERER
                    postParams // PARAMETERS
            );
        } catch (Exception e) {
            // You should implement some action but right now this should do for the example
            e.printStackTrace();
            return null;
        }

        // Do a GET to the Account page (which should not be available if you are not logged in)
        try {
            page = MainActivity.connection.get(URL_ACCOUNT);
        } catch (Exception e) {
            // You should implement some action but right now this should do for the example
            e.printStackTrace();
            return null;
        }

        // Get the data, show it as you like it.
        // For this example I'll just do a basic substring
        // of the page until I get the values I want.
        // You should implement something more appropriate and
        // performance aware.

        StringBuilder sb = new StringBuilder(page);

        int idx1 = -1, idx2 = -1;
        final String INPUT_EMAIL = "name=\"userEmailAddress\"";
        final String INPUT_LOCATION = "name=\"userLocation\"";
        final String INPUT_OCCUPATION = "name=\"userOccupation\"";
        final String INPUT_URL = "name=\"userUrl\"";
        final String INPUT_VALUE = "value=\"";
        final String END_CHAR = "\"";

        // Extract email
        idx1 = sb.indexOf(INPUT_EMAIL);
        sb.delete(0, idx1 + INPUT_EMAIL.length());
        idx1 = sb.indexOf(INPUT_VALUE);
        sb.delete(0, idx1 + INPUT_VALUE.length());
        idx1 = sb.indexOf(END_CHAR);
        String email = sb.substring(0, idx1);
        sb.delete(0, idx1 + END_CHAR.length());

        // Extract location
        idx1 = sb.indexOf(INPUT_LOCATION);
        sb.delete(0, idx1 + INPUT_LOCATION.length());
        idx1 = sb.indexOf(INPUT_VALUE);
        sb.delete(0, idx1 + INPUT_VALUE.length());
        idx1 = sb.indexOf(END_CHAR);
        String location = sb.substring(0, idx1);
        sb.delete(0, idx1 + END_CHAR.length());

        // Extract occupation
        idx1 = sb.indexOf(INPUT_OCCUPATION);
        sb.delete(0, idx1 + INPUT_OCCUPATION.length());
        idx1 = sb.indexOf(INPUT_VALUE);
        sb.delete(0, idx1 + INPUT_VALUE.length());
        idx1 = sb.indexOf(END_CHAR);
        String occupation = sb.substring(0, idx1);
        sb.delete(0, idx1 + END_CHAR.length());

        // Extract URL
        idx1 = sb.indexOf(INPUT_URL);
        sb.delete(0, idx1 + INPUT_URL.length());
        idx1 = sb.indexOf(INPUT_VALUE);
        sb.delete(0, idx1 + INPUT_VALUE.length());
        idx1 = sb.indexOf(END_CHAR);
        String url = sb.substring(0, idx1);
        sb.delete(0, idx1 + END_CHAR.length());

        String[] values = {email, location, occupation, url};

        return values;
    }

    @Override
    protected void onPostExecute(String[] values) {
        super.onPostExecute(values);
        if(activity != null && values != null) {
            activity.updateValues(values[0], values[1], values[2], values[3]);
        }

    }

}
