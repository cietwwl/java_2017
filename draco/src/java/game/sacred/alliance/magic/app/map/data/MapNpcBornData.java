//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.11.26 at 02:21:28 芟爬 CST 
//


package sacred.alliance.magic.app.map.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for MapNpcBornData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MapNpcBornData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="npcborn" type="{}NpcBorn" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="mapnpcborndata")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapNpcBornData", propOrder = {
    "npcborn"
})
public class MapNpcBornData {

    protected List<NpcBorn> npcborn = new ArrayList<NpcBorn>();
    

    public void setNpcborn(List<NpcBorn> npcborn) {
		this.npcborn = npcborn;
	}

	/**
     * Gets the value of the npcborn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the npcborn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNpcborn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NpcBorn }
     * 
     * 
     */
    public List<NpcBorn> getNpcborn() {
        if (npcborn == null) {
            npcborn = new ArrayList<NpcBorn>();
        }
        return this.npcborn;
    }

    public MapNpcBornData(List<NpcBorn> npcBorn){
    	this.npcborn = npcBorn ;
    }
    
    
    public MapNpcBornData(){
    	
    }
}