package me.zdnuist.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import me.zdnuist.annotation.module_annotation.Alias;

@Alias(name = "home")
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
}
