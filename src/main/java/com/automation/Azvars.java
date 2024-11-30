package com.automation;

import java.util.List;

public class Azvars {

	String PIP_Name="PIP_Name";
	String RG_Name="RG_Name";
	String SubName="SubName";
	String SubId="SubId";
	String TenantId="TenantId";
	String SubStatus="SubStatus";
	String ResourceGroups="ResourceGroups";
	String Classic_Cloud_Services_IaaS="Classic_Cloud_Services_IaaS";
	String Classic_Cloud_Services_PaaS="Classic_Cloud_Services_PaaS";
	String Classic_Cloud_Services_PaaSNames="Classic_Cloud_Services_PaaSNames";
	String Load_Balancers_STANDARD_SKU="Load_Balancers_STANDARD_SKU";
	String PublicIPs="PublicIPs";
	String vNetPeers="vNetPeers";
	String VNET_Name="VNET_Name";
	String PlanInfoVMs="PlanInfoVMs";
	String VM_Name="VM_Name";
	String Plan_Available="Plan_Available";
	String Plan_Product="Plan_Product";
	String Resources="Resources";
	String Resource_Type="Resource_Type";
	String Subscription_Disabled="Subscription_Disabled";
	String Subscription_Has_No_Resources="Subscription_Has_No_Resources";
	String complex="complex";
	String simple="simple";
	String Classic="Classic";
	String certificates="certificates";
	String vMX100="vMX100";
	String expressroute="expressroute";
	String loadbalancers="loadbalancers";
	String accounts="accounts";
	String Subscriptions="Subscriptions";
	String classic="classic";
	String url = "https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/move-support-resources";
	String azstore = "Microsoft.ClassicStorage/storageAccounts";
	String Resource_Total ="Resource_Total";
	String solutions = "microsoft.operationsmanagement/solutions";
	String workspace ="microsoft.operationalinsights/workspaces";
	String certificate = "microsoft.web/certificates";
	String domainservices="microsoft.aad/domainservices";
	String domainnames=	"microsoft.classiccompute/domainnames";
	String sshkeys = "microsoft.compute/sshpublickeys";
	String streamanalytics = "microsoft.streamanalytics/streamingjobs";
	String azloadbalancer = "microsoft.network/loadbalancer";
	String azappgateway = "microsoft.network/applicationgateways";
	String azflowlogs = "microsoft.network/networkwatchers/flowlogs";
	String azpvtendpoints = "microsoft.network/privateendpoints";
	String azexpressroute = "microsoft.network/expressroutecircuits";
	String bastionhost = "microsoft.network/bastionhosts";
	String ddosprotectionplans = "microsoft.network/ddosprotectionplans";
	String natgateways = "microsoft.network/natgateways";
	String connectiongateways = "microsoft.web/connectiongateways";
	String classicstorage =	"microsoft.classicstorage/storageaccounts";
	String vNet_Peering_Count ="vNet_Peering_Count";
	String Public_IP_Address_Count = "Public_IP_Address_Count";
	String Encrypted_VM_Count = "Encrypted_VM_Count";
	String Frontdoor_Count = "Frontdoor_VM_Count";
	String fdstandard = "microsoft.network/frontdoors";
	String fdcdn = "microsoft.cdn/profiles/afdendpoints";
	List<String> supportedAzResources = List.of(azstore, solutions,
			workspace, fdstandard, classicstorage, connectiongateways, natgateways, ddosprotectionplans, bastionhost,
			azexpressroute, azpvtendpoints, azflowlogs, azappgateway, azloadbalancer, streamanalytics, sshkeys, domainnames, certificate, domainservices);

}
