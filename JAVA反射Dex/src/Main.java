import java.util.*;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import java.io.InputStream;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import dalvik.system.DexClassLoader;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class Main {

    private static Main m = new Main();
    private static String filePath = "/AppProjects/DOM4jTest/UI设计器/app/build/intermediates/dex/debug/out/classes.dex";
    private static String apkDexPath = "AppProjects/Java_Reflect/JAVA反射Dex/";
    private static DexClassLoader dexClassLoader;

	public static void main(String[] args) {
		System.out.println("Hello World!");
        Method[] methods = Main.class.getMethods();
        for (Method method:methods) {
            System.out.println("获取的方法名 : " + method.getName());
        }
        test();
        m.init();
    }
    private void init(){
        dexClassLoader = new DexClassLoader(filePath,apkDexPath,null,getClass().getClassLoader());
        loadPlugin();
    }
    
    public void  loadPlugin(){
        try {
            Class<?> mClass = dexClassLoader.loadClass("com.wuxianggujun.uidesign.MainActivity");
            //获取类的实例
            Object beanObject = mClass.newInstance();
            //通过反射获取对应的方法
          //  Method setFeatureMethod = mClass.getMethod("",String.class);
          // setFeatureMethod.setAccessible(true);
            Method getFeatureMethod = mClass.getMethod("getFeature");
            getFeatureMethod.setAccessible(true);
            
            //然后执行对应方法
           // setFeatureMethod.invoke(beanObject,"丑的不行(▼皿▼#)");
            
            String feature = (String) getFeatureMethod.invoke(beanObject);
            System.out.println(feature);
        
             
        } catch (ClassNotFoundException|IllegalAccessException|InstantiationException|NoSuchMethodException|InvocationTargetException e) {
            e.printStackTrace();
        } 
        
        
        
    }
    
    
    
    
    private static void test() {
        String file = FilenameUtils.getName(filePath);
        System.out.println(file);
        File f = FileUtils.getFile(filePath);
        System.out.println(f.getName());
        System.out.println(f.getPath());

        System.out.println(m.loadDex(filePath, apkDexPath));
    }

    public DexClassLoader loadDex(String path, String luj) {
        DexClassLoader dex = new DexClassLoader(path, luj, null, getClass().getClassLoader());
        return dex;
    }



    /*


     package cn.gemini.k.myloaderdex;
     import androidx.appcompat.app.AppCompatActivity;
     import android.content.Context;
     import android.os.Bundle;
     import android.util.Log;
     import java.io.File;
     import java.io.IOException;
     import java.lang.reflect.Array;
     import java.lang.reflect.Constructor;
     import java.lang.reflect.Field;
     import java.lang.reflect.InvocationTargetException;
     import java.lang.reflect.Method;
     import dalvik.system.DexClassLoader;
     import dalvik.system.DexFile;

     public class MainActivity extends AppCompatActivity {
     // 通过反射实现自定义类加载器,加载一个新dex到应用中
     public void MyClassLoader(ClassLoader classloader,File dexFile,File optDexFile) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, IOException, InvocationTargetException, InstantiationException {
     // 一.获取原始的Element对象数组对象dexElements
     // 1.拿到DexClassLoader父类中的pathList对象
     Field pathList,dexElements;
     pathList = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
     pathList.setAccessible(true);
     Object pathListObj = pathList.get(classloader);
     // 2.利用pathList对象获取到dexElements数组对象,dexElements存放着所有dex文件的Elements类
     dexElements = pathListObj.getClass().getDeclaredField("dexElements");
     dexElements.setAccessible(true);
     Object[] dexElementsObj = (Object[]) dexElements.get(pathListObj);  //调用这个Field类的get方法,获取到dexElements

     // 二.将需要注入的dex文件构造成Element对象数组，并与原始的Element对象数组合并
     // 1.通过getComponentType方法获取dexElements成员对象的Class
     Class<?> elementClass = dexElementsObj.getClass().getComponentType();  //https://blog.csdn.net/qq_33546330/article/details/89784811
     // 2.通过Array.newInstance方法,传入dexElements成员对象的Class来构造一个新的newdexElements数组
     Object[] newElementsArray = (Object[]) Array.newInstance(elementClass,dexElementsObj.length+1);
     // 通过类对象的getConstructor()或getDeclaredConstructor()方法获得构造器(Constructor)对象并调用其newInstance()方法创建对象，适用于无参和有参构造方法
     Constructor<?> constructor = elementClass.getConstructor(File.class,boolean.class,File.class, DexFile.class);       
     //DexFile.loadDex:打开一个DEX文件，指定应在其中写入优化的DEX数据的文件路径。
     DexFile df = DexFile.loadDex(dexFile.getCanonicalPath(),optDexFile.getAbsolutePath(),0);

     // 传入需要注入的dex路径并初始化一个Element对象
     // public dalvik.system.DexPathList$Element(java.io.File,boolean,java.io.File,dalvik.system.DexFile)
     Object o = constructor.newInstance(null,false,dexFile,df); 
     // 3.使用需要注入的dex的Element对象创建一个数组
     Object[] ElementArray = new Object[]{o};
     // 4.将原始的Element对象数组与需要注入的dex的Element对象数组进行合并
     System.arraycopy(dexElementsObj,0,newElementsArray,0,dexElementsObj.length);
     System.arraycopy(ElementArray,0,newElementsArray,dexElementsObj.length,ElementArray.length);

     // 三.将合并后的Element对象数组更新到dexElements变量中，完成替换；
     dexElements.set(pathListObj,newElementsArray);
     }

     public void LoadMyDex() throws IllegalAccessException, InvocationTargetException, IOException, InstantiationException, NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {
     File dexFile = new File("/data/local/tmp/Injection.dex");
     File optPath = new File(getExternalCacheDir()+"/odex.dex");         //在外部SD上创建odex.dex文件
     MyClassLoader(getClassLoader(),dexFile,optPath);                               //调用我们自己定义的ClassLoader加载器
     Class cls = Class.forName("cn.gemini.k.dexinjection.MainActivity",true,getClassLoader());    //反射获取注入dex文件中的"cn.gemini.k.mydex.MainActivity"
     Object obj = cls.newInstance();
     if(cls != null){
     Log.i("LoadMyDex","Load success!");
     Method method = cls.getMethod("ShowMessage", Context.class);
     if(method != null){
     method.invoke(obj,this);
     Log.i("LoadMyDex","Call success!");
     }
     }
     }

     // 通过系统自带的DexClassLoader类加载器,加载一个新dex到应用中
     public void SystemClassLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
     DexClassLoader dexClassLoader = new DexClassLoader("/data/local/tmp/Injection.dex",getExternalCacheDir()+"/odex.dex",null,getClassLoader());
     Class<?> cls = dexClassLoader.loadClass("cn.gemini.k.dexinjection.MainActivity");
     Object obj = cls.newInstance();
     if(cls != null){
     Log.i("SystemClassLoader","Load success!");
     Method method = cls.getMethod("ShowMessage", Context.class);
     if(method != null){
     method.invoke(obj,this);
     Log.i("SystemClassLoader","Call success!");
     }
     }
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);
     //Android类加载器
     //Android虚拟机在运行程序时,虚拟机需要将Class加载到内存中才能运行,完成这一加载工作的类就是ClassLoader,
     int count = 0;
     ClassLoader classLoader = getClassLoader(); //获取当前进程的ClassLoader
     if(classLoader != null){
     Log.i("MyLoaderDex","classLoader"+ count++ + ":" + classLoader.toString());
     while (classLoader.getParent()!=null){      //循环遍历父类加载器
     classLoader = classLoader.getParent();
     Log.i("MyLoaderDex","classLoader"+count++ +":"+classLoader.toString());
     }
     }
     // I/MyLoaderDex: classLoader0:dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/cn.gemini.k.myloaderdex-kJC4L5FvbCMB3s0dMBzRqQ==/base.apk"],nativeLibraryDirectories=[/data/app/cn.gemini.k.myloaderdex-kJC4L5FvbCMB3s0dMBzRqQ==/lib/arm64, /system/lib64]]]
     // I/MyLoaderDex: classLoader1:java.lang.BootClassLoader@ed7b5bc
     // 一个是BootClassLoader,系统启动的时候创建的,另一个是PathClassLoader,应用启动时创建的
     // 我们加载dex时,常用的ClassLoader: DexClassLoader 和 PathClassLoader:

     //自定义ClassLoader加载dex
     try {
     LoadMyDex();
     //MyClassLoader();
     } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | IOException | InstantiationException | InvocationTargetException | ClassNotFoundException e) {
     e.printStackTrace();
     }
     //使用系统提供的ClassLoader加载dex
     //try {
     //    SystemClassLoader();
     //} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
     //    e.printStackTrace();
     //}
     }
     }


     */
}
