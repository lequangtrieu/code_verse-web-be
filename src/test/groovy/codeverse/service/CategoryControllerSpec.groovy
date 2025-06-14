package codeverse.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import codeverse.com.web_be.controller.CategoryController
import codeverse.com.web_be.dto.response.CategoryResponse.CategoryResponse
import codeverse.com.web_be.entity.Category
import codeverse.com.web_be.repository.CategoryRepository
import codeverse.com.web_be.service.CategoryService.CategoryServiceImpl
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest
@Unroll
@TestExecutionListeners([DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class])
class CategoryControllerSpec extends Specification {

    @InjectMocks
    CategoryController categoryController;

    @MockBean
    Appender<ILoggingEvent> mockAppender;

    @Autowired
    private ApplicationContext ctx;

    @Captor
    ArgumentCaptor<LoggingEvent> captorLoggingEvent

    def categoryRepository = Mock(CategoryRepository)

    def setup() {
        log.info("🛠️  [Setup] Preparing test case...")

        def logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
        logger.addAppender(mockAppender)

        reset(mockAppender)

        categoryController = ctx.getAutowireCapableBeanFactory().createBean(categoryController.class)

        MockitoAnnotations.openMocks(this)
    }

    def cleanup() {
        def logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
        logger.detachAppender(mockAppender)
    }

    void checkLogMessage(Level level, String logMessage) {
        Mockito.verify(mockAppender, Mockito.atLeast(1)).doAppend(captorLoggingEvent.capture())
        def loggingEvent = captorLoggingEvent.getAllValues()
        assert loggingEvent.toString().concat(logMessage)
        for (int i = 0; i <= loggingEvent.size(); i++) {
            if (loggingEvent[i].toString().contains(logMessage)) {
                assert  loggingEvent[i].getLevel() == level
                break
            }
        }
    }

    def "No: #no → getAllCategories returns #expectedSize result(s) when repo returns #inputSize categories"() {
        given:
        def categories = buildCategories(inputSize)
        categoryRepository.findAllByIsDeletedFalse() >> categories

        when:
        List<CategoryResponse> responses = categoryService.getAllCategories()

        then:
        responses.size() == expectedSize
        responses*.name == categories*.name

        and:
        checkLogMessage(Level.INFO, "✅ $no - Repo returns $inputSize → Service returned ${responses.size()} items.")

        where:
        no       | inputSize || expectedSize
        "case01" | 0         || 0
        "case02" | 1         || 1
        "case03" | 3         || 3
    }

    private static List<Category> buildCategories(int count) {
        return (0..<count).collect { i ->
            Category.builder()
                    .id((long) (i + 1))
                    .name("Category ${i + 1}")
                    .isDeleted(false)
                    .build()
        }
    }
}
