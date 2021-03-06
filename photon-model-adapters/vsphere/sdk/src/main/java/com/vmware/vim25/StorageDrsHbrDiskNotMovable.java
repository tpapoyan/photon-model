
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StorageDrsHbrDiskNotMovable complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StorageDrsHbrDiskNotMovable"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:vim25}VimFault"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nonMovableDiskIds" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StorageDrsHbrDiskNotMovable", propOrder = {
    "nonMovableDiskIds"
})
public class StorageDrsHbrDiskNotMovable
    extends VimFault
{

    @XmlElement(required = true)
    protected String nonMovableDiskIds;

    /**
     * Gets the value of the nonMovableDiskIds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNonMovableDiskIds() {
        return nonMovableDiskIds;
    }

    /**
     * Sets the value of the nonMovableDiskIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNonMovableDiskIds(String value) {
        this.nonMovableDiskIds = value;
    }

}
