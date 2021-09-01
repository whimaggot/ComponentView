package com.netease.componentview.annotion;


import com.netease.componentview.base.IComponentView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Repeatable(ComponentViewDatas.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ComponentViewData {
    Class<? extends IComponentView> value();
}


