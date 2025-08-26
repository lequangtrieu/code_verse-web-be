package codeverse.com.web_be.config.SystemConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    private String clientId = System.getenv("PAYOS_CLIENT_ID");
    private String apiKey = System.getenv("PAYOS_API_KEY");
    private String checksumKey = System.getenv("PAYOS_CHECKSUM_KEY");

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
