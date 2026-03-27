package com.top.talent.management.service;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.dto.AccessTokenResponse;
import com.top.talent.management.exception.ApiException;
import com.top.talent.management.feign.AccessTokenClient;
import com.top.talent.management.service.impl.AccessTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AccessTokenServiceImplTest {

    @Mock
    private AccessTokenClient accessTokenClient;

    private AccessTokenServiceImpl accessEpamService;

    private static final String CLIENT_ID = "test-client-id";
    private static final String CLIENT_SECRET = "test-client-secret";
    private static final String AUDIENCE = "test-audience";
    private static final String CLIENT_CREDENTIALS_ACCESS_TOKEN = "client-credentials-access-token";
    private static final String TOKEN_EXCHANGE_ACCESS_TOKEN = "token-exchange-access-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accessEpamService = new AccessTokenServiceImpl(
                CLIENT_ID,
                CLIENT_SECRET,
                AUDIENCE,
                accessTokenClient
        );
    }

    @Test
    void testBuildBearerToken_Success() {
        AccessTokenResponse clientCredentialsResponse = new AccessTokenResponse();
        clientCredentialsResponse.setAccessToken(CLIENT_CREDENTIALS_ACCESS_TOKEN);

        AccessTokenResponse tokenExchangeResponse = new AccessTokenResponse();
        tokenExchangeResponse.setAccessToken(TOKEN_EXCHANGE_ACCESS_TOKEN);

        when(accessTokenClient.getAccessToken(anyMap()))
                .thenReturn(clientCredentialsResponse)
                .thenReturn(tokenExchangeResponse);

        String bearerToken = accessEpamService.buildBearerToken();

        assertEquals(Constants.BEARER + TOKEN_EXCHANGE_ACCESS_TOKEN, bearerToken);

        verify(accessTokenClient, times(2)).getAccessToken(anyMap());
    }


    @Test
    void testFetchExchangeToken_ThrowsException() {
        AccessTokenResponse clientCredentialsResponse = new AccessTokenResponse();
        clientCredentialsResponse.setAccessToken(CLIENT_CREDENTIALS_ACCESS_TOKEN);

        when(accessTokenClient.getAccessToken(anyMap()))
                .thenReturn(clientCredentialsResponse)
                .thenThrow(new RuntimeException("Error fetching token"));

        ApiException exception = assertThrows(ApiException.class, () -> {
            accessEpamService.buildBearerToken();
        });
        assertEquals("Unable to fetch token java.lang.RuntimeException: Error fetching token", exception.getMessage());
        verify(accessTokenClient, times(2)).getAccessToken(anyMap());
    }
}
