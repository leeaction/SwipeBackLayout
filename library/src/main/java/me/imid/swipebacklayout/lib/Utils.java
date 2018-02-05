
package me.imid.swipebacklayout.lib;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Chaojun Wang on 6/9/14.
 */
public class Utils {
    private Utils() {
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(android.app.Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity,TranslucentConversionListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity,listener);
        } else {
            convertActivityToTranslucentBeforeL(activity,null);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    public static void convertActivityToTranslucentBeforeL(Activity activity,TranslucentConversionListener listener) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[] {
                null
            });
        } catch (Throwable t) {
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertActivityToTranslucentAfterL(Activity activity,TranslucentConversionListener listener) {
        try {
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object options = getActivityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            Object TCLProxy = null;
            if(listener != null){
                TCLProxy = Proxy.newProxyInstance(translucentConversionListenerClazz.getClassLoader(),new Class[]{translucentConversionListenerClazz}, new TranslucentConversionListenerInvocationHandler(listener));
            }
            convertToTranslucent.invoke(activity, TCLProxy, options);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static class TranslucentConversionListenerInvocationHandler implements InvocationHandler{

        private TranslucentConversionListener mListener;

        public TranslucentConversionListenerInvocationHandler(TranslucentConversionListener listener){
            mListener = listener;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("onTranslucentConversionComplete")) {
                if(mListener != null){
                    mListener.onTranslucentConversionComplete();
                }
            }
            return null;
        }
    }
}
