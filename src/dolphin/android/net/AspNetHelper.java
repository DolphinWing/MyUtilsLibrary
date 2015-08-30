package dolphin.android.net;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jimmyhu on 2015/1/5.
 * ASP.NET helper to get rid of __VIEWSTATE & __EVENTVALIDATION
 */
public class AspNetHelper {
    private final static String TAG = "AspNetHelper";
    private String mUrl;
    private String mViewState;
    private String mValidation;
    private String mEncoding = "utf-8";

    public AspNetHelper(String url) {
        this(url, "utf-8");
    }

    public AspNetHelper(String url, String encoding) {
        mUrl = url;
        mEncoding = encoding;
        mValidation = "";
        mViewState = "";
        //make first request to get view state and validation
        makeUrlRequest("", "");
    }

    private void parseResponseForNextUse(String response) {
        if (response != null && !response.isEmpty()) {
            //Log.d(TAG, "response " + response.length());

            //save the validation for next request
            if (response.contains("__EVENTVALIDATION")) {
                mValidation = response.substring(response.lastIndexOf("__EVENTVALIDATION"));
                mValidation = mValidation.substring(mValidation.indexOf("value=\""),
                        mValidation.indexOf("\" />"));
                mValidation = mValidation.substring("value=\"".length(), mValidation.length());
                //Log.d(TAG, mValidation);
            }

            //save the view state for next request
            if (response.contains("__VIEWSTATE")) {
                mViewState = response.substring(response.lastIndexOf("__VIEWSTATE"));
                mViewState = mViewState.substring(mViewState.indexOf("value=\""),
                        mViewState.indexOf("\" />"));
                mViewState = mViewState.substring("value=\"".length(), mViewState.length());
                //Log.d(TAG, mViewState);
            }
        }
    }

    /**
     * make POST/GET request by UrlConnection
     * http://www.cnblogs.com/menlsh/archive/2013/05/22/3091983.html
     *
     * @param name  changed field
     * @param value changed data
     * @return html content
     */
    public String makeUrlRequest(String name, String value) {
        URL url;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
            return null;
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "openConnection: " + e.getMessage());
            return null;
        }

        String response = null;
        if (!mValidation.isEmpty() && !mViewState.isEmpty()) {//add POST header
            Map<String, String> params = new HashMap<>();

            if (name != null && !name.isEmpty()) {
                params.put(name, value);
                params.put("__EVENTTARGET", name);
                params.put("__EVENTARGUMENT", "");
            } else {
                params.put("__EVENTTARGET", "");
                params.put("__EVENTARGUMENT", "");
            }
            params.put("__EVENTVALIDATION", mValidation);
            params.put("__LASTFOCUS", "");
            params.put("__VIEWSTATE", mViewState);

            try {
                conn.setRequestMethod("POST");
                //Log.d(TAG, "post " + name + ", " + value);
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            conn.setDoInput(true);
            conn.setDoOutput(true);//set enable output for POST
            conn.setUseCaches(false);//don't use cache for POST

            byte[] data = getRequestData(params, mEncoding).toString().getBytes();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));

            int responseCode = 0;
            OutputStream outputStream = null;
            try {//write POST data
                outputStream = conn.getOutputStream();
                outputStream.write(data);
                responseCode = conn.getResponseCode();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "write IOException: " + e.getMessage());
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "close out stream exception");
                }
            }

            //Log.d(TAG, "response code = " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, String.format("POST failed (%d)", responseCode));
                return null;
            }
        }

        InputStream inputStream = null;
        try {//read POST response
            conn.setReadTimeout(5000);
            inputStream = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            // Read response into a buffered stream
            byte[] sBuffer = new byte[512];
            int readBytes = 0;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            response = new String(content.toByteArray(), mEncoding);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "read IOException: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "close in stream exception");
            }
        }

        //Log.d(TAG, String.format("response length = %d", response.length()));
        parseResponseForNextUse(response);
        return response;
    }

    /**
     * encode POST request data
     *
     * @param params post field
     * @param encode text encoding
     * @return encoded request data
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //store the request
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //delete last "&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
