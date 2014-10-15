package com.yoshtec.owl;
/**
 * 
 * @author Jonas von Malottki
 */
public enum PropertyAccessType {
	/** 
	 * All non transient non static member fields are bound to 
	 * Owl properties.  
	 * 
	 */
    FIELD,
    /**
     * All public setter/getter pairs are bound to Owl Properties
     * 
     * <b>NOT IMPLEMENTED YET</b>
     */
	METHOD;
}