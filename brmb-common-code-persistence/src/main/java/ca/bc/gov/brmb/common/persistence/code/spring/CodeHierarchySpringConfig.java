package ca.bc.gov.brmb.common.persistence.code.spring;

import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Import({
        CodePersistenceSpringConfig.class
})
public class CodeHierarchySpringConfig {

    private static final Logger logger = LoggerFactory.getLogger(CodeHierarchySpringConfig.class);

    public CodeHierarchySpringConfig() {
        logger.debug("<CodeHierarchySpringConfig");

        logger.debug(">CodeHierarchySpringConfig");
    }

    @Bean
    public List<CodeHierarchyConfig> codeHierarchyConfigs() {
        List<CodeHierarchyConfig> result = new ArrayList<>();
        return result;
    }
}
