package com.yoshtec.owl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps XsdTypes to Java Classes and vice versa.
 * 
 * @author Jonas von Malottki
 */
public class XsdTypeMapper {

  static final XsdType[] longtypes = {//
      XsdType.LONG, //
      XsdType.INTEGER, //
      XsdType.NEGATIVE_INTEGER, //
      XsdType.NON_NEGATIVE_INTEGER, //
      XsdType.NON_POSITIVE_INTEGER, //
      XsdType.UNSIGNED_LONG, //
      XsdType.UNSIGNED_INT, //
      XsdType.POSITIVE_INTEGER//
  };

  static final XsdType[] inttypes = {//
      XsdType.INT, //
      XsdType.SHORT, //
      XsdType.BYTE, //
      XsdType.UNSIGNED_SHORT, //
      XsdType.UNSIGNED_BYTE, //
      XsdType.GYEAR, //
      XsdType.GDAY, //
      XsdType.GMONTH//
  };


  static final XsdType[] stringtypes = {//
      XsdType.STRING, //
      XsdType.NORMALIZED_STRING, //
      XsdType.TOKEN, //
      XsdType.LANGUAGE, //
      XsdType.NAME, //
      XsdType.NCNAME, //
      XsdType.ID, //
      XsdType.IDREF, //
      XsdType.IDREFS, //
      XsdType.ENTITY, //
      XsdType.ENTITIES, //
      XsdType.NMTOKEN, //
      XsdType.NMTOKENS, //
      XsdType.ANYURI, //
      XsdType.QNAME, //
      XsdType.NOTATION, //
      XsdType.DURATION, //
      XsdType.GYEARMONTH, //
      XsdType.GMONTHDAY//
  };

  static final XsdType[] datetypes = {//
      XsdType.DATETIME, //
      XsdType.TIME, //
      XsdType.DATE//
  };

  /** Type map. */
  private Map<XsdType, Class<?>> mapping = new HashMap<XsdType, Class<?>>();

  /** Reverse Type map. */
  private Map<Class<?>, XsdType> reverse = new HashMap<Class<?>, XsdType>();

  /**
   * Initialization. Creates the default Mapping.
   */
  public XsdTypeMapper() {
    init();
  }

  // initialize the default mapping table
  protected void init() {
    mapping.put(XsdType.DECIMAL, BigDecimal.class);
    reverse.put(BigDecimal.class, XsdType.DECIMAL);

    mapping.put(XsdType.FLOAT, Float.class);
    reverse.put(Float.class, XsdType.FLOAT);
    reverse.put(float.class, XsdType.FLOAT);

    mapping.put(XsdType.DOUBLE, Double.class);
    reverse.put(Double.class, XsdType.DOUBLE);
    reverse.put(double.class, XsdType.DOUBLE);

    mapping.put(XsdType.BOOLEAN, Boolean.class);
    reverse.put(Boolean.class, XsdType.BOOLEAN);
    reverse.put(boolean.class, XsdType.BOOLEAN);

    // java.lang.int..
    for (XsdType t : inttypes) {
      mapping.put(t, Integer.class);
    }
    reverse.put(Integer.class, XsdType.INT);
    reverse.put(int.class, XsdType.INT);
    reverse.put(Byte.class, XsdType.BYTE);
    reverse.put(byte.class, XsdType.BYTE);
    reverse.put(Short.class, XsdType.SHORT);
    reverse.put(short.class, XsdType.SHORT);

    // long
    for (XsdType t : longtypes) {
      mapping.put(t, Long.class);
    }
    reverse.put(Long.class, XsdType.LONG);
    reverse.put(long.class, XsdType.LONG);

    // Strings
    for (XsdType t : stringtypes) {
      mapping.put(t, String.class);
    }
    reverse.put(String.class, XsdType.STRING);

    // Date
    for (XsdType t : datetypes) {
      mapping.put(t, Calendar.class);
    }
    reverse.put(Calendar.class, XsdType.DATETIME);
    reverse.put(Date.class, XsdType.DATETIME);
  }

  /**
   * Sets a new or overwrites an existing Mapping from a {@link XsdType} to a Java class.
   * 
   * @param type xsd type to map to
   * @param clazz a Java class
   */
  public void setMapping(XsdType type, Class<?> clazz) {
    mapping.put(type, clazz);
  }

  /**
   * Sets or overwrites an Reverse Mapping from a Java class to an {@link XsdType}.
   * 
   * @param clazz the java class to map
   * @param type to the xsd type
   */
  public void setReverseMapping(Class<?> clazz, XsdType type) {
    reverse.put(clazz, type);
  }

  /**
   * Get the corresponding {@link XsdType} of the passed clazz.
   * 
   * @param clazz class to get the mapping
   * @return the corresponding {@link XsdType}
   */
  public XsdType getXsdType(Class<?> clazz) {
    return reverse.get(clazz);
  }

  /**
   * Return the corresponding {@link XsdType}.
   * 
   * @param type the java type
   * @return the corresponding {@link XsdType}
   */
  public XsdType getXsdType(Type type) {
    return reverse.get(type);
  }

  /**
   * Checks if the URI is from XML Schema and returns the appropriate Java Type.
   * 
   * @param type the XsdType
   * @return Mapping Class for this type
   */
  public Class<?> getType(XsdType type) {
    return mapping.get(type);
  }

}
