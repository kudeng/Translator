package com.example.thinkpad.star;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ObjectStreamException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    private EditText editText;
    private EditText outputView;
    private Button button;
    private Spinner spinner_input;
    private Spinner spinner_output;
    //private MediaPlayer mp;
    private Map<String,String> map = new HashMap<String, String>();
    TranslateRequest transrequest = new TranslateRequest();
    OkHttpClient client;
    Request request;
    String url;


    String[] history = new String[10];
    int i = 0;
    int j = 0;
    {
        for(int c=0;c<history.length;c++)
        {
            history[c]="null";
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.input);
        outputView = findViewById(R.id.output);
        button = (Button) findViewById(R.id.button);
        spinner_input = (Spinner) findViewById(R.id.spinner_input);
        spinner_output = (Spinner) findViewById(R.id.spinner_output);
        spinner_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getResources().getStringArray(R.array.languages);
                String s1 = spinner_input.getSelectedItem().toString();
                switch (s1){
                    case "中文": transrequest.from = "zh-CHS"; break;
                    case "英文": transrequest.from = "EN"; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_output.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getResources().getStringArray(R.array.languages);
                String s2 = spinner_output.getSelectedItem().toString();
                switch(s2){
                    case "中文": transrequest.to = "zh-CHS"; break;
                    case "英文": transrequest.to = "EN"; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    public void onClickOfButton(View view) {
        translate(editText.getText().toString());

        history[i] = editText.getText().toString();
        j=i;
        i++;
        if(i==10) i=0;
    }

    public void onClickOfButton1(View view) {
        editText.setText("");
        outputView.setText("");
    }

    public void translate(String s) {
        try {
            transrequest.q = s;
            transrequest.salt = String.valueOf(System.currentTimeMillis());
            transrequest.sign = md5(transrequest.appKey + transrequest.q + transrequest.salt + transrequest.key );
            Map<String, String> params = new HashMap<String, String>();
            params.put("q", transrequest.q);
            params.put("from", transrequest.from);
            params.put("to", transrequest.to);
            params.put("appKey", transrequest.appKey);
            params.put("salt", transrequest.salt);
            params.put("sign", transrequest.sign);
            params.put("ext", transrequest.ext);
            params.put("voice", transrequest.voice);
            String requestString = getUrlWithQueryString("https://openapi.youdao.com/api", params);

            client = new OkHttpClient();
            request = new Request.Builder()
                    .url(requestString)
                    .build();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        String json = response.body().string();
//                        response.header("Connection", "close");
                        System.out.println(json);
                        Gson gson = new Gson();
                        TranslateResult result = gson.fromJson(json, TranslateResult.class);
                        url = result.tSpeakUrl;
                        System.out.println(url);
                        outputView = findViewById(R.id.output);
                        outputView.setText(result.translation[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 生成32位MD5摘要
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = string.getBytes("utf-8");
            /** 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /** 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /** 获得密文 */
            byte[] md = mdInst.digest();
            /** 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }
    /**
     * 根据api地址和参数生成请求URL
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }
    /**
     * 进行URL编码
     * @param input
     * @return
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.action_read:
                openWordlist();
                return true;
            case R.id.action_history:
                openHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openWordlist() {
        // 语音播放
        //Intent intent = new Intent(this,WordlistActivity.class);
        String input = editText.getText().toString();
        String output = outputView.getText().toString();
        map.put(input, output);

    }

    public void openHistory() {
        // 历史纪录
        editText.setText(history[j]);
        j--;
        if(j==-1) j=9;
    }

    public void onClickOfList(View view){
        Intent intent = new Intent(this,WordlistActivity.class);
        final SerializableMap myMap = new SerializableMap();
        myMap.setMap(map);
        Bundle bundle = new Bundle();
        bundle.putSerializable("map",myMap);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}

