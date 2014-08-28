
package buckettest;

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
     * Create an instance of {@link Bucket}
     * 
     */
    public Bucket createBucket() {
        return new BucketImpl();
    }

    /**
     * Create an instance of {@link Stone}
     * 
     */
    public Stone createStone() {
        return new StoneImpl();
    }

    /**
     * Create an instance of {@link Stuff}
     * 
     */
    public Stuff createStuff() {
        return new StuffImpl();
    }

}
