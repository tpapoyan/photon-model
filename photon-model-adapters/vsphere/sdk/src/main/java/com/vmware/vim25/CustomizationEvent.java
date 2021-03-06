
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomizationEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomizationEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:vim25}VmEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="logLocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomizationEvent", propOrder = {
    "logLocation"
})
@XmlSeeAlso({
    CustomizationStartedEvent.class,
    CustomizationSucceeded.class,
    CustomizationFailed.class
})
public class CustomizationEvent
    extends VmEvent
{

    protected String logLocation;

    /**
     * Gets the value of the logLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogLocation() {
        return logLocation;
    }

    /**
     * Sets the value of the logLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogLocation(String value) {
        this.logLocation = value;
    }

}
