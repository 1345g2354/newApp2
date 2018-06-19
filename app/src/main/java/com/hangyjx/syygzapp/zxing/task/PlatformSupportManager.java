package com.hangyjx.syygzapp.zxing.task;

import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>Sometimes the application wants to access advanced functionality exposed by Android APIs that are only available
 * in later versions of the platform. While {@code Build.VERSION} can be used to determine the device's API level
 * and alter behavior accordingly, and it is possible to write code that uses both old and new APIs selectively,
 * such code would fail to load on older devices that do not have the new API methods.</p>
 *
 * <p>It is necessary to only load classes that use newer APIs than the device may support after the app
 * has checked the API level. This requires reflection, loading one of several implementations based on the
 * API level.</p>
 *
 * <p>This class manages that process. Subclasses of this class manage access to implementations of a given interface
 * in an API-level-aware way. Subclasses implementation classes <em>by name</em>, and the minimum API level that
 * the implementation is compatible with. They also provide a default implementation.</p>
 *
 * <p>At runtime an appropriate implementation is then chosen, instantiated and returned from {@link #build()}.</p>
 *
 * @param <T> the interface which managed implementations implement
 */
public abstract class PlatformSupportManager<T> {
  
  private static final String TAG = PlatformSupportManager.class.getSimpleName();

  private final Class<T> managedInterface;
  private final T defaultImplementation;
  private final SortedMap<Integer,String> implementations;
  
  protected PlatformSupportManager(Class<T> managedInterface, T defaultImplementation) {
    if (!managedInterface.isInterface()) {
      throw new IllegalArgumentException();
    }
    if (!managedInterface.isInstance(defaultImplementation)) {
      throw new IllegalArgumentException();
    }
    this.managedInterface = managedInterface;
    this.defaultImplementation = defaultImplementation;
    this.implementations = new TreeMap<Integer,String>(Collections.reverseOrder());
  }
  
  protected void addImplementationClass(int minVersion, String className) {
    implementations.put(minVersion, className);
  }

  public T build() {
    for (Integer minVersion : implementations.keySet()) {
      if (Build.VERSION.SDK_INT >= minVersion) {
        String className = implementations.get(minVersion);
        try {
          Class<? extends T> clazz = Class.forName(className).asSubclass(managedInterface);
          return clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException cnfe) {
        } catch (IllegalAccessException iae) {
        } catch (InstantiationException ie) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        }
      }
    }
    return defaultImplementation;
  }

}
