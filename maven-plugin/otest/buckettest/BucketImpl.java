
package buckettest;

import java.util.ArrayList;
import java.util.List;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlObjectProperty;

@OwlClassImplementation({
    Bucket.class
})
public class BucketImpl
    implements Bucket
{

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Engraving</code></br>
     * OWLComment: Some engraved Text on the Bucket
     * 
     */
    @OwlDataProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#Engraving")
    @OwlDataType(uri = "http://www.w3.org/2001/XMLSchema#string")
    protected List<String> engraving;
    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Contains</code>
     * 
     */
    @OwlObjectProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#Contains")
    protected List<Object> contains;
    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Material</code></br>
     * OWLComment: The Material the Bucket is made of Plastik, Iron, Gold etc.
     * 
     */
    @OwlDataProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#Material")
    @OwlDataType(uri = "http://www.w3.org/2001/XMLSchema#string")
    protected List<String> material;

    /**
     * {@inheritDoc}
     * 
     */
    public List<String> getEngraving() {
        if (engraving == null) {
            engraving = new ArrayList<String>();
        }
        return engraving;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setEngraving(List<String> engraving) {
        this.engraving = engraving;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public List<Object> getContains() {
        if (contains == null) {
            contains = new ArrayList<Object>();
        }
        return contains;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setContains(List<Object> contains) {
        this.contains = contains;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public List<String> getMaterial() {
        if (material == null) {
            material = new ArrayList<String>();
        }
        return material;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setMaterial(List<String> material) {
        this.material = material;
    }

}
