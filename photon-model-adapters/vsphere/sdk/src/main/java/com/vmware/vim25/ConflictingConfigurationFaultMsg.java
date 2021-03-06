
package com.vmware.vim25;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.6
 * 2017-05-25T13:48:07.205+05:30
 * Generated source version: 3.1.6
 */

@WebFault(name = "ConflictingConfigurationFault", targetNamespace = "urn:vim25")
public class ConflictingConfigurationFaultMsg extends Exception {
    public static final long serialVersionUID = 1L;
    
    private com.vmware.vim25.ConflictingConfiguration conflictingConfigurationFault;

    public ConflictingConfigurationFaultMsg() {
        super();
    }
    
    public ConflictingConfigurationFaultMsg(String message) {
        super(message);
    }
    
    public ConflictingConfigurationFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictingConfigurationFaultMsg(String message, com.vmware.vim25.ConflictingConfiguration conflictingConfigurationFault) {
        super(message);
        this.conflictingConfigurationFault = conflictingConfigurationFault;
    }

    public ConflictingConfigurationFaultMsg(String message, com.vmware.vim25.ConflictingConfiguration conflictingConfigurationFault, Throwable cause) {
        super(message, cause);
        this.conflictingConfigurationFault = conflictingConfigurationFault;
    }

    public com.vmware.vim25.ConflictingConfiguration getFaultInfo() {
        return this.conflictingConfigurationFault;
    }
}
