package CoBo.ChatbotSmtp.Service

import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPostVerificationCodeReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

interface EmailService {

    fun postVerificationCode(emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus>
}