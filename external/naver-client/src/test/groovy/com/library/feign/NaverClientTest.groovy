package com.library.feign

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.test.context.ActiveProfiles
import spock.lang.Ignore
import spock.lang.Specification

@Ignore /* 외부 API는 항상 똑같은 테스트 결과를 제시하지 못함 -> ignore 처리 */
@SpringBootTest(classes = NaverClientIntegrationTest.TestConfig.class)
@ActiveProfiles("test")
class NaverClientIntegrationTest extends Specification {
    @EnableAutoConfiguration
    @EnableFeignClients(clients = NaverClient.class)
    static class TestConfig{}

    @Autowired
    NaverClient naverClient

    def "naver 호출"() {
        given:
        when:
        def response = naverClient.search("HTTP", 1, 10)

        then:
        response.total == 31
    }
}
