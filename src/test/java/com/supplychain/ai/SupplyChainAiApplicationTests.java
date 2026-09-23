package com.supplychain.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: fails fast if any bean is misconfigured (e.g. a missing
 * dependency, a broken @Value property, a bean that can't be wired) rather
 * than only finding out at deploy time. This is exactly the kind of check
 * that would have caught the original "security starter with no
 * SecurityConfig" problem immediately.
 */
@SpringBootTest
@ActiveProfiles("test")
class SupplyChainAiApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty: just proves the full application context starts.
    }
}
