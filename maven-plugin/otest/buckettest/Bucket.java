
package buckettest;

import java.util.List;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.classes.OwlSubclassOf;


/**
 * Generated Class from Ontology:
 * Class URI: <code>http://www.yoshtec.com/ontology/test/Bucket#Bucket</code>
 * 
 * 
 */
@OwlClass(uri = "http://www.yoshtec.com/ontology/test/Bucket#Bucket")
@OwlSubclassOf({
    @OwlClass(uri = "http://www.w3.org/2002/07/owl#Thing")
})
public interface Bucket
    extends Thing
{


    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Engraving</code></br>
     * OWLComment: Some engraved Text on the Bucket
     * 
     * @return
     *     a List of engraving
     */
    public List<String> getEngraving();

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Engraving</code></br>
     * OWLComment: Some engraved Text on the Bucket
     * 
     */
    public void setEngraving(List<String> engraving);

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Contains</code>
     * 
     * @return
     *     a List of contains
     */
    public List<Object> getContains();

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Contains</code>
     * 
     */
    public void setContains(List<Object> contains);

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Material</code></br>
     * OWLComment: The Material the Bucket is made of Plastik, Iron, Gold etc.
     * 
     * @return
     *     a List of material
     */
    public List<String> getMaterial();

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#Material</code></br>
     * OWLComment: The Material the Bucket is made of Plastik, Iron, Gold etc.
     * 
     */
    public void setMaterial(List<String> material);

}
