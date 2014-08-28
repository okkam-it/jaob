
package buckettest;

import java.util.Calendar;
import java.util.List;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.classes.OwlSubclassOf;


/**
 * Generated Class from Ontology:
 * Class URI: <code>http://www.yoshtec.com/ontology/test/Bucket#Stone</code>
 * 
 * 
 */
@OwlClass(uri = "http://www.yoshtec.com/ontology/test/Bucket#Stone")
@OwlSubclassOf({
    @OwlClass(uri = "http://www.w3.org/2002/07/owl#Thing")
})
public interface Stone
    extends Thing
{


    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#DateFound</code></br>
     * OWLComment: The Date this stone was found
     * 
     * @return
     *     a List of dateFound
     */
    public List<Calendar> getDateFound();

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#DateFound</code></br>
     * OWLComment: The Date this stone was found
     * 
     */
    public void setDateFound(List<Calendar> dateFound);

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#weight</code></br>
     * OWLComment: weight of the stone in gramm
     * 
     * @return
     *     a List of weight
     */
    public List<Integer> getWeight();

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#weight</code></br>
     * OWLComment: weight of the stone in gramm
     * 
     */
    public void setWeight(List<Integer> weight);

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#is_in_Bucket</code>
     * 
     * @return
     *     a List of is_in_Bucket
     */
    public List<Bucket> getIs_in_Bucket();

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#is_in_Bucket</code>
     * 
     */
    public void setIs_in_Bucket(List<Bucket> is_in_Bucket);

}
