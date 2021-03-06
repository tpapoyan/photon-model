
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdatePortGroupRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdatePortGroupRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="_this" type="{urn:vim25}ManagedObjectReference"/&gt;
 *         &lt;element name="pgName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="portgrp" type="{urn:vim25}HostPortGroupSpec"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdatePortGroupRequestType", propOrder = {
    "_this",
    "pgName",
    "portgrp"
})
public class UpdatePortGroupRequestType {

    @XmlElement(required = true)
    protected ManagedObjectReference _this;
    @XmlElement(required = true)
    protected String pgName;
    @XmlElement(required = true)
    protected HostPortGroupSpec portgrp;

    /**
     * Gets the value of the this property.
     * 
     * @return
     *     possible object is
     *     {@link ManagedObjectReference }
     *     
     */
    public ManagedObjectReference getThis() {
        return _this;
    }

    /**
     * Sets the value of the this property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManagedObjectReference }
     *     
     */
    public void setThis(ManagedObjectReference value) {
        this._this = value;
    }

    /**
     * Gets the value of the pgName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPgName() {
        return pgName;
    }

    /**
     * Sets the value of the pgName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPgName(String value) {
        this.pgName = value;
    }

    /**
     * Gets the value of the portgrp property.
     * 
     * @return
     *     possible object is
     *     {@link HostPortGroupSpec }
     *     
     */
    public HostPortGroupSpec getPortgrp() {
        return portgrp;
    }

    /**
     * Sets the value of the portgrp property.
     * 
     * @param value
     *     allowed object is
     *     {@link HostPortGroupSpec }
     *     
     */
    public void setPortgrp(HostPortGroupSpec value) {
        this.portgrp = value;
    }

}
