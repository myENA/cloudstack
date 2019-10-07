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
package org.apache.cloudstack.api.command.admin.offering;

import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.ServiceOfferingResponse;

import com.cloud.offering.ServiceOffering;
import com.cloud.user.Account;

@APICommand(name = "updateServiceOffering", description = "Updates a service offering.", responseObject = ServiceOfferingResponse.class,
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class UpdateServiceOfferingCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(UpdateServiceOfferingCmd.class.getName());
    private static final String s_name = "updateserviceofferingresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////
    @Parameter(name = ApiConstants.ID,
               type = CommandType.UUID,
               entityType = ServiceOfferingResponse.class,
               required = true,
               description = "the ID of the service offering to be updated")
    private Long id;

    @Parameter(name = ApiConstants.DISPLAY_TEXT, type = CommandType.STRING, description = "the display text of the service offering to be updated")
    private String displayText;

    @Parameter(name = ApiConstants.NAME, type = CommandType.STRING, description = "the name of the service offering to be updated")
    private String serviceOfferingName;

    @Parameter(name = ApiConstants.SORT_KEY, type = CommandType.INTEGER, description = "sort key of the service offering, integer")
    private Integer sortKey;

    @Parameter(name = ApiConstants.BYTES_READ_RATE, type = CommandType.LONG, required = false, description = "bytes read rate of the disk offering, long")
    private Long diskBytesReadRate;

    @Parameter(name = ApiConstants.BYTES_READ_RATE_MAX, type = CommandType.LONG, required = false, description = "maximum bytes read rate of the disk offering, long")
    private Long diskBytesReadRateMax;

    @Parameter(name = ApiConstants.BYTES_READ_RATE_MAX_LENGTH, type = CommandType.LONG, required = false, description = "maximum bytes read rate length of the disk offering, long")
    private Long diskBytesReadRateMaxLength;

    @Parameter(name = ApiConstants.BYTES_WRITE_RATE, type = CommandType.LONG, required = false, description = "bytes write rate of the disk offering, long")
    private Long diskBytesWriteRate;

    @Parameter(name = ApiConstants.BYTES_WRITE_RATE_MAX, type = CommandType.LONG, required = false, description = "maximum bytes write rate of the disk offering, long")
    private Long diskBytesWriteRateMax;

    @Parameter(name = ApiConstants.BYTES_WRITE_RATE_MAX_LENGTH, type = CommandType.LONG, required = false, description = "maximum bytes write rate length of the disk offering, long")
    private Long diskBytesWriteRateMaxLength;

    @Parameter(name = ApiConstants.IOPS_READ_RATE, type = CommandType.LONG, required = false, description = "iops read rate of the disk offering, long")
    private Long diskIopsReadRate;

    @Parameter(name = ApiConstants.IOPS_READ_RATE_MAX, type = CommandType.LONG, required = false, description = "maximum iops read rate of the disk offering, long")
    private Long diskIopsReadRateMax;

    @Parameter(name = ApiConstants.IOPS_READ_RATE_MAX_LENGTH, type = CommandType.LONG, required = false, description = "maximum iops read rate length of the disk offering, long")
    private Long diskIopsReadRateMaxLength;

    @Parameter(name = ApiConstants.IOPS_WRITE_RATE, type = CommandType.LONG, required = false, description = "iops write rate of the disk offering, long")
    private Long diskIopsWriteRate;

    @Parameter(name = ApiConstants.IOPS_WRITE_RATE_MAX, type = CommandType.LONG, required = false, description = "maximum iops write rate of the disk offering, long")
    private Long diskIopsWriteRateMax;

    @Parameter(name = ApiConstants.IOPS_WRITE_RATE_MAX_LENGTH, type = CommandType.LONG, required = false, description = "maximum iops write rate length of the disk offering, long")
    private Long diskIopsWriteRateMaxLength;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getDisplayText() {
        return displayText;
    }

    public Long getId() {
        return id;
    }

    public String getServiceOfferingName() {
        return serviceOfferingName;
    }

    public Integer getSortKey() {
        return sortKey;
    }

    public Long getDiskBytesReadRate() {
        return diskBytesReadRate;
    }

    public Long getDiskBytesReadRateMax() {
        return diskBytesReadRateMax;
    }

    public Long getDiskBytesReadRateMaxLength() {
        return diskBytesReadRateMaxLength;
    }

    public Long getDiskBytesWriteRate() {
        return diskBytesWriteRate;
    }

    public Long getDiskBytesWriteRateMax() {
        return diskBytesWriteRateMax;
    }

    public Long getDiskBytesWriteRateMaxLength() {
        return diskBytesWriteRateMaxLength;
    }

    public Long getDiskIopsReadRate() {
        return diskIopsReadRate;
    }

    public Long getDiskIopsReadRateMax() {
        return diskIopsReadRateMax;
    }

    public Long getDiskIopsReadRateMaxLength() {
        return diskIopsReadRateMaxLength;
    }

    public Long getDiskIopsWriteRate() {
        return diskIopsWriteRate;
    }

    public Long getDiskIopsWriteRateMax() {
        return diskIopsWriteRateMax;
    }

    public Long getDiskIopsWriteRateMaxLength() {
        return diskIopsWriteRateMaxLength;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }

    @Override
    public void execute() {
        //Note
        //Once an offering is created, we cannot update the domainId field (keeping consistent with zones logic)
        ServiceOffering result = _configService.updateServiceOffering(this);
        if (result != null) {
            ServiceOfferingResponse response = _responseGenerator.createServiceOfferingResponse(result);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to update service offering");
        }
    }
}
