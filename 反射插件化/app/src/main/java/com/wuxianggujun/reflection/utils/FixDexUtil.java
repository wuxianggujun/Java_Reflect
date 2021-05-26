package com.wuxianggujun.reflection.utils;
import java.io.File;
import android.content.Context;
import dalvik.system.PathClassLoader;
import java.util.HashSet;
import dalvik.system.DexClassLoader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.lang.reflect.Array;
import android.util.Log;



public class FixDexUtil {

    
    public static String TAG = "FixDexUtil";

    public static void loadDex(Context context) {
        File filePath = context.getExternalFilesDir("fix_dex");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File file = new File(filePath.getPath(), "class.dex");
        if (file.exists() && file.isFile()) {
            try {
                //加载程序dex的代码
                PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
                    //加载指定修复的dex
                    DexClassLoader dexLoader = new DexClassLoader(file.getAbsolutePath(), file.getAbsolutePath(), null, pathLoader);
                    //开始合并
                    Log.i(TAG,"获取的文件名:"+file.getName());
                
                    Object dexPathList = getPathList(dexLoader);
                    Object pathPathList = getPathList(pathLoader);
                    //从pathList反射出element集合
                    Object leftDexElements = getDexElements(dexPathList);
                    Object rightDexElements = getDexElements(pathPathList);
                    //合并两个数组
                    Object dexElements = combineArray(leftDexElements,rightDexElements);

                    Object pathList = getPathList(pathLoader);
                    setField(pathList,pathList.getClass(),"dexElements",dexElements);              

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }
    
    
    
    public static void loadDex(Context context, HashSet<File> loadedDex) {
        File filePath = context.getExternalFilesDir("fix_dex");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File file = new File(filePath.getPath(), "class.dex");
        if (file.exists() && file.isFile()) {
            try {
                //加载程序dex的代码
                PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
                for (File dex:loadedDex) {
                    //加载指定修复的dex
                    DexClassLoader dexLoader = new DexClassLoader(dex.getAbsolutePath(), file.getAbsolutePath(), null, pathLoader);
                    //开始合并
                    Object dexPathList = getPathList(dexLoader);
                    Object pathPathList = getPathList(pathLoader);
                    //从pathList反射出element集合
                    Object leftDexElements = getDexElements(dexPathList);
                    Object rightDexElements = getDexElements(pathPathList);
                    //合并两个数组
                    Object dexElements = combineArray(leftDexElements,rightDexElements);

                    Object pathList = getPathList(pathLoader);
                    setField(pathList,pathList.getClass(),"dexElements",dexElements);
                    
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private static void setField(Object obj, Class<?> clz, String field, Object value) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field declaredField = clz.getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(obj,value);
    }

    /**
    *数组合并
    */
    private static Object combineArray(Object leftDexElements, Object rightDexElements) {
        Class<?> clazz = leftDexElements.getClass().getComponentType();
        int i = Array.getLength(leftDexElements);
        Log.i(TAG,"数组 i :"+i);
        int j = Array.getLength(rightDexElements);
        Log.i(TAG,"数组 j :"+j);
        int k = i+j;
        Log.i(TAG,"数组 k :"+k);
        Object result = Array.newInstance(clazz,k);
        System.arraycopy(leftDexElements, 0, result, 0, i);
        System.arraycopy(rightDexElements, 0, result, i, j);       
        return result;
    }

    private static Object getDexElements(Object dexPathList) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return getField(dexPathList,dexPathList.getClass(),"dexElements");
    }
    /**
     * 反射得到对象中的属性值
     */
    private static Object getField(Object obj, Class<?> cls, String field) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cls.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);     
    }
    private static Object getPathList(Object dexLoader) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return getField(dexLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }






}
