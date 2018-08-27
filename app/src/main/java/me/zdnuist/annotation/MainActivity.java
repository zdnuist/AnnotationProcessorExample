package me.zdnuist.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zdnuist.annotation.fragment.ViewFragment;
import me.zdnuist.annotation.module_annotation.Alias;
import me.zdnuist.annotation.module_annotation.FindView;
import me.zdnuist.annotation.utils.FindViewUtil;

@Alias(name = "home")
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  @BindView(R.id.id_title)
  TextView title;

  @FindView(R.id.id_btn)
  Button button;

  @FindView(R.id.id_root)
  ViewGroup root;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);
    FindViewUtil.bind(this);
    try {
      title.setText("find id by ButterKnife");
      button.setText("find id by FinderView");
      button.setOnClickListener(this);
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.id_btn:
        getSupportFragmentManager().beginTransaction().add(R.id.id_root, ViewFragment.newInstance()).commit();
        break;
    }
  }
}
