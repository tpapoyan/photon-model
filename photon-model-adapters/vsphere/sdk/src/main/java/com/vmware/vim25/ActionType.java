
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MigrationV1"/&gt;
 *     &lt;enumeration value="VmPowerV1"/&gt;
 *     &lt;enumeration value="HostPowerV1"/&gt;
 *     &lt;enumeration value="HostMaintenanceV1"/&gt;
 *     &lt;enumeration value="StorageMigrationV1"/&gt;
 *     &lt;enumeration value="StoragePlacementV1"/&gt;
 *     &lt;enumeration value="PlacementV1"/&gt;
 *     &lt;enumeration value="HostInfraUpdateHaV1"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ActionType")
@XmlEnum
public enum ActionType {

    @XmlEnumValue("MigrationV1")
    MIGRATION_V_1("MigrationV1"),
    @XmlEnumValue("VmPowerV1")
    VM_POWER_V_1("VmPowerV1"),
    @XmlEnumValue("HostPowerV1")
    HOST_POWER_V_1("HostPowerV1"),
    @XmlEnumValue("HostMaintenanceV1")
    HOST_MAINTENANCE_V_1("HostMaintenanceV1"),
    @XmlEnumValue("StorageMigrationV1")
    STORAGE_MIGRATION_V_1("StorageMigrationV1"),
    @XmlEnumValue("StoragePlacementV1")
    STORAGE_PLACEMENT_V_1("StoragePlacementV1"),
    @XmlEnumValue("PlacementV1")
    PLACEMENT_V_1("PlacementV1"),
    @XmlEnumValue("HostInfraUpdateHaV1")
    HOST_INFRA_UPDATE_HA_V_1("HostInfraUpdateHaV1");
    private final String value;

    ActionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActionType fromValue(String v) {
        for (ActionType c: ActionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
