package com.IDP.FVN;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// We provide dummy properties here so the context loads successfully
// even without Docker environment variables present!
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.flyway.enabled=false"
})
class FvnApplicationTests {

    @Test
    void contextLoads() {
    }

}