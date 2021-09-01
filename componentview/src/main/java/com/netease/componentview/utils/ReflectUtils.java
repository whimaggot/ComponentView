package com.netease.componentview.utils;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {

    public static String getGetterName(String name){
        if(!TextUtils.isEmpty(name)){
            try {
                String firstLetter = name.substring(0,1).toUpperCase();
                String methodName = "get"+firstLetter+name.substring(1);
                return methodName;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object getData(String name,Object source){
        try {
            Method method = source.getClass().getMethod(getGetterName(name));
            return method.invoke(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field[] getField(Class source){
        if(source==null){
            return new Field[]{};
        }

        return source.getClass().getFields();
    }
}
