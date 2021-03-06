
package com.vmware.vim25;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.6
 * 2017-05-25T13:48:06.864+05:30
 * Generated source version: 3.1.6
 */

@WebFault(name = "NoActiveHostInClusterFault", targetNamespace = "urn:vim25")
public class NoActiveHostInClusterFaultMsg extends Exception {
    public static final long serialVersionUID = 1L;
    
    private com.vmware.vim25.NoActiveHostInCluster noActiveHostInClusterFault;

    public NoActiveHostInClusterFaultMsg() {
        super();
    }
    
    public NoActiveHostInClusterFaultMsg(String message) {
        super(message);
    }
    
    public NoActiveHostInClusterFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public NoActiveHostInClusterFaultMsg(String message, com.vmware.vim25.NoActiveHostInCluster noActiveHostInClusterFault) {
        super(message);
        this.noActiveHostInClusterFault = noActiveHostInClusterFault;
    }

    public NoActiveHostInClusterFaultMsg(String message, com.vmware.vim25.NoActiveHostInCluster noActiveHostInClusterFault, Throwable cause) {
        super(message, cause);
        this.noActiveHostInClusterFault = noActiveHostInClusterFault;
    }

    public com.vmware.vim25.NoActiveHostInCluster getFaultInfo() {
        return this.noActiveHostInClusterFault;
    }
}
