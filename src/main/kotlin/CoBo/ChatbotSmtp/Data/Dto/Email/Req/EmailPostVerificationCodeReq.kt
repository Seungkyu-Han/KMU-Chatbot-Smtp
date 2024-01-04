package CoBo.ChatbotSmtp.Data.Dto.Email.Req

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
data class EmailPostVerificationCodeReq(

    @JsonProperty("email")
    @Schema(description = "인증번호를 전송할 이메일", example = "trust1204@kmu.ac.kr")
    val email: String
)