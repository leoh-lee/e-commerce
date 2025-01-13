package kr.hhplus.be.server.support;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testcontainers.shaded.com.google.common.base.CaseFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;

@Component
public class DynamicEntityCleanupListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        EntityManager em = testContext.getApplicationContext().getBean(EntityManager.class);
        PlatformTransactionManager transactionManager = testContext.getApplicationContext().getBean(PlatformTransactionManager.class);
        
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        
        try {
            em.flush();

            List<String> tableNames = em.getMetamodel().getEntities().stream()
                    .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                    .map(e -> {
                        Table tableMetaData = e.getJavaType().getAnnotation(Table.class);
                        String entityName = tableMetaData != null && tableMetaData.name() != null ? tableMetaData.name() : e.getName();
                        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityName);
                    })
                    .toList();

            // MySQL용 foreign key check 비활성화
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

            for (String tableName : tableNames) {
                em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                // MySQL에서는 AUTO_INCREMENT를 1로 리셋
                em.createNativeQuery("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1").executeUpdate();
            }

            // MySQL용 foreign key check 활성화
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            transactionManager.commit(status);
        } catch (TransactionException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
