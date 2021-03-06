
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.vmware.pbm.PbmAboutInfo;
import com.vmware.pbm.PbmCapabilityConstraintInstance;
import com.vmware.pbm.PbmCapabilityConstraints;
import com.vmware.pbm.PbmCapabilityDescription;
import com.vmware.pbm.PbmCapabilityDiscreteSet;
import com.vmware.pbm.PbmCapabilityInstance;
import com.vmware.pbm.PbmCapabilityMetadata;
import com.vmware.pbm.PbmCapabilityMetadataPerCategory;
import com.vmware.pbm.PbmCapabilityMetadataUniqueId;
import com.vmware.pbm.PbmCapabilityNamespaceInfo;
import com.vmware.pbm.PbmCapabilityProfileCreateSpec;
import com.vmware.pbm.PbmCapabilityProfileUpdateSpec;
import com.vmware.pbm.PbmCapabilityPropertyInstance;
import com.vmware.pbm.PbmCapabilityPropertyMetadata;
import com.vmware.pbm.PbmCapabilityRange;
import com.vmware.pbm.PbmCapabilitySchema;
import com.vmware.pbm.PbmCapabilitySchemaVendorInfo;
import com.vmware.pbm.PbmCapabilitySubProfile;
import com.vmware.pbm.PbmCapabilityTimeSpan;
import com.vmware.pbm.PbmCapabilityTypeInfo;
import com.vmware.pbm.PbmCapabilityVendorNamespaceInfo;
import com.vmware.pbm.PbmCapabilityVendorResourceTypeInfo;
import com.vmware.pbm.PbmComplianceOperationalStatus;
import com.vmware.pbm.PbmCompliancePolicyStatus;
import com.vmware.pbm.PbmComplianceResult;
import com.vmware.pbm.PbmDataServiceToPoliciesMap;
import com.vmware.pbm.PbmDatastoreSpaceStatistics;
import com.vmware.pbm.PbmDefaultProfileInfo;
import com.vmware.pbm.PbmExtendedElementDescription;
import com.vmware.pbm.PbmLineOfServiceInfo;
import com.vmware.pbm.PbmPlacementCompatibilityResult;
import com.vmware.pbm.PbmPlacementHub;
import com.vmware.pbm.PbmPlacementMatchingResources;
import com.vmware.pbm.PbmPlacementRequirement;
import com.vmware.pbm.PbmPlacementResourceUtilization;
import com.vmware.pbm.PbmProfile;
import com.vmware.pbm.PbmProfileId;
import com.vmware.pbm.PbmProfileOperationOutcome;
import com.vmware.pbm.PbmProfileResourceType;
import com.vmware.pbm.PbmProfileType;
import com.vmware.pbm.PbmQueryProfileResult;
import com.vmware.pbm.PbmQueryReplicationGroupResult;
import com.vmware.pbm.PbmRollupComplianceResult;
import com.vmware.pbm.PbmServerObjectRef;
import com.vmware.pbm.PbmServiceInstanceContent;


/**
 * <p>Java class for DynamicData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DynamicData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DynamicData")
@XmlSeeAlso({
    KeyAnyValue.class,
    LocalizableMessage.class,
    LocalizedMethodFault.class,
    PropertyFilterSpec.class,
    PropertySpec.class,
    ObjectSpec.class,
    SelectionSpec.class,
    ObjectContent.class,
    UpdateSet.class,
    PropertyFilterUpdate.class,
    ObjectUpdate.class,
    PropertyChange.class,
    MissingProperty.class,
    MissingObject.class,
    WaitOptions.class,
    RetrieveOptions.class,
    RetrieveResult.class,
    AboutInfo.class,
    AuthorizationDescription.class,
    Permission.class,
    AuthorizationRole.class,
    AuthorizationPrivilege.class,
    PrivilegeAvailability.class,
    EntityPrivilege.class,
    UserPrivilegeResult.class,
    BatchResult.class,
    Capability.class,
    ComputeResourceSummary.class,
    ComputeResourceHostSPBMLicenseInfo.class,
    CustomFieldDef.class,
    CustomFieldValue.class,
    CustomizationSpecInfo.class,
    CustomizationSpecItem.class,
    DatacenterConfigInfo.class,
    DatacenterConfigSpec.class,
    DatastoreSummary.class,
    DatastoreCapability.class,
    DatastoreHostMount.class,
    DatastoreMountPathDatastorePair.class,
    DatastoreVVolContainerFailoverPair.class,
    DiagnosticManagerLogDescriptor.class,
    DiagnosticManagerLogHeader.class,
    DiagnosticManagerBundleInfo.class,
    DVSContactInfo.class,
    DVSNetworkResourceManagementCapability.class,
    DVSRollbackCapability.class,
    DVSBackupRestoreCapability.class,
    DVSCapability.class,
    DVSSummary.class,
    DVSPolicy.class,
    DVSUplinkPortPolicy.class,
    DVSCreateSpec.class,
    DvsHostInfrastructureTrafficResourceAllocation.class,
    DvsHostInfrastructureTrafficResource.class,
    DvsResourceRuntimeInfo.class,
    DVSRuntimeInfo.class,
    EnumDescription.class,
    EnvironmentBrowserConfigOptionQuerySpec.class,
    ExtensionServerInfo.class,
    ExtensionClientInfo.class,
    ExtensionTaskTypeInfo.class,
    ExtensionEventTypeInfo.class,
    ExtensionFaultTypeInfo.class,
    ExtensionPrivilegeInfo.class,
    ExtensionResourceInfo.class,
    ExtensionHealthInfo.class,
    ExtensionOvfConsumerInfo.class,
    Extension.class,
    ExtensionManagerIpAllocationUsage.class,
    ReplicationVmProgressInfo.class,
    HbrManagerReplicationVmInfo.class,
    HbrManagerVmReplicationCapability.class,
    HealthUpdateInfo.class,
    HealthUpdate.class,
    HostServiceTicket.class,
    HostSystemReconnectSpec.class,
    HttpNfcLeaseDatastoreLeaseInfo.class,
    HttpNfcLeaseHostInfo.class,
    HttpNfcLeaseInfo.class,
    HttpNfcLeaseDeviceUrl.class,
    HttpNfcLeaseManifestEntry.class,
    IoFilterInfo.class,
    IoFilterHostIssue.class,
    IoFilterQueryIssueResult.class,
    IpPoolManagerIpAllocation.class,
    KeyValue.class,
    LatencySensitivity.class,
    LicenseAssignmentManagerLicenseAssignment.class,
    LicenseSource.class,
    LicenseFeatureInfo.class,
    LicenseReservationInfo.class,
    LicenseAvailabilityInfo.class,
    LicenseDiagnostics.class,
    LicenseUsageInfo.class,
    LicenseManagerEvaluationInfo.class,
    HostLicensableResourceInfo.class,
    LicenseManagerLicenseInfo.class,
    LocalizationManagerMessageCatalog.class,
    NumericRange.class,
    NetworkSummary.class,
    OpaqueNetworkCapability.class,
    OvfConsumerOvfSection.class,
    OvfConsumerOstNode.class,
    OvfOptionInfo.class,
    OvfDeploymentOption.class,
    OvfValidateHostResult.class,
    OvfParseDescriptorResult.class,
    OvfNetworkInfo.class,
    OvfManagerCommonParams.class,
    OvfResourceMap.class,
    OvfNetworkMapping.class,
    OvfCreateImportSpecResult.class,
    OvfFileItem.class,
    OvfCreateDescriptorParams.class,
    OvfCreateDescriptorResult.class,
    OvfFile.class,
    PasswordField.class,
    PerformanceDescription.class,
    PerfProviderSummary.class,
    PerfCounterInfo.class,
    PerfMetricId.class,
    PerfQuerySpec.class,
    PerfSampleInfo.class,
    PerfMetricSeries.class,
    PerfEntityMetricBase.class,
    PerfCompositeMetric.class,
    PerformanceManagerCounterLevelMapping.class,
    PerfInterval.class,
    PrivilegePolicyDef.class,
    ResourceAllocationOption.class,
    ResourceConfigOption.class,
    ResourceConfigSpec.class,
    DatabaseSizeParam.class,
    InventoryDescription.class,
    PerformanceStatisticsDescription.class,
    DatabaseSizeEstimate.class,
    ResourcePoolResourceUsage.class,
    ResourcePoolRuntimeInfo.class,
    ResourcePoolQuickStats.class,
    HostVMotionCompatibility.class,
    ProductComponentInfo.class,
    ServiceContent.class,
    ServiceLocatorCredential.class,
    ServiceLocator.class,
    ServiceManagerServiceInfo.class,
    SessionManagerLocalTicket.class,
    SessionManagerGenericServiceTicket.class,
    SessionManagerServiceRequestSpec.class,
    SharesInfo.class,
    SharesOption.class,
    StoragePodSummary.class,
    StorageIOAllocationInfo.class,
    StorageIOAllocationOption.class,
    StorageIORMInfo.class,
    StorageIORMConfigSpec.class,
    StorageIORMConfigOption.class,
    StoragePerformanceSummary.class,
    PodStorageDrsEntry.class,
    StorageResourceManagerStorageProfileStatistics.class,
    Tag.class,
    TaskDescription.class,
    TaskFilterSpecByEntity.class,
    TaskFilterSpecByTime.class,
    TaskFilterSpecByUsername.class,
    TaskFilterSpec.class,
    TaskInfo.class,
    TaskReason.class,
    UpdateVirtualMachineFilesResultFailedVmFileInfo.class,
    UpdateVirtualMachineFilesResult.class,
    UserSearchResult.class,
    UserSession.class,
    ResourceAllocationInfo.class,
    VirtualResourcePoolSpec.class,
    VRPEditSpec.class,
    VirtualResourcePoolUsage.class,
    VVolVmConfigFileUpdateResultFailedVmConfigFileInfo.class,
    VVolVmConfigFileUpdateResult.class,
    VASAStorageArray.class,
    VasaProviderContainerSpec.class,
    VimVasaProviderStatePerArray.class,
    VimVasaProvider.class,
    VimVasaProviderInfo.class,
    ResourcePoolSummary.class,
    VirtualAppLinkInfo.class,
    VirtualDiskSpec.class,
    StorageRequirement.class,
    VirtualMachineTicket.class,
    VirtualMachineMksTicket.class,
    VirtualMachineDisplayTopology.class,
    DiskChangeExtent.class,
    DiskChangeInfo.class,
    VirtualMachineWipeResult.class,
    VsanUpgradeSystemNetworkPartitionInfo.class,
    VsanUpgradeSystemPreflightCheckIssue.class,
    VsanUpgradeSystemPreflightCheckResult.class,
    VsanUpgradeSystemUpgradeHistoryItem.class,
    VsanUpgradeSystemUpgradeStatus.class,
    MethodActionArgument.class,
    Action.class,
    AlarmTriggeringActionTransitionSpec.class,
    AlarmAction.class,
    AlarmDescription.class,
    EventAlarmExpressionComparison.class,
    AlarmExpression.class,
    AlarmSetting.class,
    AlarmSpec.class,
    AlarmState.class,
    ClusterActionHistory.class,
    ClusterAttemptedVmInfo.class,
    ClusterConfigInfo.class,
    ClusterDrsConfigInfo.class,
    ClusterDrsVmConfigInfo.class,
    ComputeResourceConfigInfo.class,
    ClusterDpmConfigInfo.class,
    ClusterDpmHostConfigInfo.class,
    ClusterInfraUpdateHaConfigInfo.class,
    ClusterProactiveDrsConfigInfo.class,
    ClusterConfigSpec.class,
    ComputeResourceConfigSpec.class,
    ClusterDasAamNodeState.class,
    ClusterDasAdvancedRuntimeInfoVmcpCapabilityInfo.class,
    DasHeartbeatDatastoreInfo.class,
    ClusterDasConfigInfo.class,
    ClusterDasData.class,
    ClusterDasFailoverLevelAdvancedRuntimeInfoSlotInfo.class,
    ClusterDasFailoverLevelAdvancedRuntimeInfoHostSlots.class,
    ClusterDasFailoverLevelAdvancedRuntimeInfoVmSlots.class,
    ClusterDasAdvancedRuntimeInfo.class,
    ClusterDasFdmHostState.class,
    ClusterDasHostInfo.class,
    ClusterDasHostRecommendation.class,
    ClusterDasVmConfigInfo.class,
    ClusterDasVmSettings.class,
    ClusterDrsFaultsFaultsByVm.class,
    ClusterDrsFaults.class,
    ClusterDrsMigration.class,
    ClusterDrsRecommendation.class,
    ClusterEVCManagerEVCState.class,
    ClusterEVCManagerCheckResult.class,
    ClusterEnterMaintenanceResult.class,
    ClusterFailoverHostAdmissionControlInfoHostStatus.class,
    ClusterDasAdmissionControlInfo.class,
    ClusterDasAdmissionControlPolicy.class,
    ClusterHostRecommendation.class,
    ClusterNotAttemptedVmInfo.class,
    ClusterOrchestrationInfo.class,
    PlacementResult.class,
    PlacementSpec.class,
    ClusterPowerOnVmResult.class,
    ClusterRecommendation.class,
    ClusterResourceUsageSummary.class,
    ClusterSlotPolicy.class,
    ClusterUsageSummary.class,
    ClusterVmComponentProtectionSettings.class,
    ClusterGroupInfo.class,
    ClusterVmOrchestrationInfo.class,
    ClusterVmReadiness.class,
    ClusterVmToolsMonitoringSettings.class,
    DVPortConfigSpec.class,
    DVPortConfigInfo.class,
    DVSHostLocalPortInfo.class,
    DvsFilterParameter.class,
    DVPortStatus.class,
    DVPortState.class,
    DistributedVirtualPort.class,
    DVPortgroupConfigSpec.class,
    DVPortgroupConfigInfo.class,
    DistributedVirtualPortgroupInfo.class,
    DistributedVirtualSwitchInfo.class,
    DVSManagerDvsConfigTarget.class,
    DistributedVirtualSwitchManagerCompatibilityResult.class,
    DistributedVirtualSwitchManagerHostContainer.class,
    DistributedVirtualSwitchManagerHostDvsFilterSpec.class,
    DistributedVirtualSwitchManagerDvsProductSpec.class,
    DistributedVirtualSwitchManagerImportResult.class,
    SelectionSet.class,
    EntityBackupConfig.class,
    EntityBackup.class,
    DistributedVirtualSwitchHostMemberConfigSpec.class,
    DistributedVirtualSwitchHostMemberPnicSpec.class,
    DistributedVirtualSwitchHostMemberBacking.class,
    DistributedVirtualSwitchHostMemberRuntimeState.class,
    DistributedVirtualSwitchHostMemberConfigInfo.class,
    HostMemberRuntimeInfo.class,
    DistributedVirtualSwitchHostMember.class,
    DistributedVirtualSwitchHostProductSpec.class,
    DistributedVirtualSwitchKeyedOpaqueBlob.class,
    DVSNetworkResourcePoolAllocationInfo.class,
    DVSNetworkResourcePoolConfigSpec.class,
    DVSNetworkResourcePool.class,
    DistributedVirtualSwitchPortConnectee.class,
    DistributedVirtualSwitchPortConnection.class,
    DistributedVirtualSwitchPortCriteria.class,
    DistributedVirtualSwitchPortStatistics.class,
    DistributedVirtualSwitchProductSpec.class,
    NegatableExpression.class,
    DvsNetworkRuleQualifier.class,
    DvsNetworkRuleAction.class,
    DvsTrafficRule.class,
    DvsTrafficRuleset.class,
    DvsVmVnicResourceAllocation.class,
    DvsVmVnicResourcePoolConfigSpec.class,
    DvsVnicAllocatedResource.class,
    DvsVmVnicNetworkResourcePoolRuntimeInfo.class,
    DVSVmVnicNetworkResourcePool.class,
    DVSFeatureCapability.class,
    VMwareDvsIpfixCapability.class,
    VMwareDvsLacpCapability.class,
    DVSHealthCheckCapability.class,
    VMwareDVSVspanCapability.class,
    VMwareVspanPort.class,
    VMwareVspanSession.class,
    VMwareIpfixConfig.class,
    DVSConfigInfo.class,
    DVSConfigSpec.class,
    DVPortSetting.class,
    DVPortgroupPolicy.class,
    VMwareDVSPvlanConfigSpec.class,
    VMwareDVSPvlanMapEntry.class,
    VMwareDVSVspanConfigSpec.class,
    DVSHealthCheckConfig.class,
    HostMemberHealthCheckResult.class,
    InheritablePolicy.class,
    VMwareDvsLacpGroupConfig.class,
    VMwareDvsLagVlanConfig.class,
    VMwareDvsLagIpfixConfig.class,
    VMwareDvsLacpGroupSpec.class,
    CryptoKeyId.class,
    CryptoKeyPlain.class,
    CryptoKeyResult.class,
    CryptoManagerKmipCertificateInfo.class,
    CryptoManagerKmipServerStatus.class,
    CryptoManagerKmipClusterStatus.class,
    CryptoManagerKmipServerCertInfo.class,
    CryptoSpec.class,
    KeyProviderId.class,
    KmipClusterInfo.class,
    KmipServerInfo.class,
    KmipServerSpec.class,
    KmipServerStatus.class,
    ExtendedEventPair.class,
    VnicPortArgument.class,
    DvsOutOfSyncHostArgument.class,
    EventArgument.class,
    ChangesInfoEventArgument.class,
    EventArgDesc.class,
    EventDescriptionEventDetail.class,
    EventDescription.class,
    Event.class,
    EventFilterSpecByEntity.class,
    EventFilterSpecByTime.class,
    EventFilterSpecByUsername.class,
    EventFilterSpec.class,
    ExtExtendedProductInfo.class,
    ManagedByInfo.class,
    ExtManagedEntityInfo.class,
    ExtSolutionManagerInfoTabInfo.class,
    ExtSolutionManagerInfo.class,
    AnswerFileUpdateFailure.class,
    ConflictingConfigurationConfig.class,
    DatacenterMismatchArgument.class,
    DvsApplyOperationFaultFaultOnObject.class,
    DvsOperationBulkFaultFaultOnHost.class,
    ImportOperationBulkFaultFaultOnImport.class,
    MultipleCertificatesVerifyFaultThumbprintData.class,
    ProfileUpdateFailedUpdateFailure.class,
    HostActiveDirectorySpec.class,
    HostActiveDirectory.class,
    HostAuthenticationManagerInfo.class,
    AutoStartDefaults.class,
    AutoStartPowerInfo.class,
    HostAutoStartManagerConfig.class,
    HostBootDeviceInfo.class,
    HostBootDevice.class,
    HostCacheConfigurationSpec.class,
    HostCacheConfigurationInfo.class,
    HostCapability.class,
    HostCertificateManagerCertificateInfo.class,
    HostConfigChange.class,
    HostConfigInfo.class,
    HostConfigManager.class,
    HostConfigSpec.class,
    HostConnectInfoNetworkInfo.class,
    HostDatastoreConnectInfo.class,
    HostLicenseConnectInfo.class,
    HostConnectInfo.class,
    HostConnectSpec.class,
    HostCpuIdInfo.class,
    HostHyperThreadScheduleInfo.class,
    FileQueryFlags.class,
    VmConfigFileQueryFilter.class,
    VmConfigFileQueryFlags.class,
    VmDiskFileQueryFilter.class,
    VmDiskFileQueryFlags.class,
    FileQuery.class,
    VmConfigFileEncryptionInfo.class,
    VmDiskFileEncryptionInfo.class,
    FileInfo.class,
    HostDatastoreBrowserSearchSpec.class,
    HostDatastoreBrowserSearchResults.class,
    HostDatastoreSystemCapabilities.class,
    HostDatastoreSystemVvolDatastoreSpec.class,
    HostDatastoreSystemDatastoreResult.class,
    VmfsDatastoreSpec.class,
    VmfsDatastoreBaseOption.class,
    VmfsDatastoreOption.class,
    DatastoreInfo.class,
    HostDateTimeConfig.class,
    HostDateTimeInfo.class,
    HostDateTimeSystemTimeZone.class,
    HostDeploymentInfo.class,
    HostDhcpServiceSpec.class,
    HostDhcpServiceConfig.class,
    HostDhcpService.class,
    HostDiagnosticPartitionCreateOption.class,
    HostDiagnosticPartitionCreateSpec.class,
    HostDiagnosticPartitionCreateDescription.class,
    HostDiagnosticPartition.class,
    HostDiskConfigurationResult.class,
    HostDiskDimensionsChs.class,
    HostDiskDimensionsLba.class,
    HostDiskDimensions.class,
    HostDiskPartitionAttributes.class,
    HostDiskPartitionBlockRange.class,
    HostDiskPartitionSpec.class,
    HostDiskPartitionLayout.class,
    HostDiskPartitionInfo.class,
    HostDnsConfig.class,
    HostEsxAgentHostManagerConfigInfo.class,
    HostFaultToleranceManagerComponentHealthInfo.class,
    FcoeConfigVlanRange.class,
    FcoeConfigFcoeCapabilities.class,
    FcoeConfigFcoeSpecification.class,
    FcoeConfig.class,
    HostFeatureCapability.class,
    HostFeatureMask.class,
    HostFeatureVersionInfo.class,
    ModeInfo.class,
    HostFileAccess.class,
    HostFileSystemVolumeInfo.class,
    HostFileSystemMountInfo.class,
    HostNasVolumeUserInfo.class,
    HostNasVolumeSpec.class,
    HostNasVolumeConfig.class,
    HostLocalFileSystemVolumeSpec.class,
    HostFirewallConfigRuleSetConfig.class,
    HostFirewallConfig.class,
    HostFirewallDefaultPolicy.class,
    HostFirewallInfo.class,
    HostFlagInfo.class,
    HostForceMountedInfo.class,
    HostGatewaySpec.class,
    HostGraphicsConfigDeviceType.class,
    HostGraphicsConfig.class,
    HostGraphicsInfo.class,
    HostHardwareInfo.class,
    HostSystemInfo.class,
    HostCpuPowerManagementInfo.class,
    HostCpuInfo.class,
    HostCpuPackage.class,
    HostNumaInfo.class,
    HostNumaNode.class,
    HostBIOSInfo.class,
    HostReliableMemoryInfo.class,
    HostStorageOperationalInfo.class,
    HostHardwareElementInfo.class,
    HostHardwareStatusInfo.class,
    HealthSystemRuntime.class,
    HostAccessControlEntry.class,
    HostInternetScsiHbaDiscoveryCapabilities.class,
    HostInternetScsiHbaDiscoveryProperties.class,
    HostInternetScsiHbaAuthenticationCapabilities.class,
    HostInternetScsiHbaAuthenticationProperties.class,
    HostInternetScsiHbaDigestCapabilities.class,
    HostInternetScsiHbaDigestProperties.class,
    HostInternetScsiHbaIPCapabilities.class,
    HostInternetScsiHbaIscsiIpv6Address.class,
    HostInternetScsiHbaIPv6Properties.class,
    HostInternetScsiHbaIPProperties.class,
    HostInternetScsiHbaSendTarget.class,
    HostInternetScsiHbaStaticTarget.class,
    HostInternetScsiHbaTargetSet.class,
    HostFibreChannelOverEthernetHbaLinkInfo.class,
    HostHostBusAdapter.class,
    HostProxySwitchSpec.class,
    HostProxySwitchConfig.class,
    HostProxySwitchHostLagConfig.class,
    HostProxySwitch.class,
    HostImageProfileSummary.class,
    HostIpConfigIpV6Address.class,
    HostIpConfigIpV6AddressConfiguration.class,
    HostIpConfig.class,
    HostIpRouteConfig.class,
    HostIpRouteEntry.class,
    HostIpRouteOp.class,
    HostIpRouteTableConfig.class,
    HostIpRouteTableInfo.class,
    HostIpmiInfo.class,
    IscsiStatus.class,
    IscsiPortInfo.class,
    IscsiDependencyEntity.class,
    IscsiMigrationDependency.class,
    KernelModuleSectionInfo.class,
    KernelModuleInfo.class,
    HostLicenseSpec.class,
    LinkDiscoveryProtocolConfig.class,
    HostAccountSpec.class,
    HostAuthenticationStoreInfo.class,
    HostLowLevelProvisioningManagerVmRecoveryInfo.class,
    HostLowLevelProvisioningManagerVmMigrationStatus.class,
    HostLowLevelProvisioningManagerDiskLayoutSpec.class,
    HostLowLevelProvisioningManagerSnapshotLayoutSpec.class,
    HostLowLevelProvisioningManagerFileReserveSpec.class,
    HostLowLevelProvisioningManagerFileReserveResult.class,
    HostLowLevelProvisioningManagerFileDeleteSpec.class,
    HostLowLevelProvisioningManagerFileDeleteResult.class,
    HostMaintenanceSpec.class,
    ServiceConsoleReservationInfo.class,
    VirtualMachineMemoryReservationInfo.class,
    VirtualMachineMemoryReservationSpec.class,
    HostMemorySpec.class,
    HostMountInfo.class,
    HostMultipathInfoLogicalUnitStorageArrayTypePolicy.class,
    HostMultipathInfoLogicalUnitPolicy.class,
    HostMultipathInfoLogicalUnit.class,
    HostMultipathInfoPath.class,
    HostMultipathInfo.class,
    HostMultipathStateInfoPath.class,
    HostMultipathStateInfo.class,
    HostNatServicePortForwardSpec.class,
    HostNatServiceNameServiceSpec.class,
    HostNatServiceSpec.class,
    HostNatServiceConfig.class,
    HostNatService.class,
    HostNetCapabilities.class,
    HostNetOffloadCapabilities.class,
    HostNetStackInstance.class,
    HostNetworkConfigResult.class,
    HostNetworkConfigNetStackSpec.class,
    HostNetworkConfig.class,
    HostNetworkInfo.class,
    HostNetworkSecurityPolicy.class,
    HostNetworkTrafficShapingPolicy.class,
    HostNicFailureCriteria.class,
    HostNicOrderPolicy.class,
    HostNicTeamingPolicy.class,
    HostNetworkPolicy.class,
    HostNtpConfig.class,
    HostNumericSensorInfo.class,
    HostOpaqueNetworkInfo.class,
    HostOpaqueSwitchPhysicalNicZone.class,
    HostOpaqueSwitch.class,
    HostPatchManagerResult.class,
    HostPatchManagerStatusPrerequisitePatch.class,
    HostPatchManagerStatus.class,
    HostPatchManagerLocator.class,
    HostPatchManagerPatchManagerOperationSpec.class,
    HostPathSelectionPolicyOption.class,
    HostPciDevice.class,
    PhysicalNicSpec.class,
    PhysicalNicConfig.class,
    PhysicalNicLinkInfo.class,
    PhysicalNicHint.class,
    PhysicalNicHintInfo.class,
    PhysicalNicCdpDeviceCapability.class,
    PhysicalNicCdpInfo.class,
    LinkLayerDiscoveryProtocolInfo.class,
    PhysicalNic.class,
    HostPlugStoreTopologyAdapter.class,
    HostPlugStoreTopologyPath.class,
    HostPlugStoreTopologyDevice.class,
    HostPlugStoreTopologyPlugin.class,
    HostPlugStoreTopologyTarget.class,
    HostPlugStoreTopology.class,
    HostPortGroupSpec.class,
    HostPortGroupConfig.class,
    HostPortGroupPort.class,
    HostPortGroup.class,
    HostPowerPolicy.class,
    PowerSystemCapability.class,
    PowerSystemInfo.class,
    HostProtocolEndpoint.class,
    HostResignatureRescanResult.class,
    HostFirewallRulesetIpNetwork.class,
    HostFirewallRulesetIpList.class,
    HostFirewallRulesetRulesetSpec.class,
    HostFirewallRule.class,
    HostFirewallRuleset.class,
    HostRuntimeInfoNetStackInstanceRuntimeInfo.class,
    HostPlacedVirtualNicIdentifier.class,
    HostPnicNetworkResourceInfo.class,
    HostNetworkResourceRuntime.class,
    HostRuntimeInfoNetworkRuntimeInfo.class,
    HostRuntimeInfo.class,
    HostScsiDiskPartition.class,
    ScsiLunCapabilities.class,
    ScsiLunDurableName.class,
    ScsiLunDescriptor.class,
    HostDevice.class,
    HostScsiTopologyInterface.class,
    HostScsiTopologyTarget.class,
    HostScsiTopologyLun.class,
    HostScsiTopology.class,
    HostSecuritySpec.class,
    HostServiceSourcePackage.class,
    HostService.class,
    HostServiceConfig.class,
    HostServiceInfo.class,
    HostSnmpDestination.class,
    HostSnmpConfigSpec.class,
    HostSnmpSystemAgentLimits.class,
    SoftwarePackageCapability.class,
    Relation.class,
    SoftwarePackage.class,
    HostPciPassthruConfig.class,
    HostPciPassthruInfo.class,
    HostSriovDevicePoolInfo.class,
    HostSslThumbprintInfo.class,
    HostStorageArrayTypePolicyOption.class,
    HostStorageDeviceInfo.class,
    HostStorageSystemVmfsVolumeResult.class,
    HostStorageSystemScsiLunResult.class,
    HostStorageSystemDiskLocatorLedResult.class,
    HostHardwareSummary.class,
    HostListSummaryQuickStats.class,
    HostConfigSummary.class,
    HostListSummaryGatewaySummary.class,
    HostListSummary.class,
    SystemEventInfo.class,
    HostSystemHealthInfo.class,
    HostSystemIdentificationInfo.class,
    HostSystemResourceInfo.class,
    HostSystemSwapConfigurationSystemSwapOption.class,
    HostSystemSwapConfiguration.class,
    HostTargetTransport.class,
    HostTpmAttestationReport.class,
    HostDigestInfo.class,
    HostTpmEventLogEntry.class,
    HostTpmEventDetails.class,
    HostUnresolvedVmfsExtent.class,
    HostUnresolvedVmfsResignatureSpec.class,
    HostUnresolvedVmfsResolutionResult.class,
    HostUnresolvedVmfsResolutionSpec.class,
    HostUnresolvedVmfsVolumeResolveStatus.class,
    HostUnresolvedVmfsVolume.class,
    HostVFlashManagerVFlashResourceConfigSpec.class,
    HostVFlashManagerVFlashResourceConfigInfo.class,
    HostVFlashManagerVFlashResourceRunTimeInfo.class,
    HostVFlashManagerVFlashCacheConfigSpec.class,
    HostVFlashManagerVFlashCacheConfigInfoVFlashModuleConfigOption.class,
    HostVFlashManagerVFlashCacheConfigInfo.class,
    HostVFlashManagerVFlashConfigInfo.class,
    HostVFlashResourceConfigurationResult.class,
    HostVMotionConfig.class,
    HostVMotionInfo.class,
    HostVMotionNetConfig.class,
    HostVffsSpec.class,
    HostVirtualNicSpec.class,
    HostVirtualNicConfig.class,
    HostVirtualNicOpaqueNetworkSpec.class,
    HostVirtualNicIpRouteSpec.class,
    HostVirtualNic.class,
    HostVirtualNicConnection.class,
    HostVirtualNicManagerNicTypeSelection.class,
    VirtualNicManagerNetConfig.class,
    HostVirtualNicManagerInfo.class,
    HostVirtualSwitchBridge.class,
    HostVirtualSwitchBeaconConfig.class,
    HostVirtualSwitchSpec.class,
    HostVirtualSwitchConfig.class,
    HostVirtualSwitch.class,
    HostVmciAccessManagerAccessSpec.class,
    HostVmfsRescanResult.class,
    HostVmfsSpec.class,
    VmfsConfigOption.class,
    HostVsanInternalSystemCmmdsQuery.class,
    VsanPolicyCost.class,
    VsanPolicySatisfiability.class,
    VsanPolicyChangeBatch.class,
    VsanNewPolicyBatch.class,
    HostVsanInternalSystemVsanPhysicalDiskDiagnosticsResult.class,
    HostVsanInternalSystemDeleteVsanObjectsResult.class,
    HostVsanInternalSystemVsanObjectOperationResult.class,
    HostVvolVolumeSpecification.class,
    VVolHostPE.class,
    HostFileSystemVolume.class,
    NetDhcpConfigInfoDhcpOptions.class,
    NetDhcpConfigInfo.class,
    NetDhcpConfigSpecDhcpOptionsSpec.class,
    NetDhcpConfigSpec.class,
    NetDnsConfigInfo.class,
    NetDnsConfigSpec.class,
    NetIpConfigInfoIpAddress.class,
    NetIpConfigInfo.class,
    NetIpConfigSpecIpAddressSpec.class,
    NetIpConfigSpec.class,
    NetIpRouteConfigInfoGateway.class,
    NetIpRouteConfigInfoIpRoute.class,
    NetIpRouteConfigInfo.class,
    NetIpRouteConfigSpecGatewaySpec.class,
    NetIpRouteConfigSpecIpRouteSpec.class,
    NetIpRouteConfigSpec.class,
    NetIpStackInfoNetToMedia.class,
    NetIpStackInfoDefaultRouter.class,
    NetIpStackInfo.class,
    NetBIOSConfigInfo.class,
    OptionValue.class,
    OptionType.class,
    ProfileApplyProfileProperty.class,
    ComplianceLocator.class,
    ComplianceProfile.class,
    ComplianceFailureComplianceFailureValues.class,
    ComplianceFailure.class,
    ComplianceResult.class,
    ProfileDeferredPolicyOptionParameter.class,
    ProfileExpression.class,
    ProfileExpressionMetadata.class,
    ProfileParameterMetadata.class,
    ProfilePolicy.class,
    ProfilePolicyOptionMetadata.class,
    ProfilePolicyMetadata.class,
    PolicyOption.class,
    ProfileDescriptionSection.class,
    ProfileDescription.class,
    ProfileMetadataProfileSortSpec.class,
    ProfileMetadata.class,
    ProfilePropertyPath.class,
    ProfileProfileStructure.class,
    ProfileProfileStructureProperty.class,
    AnswerFile.class,
    AnswerFileStatusError.class,
    AnswerFileStatusResult.class,
    ProfileExecuteError.class,
    ApplyProfile.class,
    ProfileConfigInfo.class,
    ProfileCreateSpec.class,
    HostSpecification.class,
    HostSubSpecification.class,
    HostProfileManagerConfigTaskList.class,
    AnswerFileCreateSpec.class,
    HostProfilesEntityCustomizations.class,
    HostProfileManagerHostToConfigSpecMap.class,
    ProfileExecuteResult.class,
    HostProfileManagerCompositionValidationResultResultElement.class,
    Description.class,
    ScheduledTaskDescription.class,
    TaskScheduler.class,
    ScheduledTaskSpec.class,
    ApplyStorageRecommendationResult.class,
    StorageDrsConfigInfo.class,
    StorageDrsConfigSpec.class,
    PlacementAffinityRule.class,
    PlacementRankResult.class,
    PlacementRankSpec.class,
    StorageDrsPlacementRankVmSpec.class,
    StorageDrsPodConfigInfo.class,
    StorageDrsSpaceLoadBalanceConfig.class,
    StorageDrsIoLoadBalanceConfig.class,
    StorageDrsAutomationConfig.class,
    StorageDrsPodConfigSpec.class,
    VmPodConfigForPlacement.class,
    PodDiskLocator.class,
    StorageDrsPodSelectionSpec.class,
    ClusterAction.class,
    StoragePlacementResult.class,
    StoragePlacementSpec.class,
    ClusterRuleInfo.class,
    StorageDrsVmConfigInfo.class,
    VAppCloneSpecNetworkMappingPair.class,
    VAppCloneSpecResourceMap.class,
    VAppCloneSpec.class,
    VAppEntityConfigInfo.class,
    VAppIPAssignmentInfo.class,
    IpPoolIpPoolConfigInfo.class,
    IpPoolAssociation.class,
    IpPool.class,
    VAppOvfSectionInfo.class,
    VAppProductInfo.class,
    VAppPropertyInfo.class,
    VmConfigInfo.class,
    VmConfigSpec.class,
    ClusterNetworkConfigSpec.class,
    SourceNodeSpec.class,
    NodeNetworkSpec.class,
    VchaClusterNetworkSpec.class,
    NodeDeploymentSpec.class,
    VchaClusterConfigSpec.class,
    VchaClusterDeploymentSpec.class,
    FailoverNodeInfo.class,
    WitnessNodeInfo.class,
    VchaClusterConfigInfo.class,
    VchaNodeRuntimeInfo.class,
    VchaClusterRuntimeInfo.class,
    VchaClusterHealth.class,
    VirtualMachineAffinityInfo.class,
    VirtualMachineBootOptionsBootableDevice.class,
    VirtualMachineBootOptions.class,
    VirtualMachineCapability.class,
    VirtualMachineCloneSpec.class,
    VirtualMachineConfigInfoDatastoreUrlPair.class,
    VirtualMachineConfigInfoOverheadInfo.class,
    VirtualMachineConfigInfo.class,
    VirtualMachineConfigOption.class,
    VirtualMachineConfigOptionDescriptor.class,
    ArrayUpdateSpec.class,
    VirtualMachineConfigSpec.class,
    ConfigTarget.class,
    VirtualMachineConsolePreferences.class,
    VirtualMachineDatastoreVolumeOption.class,
    DatastoreOption.class,
    VirtualMachineDefaultPowerOpInfo.class,
    VirtualMachineDeviceRuntimeInfoDeviceRuntimeState.class,
    VirtualMachineDeviceRuntimeInfo.class,
    FaultToleranceConfigInfo.class,
    FaultToleranceConfigSpec.class,
    FaultToleranceMetaSpec.class,
    FaultToleranceSecondaryOpResult.class,
    FaultToleranceDiskSpec.class,
    FaultToleranceVMConfigSpec.class,
    VirtualMachineFeatureRequirement.class,
    VirtualMachineFileInfo.class,
    VirtualMachineFileLayoutDiskLayout.class,
    VirtualMachineFileLayoutSnapshotLayout.class,
    VirtualMachineFileLayout.class,
    VirtualMachineFileLayoutExFileInfo.class,
    VirtualMachineFileLayoutExDiskUnit.class,
    VirtualMachineFileLayoutExDiskLayout.class,
    VirtualMachineFileLayoutExSnapshotLayout.class,
    VirtualMachineFileLayoutEx.class,
    VirtualMachineFlagInfo.class,
    VirtualMachineForkConfigInfo.class,
    GuestDiskInfo.class,
    GuestNicInfo.class,
    GuestStackInfo.class,
    GuestScreenInfo.class,
    GuestInfoNamespaceGenerationInfo.class,
    GuestInfo.class,
    VirtualMachineGuestIntegrityInfo.class,
    GuestOsDescriptor.class,
    VirtualMachineIdeDiskDevicePartitionInfo.class,
    VirtualMachineLegacyNetworkSwitchInfo.class,
    VirtualMachineMessage.class,
    VirtualMachineMetadataManagerVmMetadataOwner.class,
    VirtualMachineMetadataManagerVmMetadata.class,
    VirtualMachineMetadataManagerVmMetadataInput.class,
    VirtualMachineMetadataManagerVmMetadataResult.class,
    VirtualMachineNetworkShaperInfo.class,
    VirtualMachineProfileRawData.class,
    VirtualMachineProfileSpec.class,
    VirtualMachineQuestionInfo.class,
    VirtualMachineRelocateSpecDiskLocator.class,
    VirtualMachineRelocateSpec.class,
    ReplicationInfoDiskSettings.class,
    ReplicationConfigSpec.class,
    VirtualMachineRuntimeInfoDasProtectionState.class,
    VirtualMachineRuntimeInfo.class,
    ScheduledHardwareUpgradeInfo.class,
    VirtualMachineSnapshotInfo.class,
    VirtualMachineSnapshotTree.class,
    VirtualMachineSriovDevicePoolInfo.class,
    VirtualMachineUsageOnDatastore.class,
    VirtualMachineStorageInfo.class,
    VirtualMachineConfigSummary.class,
    VirtualMachineQuickStats.class,
    VirtualMachineGuestSummary.class,
    VirtualMachineStorageSummary.class,
    VirtualMachineSummary.class,
    ToolsConfigInfoToolsLastInstallInfo.class,
    ToolsConfigInfo.class,
    UsbScanCodeSpecModifierType.class,
    UsbScanCodeSpecKeyEvent.class,
    UsbScanCodeSpec.class,
    VirtualMachineTargetInfo.class,
    VirtualHardware.class,
    VirtualHardwareOption.class,
    ImportSpec.class,
    VirtualMachineGuestQuiesceSpec.class,
    CheckResult.class,
    CustomizationIPSettingsIpV6AddressSpec.class,
    CustomizationIPSettings.class,
    CustomizationSpec.class,
    CustomizationName.class,
    CustomizationPassword.class,
    CustomizationOptions.class,
    CustomizationGuiUnattended.class,
    CustomizationUserData.class,
    CustomizationGuiRunOnce.class,
    CustomizationIdentification.class,
    CustomizationLicenseFilePrintData.class,
    CustomizationIdentitySettings.class,
    CustomizationGlobalIPSettings.class,
    CustomizationIpGenerator.class,
    CustomizationIpV6Generator.class,
    CustomizationAdapterMapping.class,
    HostDiskMappingPartitionInfo.class,
    HostDiskMappingInfo.class,
    HostDiskMappingPartitionOption.class,
    HostDiskMappingOption.class,
    VirtualDeviceConnectInfo.class,
    VirtualDeviceConnectOption.class,
    VirtualDeviceBusSlotOption.class,
    VirtualDeviceConfigSpecBackingSpec.class,
    VirtualDiskVFlashCacheConfigInfo.class,
    VirtualDiskId.class,
    VirtualDiskDeltaDiskFormatsSupported.class,
    VirtualDiskOptionVFlashCacheConfigOption.class,
    VirtualDeviceConfigSpec.class,
    VirtualEthernetCardResourceAllocation.class,
    VirtualDeviceBackingInfo.class,
    VirtualDeviceBusSlotInfo.class,
    VirtualDeviceBackingOption.class,
    VirtualMachineVMCIDeviceFilterSpec.class,
    VirtualMachineVMCIDeviceFilterInfo.class,
    VirtualMachineVMCIDeviceOptionFilterSpecOption.class,
    VirtualDevice.class,
    VirtualDeviceOption.class,
    GuestAuthSubject.class,
    GuestAuthAliasInfo.class,
    GuestAliases.class,
    GuestMappedAliases.class,
    GuestFileAttributes.class,
    GuestFileInfo.class,
    GuestListFileInfo.class,
    FileTransferInformation.class,
    GuestProgramSpec.class,
    GuestProcessInfo.class,
    GuestAuthentication.class,
    GuestRegKeyNameSpec.class,
    GuestRegKeySpec.class,
    GuestRegKeyRecordSpec.class,
    GuestRegValueNameSpec.class,
    GuestRegValueDataSpec.class,
    GuestRegValueSpec.class,
    DeviceGroupId.class,
    FaultDomainId.class,
    ReplicationGroupId.class,
    ReplicationSpec.class,
    VsanClusterConfigInfoHostDefaultInfo.class,
    VsanClusterConfigInfo.class,
    VsanHostClusterStatusStateCompletionEstimate.class,
    VsanHostClusterStatusState.class,
    VsanHostClusterStatus.class,
    VsanHostConfigInfoStorageInfo.class,
    VsanHostConfigInfoClusterInfo.class,
    VsanHostConfigInfoNetworkInfoPortConfig.class,
    VsanHostConfigInfoNetworkInfo.class,
    VsanHostFaultDomainInfo.class,
    VsanHostConfigInfo.class,
    VsanHostDecommissionMode.class,
    VsanHostDiskMapInfo.class,
    VsanHostDiskMapResult.class,
    VsanHostDiskMapping.class,
    VsanHostDiskResult.class,
    VsanHostIpConfig.class,
    VsanHostMembershipInfo.class,
    VsanHostVsanDiskInfo.class,
    VsanHostRuntimeInfoDiskIssue.class,
    VsanHostRuntimeInfo.class,
    BaseConfigInfoBackingInfo.class,
    VslmCreateSpecBackingSpec.class,
    VslmCreateSpec.class,
    ID.class,
    VslmMigrateSpec.class,
    VStorageObjectStateInfo.class,
    VslmTagEntry.class,
    BaseConfigInfo.class,
    VStorageObject.class,
    PbmServiceInstanceContent.class,
    PbmComplianceResult.class,
    PbmRollupComplianceResult.class,
    PbmServerObjectRef.class,
    PbmPlacementHub.class,
    PbmPlacementCompatibilityResult.class,
    PbmProfileResourceType.class,
    PbmCapabilityVendorResourceTypeInfo.class,
    PbmCapabilityMetadataPerCategory.class,
    PbmCapabilitySchema.class,
    PbmProfileId.class,
    PbmProfileOperationOutcome.class,
    PbmQueryProfileResult.class,
    PbmDefaultProfileInfo.class,
    PbmDatastoreSpaceStatistics.class,
    PbmQueryReplicationGroupResult.class,
    PbmAboutInfo.class,
    PbmExtendedElementDescription.class,
    PbmCapabilityInstance.class,
    PbmCapabilityMetadataUniqueId.class,
    PbmCapabilityMetadata.class,
    PbmCapabilityConstraintInstance.class,
    PbmCapabilityPropertyInstance.class,
    PbmCapabilityPropertyMetadata.class,
    PbmCapabilityTypeInfo.class,
    PbmCapabilitySchemaVendorInfo.class,
    PbmCapabilityNamespaceInfo.class,
    PbmCapabilityVendorNamespaceInfo.class,
    PbmLineOfServiceInfo.class,
    PbmCapabilityDescription.class,
    PbmCapabilityDiscreteSet.class,
    PbmCapabilityRange.class,
    PbmCapabilityTimeSpan.class,
    PbmComplianceOperationalStatus.class,
    PbmCompliancePolicyStatus.class,
    PbmPlacementMatchingResources.class,
    PbmPlacementRequirement.class,
    PbmPlacementResourceUtilization.class,
    PbmCapabilityProfileCreateSpec.class,
    PbmCapabilityProfileUpdateSpec.class,
    PbmDataServiceToPoliciesMap.class,
    PbmProfile.class,
    PbmProfileType.class,
    PbmCapabilitySubProfile.class,
    PbmCapabilityConstraints.class
})
public class DynamicData {


}
