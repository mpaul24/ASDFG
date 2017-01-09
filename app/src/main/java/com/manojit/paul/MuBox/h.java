package com.manojit.paul.MuBox;

/**
 * Created by Manojit Paul on 11/3/2016.
 */


       /* import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.RequestQueue;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.squareup.okhttp.Callback;
        import com.squareup.okhttp.OkHttpClient;
        import com.squareup.okhttp.Request;
        import com.squareup.okhttp.Response;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;*/

public class h {





       /* @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            loadImages();
        }



        void loadImages(){
            OkHttpClient client=new OkHttpClient();
            Request request= new Request.Builder().url("https://www.instagram.com/superman/media/").build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.d("Failure",e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String result=response.body().string();
                    if(result==null){
                        Toast.makeText(getApplicationContext(),"Failed to Load Images!\n Please Try Again",Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        JSONObject jo1=new JSONObject(result);
                        JSONArray ja1=jo1.getJSONArray("items");
                        Log.d("After","items");
                        JSONObject jo2,jo3,jo4,jo5,jo6;
                        JSONObject low=new JSONObject(),thumb=new JSONObject(),standard=new JSONObject();
                        for(int i=0;i<ja1.length();i++){
                            jo2=ja1.getJSONObject(i);
                            Log.d("After","i");
                            jo3=jo2.getJSONObject("images");
                            Log.d("After","images");
                            jo4=jo3.getJSONObject("low_resolution");
                            Log.d("After","low_resolution");
                            jo5=jo3.getJSONObject("thumbnail");
                            Log.d("After","thumbnail");
                            jo6=jo3.getJSONObject("standard_resolution");
                            Log.d("After","standard_resolution");
                            low.put(String.valueOf(i),jo4.getString("url"));
                            Log.d("After","url");
                            thumb.put(String.valueOf(i),jo5.getString("url"));
                            Log.d("After","url");
                            standard.put(String.valueOf(i),jo6.getString("url"));
                            Log.d("After","url");
                            Log.d("TEXTJHJHJHJHJHJHJHJHJHJHJHJHJ",low.toString());
                        }
                        //HERE LOAD IMAGES IN PICASO VIA ANYONE OF low,thumb,standard JSON OBjects. They represent quality of pics.
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"XXXXCEPTION.!!!Failed to Load Images!\n Please Try Again",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }*/



    }


