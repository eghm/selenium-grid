package com.thoughtworks.selenium.grid.remotecontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Launch a Self-registering Selenium Remote Control.
 */
public class SelfRegisteringRemoteControlLauncher {

    private static final Log LOGGER = LogFactory.getLog(SelfRegisteringRemoteControlLauncher.class);


    public static void main(String[] args) throws Exception {
        final SelfRegisteringRemoteControl server;
        final RegistrationInfo registrationInfo;
        final OptionParser.Options options;

        options = new OptionParser().parseOptions(args);
        if ("hostaddress".equals(options.host())) {
            try {
            	options.setHost(InetAddress.getLocalHost().getHostAddress());
            } catch (Exception e) {
                LOGGER.error("Unable to set Host Address.  Exception: " + e.getMessage() +
                		"  Set host with host parameter or resolve exception and run again.");
            }
        }
        registrationInfo = new RegistrationInfo(
                options.hubURL(), options.environment(), options.host(), options.port());
        server = new SelfRegisteringRemoteControl(registrationInfo,
                                                  options.hubPollerIntervalInSeconds());
        try {
            server.register();
            server.ensureUnregisterOnShutdown();
            server.launch(options.seleniumServerArgs());
        } catch (ConnectException e) {
            LOGGER.error("Could not contact the Selenium Hub at '" + server.registrationInfo().hubURL()
                    + "' : " + e.getMessage()
                    + ". Check that the Hub is running and check its status at "
                    + server.registrationInfo().hubURL() + "/console");
            throw e;
        }
    }

}
