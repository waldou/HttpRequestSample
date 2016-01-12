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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class HttpConn {

    private static final int REQUEST_TIMEOUT = 10000;
    private static final String ENCODING = "UTF-8";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static final String ACCEPT_ENCODING = "gzip, deflate, sdch";
    private static final String ACCEPT_LANGUAGE = "en-US,en;q=0.8,es;q=0.6";
    private static final String CONNECTION = "keep-alive";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    private String host;
    private List<String> cookies;
    private int lastResponseCode;

    public HttpConn(String host) {
        CookieHandler.setDefault(new CookieManager());
        this.host = host;
    }

    public String get(String url) throws ProtocolException, MalformedURLException, IOException {

        // Setup the new connection.
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        // We need to set up all the params for the request.
        conn.setRequestMethod("GET");
        conn.setUseCaches(false); //no-cache
        conn.setConnectTimeout(REQUEST_TIMEOUT); // In case the URL is unavailable we use this timeout.
        conn.setReadTimeout(REQUEST_TIMEOUT);
        conn.setRequestProperty("Host", host);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", ACCEPT);
        conn.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
        conn.setRequestProperty("Accept-Encoding", ACCEPT_ENCODING);
        conn.setRequestProperty("Connection", CONNECTION);

        // This is used to not mess with the cookies already grabbed on a previous request
        if (cookies != null) {
            for (String cookie : cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }

        // Check if the response is Gzip encoded
        InputStream rinput = conn.getInputStream();
        BufferedReader in = null;
        List<String> content_encoding = conn.getHeaderFields().get("Content-Encoding");
        if(content_encoding != null) {
            String enc = content_encoding.get(0);
            if(enc.equals("gzip"))
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream()), ENCODING));
            else
                in = new BufferedReader( new InputStreamReader(conn.getInputStream(), ENCODING) );
        } else {
            in = new BufferedReader( new InputStreamReader(conn.getInputStream(), ENCODING) );
        }

        String line;
        StringBuffer response = new StringBuffer();

        // Get whole html response.
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Save the last response code for further checking if needed.
        lastResponseCode = conn.getResponseCode();

        // We store the cookies.
        if (cookies == null) {
            List<String> cooks = conn.getHeaderFields().get("Set-Cookie");
            if(cooks != null)
                cookies = cooks;
        }

        return response.toString();

    }

    public String post(String url, String referer, String postParams) throws ProtocolException, MalformedURLException, IOException {

        // Setup the new connection.
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        // We need to set up all the params for the request.
        conn.setConnectTimeout(REQUEST_TIMEOUT);
        conn.setReadTimeout(REQUEST_TIMEOUT);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", host);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", ACCEPT);
        conn.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
        conn.setRequestProperty("Connection", CONNECTION);
        conn.setRequestProperty("Referer", referer);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        // This is used to not mess with the cookies already grabbed on a previous request
        if (cookies != null) {
            for (String cookie : cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post params
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        lastResponseCode = conn.getResponseCode();

        BufferedReader in = new BufferedReader( new InputStreamReader(conn.getInputStream(), ENCODING) );

        String line;
        StringBuffer response = new StringBuffer();

        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();

    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }

}
