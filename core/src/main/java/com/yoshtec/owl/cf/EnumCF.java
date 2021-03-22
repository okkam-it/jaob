package com.yoshtec.owl.cf;

import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlEnumValue;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EnumCF<E extends Enum<E>> extends SimpleClassFacade<E> implements ClassFacade {

  private static final Logger log = LoggerFactory.getLogger(EnumCF.class);

  /**
   * Stores the names - Enum value association the enum value is often different to the vaue of the
   * Enum.
   */
  private Map<E, String> names = new HashMap<E, String>();


  EnumCF(Class<E> clazz, XsdTypeMapper typeMapper) {
    super(clazz, typeMapper);

    OwlClass oc = clazz.getAnnotation(OwlClass.class);
    if (oc == null) {
      throw new IllegalStateException("Class " + clazz + " is not annotated with @OwlClass ");
    }

    // filter out the Name Enum association
    for (E en : clazz.getEnumConstants()) {
      String name = en.name();
      try {
        // try to find another name for the Enum value
        Field enumdecl = clazz.getField(name);
        if (enumdecl.isAnnotationPresent(OwlEnumValue.class)) {
          name = enumdecl.getAnnotation(OwlEnumValue.class).value();
        }
      } catch (NoSuchFieldException nsfe) {
        // this should actually never happen
        log.debug("Enum {} does not have  the corresponding field Declaration for {}", clazz, name);
      }
      // associate the name defined name with the Enum value
      names.put(en, name);
    }

    classuris.add(ReflectUtil.getClassIri(clazz));

    readPackage();
  }

  @Override
  public String getIdString(Object o) {
    if (clazz.isInstance(o)) {
      return names.get(o);
    }
    log.error("The object '{}' is not an instance of enum '{}' ", o, clazz);
    return null;
  }

  @Override
  public Object getNewInstance() throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("new Instance called with no ID on an enum", new Exception());
    }
    return null;
  }

  @Override
  public E getNewInstance(String id) throws IllegalArgumentException, InstantiationException,
      IllegalAccessException, InvocationTargetException {
    for (Entry<E, String> entry : names.entrySet()) {
      if (entry.getValue().equals(id)) {
        return entry.getKey();
      }
    }
    // could not find the correct enum constant
    log.info("Could not find an enum in '{}' constant for '{}'", this.clazz, id);
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @return false, since enums are not setable
   */
  @Override
  public boolean hasSetableId() {
    return false;
  }

  /**
   * Does nothing, since IDs on enums are not settable.
   */
  @Override
  public void setId(Object obj, String id) {
    return; // nothing to do here
  }



}
