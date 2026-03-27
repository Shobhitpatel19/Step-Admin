package com.top.talent.management.service.impl;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.AccessTokenResponse;
import com.top.talent.management.exception.ApiException;
import com.top.talent.management.feign.AccessTokenClient;
import com.top.talent.management.service.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.top.talent.management.constants.AccessTokenConstants.AUDIENCE;
import static com.top.talent.management.constants.AccessTokenConstants.CLIENT_CREDENTIALS_GRANT_TYPE;
import static com.top.talent.management.constants.AccessTokenConstants.CLIENT_ID;
import static com.top.talent.management.constants.AccessTokenConstants.CLIENT_SECRET;
import static com.top.talent.management.constants.AccessTokenConstants.GRANT_TYPE;
import static com.top.talent.management.constants.AccessTokenConstants.SUBJECT_TOKEN;
import static com.top.talent.management.constants.AccessTokenConstants.SUBJECT_TOKEN_TYPE;
import static com.top.talent.management.constants.AccessTokenConstants.TOKEN_EXCHANGE_GRANT_TYPE;
import static com.top.talent.management.constants.AccessTokenConstants.TOKEN_TYPE_ACCESS;


@Service
@Slf4j
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenClient accessTokenClient;

    private final Map<String, String> clientCredentialsRequest;
    private final Map<String, String> tokenExchangeRequestTemplate;

    public AccessTokenServiceImpl(@Value("${spring.security.oauth2.client.registration.epam.client-id}") String clientId,
                                  @Value("${spring.security.oauth2.client.registration.epam.client-secret}") String clientSecret,
                                  @Value("${api.epam.client-id}") String audience,
                                  AccessTokenClient accessTokenClient) {
        this.accessTokenClient = accessTokenClient;

        this.clientCredentialsRequest = Map.of(
                GRANT_TYPE, CLIENT_CREDENTIALS_GRANT_TYPE,
                CLIENT_ID, clientId,
                CLIENT_SECRET, clientSecret);

        this.tokenExchangeRequestTemplate = Map.of(
                GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE,
                CLIENT_ID, clientId,
                CLIENT_SECRET, clientSecret,
                SUBJECT_TOKEN_TYPE, TOKEN_TYPE_ACCESS,
                AUDIENCE, audience
        );
        log.info("AccessEpamServiceImpl initialized with clientId: {}, audience: {}", clientId, audience);

    }

    @Override
    public String buildBearerToken() {
        return Constants.BEARER + fetchExchangeToken();
    }

    private String fetchExchangeToken() {
        try {
            log.info("Fetching client credentials token...");
            AccessTokenResponse clientCredentialsResponse = accessTokenClient.getAccessToken(clientCredentialsRequest);
            String accessToken = clientCredentialsResponse.getAccessToken();

            log.info("Fetching token exchange token...");
            Map<String, String> tokenExchangeRequest = new HashMap<>(tokenExchangeRequestTemplate);
            tokenExchangeRequest.put(SUBJECT_TOKEN, accessToken);
            AccessTokenResponse tokenExchangeResponse = accessTokenClient.getAccessToken(tokenExchangeRequest);

            log.debug("Token exchange token fetched successfully");
            return tokenExchangeResponse.getAccessToken();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching tokens", e);
            throw new ApiException(ErrorMessages.UNABLE_TOKEN_FETCH + e);
        }
    }
}
