
package buckettest;

import java.util.ArrayList;
import java.util.List;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlObjectProperty;

@OwlClassImplementation({
    Stuff.class
})
public class StuffImpl
    implements Stuff
{

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#is_in_Bucket</code>
     * 
     */
    @OwlObjectProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#is_in_Bucket")
    @OwlDataType(uri = "http://www.yoshtec.com/ontology/test/Bucket#Bucket")
    protected List<Bucket> is_in_Bucket;
    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#description</code>
     * 
     */
    @OwlDataProperty(uri = "http://www.yoshtec.com/ontology/test/Bucket#description")
    @OwlDataType(uri = "http://www.w3.org/2001/XMLSchema#string")
    protected List<String> description;

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

    /**
     * {@inheritDoc}
     * 
     */
    public List<String> getDescription() {
        if (description == null) {
            description = new ArrayList<String>();
        }
        return description;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setDescription(List<String> description) {
        this.description = description;
    }

}
