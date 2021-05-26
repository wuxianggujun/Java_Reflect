
import dalvik.system.DexClassLoader;
import java.lang.reflect.Method;


public class Main2 
{
    
    public static void main(String[] args){
        Main2 m =new Main2();
        m.init();
    }
    
    
    private void init(){
        DexClassLoader dcl = new DexClassLoader("AppProjects/Java_Reflect/JAVA反射Dex/classes.dex",
                                               "AppProjects/Java_Reflect/JAVA反射Dex/", null,
                                                getClass().getClassLoader());
        try {
            Class<?> cla=dcl.loadClass("com.wuxianggujun.uidesign.MainActivity");
            Object obj=cla.newInstance();
            Method action=cla.getMethod("SendMes", String.class);
           /* System.out.println(mes);
            action.invoke(obj, mes);*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    
    
    
}
