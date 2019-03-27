package com.ustadmobile.port.sharedse.networkmanager;

/**
 * Class which defines WiFi Direct group in a cross platform way.
 *
 * @author kileha3
 */

public class WiFiDirectGroupBle{

    private String ssid;

    private String passphrase;

    private int port;

    private String endpoint;

    private String ipAddress;

    /**
     * Create Wi-Fi Direct group
     * @param ssid Group SSID
     * @param passphrase Group passphrase
     */
    public WiFiDirectGroupBle(String ssid, String passphrase) {
        this.ssid = ssid;
        this.passphrase = passphrase;
    }

    /**
     * @return WiFi direct group SSID
     */
    public String getSsid() {
        return ssid;
    }

    /**@return Wifi direct group passphrase
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * @return current endpoint to serve requests from peer devices
     */
    public String getEndpoint() {
        return endpoint;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Setting endpoint which will serve peer requests
     * @param endpoint group endpoint
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
