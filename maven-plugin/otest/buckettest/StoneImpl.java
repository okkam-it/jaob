
package buckettest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlObjectProperty;

@OwlClassImplementation({
    Stone.class
})
public class StoneImpl
    implements Stone
{

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#DateFound</code></br>
     * OWLComment: The Date this stone was found
     * 
     */
    @OwlDataProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#DateFound")
    @OwlDataType(uri = "http://www.w3.org/2001/XMLSchema#dateTime")
    protected List<Calendar> dateFound;
    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#weight</code></br>
     * OWLComment: weight of the stone in gramm
     * 
     */
    @OwlDataProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#weight")
    @OwlDataType(uri = "http://www.w3.org/2001/XMLSchema#int")
    protected List<Integer> weight;
    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#is_in_Bucket</code>
     * 
     */
    @OwlObjectProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#is_in_Bucket")
    @OwlDataType(uri = "http://www.yoshtec.com/ontology/test/Bucket#Bucket")
    protected List<Bucket> is_in_Bucket;

    /**
     * {@inheritDoc}
     * 
     */
    public List<Calendar> getDateFound() {
        if (dateFound == null) {
            dateFound = new ArrayList<Calendar>();
        }
        return dateFound;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setDateFound(List<Calendar> dateFound) {
        this.dateFound = dateFound;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public List<Integer> getWeight() {
        if (weight == null) {
            weight = new ArrayList<Integer>();
        }
        return weight;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setWeight(List<Integer> weight) {
        this.weight = weight;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public List<Bucket> getIs_in_Bucket() {
        if (is_in_Bucket == null) {
            is_in_Bucket = new ArrayList<Bucket>();
        }
        return is_in_Bucket;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setIs_in_Bucket(List<Bucket> is_in_Bucket) {
        this.is_in_Bucket = is_in_Bucket;
    }

}
