package CoBo.ChatbotSmtp.Data.Dto.Email.Req

import io.swagger.v3.oas.annotations.media.Schema

data class EmailPatchVerificationCodeReq(
    @Schema(description = "인증번호를 받은 이메일", example = "trust1204@kmu.ac.kr")
    val email: String,
    @Schema(description = "인증번호", example = "abcd1234")
    val code: String
)