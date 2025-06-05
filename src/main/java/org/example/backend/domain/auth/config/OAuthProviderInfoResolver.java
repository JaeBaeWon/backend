package org.example.backend.domain.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthProviderInfoResolver {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public ClientRegistration getRegistration(String provider) {
        return ((InMemoryClientRegistrationRepository) clientRegistrationRepository)
                .findByRegistrationId(provider);
    }

    public String getClientId(String provider) {
        return getRegistration(provider).getClientId();
    }

    public String getClientSecret(String provider) {
        return getRegistration(provider).getClientSecret();
    }

    public String getRedirectUri(String provider) {
        return getRegistration(provider).getRedirectUri();
    }

    public String getTokenUri(String provider) {
        return getRegistration(provider).getProviderDetails().getTokenUri();
    }

    public String getUserInfoUri(String provider) {
        return getRegistration(provider).getProviderDetails().getUserInfoEndpoint().getUri();
    }
}

