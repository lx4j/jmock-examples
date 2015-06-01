package util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L.x on 15-6-1.
 */
public class Announcer<T> {
    private final T proxy;
    private List<T> listeners = new ArrayList<T>();

    public static <T> Announcer<T> to(Class<T> listenerType) {
        return new Announcer<T>(listenerType);
    }

    public Announcer(Class<T> listenerType) {
        proxy = listenerType.cast(Proxy.newProxyInstance(Announcer.class.getClassLoader(), new Class[]{listenerType}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                announce(method, args);
                return null;
            }
        }));
    }

    private void announce(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        for (T listener : listeners) {
            try {
                method.invoke(listener, args);
            } catch (Exception ignored) {
            }
        }
    }

    public void add(T listener) {
        listeners.add(listener);
    }

    public void remove(T listener) {
        listeners.remove(listener);
    }

    public T proxy() {
        return proxy;
    }
}
