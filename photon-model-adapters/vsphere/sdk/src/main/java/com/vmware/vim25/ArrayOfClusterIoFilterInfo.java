
package com.vmware.vim25;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfClusterIoFilterInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfClusterIoFilterInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ClusterIoFilterInfo" type="{urn:vim25}ClusterIoFilterInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfClusterIoFilterInfo", propOrder = {
    "clusterIoFilterInfo"
})
public class ArrayOfClusterIoFilterInfo {

    @XmlElement(name = "ClusterIoFilterInfo")
    protected List<ClusterIoFilterInfo> clusterIoFilterInfo;

    /**
     * Gets the value of the clusterIoFilterInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clusterIoFilterInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClusterIoFilterInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClusterIoFilterInfo }
     * 
     * 
     */
    public List<ClusterIoFilterInfo> getClusterIoFilterInfo() {
        if (clusterIoFilterInfo == null) {
            clusterIoFilterInfo = new ArrayList<ClusterIoFilterInfo>();
        }
        return this.clusterIoFilterInfo;
    }

}
