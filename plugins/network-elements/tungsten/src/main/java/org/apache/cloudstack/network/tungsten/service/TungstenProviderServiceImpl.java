package org.apache.cloudstack.network.tungsten.service;

import com.cloud.dc.DataCenterVO;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.host.DetailVO;
import com.cloud.host.Host;
import com.cloud.host.dao.HostDetailsDao;
import com.cloud.network.TungstenProvider;
import com.cloud.network.dao.TungstenProviderDao;
import com.cloud.network.element.TungstenProviderVO;
import com.cloud.resource.ResourceManager;
import com.cloud.utils.db.Transaction;
import com.cloud.utils.db.TransactionCallback;
import com.cloud.utils.db.TransactionStatus;
import com.cloud.utils.exception.CloudRuntimeException;
import com.google.common.collect.Lists;
import org.apache.cloudstack.network.tungsten.api.command.ConfigTungstenPublicNetworkCmd;
import org.apache.cloudstack.network.tungsten.api.command.CreateTungstenProviderCmd;
import org.apache.cloudstack.network.tungsten.api.response.TungstenProviderResponse;
import org.apache.cloudstack.network.tungsten.resource.TungstenResource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TungstenProviderServiceImpl implements TungstenProviderService {

    private static final Logger s_logger = Logger.getLogger(TungstenProviderServiceImpl.class);

    @Inject
    TungstenProviderDao _tungstenProviderDao;
    @Inject
    DataCenterDao _zoneDao;
    @Inject
    ResourceManager _resourceMgr;
    @Inject
    HostDetailsDao _hostDetailsDao;

    @Override
    public List<Class<?>> getCommands() {
        return Lists.<Class<?>>newArrayList(CreateTungstenProviderCmd.class, ConfigTungstenPublicNetworkCmd.class);
    }

    @Override
    public TungstenProvider addProvider(CreateTungstenProviderCmd cmd) {
        TungstenProviderVO tungstenProvider;
        final Long zoneId = cmd.getZoneId();
        final String name = cmd.getName();
        final String hostname = cmd.getHostname();
        final String port = cmd.getPort();
        final String vrouter = cmd.getVrouter();
        final String vrouterPort = cmd.getVrouterPort();

        TungstenResource tungstenResource = new TungstenResource();

        DataCenterVO zone = _zoneDao.findById(zoneId);
        String zoneName;
        if (zone != null) {
            zoneName = zone.getName();
        } else {
            zoneName = String.valueOf(zoneId);
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("guid", UUID.randomUUID().toString());
        params.put("zoneId", zoneName);
        params.put("name", "TungstenDevice - " + cmd.getName());
        params.put("hostname", cmd.getHostname());
        params.put("port", cmd.getPort());
        params.put("vrouter", cmd.getVrouter());
        params.put("vrouterPort", cmd.getVrouterPort());

        Map<String, Object> hostdetails = new HashMap<String, Object>();
        hostdetails.putAll(params);

        try {
            tungstenResource.configure(cmd.getHostname(), hostdetails);
            final Host host = _resourceMgr.addHost(zoneId, tungstenResource, Host.Type.L2Networking, params);
            if (host != null) {
                tungstenProvider = Transaction.execute(new TransactionCallback<TungstenProviderVO>() {
                    @Override
                    public TungstenProviderVO doInTransaction(TransactionStatus status) {
                        TungstenProviderVO tungstenProviderVO = new TungstenProviderVO(zoneId, name, host.getId(), port,
                            hostname, vrouter, vrouterPort);
                        _tungstenProviderDao.persist(tungstenProviderVO);

                        DetailVO detail = new DetailVO(host.getId(), "tungstendeviceid",
                            String.valueOf(tungstenProviderVO.getId()));
                        _hostDetailsDao.persist(detail);

                        return tungstenProviderVO;
                    }
                });
            } else {
                throw new CloudRuntimeException("Failed to add Tungsten provider due to internal error.");
            }
        } catch (ConfigurationException e) {
            throw new CloudRuntimeException(e.getMessage());
        }

        return tungstenProvider;
    }

    @Override
    public TungstenProviderResponse getTungstenProvider(long zoneId) {
        TungstenProviderVO tungstenProvider = _tungstenProviderDao.findByZoneId(zoneId);
        if (tungstenProvider != null)
            return createTungstenProviderResponse(tungstenProvider);
        else
            return null;
    }

    public TungstenProviderResponse createTungstenProviderResponse(TungstenProviderVO tungstenProviderVO) {
        TungstenProviderResponse tungstenProviderResponse = new TungstenProviderResponse();
        tungstenProviderResponse.setHostname(tungstenProviderVO.getHostname());
        tungstenProviderResponse.setName(tungstenProviderVO.getProviderName());
        tungstenProviderResponse.setPort(tungstenProviderVO.getPort());
        tungstenProviderResponse.setUuid(tungstenProviderVO.getUuid());
        tungstenProviderResponse.setVrouter(tungstenProviderVO.getVrouter());
        tungstenProviderResponse.setVrouterPort(tungstenProviderVO.getVrouterPort());
        tungstenProviderResponse.setObjectName("tungstenProvider");
        return tungstenProviderResponse;
    }
}
