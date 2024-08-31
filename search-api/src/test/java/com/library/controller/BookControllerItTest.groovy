package com.library.controller

import com.library.controller.request.SearchRequest
import com.library.controller.response.PageResult
import com.library.controller.response.SearchResponse
import com.library.feign.KakaoClient
import com.library.service.BookQueryService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerItTest extends Specification {
    @Autowired
    MockMvc mockMvc

    @SpringBean
    BookQueryService bookQueryService = Mock()

    // 정상인자, 잘못된 인자 (3가지)
    def "정상인자로 요청시 성공한다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: 10)

        and:
        bookQueryService.search(*_) >> new PageResult<>(1, 10, 10, [Mock(SearchResponse)])

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.totalElements').value(10))
                .andExpect(jsonPath('$.page').value(1))
                .andExpect(jsonPath('$.size').value(10))
                .andExpect(jsonPath('$.contents').isArray())
    }

    def "query가 비어있을때 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "", page: 1, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("입력은 비어있을 수 없습니다."))
    }

    def "query가 50을 초과하면 BadRequest 응답반환된다."() {
        given:
        def largeQuery = "A" * 51
        def request = new SearchRequest(query: largeQuery, page: 1, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("query는 최대 50자를 초과할 수 없습니다."))
    }

    def "page가 비어있을때 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: null, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지 번호는 필수입니다."))
    }

    def "page가 0과 음수일경우에 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: pageParam, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지번호는 1이상이어야 합니다."))

        where:
        pageParam << [0, -1, -10]  // 여러 테스트 케이스를 제공
    }

    def "page가 10000이상일경우에 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 10001, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지번호는 10000이하여야 합니다."))
    }

    def "size가 비어있을때 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: null)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지 사이즈는 필수입니다."))
    }

    def "size가 0과 음수일경우에 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: sizeParam)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지크기는 1이상이어야 합니다."))

        where:
        sizeParam << [0, -1]
    }

    def "size가 50을 초과하면 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: 51)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지크기는 50이하여야 합니다."))
    }
}
