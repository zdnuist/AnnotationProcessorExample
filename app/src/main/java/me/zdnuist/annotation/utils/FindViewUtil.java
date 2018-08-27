package me.zdnuist.annotation.utils;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;
import java.lang.reflect.Constructor;

/**
 * @createtime: 2018/8/24 on 17:49
 * @desc:
 * @author: zd
 */
public final class FindViewUtil {

  private FindViewUtil(){}

  public static void bind(final Activity target){
    View sourceView = target.getWindow().getDecorView();
    try{
      Class cls = target.getClass();
      Class bindingClass = cls.getClassLoader().loadClass(cls.getName() + "_FindView");
      Constructor<?> constructor =  bindingClass.getConstructor(cls, View.class);
      constructor.newInstance(target, sourceView);
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void bind(final Object target ,final View sourceView){
    try{
      Class cls = target.getClass();
      Class bindingClass = cls.getClassLoader().loadClass(cls.getName() + "_FindView");
      Constructor<?> constructor =  bindingClass.getConstructor(cls, View.class);
      constructor.newInstance(target, sourceView);
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public static <T> T findRequiredViewAsType(View source, @IdRes int id, String who,
      Class<T> cls) {
    View view = findRequiredView(source, id, who);
    return castView(view, id, who, cls);
  }

  public static View findRequiredView(View source, @IdRes int id, String who) {
    View view = source.findViewById(id);
    if (view != null) {
      return view;
    }
    String name = getResourceEntryName(source, id);
    throw new IllegalStateException("Required view '"
        + name
        + "' with ID "
        + id
        + " for "
        + who
        + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional'"
        + " (methods) annotation.");
  }

  private static String getResourceEntryName(View view, @IdRes int id) {
    if (view.isInEditMode()) {
      return "<unavailable while editing>";
    }
    return view.getContext().getResources().getResourceEntryName(id);
  }

  public static <T> T castView(View view, @IdRes int id, String who, Class<T> cls) {
    try {
      return cls.cast(view);
    } catch (ClassCastException e) {
      String name = getResourceEntryName(view, id);
      throw new IllegalStateException("View '"
          + name
          + "' with ID "
          + id
          + " for "
          + who
          + " was of the wrong type. See cause for more info.", e);
    }
  }

}
