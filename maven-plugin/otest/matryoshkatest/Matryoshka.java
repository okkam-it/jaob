
package matryoshkatest;

import java.util.List;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.classes.OwlSubclassOf;


/**
 * Generated Class from Ontology:
 * Class URI: <code>http://www.yoshtec.com/ontology/test/matryoshka#Matryoshka</code>
 * 
 * 
 */
@OwlClass(uri = "http://www.yoshtec.com/ontology/test/matryoshka#Matryoshka")
@OwlSubclassOf({
    @OwlClass(uri = "http://www.w3.org/2002/07/owl#Thing")
})
public interface Matryoshka
    extends Thing
{


    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Color</code>
     * 
     * @return
     *     a List of color
     */
    public List<String> getColor();

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Color</code>
     * 
     */
    public void setColor(List<String> color);

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Contains</code>
     * 
     * @return
     *     a List of contains
     */
    public List<Matryoshka> getContains();

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Contains</code>
     * 
     */
    public void setContains(List<Matryoshka> contains);

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Contained_in</code>
     * 
     * @return
     *     a List of contained_in
     */
    public List<Matryoshka> getContained_in();

    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Contained_in</code>
     * 
     */
    public void setContained_in(List<Matryoshka> contained_in);

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Size</code>
     * 
     * @return
     *     a List of size
     */
    public List<Integer> getSize();

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Size</code>
     * 
     */
    public void setSize(List<Integer> size);

}
