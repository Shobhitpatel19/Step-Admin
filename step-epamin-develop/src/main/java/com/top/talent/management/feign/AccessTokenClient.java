package com.top.talent.management.feign;

import com.top.talent.management.dto.AccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "accessepam", url = "${access.epam.url}")
public interface AccessTokenClient {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AccessTokenResponse getAccessToken(@RequestBody Map<String, String> body);

}
