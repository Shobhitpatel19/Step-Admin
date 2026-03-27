package com.top.talent.management.feign;

import com.top.talent.management.dto.ApiProfileResponse;
import com.top.talent.management.dto.AssessmentDetailsResponse;
import com.top.talent.management.dto.BenchHistoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "epamApiClient", url = "${api.epam.url}")
public interface ApiClient {

    @GetMapping("/profiles-full-info/search")
    ApiProfileResponse fetchUserDetails(
            @RequestParam("q") String query,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    );

    @GetMapping("/assessment-requests/search")
    AssessmentDetailsResponse fetchUserAssessmentRequests(
            @RequestParam("q") String query,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    );

    @GetMapping("/bench-history/search")
    BenchHistoryResponse fetchBenchHistory(
            @RequestParam("q") String query,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    );

}
