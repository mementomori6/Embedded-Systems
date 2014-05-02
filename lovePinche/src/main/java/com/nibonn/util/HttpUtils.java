package com.nibonn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GuYifan on 2014/4/29.
 */
public class HttpUtils {

    public static final int DEFAULT_TIMEOUT = 3000;
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static String post(String url, String data, int timeout, String charset) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);

        BufferedReader in = null;
        OutputStream out = null;

        try {
            out = conn.getOutputStream();
            out.write(data.getBytes(charset));
        } finally {
            if (out != null) {
                out.close();
            }
        }

        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);   // delete final \n
            }
            return sb.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String post(String url, String data) throws IOException {
        return post(url, data, DEFAULT_TIMEOUT, DEFAULT_CHARSET);
    }
}
