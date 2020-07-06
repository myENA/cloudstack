package org.apache.cloudstack.network.tungsten.api.command;

import com.cloud.event.EventTypes;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.NetworkRuleConflictException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.exception.ResourceUnavailableException;
import net.juniper.contrail.api.types.VirtualNetwork;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCreateCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.DomainResponse;
import org.apache.cloudstack.api.response.ProjectResponse;
import org.apache.cloudstack.context.CallContext;
import org.apache.cloudstack.network.tungsten.api.response.TungstenNetworkResponse;
import org.apache.cloudstack.network.tungsten.service.TungstenManager;
import org.apache.cloudstack.network.tungsten.service.TungstenResponseHelper;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@APICommand(name = "createTungstenNetwork",
        description = "Create tungsten network",
        responseObject = TungstenNetworkResponse.class)
public class CreateTungstenNetworkCmd extends BaseAsyncCreateCmd {

    private static final String s_name = "createtungstennetworkresponse";

    //Owner information
    @Parameter(name = ApiConstants.ACCOUNT, type = CommandType.STRING, description = "An optional account for the virtual machine. Must be used with domainId.")
    private String accountName;

    @Parameter(name = ApiConstants.DOMAIN_ID,
            type = CommandType.UUID,
            entityType = DomainResponse.class,
            description = "An optional domainId for the virtual machine. If the account parameter is used, domainId must also be used.")
    private Long domainId;

    @Parameter(name = ApiConstants.PROJECT_ID, type = CommandType.UUID, entityType = ProjectResponse.class, description = "Project ID for the service instance")
    private Long projectId;

    @Parameter(name = ApiConstants.NAME, type = CommandType.STRING, required = true, description = "Tungsten network Name")
    private String name;

    @Parameter(name = ApiConstants.TUNGSTEN_NETWORK_IPAM_UUID, type = CommandType.STRING, description = "Network Ipam UUID")
    private String networkIpamUUID;

    @Parameter(name = ApiConstants.TUNGSTEN_NETWORK_SUBNET_IP_PREFIX, type = CommandType.STRING, description = "Subnet ip prefix")
    private String subnetIpPrefix;

    @Parameter(name = ApiConstants.TUNGSTEN_NETWORK_SUBNET_IP_PREFIX_LEN, type = CommandType.INTEGER, description = "Subnet ip prefix length")
    private int subnetIpPrefixLength;

    @Parameter(name = ApiConstants.TUNGSTEN_NETWORK_DEFAULT_GATEWAY, type = CommandType.STRING, description = "Tungsten network default gateway")
    private String defaultGateway;

    @Parameter(name = ApiConstants.TUNGSTEN_NETWORK_ENABLE_DHC, type = CommandType.BOOLEAN, description = "Tungsten network enable dhcp")
    private boolean enableDHCP;

    @Parameter(name = ApiConstants.TUNGSTEN_DNS_NAME_SERVERS, type = CommandType.LIST, description = "Tungsten network DNS name servers")
    private List<String> dnsNameservers;

    @Parameter(name = ApiConstants.TUNGSTEN_IP_ALLOC_POOL_START, type = CommandType.STRING, description = "Tungsten network ip allocation pool start")
    private String ipAllocPoolStart;

    @Parameter(name = ApiConstants.TUNGSTEN_IP_ALLOC_POOL_END, type = CommandType.STRING, description = "Tungsten network ip allocation pool end")
    private String ipAllocPoolEnd;

    @Parameter(name = ApiConstants.TUNGSTEN_ADDR_FROM_START, type = CommandType.BOOLEAN, description = "Subnet ip prefix")
    private boolean addrFromStart;

    @Parameter(name = ApiConstants.TUNGSTEN_NETWORK_SUBNET_NAME, type = CommandType.STRING, description = "Tungsten network subnet name")
    private String subnetName;


    public String getName() {
        return name;
    }

    public String getNetworkIpamUUID() {
        return networkIpamUUID;
    }

    public String getSubnetIpPrefix() {
        return subnetIpPrefix;
    }

    public int getSubnetIpPrefixLength() {
        return subnetIpPrefixLength;
    }

    public String getDefaultGateway() {
        return defaultGateway;
    }

    public boolean isEnableDHCP() {
        return enableDHCP;
    }

    public List<String> getDnsNameservers() {
        return dnsNameservers;
    }

    public String getIpAllocPoolStart() {
        return ipAllocPoolStart;
    }

    public String getIpAllocPoolEnd() {
        return ipAllocPoolEnd;
    }

    public boolean isAddrFromStart() {
        return addrFromStart;
    }

    public String getSubnetName() {
        return subnetName;
    }

    @Inject
    TungstenManager tungstenManager;

    @Override
    public void create() throws ResourceAllocationException {
        VirtualNetwork network = tungstenManager.createTungstenNetwork(this);
        if(network != null) {
            setEntityId(1L);
            setEntityUuid(network.getUuid());
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to create network into tungsten.");
        }
    }

    @Override
    public String getEventType() {
        return EventTypes.EVENT_TUNGSTEN_NETWORK_CREATE;
    }

    @Override
    public String getEventDescription() {
        return "Create tungsten network";
    }

    @Override
    public void execute() throws ResourceUnavailableException, InsufficientCapacityException, ServerApiException, ConcurrentOperationException, ResourceAllocationException, NetworkRuleConflictException {
        try {
            VirtualNetwork network = (VirtualNetwork) tungstenManager.getTungstenObjectByUUID(VirtualNetwork.class, getEntityUuid());
            TungstenNetworkResponse response = TungstenResponseHelper.createTungstenNetworkResponse(network);
            response.setResponseName(getCommandName());
            setResponseObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        Long accountId = _accountService.finalyzeAccountId(accountName, domainId, projectId, true);
        if (accountId == null) {
            return CallContext.current().getCallingAccount().getId();
        }

        return accountId;
    }
}
