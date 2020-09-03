package org.apache.cloudstack.network.tungsten.api.response;

import com.cloud.network.TungstenProvider;
import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseResponse;
import org.apache.cloudstack.api.EntityReference;

@EntityReference(value = {TungstenProvider.class})
public class TungstenProviderResponse extends BaseResponse {

    @SerializedName(ApiConstants.NAME)
    @Param(description = "tungsten provider name")
    private String name;

    @SerializedName(ApiConstants.UUID)
    @Param(description = "tungsten provider uuid")
    private String uuid;

    @SerializedName(ApiConstants.TUNGSTEN_PROVIDER_HOSTNAME)
    @Param(description = "tungsten provider hostname")
    private String hostname;

    @SerializedName(ApiConstants.TUNGSTEN_PROVIDER_PORT)
    @Param(description = "tungsten provider port")
    private String port;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
