package betatweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.src.StringTranslate;

import java.lang.reflect.Field;
import java.util.Properties;

public class ModLoaderUtils {

    public static Object getPrivateValue(Class class1, Object obj, int i)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field field = class1.getDeclaredFields()[i];
            field.setAccessible(true);
            return field.get(obj);
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            return null;
        }
    }

    public static Object getPrivateValue(Class class1, Object obj, String s)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field field = class1.getDeclaredField(s);
            field.setAccessible(true);
            return field.get(obj);
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            return null;
        }
    }

    public static void AddLocalization(String s, String s1)
    {
        Properties properties = null;
        try
        {
            properties = (Properties)getPrivateValue(net.minecraft.src.StringTranslate.class, StringTranslate.getInstance(), 1);
        }
        catch(SecurityException securityexception)
        {
        }
        catch(NoSuchFieldException nosuchfieldexception)
        {
        }
        if(properties != null)
        {
            properties.put(s, s1);
        }
    }
}
