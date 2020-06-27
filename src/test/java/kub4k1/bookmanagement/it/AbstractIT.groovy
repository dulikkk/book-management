package kub4k1.bookmanagement.it

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AbstractIT extends Specification {

    @Autowired
    protected TestRestTemplate testRestTemplate

    @Autowired
    protected MongoTemplate mongoTemplate

    @LocalServerPort
    protected int randomServerPort

    protected String baseUrl

    protected HttpHeaders headers = new HttpHeaders()

    def setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
        baseUrl = "http://localhost:" + randomServerPort
    }
}
