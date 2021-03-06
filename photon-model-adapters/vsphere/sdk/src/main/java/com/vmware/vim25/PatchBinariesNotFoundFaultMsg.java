
package com.vmware.vim25;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.6
 * 2017-05-25T13:48:06.956+05:30
 * Generated source version: 3.1.6
 */

@WebFault(name = "PatchBinariesNotFoundFault", targetNamespace = "urn:vim25")
public class PatchBinariesNotFoundFaultMsg extends Exception {
    public static final long serialVersionUID = 1L;
    
    private com.vmware.vim25.PatchBinariesNotFound patchBinariesNotFoundFault;

    public PatchBinariesNotFoundFaultMsg() {
        super();
    }
    
    public PatchBinariesNotFoundFaultMsg(String message) {
        super(message);
    }
    
    public PatchBinariesNotFoundFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public PatchBinariesNotFoundFaultMsg(String message, com.vmware.vim25.PatchBinariesNotFound patchBinariesNotFoundFault) {
        super(message);
        this.patchBinariesNotFoundFault = patchBinariesNotFoundFault;
    }

    public PatchBinariesNotFoundFaultMsg(String message, com.vmware.vim25.PatchBinariesNotFound patchBinariesNotFoundFault, Throwable cause) {
        super(message, cause);
        this.patchBinariesNotFoundFault = patchBinariesNotFoundFault;
    }

    public com.vmware.vim25.PatchBinariesNotFound getFaultInfo() {
        return this.patchBinariesNotFoundFault;
    }
}
