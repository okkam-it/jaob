
package buckettest;

import java.util.List;
import com.yoshtec.owl.annotations.OwlAnnotation;
import com.yoshtec.owl.annotations.OwlAnnotations;
import com.yoshtec.owl.annotations.OwlClass;


/**
 * Generated Class from Ontology:
 * Class URI: <code>http://www.yoshtec.com/ontology/test/Bucket#Stuff</code>
 * 
 * <br>
 * OWLComment: "Could be anything"^^xsd:string
 * 
 */
@OwlClass(uri = "http://www.yoshtec.com/ontology/test/Bucket#Stuff")
@OwlAnnotations({
    @OwlAnnotation(uri = "http://www.w3.org/2000/01/rdf-schema#comment", content = "\"Could be anything\"^^xsd:string", dataTypeUri = "http://www.w3.org/2000/01/rdf-schema#comment")
})
public interface Stuff {


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

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#description</code>
     * 
     * @return
     *     a List of description
     */
    public List<String> getDescription();

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/Bucket#description</code>
     * 
     */
    public void setDescription(List<String> description);

}
