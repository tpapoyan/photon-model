
package com.vmware.vim25;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfVirtualMachineCdromInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfVirtualMachineCdromInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="VirtualMachineCdromInfo" type="{urn:vim25}VirtualMachineCdromInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVirtualMachineCdromInfo", propOrder = {
    "virtualMachineCdromInfo"
})
public class ArrayOfVirtualMachineCdromInfo {

    @XmlElement(name = "VirtualMachineCdromInfo")
    protected List<VirtualMachineCdromInfo> virtualMachineCdromInfo;

    /**
     * Gets the value of the virtualMachineCdromInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the virtualMachineCdromInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVirtualMachineCdromInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VirtualMachineCdromInfo }
     * 
     * 
     */
    public List<VirtualMachineCdromInfo> getVirtualMachineCdromInfo() {
        if (virtualMachineCdromInfo == null) {
            virtualMachineCdromInfo = new ArrayList<VirtualMachineCdromInfo>();
        }
        return this.virtualMachineCdromInfo;
    }

}
