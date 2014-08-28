package com.yoshtec.owl.util;

import java.lang.reflect.Method;

import org.slf4j.Logger;
/**
 * Helps Debugging and retrieve Runtime Information 
 * for Java Objects at runtime
 * 
 * @author Jonas von Malottki
 *
 */
public class LogUtil {

	/**
	 * Logs the contents of an Object to the debug logger. 
	 * Uses reflection to get all getters and invokes them,
	 * logging the result to debug.
	 * 
	 * @param o Object to log
	 * @param log the Logger to log to
	 */
	public static void logObject(Object o, Logger log) {
		if(log.isDebugEnabled()){
			
			
			//Java reflection magic ;)
			Class<? extends Object> c = o.getClass();
			
			Method[] methods = c.getMethods();
			
			log.debug(c.getName());
			
			for (Method method : methods) {
				if(method.getName().startsWith("get")){
					StringBuffer sb = new StringBuffer();
					//sb.append(c.getName());
					//sb.append(".");
					sb.append(method.getName());
					sb.append(" : ");
					
					try {
						sb.append(method.invoke(o, (Object[])null));
					} catch (Exception e) {
						sb.append("EXCEPTION OCCURED");
					}
					//sb.append("\n");
					log.debug(sb.toString());
				}
			}
		}
	}
	
	
	/**
	 * Logs Information about the Object at hand to debug:
	 *
	 * <ul>
	 *  <li>Actual Class</li>
	 *  <li>Interfaces</li>
	 *  <li>Superclasses</li>
	 *  <li>Methods (without invoking them)</li>
	 * </ul>
	 *
	 * @see LogUtil#logObject(Object, Logger) 
	 *
	 * @param o
	 * @param log
	 */
	public static void logObjectInfo(Object o, Logger log){
		if(log.isDebugEnabled()){
			
			//Java reflection magic ;)
			Class<? extends Object> c = o.getClass();
			
			
			log.debug("CLASS");
			log.debug("C: " + c.getName());
			
			log.debug("INTERFACES:");
			for(Class<?> iface : c.getInterfaces()){
				log.debug("I: " + iface.getName());
			}
			
			{// unnamed Block
				log.debug("SUPERCLASSES:");
				Class<?> u = c;
				while(!u.getName().equals("java.lang.Object")){
					u = u.getSuperclass();
					log.debug("S: " + u.getName());
				}
			}
			
			log.debug("METHODS:");
			for (Method method : c.getMethods()) {
				log.debug(" M: {}",method.toGenericString());
			}
		}
	}
	
}
