package me.ykrank.s1next.data.pref;


/**
 * A manager manage the network preferences that are associated with settings.
 */
public final class NetworkPreferencesManager {

    private final NetworkPreferencesRepository preferencesRepository;

    public NetworkPreferencesManager(NetworkPreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    public boolean isForceBaseUrlEnable() {
        return preferencesRepository.isForceBaseUrlEnable();
    }

    public void setForceBaseUrl(String baseUrl) {
        preferencesRepository.setForceBaseUrl(baseUrl);
    }

    public String getForceBaseUrl() {
        return preferencesRepository.getForceBaseUrl();
    }

    public void setAutoCheckBaseUrl(boolean autoCheck) {
        preferencesRepository.setAutoCheckBaseUrl(autoCheck);
    }

    public boolean isAutoCheckBaseUrl() {
        return preferencesRepository.isAutoCheckBaseUrl();
    }

    public boolean isForceHostIpEnable() {
        return preferencesRepository.isForceHostIpEnable();
    }

    /**
     * Force use this ip, disable if null
     */
    public String getForceHostIp() {
        return preferencesRepository.getForceHostIp();
    }
}
