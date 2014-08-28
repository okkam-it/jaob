
package intersect;

import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.classes.OwlObjectIntersectionOf;
import com.yoshtec.owl.annotations.classes.OwlSubclassOf;


/**
 * Generated Class from Ontology:
 * Class URI: <code>http://www.yoshtec.com/test/intersect.owl#Nashi</code>
 * 
 * 
 */
@OwlObjectIntersectionOf(classes = {
    "http://www.yoshtec.com/test/intersect.owl#Apfel",
    "http://www.yoshtec.com/test/intersect.owl#Birne"
})
@OwlClass(uri = "http://www.yoshtec.com/test/intersect.owl#Nashi")
@OwlSubclassOf({
    @OwlClass(uri = "http://www.yoshtec.com/test/intersect.owl#Apfel"),
    @OwlClass(uri = "http://www.w3.org/2002/07/owl#Thing"),
    @OwlClass(uri = "http://www.yoshtec.com/test/intersect.owl#Birne")
})
public interface Nashi
    extends Apfel, Birne, Thing
{


}
