
package unionof;

import com.yoshtec.owl.annotations.OwlRegistry;


/**
 * Lets you create Classes from an OWL Ontology programmatically.
 * 
 */
@OwlRegistry
public class ObjectFactory {


    /**
     * Create an instance of {@link Malodiea}
     * 
     */
    public Malodiea createMalodiea() {
        return new MalodieaImpl();
    }

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

}
