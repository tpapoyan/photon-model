
package com.vmware.vim25;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClusterDasAdvancedRuntimeInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClusterDasAdvancedRuntimeInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:vim25}DynamicData"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dasHostInfo" type="{urn:vim25}ClusterDasHostInfo" minOccurs="0"/&gt;
 *         &lt;element name="vmcpSupported" type="{urn:vim25}ClusterDasAdvancedRuntimeInfoVmcpCapabilityInfo" minOccurs="0"/&gt;
 *         &lt;element name="heartbeatDatastoreInfo" type="{urn:vim25}DasHeartbeatDatastoreInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClusterDasAdvancedRuntimeInfo", propOrder = {
    "dasHostInfo",
    "vmcpSupported",
    "heartbeatDatastoreInfo"
})
@XmlSeeAlso({
    ClusterDasFailoverLevelAdvancedRuntimeInfo.class
})
public class ClusterDasAdvancedRuntimeInfo
    extends DynamicData
{

    protected ClusterDasHostInfo dasHostInfo;
    protected ClusterDasAdvancedRuntimeInfoVmcpCapabilityInfo vmcpSupported;
    protected List<DasHeartbeatDatastoreInfo> heartbeatDatastoreInfo;

    /**
     * Gets the value of the dasHostInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ClusterDasHostInfo }
     *     
     */
    public ClusterDasHostInfo getDasHostInfo() {
        return dasHostInfo;
    }

    /**
     * Sets the value of the dasHostInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClusterDasHostInfo }
     *     
     */
    public void setDasHostInfo(ClusterDasHostInfo value) {
        this.dasHostInfo = value;
    }

    /**
     * Gets the value of the vmcpSupported property.
     * 
     * @return
     *     possible object is
     *     {@link ClusterDasAdvancedRuntimeInfoVmcpCapabilityInfo }
     *     
     */
    public ClusterDasAdvancedRuntimeInfoVmcpCapabilityInfo getVmcpSupported() {
        return vmcpSupported;
    }

    /**
     * Sets the value of the vmcpSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClusterDasAdvancedRuntimeInfoVmcpCapabilityInfo }
     *     
     */
    public void setVmcpSupported(ClusterDasAdvancedRuntimeInfoVmcpCapabilityInfo value) {
        this.vmcpSupported = value;
    }

    /**
     * Gets the value of the heartbeatDatastoreInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the heartbeatDatastoreInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeartbeatDatastoreInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DasHeartbeatDatastoreInfo }
     * 
     * 
     */
    public List<DasHeartbeatDatastoreInfo> getHeartbeatDatastoreInfo() {
        if (heartbeatDatastoreInfo == null) {
            heartbeatDatastoreInfo = new ArrayList<DasHeartbeatDatastoreInfo>();
        }
        return this.heartbeatDatastoreInfo;
    }

}
