package me.zdnuist.annotation.fragment;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import me.zdnuist.annotation.R;
import me.zdnuist.annotation.module_annotation.FindView;
import me.zdnuist.annotation.utils.FindViewUtil;

/**
 * @createtime: 2018/8/27 on 9:33
 * @desc:
 * @author: zd
 */
@Keep
public class ViewFragment extends Fragment {

  public static ViewFragment newInstance(){
    ViewFragment viewFragment = new ViewFragment();
    return viewFragment;
  }

  @FindView(R.id.id_title)
  TextView mText;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.layout_fragment_view, container, false);

    ButterKnife.bind(this, view);
    FindViewUtil.bind(this, view);
    mText.setText("find id in Fragment by FinderView");
    return view;
  }
}
