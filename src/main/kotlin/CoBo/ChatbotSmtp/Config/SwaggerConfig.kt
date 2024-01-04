package CoBo.ChatbotSmtp.Config

import io.swagger.v3.oas.models.OpenAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(io.swagger.v3.oas.models.info.Info().title("KMU-Chat bot 메일 서버"))
    }
}
