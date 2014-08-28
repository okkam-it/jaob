
package matryoshkatest;

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
     * Create an instance of {@link Matryoshka}
     * 
     */
    public Matryoshka createMatryoshka() {
        return new MatryoshkaImpl();
    }

}
