package com.wuxianggujun.reflection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import java.io.File;
import android.widget.Toast;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Call;
import java.io.IOException;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import android.os.Looper;
import java.io.InputStream;
import java.io.FileOutputStream;
import android.os.Environment;
import android.os.Message;
import android.os.Handler;
import com.wuxianggujun.reflection.utils.OkHttpClientTool;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import com.wuxianggujun.reflection.utils.ConfigInfo;
import com.wuxianggujun.reflection.utils.FixDexUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //HotFix.copyDexFileToAppAndFix(MainActivity.this, "classes.dex", true);
 
        
        FixDexUtil.loadDex(MainActivity.this); 
        
        BugClass bug = new BugClass(MainActivity.this);
      
        
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //OkHttpClientTool.getUnsafeOkHttpClient();
        //new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("http://ssdlearn.top/wangpan/down.php/1bea11b4abcc2f828064b4f306785ea0.dex").build();
        okHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("myTag", "下载失败"+e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                              
                        writeFile(response);
                        
                        Log.i("APP", "文件下载成功");    
                        
                    }
                }
            });



    }
    
    private void writeFile(Response response) throws IOException{
        File filePath = getExternalFilesDir("fix_dex");
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        File file = new File(filePath.getPath(),"class.dex");
        if(file.exists()&&file.isFile()&&file.length() != response.body().contentLength()){
            file.delete();
            Log.i("File","文件删除成功!" +"网络文件大小"+ response.body().contentLength() +"本地文件大小:"+ file.length());       
        }
         if(file.createNewFile()){
             writeFile(file,response);
             Log.i("File","文件创建成功:"+file.getPath());        
            
        }
    }
    
    private void writeFile(File file,Response response) {
        OutputStream outputStream = null;
        InputStream inputStream = response.body().byteStream();
        try {
            outputStream = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024*10];
            while ((len = inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null)
                    inputStream.close();
                if(outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

       




}
