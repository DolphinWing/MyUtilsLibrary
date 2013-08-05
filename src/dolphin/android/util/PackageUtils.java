package dolphin.android.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class PackageUtils
{
    /**
     * get package info
     * @param context
     * @param cls
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, Class<?> cls)
    {
        try {
            ComponentName comp = new ComponentName(context, cls);
            return context.getPackageManager().getPackageInfo(
                comp.getPackageName(), 0);
            //return pinfo;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * check if any activity can handle this intent
     * @param context
     * @param intent
     * @return
     */
    public static boolean isCallable(Context context, Intent intent)
    {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
