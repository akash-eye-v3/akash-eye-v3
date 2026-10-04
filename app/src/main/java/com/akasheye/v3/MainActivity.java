package com.akasheye.v3;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle b){
    super.onCreate(b); setContentView(R.layout.activity_main);
    Button btn=findViewById(R.id.startBtn);
    btn.setOnClickListener(v->{
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())));
        Toast.makeText(this,"মামা Overlay ON করো",Toast.LENGTH_LONG).show();
      } else {
        startService(new Intent(this,FloatingEyeService.class));
        Toast.makeText(this,"চোখ চালু! বাইরে ভাসবে এখন",Toast.LENGTH_LONG).show();
      }
    });
  }
}
