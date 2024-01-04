package CoBo.ChatbotSmtp.Controller

import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPostVerificationCodeReq
import CoBo.ChatbotSmtp.Service.EmailService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
class EmailController(
    private val emailService: EmailService
) {

    @PostMapping("/verification-code")
    @Operation(summary = "인증번호 전송 API", description = "해당 이메일로 인증번호를 전송")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content())),
        ApiResponse(responseCode = "400", description = "이메일 형식이 올바르지 않습니다.", content = arrayOf(Content())),
        ApiResponse(responseCode = "401", description = "계명대학교 이메일이 아닙니다.", content = arrayOf(Content())),
        ApiResponse(responseCode = "501", description = "인증코드 생성 과정에서 오류가 발생했습니다.", content = arrayOf(Content()))
    )
    fun postVerificationCode(@RequestBody emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus>{
        return emailService.postVerificationCode(emailPostVerificationCodeReq)
    }
}