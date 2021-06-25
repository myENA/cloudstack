// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.network.tungsten.api.command;

import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.NetworkRuleConflictException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.user.Account;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.BaseListCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.ZoneResponse;
import org.apache.cloudstack.network.tungsten.api.response.TungstenFabricFirewallPolicyResponse;
import org.apache.cloudstack.network.tungsten.service.TungstenService;
import org.apache.log4j.Logger;

import java.util.List;

import javax.inject.Inject;

@APICommand(name = ListTungstenFabricFirewallPolicyCmd.APINAME, description = "list Tungsten-Fabric firewall policy",
    responseObject = TungstenFabricFirewallPolicyResponse.class, requestHasSensitiveInfo = false,
    responseHasSensitiveInfo = false)
public class ListTungstenFabricFirewallPolicyCmd extends BaseListCmd {
    public static final Logger s_logger = Logger.getLogger(ListTungstenFabricFirewallPolicyCmd.class.getName());
    public static final String APINAME = "listTungstenFabricFirewallPolicy";

    @Inject
    TungstenService tungstenService;

    @Parameter(name = ApiConstants.ZONE_ID, type = CommandType.UUID, entityType = ZoneResponse.class, required = true, description = "the ID of zone")
    private Long zoneId;

    @Parameter(name = ApiConstants.APPLICATION_POLICY_SET_UUID, type = CommandType.STRING, description = "the uuid of Tungsten-Fabric application policy set")
    private String applicationPolicySetUuid;

    @Parameter(name = ApiConstants.FIREWALL_POLICY_UUID, type = CommandType.STRING, description = "the uuid of Tungsten-Fabric firewall policy")
    private String firewallPolicyUuid;

    @Override
    public void execute() throws ResourceUnavailableException, InsufficientCapacityException, ServerApiException,
        ConcurrentOperationException, ResourceAllocationException, NetworkRuleConflictException {
        List<TungstenFabricFirewallPolicyResponse> tungstenFabricFirewallPolicyResponseList =
            tungstenService.listTungstenFirewallPolicy(
            zoneId, applicationPolicySetUuid, firewallPolicyUuid);
        ListResponse<TungstenFabricFirewallPolicyResponse> listResponse = new ListResponse<>();
        listResponse.setResponses(tungstenFabricFirewallPolicyResponseList);
        listResponse.setResponseName(getCommandName());
        setResponseObject(listResponse);
    }

    @Override
    public String getCommandName() {
        return APINAME.toLowerCase() + BaseCmd.RESPONSE_SUFFIX;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }
}
