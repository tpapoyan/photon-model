
package com.vmware.vim25;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfVsanUpgradeSystemUpgradeHistoryItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfVsanUpgradeSystemUpgradeHistoryItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="VsanUpgradeSystemUpgradeHistoryItem" type="{urn:vim25}VsanUpgradeSystemUpgradeHistoryItem" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVsanUpgradeSystemUpgradeHistoryItem", propOrder = {
    "vsanUpgradeSystemUpgradeHistoryItem"
})
public class ArrayOfVsanUpgradeSystemUpgradeHistoryItem {

    @XmlElement(name = "VsanUpgradeSystemUpgradeHistoryItem")
    protected List<VsanUpgradeSystemUpgradeHistoryItem> vsanUpgradeSystemUpgradeHistoryItem;

    /**
     * Gets the value of the vsanUpgradeSystemUpgradeHistoryItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vsanUpgradeSystemUpgradeHistoryItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVsanUpgradeSystemUpgradeHistoryItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VsanUpgradeSystemUpgradeHistoryItem }
     * 
     * 
     */
    public List<VsanUpgradeSystemUpgradeHistoryItem> getVsanUpgradeSystemUpgradeHistoryItem() {
        if (vsanUpgradeSystemUpgradeHistoryItem == null) {
            vsanUpgradeSystemUpgradeHistoryItem = new ArrayList<VsanUpgradeSystemUpgradeHistoryItem>();
        }
        return this.vsanUpgradeSystemUpgradeHistoryItem;
    }

}
