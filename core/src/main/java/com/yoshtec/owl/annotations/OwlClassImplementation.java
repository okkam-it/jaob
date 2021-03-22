package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that a Java Class is made from several OWL Classes defined by an Interface.
 * <p>
 * TODO: currently not so sure if really needed?
 * </p>
 * Example:
 * 
 * <pre>
 * {@literal @}OwlClass(uri="http://www.co-ode.org/ontologies/pizza/pizza.owl#CheeseTopping")
 * public interface CheeseTopping{
 *  // .. public methods
 * }
 * 
 * {@literal @}OwlClass(uri="http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetableTopping")
 * public interface VegetableTopping{
 *  // .. public methods
 * }
 * 
 * // ---- --- --- --- ---
 * 
 * {@literal @}OwlClassImplementation({VegetableTopping.class, CheeseTopping.class});
 * public class CheeseyVegetableTopping 
 *  implements CheeseTopping, VegetableTopping{
 *  &#47;&#47;...
 * }
 * </pre>
 * 
 * @author Jonas von Malottki
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OwlClassImplementation {
  /**
   * The list of classes.
   */
  Class<?>[] value();
}
