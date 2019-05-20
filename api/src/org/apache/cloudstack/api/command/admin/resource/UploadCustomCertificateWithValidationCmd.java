package org.apache.cloudstack.api.command.admin.resource;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Map;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.CustomCertificateResponse;
import org.apache.log4j.Logger;

import com.cloud.event.EventTypes;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.NetworkRuleConflictException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.user.Account;

@APICommand(name = "uploadCustomCertificateWithValidation", responseObject = CustomCertificateResponse.class, description = "Uploads a custom certificate for the console proxy VMs to use for SSL using an endpoint with proper validation. "
        + "Can be used to upload a single certificate signed by a known CA. Can also be used, through multiple calls, to upload a "
        + "chain of certificates from CA to the custom certificate itself.", requestHasSensitiveInfo = true, responseHasSensitiveInfo = false)
public class UploadCustomCertificateWithValidationCmd extends BaseAsyncCmd {

    public static final Logger s_logger = Logger.getLogger(UploadCustomCertificateCmd.class.getName());
    private static final String s_name = "uploadcustomcertificateresponse";

    @Parameter(name = ApiConstants.ROOT_CERTIFICATE, type = CommandType.STRING, required = false, description = "The root certificate to be uploaded.", length = 65535)
    private String rootCertificate;

    @Parameter(name = ApiConstants.INTERMIDIATE_CERTIFICATES, type = CommandType.MAP, required = false, description = "The list of intermediate certificates to be uploaded.")
    private Map<Integer, String> intermediateCertificates;

    @Parameter(name = ApiConstants.SERVER_CERTIFICATE, type = CommandType.STRING, required = true, description = "The server certificate to be uploaded.", length = 65535)
    private String serverCertificate;

    @Parameter(name = ApiConstants.PRIVATE_KEY, type = CommandType.STRING, required = true, description = "The private key for the attached certificate.", length = 65535)
    private String privateKey;

    @Parameter(name = ApiConstants.DOMAIN_SUFFIX, type = CommandType.STRING, required = true, description = "DNS domain suffix that the certificate is granted for.")
    private String domainSuffix;

    public String getRootCertificate() {
        return rootCertificate;
    }

    public Map<Integer, String> getIntermediateCertificates() {
        return intermediateCertificates;
    }

    public String getServerCertificate() {
        return serverCertificate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getDomainSuffix() {
        return domainSuffix;
    }

    @Override
    public String getEventType() {
        return EventTypes.EVENT_UPLOAD_CUSTOM_CERTIFICATE;
    }

    @Override
    public String getEventDescription() {
        return ("Uploading custom certificate to the db with validation, and applying it to all the cpvms in the system");
    }

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM; // no account info given, parent this command to SYSTEM so ERROR events are tracked
    }

    @Override
    public void execute() throws ResourceUnavailableException, InsufficientCapacityException, ServerApiException, ConcurrentOperationException, ResourceAllocationException,
            NetworkRuleConflictException {

        String result = null;
        try {
            result = _mgr.uploadCertificateWithValidation(this);
        } catch (KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
        }
        if (result != null) {
            CustomCertificateResponse response = new CustomCertificateResponse();
            response.setResponseName(getCommandName());
            response.setResultMessage(result);
            response.setObjectName("customcertificate");
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to upload custom certificate");
        }
    }
}