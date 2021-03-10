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
package org.apache.cloudstack.network.tungsten.agent.api;

public class CreateTungstenFloatingIpCommand extends TungstenCommand {
    private final String projectFqn;
    private final String networkUuid;
    private final String fipName;
    private final String name;
    private final String publicIp;

    public CreateTungstenFloatingIpCommand(final String projectFqn, final String networkUuid, final String fipName,
        final String name, final String publicIp) {
        this.projectFqn = projectFqn;
        this.networkUuid = networkUuid;
        this.fipName = fipName;
        this.name = name;
        this.publicIp = publicIp;
    }

    public String getProjectFqn() {
        return projectFqn;
    }

    public String getNetworkUuid() {
        return networkUuid;
    }

    public String getFipName() {
        return fipName;
    }

    public String getName() {
        return name;
    }

    public String getPublicIp() {
        return publicIp;
    }
}
