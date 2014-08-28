
package glass;

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
        return new Thing();
    }

    /**
     * Create an instance of {@link GlassColor}
     * 
     */
    public GlassColor createGlassColor() {
        return new GlassColor();
    }

    /**
     * Create an instance of {@link Glass}
     * 
     */
    public Glass createGlass() {
        return new Glass();
    }

}
