package me.zdnuist.annotation.module_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import android.support.annotation.IdRes;

/**
 * @createtime: 2018/8/24 on 16:33
 * @desc:
 * @author: zd
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface FindView {

  @IdRes int  value();

}
