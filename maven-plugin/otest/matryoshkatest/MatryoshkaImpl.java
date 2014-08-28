
package matryoshkatest;

import java.util.ArrayList;
import java.util.List;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlObjectProperty;

@OwlClassImplementation({
    Matryoshka.class
})
public class MatryoshkaImpl
    implements Matryoshka
{

    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Color</code>
     * 
     */
    @OwlDataProperty(uri = "http://www.yoshtec.com/ontology/test/matryoshka#Color")
    @OwlDataType(uri = "http://www.w3.org/2001/XMLSchema#string")
    protected List<String> color;
    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Contains</code>
     * 
     */
    @OwlObjectProperty(uri = "http://www.yoshtec.com/ontology/test/matryoshka#Contains")
    @OwlDataType(uri = "http://www.yoshtec.com/ontology/test/matryoshka#Matryoshka")
    protected List<Matryoshka> contains;
    /**
     * OWL Object Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Contained_in</code>
     * 
     */
    @OwlObjectProperty(uri = "http://www.yoshtec.com/ontology/test/matryoshka#Contained_in")
    @OwlDataType(uri = "http://www.yoshtec.com/ontology/test/matryoshka#Matryoshka")
    protected List<Matryoshka> contained_in;
    /**
     * OWL Data Property:</br>
     * <code>http://www.yoshtec.com/ontology/test/matryoshka#Size</code>
     * 
     */
    @OwlDataProperty(uri = "http://www.yoshtec.com/ontology/test/matryoshka#Size")
    @OwlDataType(uri = "http://www.w3.org/2001/XMLSchema#int")
    protected List<Integer> size;

    /**
     * {@inheritDoc}
     * 
     */
    public List<String> getColor() {
        if (color == null) {
            color = new ArrayList<String>();
        }
        return color;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setColor(List<String> color) {
        this.color = color;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public List<Matryoshka> getContains() {
        if (contains == null) {
            contains = new ArrayList<Matryoshka>();
        }
        return contains;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setContains(List<Matryoshka> contains) {
        this.contains = contains;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public List<Matryoshka> getContained_in() {
        if (contained_in == null) {
            contained_in = new ArrayList<Matryoshka>();
        }
        return contained_in;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setContained_in(List<Matryoshka> contained_in) {
        this.contained_in = contained_in;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public List<Integer> getSize() {
        if (size == null) {
            size = new ArrayList<Integer>();
        }
        return size;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setSize(List<Integer> size) {
        this.size = size;
    }

}
