package CoBo.ChatbotSmtp.Controller

import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPatchVerificationCodeReq
import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPostVerificationCodeReq
import CoBo.ChatbotSmtp.Service.EmailService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/verification-code-not")
    @Operation(summary = "인증번호 전송 API(계명대학교 이메일 아니어도 가능)", description = "해당 이메일로 인증번호를 전송")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content())),
        ApiResponse(responseCode = "400", description = "이메일 형식이 올바르지 않습니다.", content = arrayOf(Content())),
        ApiResponse(responseCode = "501", description = "인증코드 생성 과정에서 오류가 발생했습니다.", content = arrayOf(Content()))
    )
    fun postVerificationCodeNot(@RequestBody emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus>{
        return emailService.postVerificationCodeNot(emailPostVerificationCodeReq)
    }

    @GetMapping("/check")
    @Operation(summary = "인증번호 가능 체크 API")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "가능"),
        ApiResponse(responseCode = "400", description = "불가능")
    )
    fun getCheck(): ResponseEntity<HttpStatus>{
        return emailService.getCheck()
    }

    @PatchMapping("/verification-code")
    @Operation(summary = "인증번호 확인 API", description = "해당 이메일을 유효성을 확인")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content())),
        ApiResponse(responseCode = "401", description = "인증번호가 맞지 않습니다.", content = arrayOf(Content())),
        ApiResponse(responseCode = "404", description = "요청한 적 없는 이메일입니다.", content = arrayOf(Content()))
    )
    fun patchVerificationCode(@RequestBody emailPatchVerificationCodeReq: EmailPatchVerificationCodeReq): ResponseEntity<HttpStatus>{
        return emailService.patchVerificationCode(emailPatchVerificationCodeReq)
    }

    @PostMapping("/register")
    @Operation(summary = "메일 전송 API", description = "가입 후 메일 전송, 계명대학교 메일만 가능")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content()))
    )
    fun register(@RequestBody emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {
        return emailService.register(emailPostVerificationCodeReq)
    }

    @PostMapping("/register-not")
    @Operation(summary = "메일 전송 API", description = "가입 후 메일 전송")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content()))
    )
    fun registerNot(@RequestBody emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {
        return emailService.registerNot(emailPostVerificationCodeReq)
    }
}