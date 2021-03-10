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
package com.cloud.projects;

import java.util.List;

import com.cloud.user.Account;

public interface ProjectManager extends ProjectService {
    boolean canAccessProjectAccount(Account caller, long accountId);

    boolean canModifyProjectAccount(Account caller, long accountId);

    boolean deleteAccountFromProject(long projectId, long accountId);

    List<Long> listPermittedProjectAccounts(long accountId);

    boolean projectInviteRequired();

    boolean allowUserToCreateProject();

    boolean deleteProject(Account caller, long callerUserId, ProjectVO project);

    long getInvitationTimeout();

    public static final String MESSAGE_CREATE_TUNGSTEN_PROJECT_EVENT = "Message.CreateTungstenProject.Event";
    public static final String MESSAGE_DELETE_TUNGSTEN_PROJECT_EVENT = "Message.DeleteTungstenProject.Event";

}
