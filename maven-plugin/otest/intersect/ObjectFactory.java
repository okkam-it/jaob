
package intersect;

import com.yoshtec.owl.annotations.OwlRegistry;


/**
 * Lets you create Classes from an OWL Ontology programmatically.
 * 
 */
@OwlRegistry
public class ObjectFactory {


    /**
     * Create an instance of {@link Thing}
     * 
     */
    public Thing createThing() {
        return new ThingImpl();
    }

    /**
     * Create an instance of {@link Apfel}
     * 
     */
    public Apfel createApfel() {
        return new ApfelImpl();
    }

    /**
     * Create an instance of {@link Birne}
     * 
     */
    public Birne createBirne() {
        return new BirneImpl();
    }

    /**
     * Create an instance of {@link Nashi}
     * 
     */
    public Nashi createNashi() {
        return new NashiImpl();
    }

}
