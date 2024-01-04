package CoBo.ChatbotSmtp.Controller.ExceptionHandler

import CoBo.ChatbotSmtp.Controller.EmailController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.security.NoSuchAlgorithmException

@RestControllerAdvice(basePackageClasses = [EmailController::class])
class EmailExceptionHandler {

    @ExceptionHandler(NoSuchAlgorithmException::class)
    fun emailNoSuchAlgorithmExceptionHandler(): ResponseEntity<String>{
        return ResponseEntity("인증코드 생성 과정에서 오류가 발생했습니다.", HttpStatus.NOT_IMPLEMENTED)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun emailNullPointerExceptionHandler(): ResponseEntity<String>{
        return ResponseEntity("없는 정보입니다.", HttpStatus.NOT_FOUND)
    }
}