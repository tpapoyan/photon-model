
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VslmCreateSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VslmCreateSpec"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:vim25}DynamicData"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="backingSpec" type="{urn:vim25}VslmCreateSpecBackingSpec"/&gt;
 *         &lt;element name="capacityInMB" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VslmCreateSpec", propOrder = {
    "name",
    "backingSpec",
    "capacityInMB"
})
public class VslmCreateSpec
    extends DynamicData
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected VslmCreateSpecBackingSpec backingSpec;
    protected long capacityInMB;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the backingSpec property.
     * 
     * @return
     *     possible object is
     *     {@link VslmCreateSpecBackingSpec }
     *     
     */
    public VslmCreateSpecBackingSpec getBackingSpec() {
        return backingSpec;
    }

    /**
     * Sets the value of the backingSpec property.
     * 
     * @param value
     *     allowed object is
     *     {@link VslmCreateSpecBackingSpec }
     *     
     */
    public void setBackingSpec(VslmCreateSpecBackingSpec value) {
        this.backingSpec = value;
    }

    /**
     * Gets the value of the capacityInMB property.
     * 
     */
    public long getCapacityInMB() {
        return capacityInMB;
    }

    /**
     * Sets the value of the capacityInMB property.
     * 
     */
    public void setCapacityInMB(long value) {
        this.capacityInMB = value;
    }

}
