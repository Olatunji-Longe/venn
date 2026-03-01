package com.olatunji.venn;

import com.olatunji.venn.common.RunProfile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(RunProfile.TEST)
class FundLoadingApplicationTests {

  @Test
  void contextLoads() {}
}
