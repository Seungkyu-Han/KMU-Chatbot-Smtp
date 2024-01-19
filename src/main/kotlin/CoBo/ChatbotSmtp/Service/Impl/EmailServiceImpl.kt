package CoBo.ChatbotSmtp.Service.Impl

import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPatchVerificationCodeReq
import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPostVerificationCodeReq
import CoBo.ChatbotSmtp.Data.Entity.ValidEmail
import CoBo.ChatbotSmtp.Repository.ValidEmailRepository
import CoBo.ChatbotSmtp.Service.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class EmailServiceImpl(
    val validEmailRepository: ValidEmailRepository,

    @Value("\${kmu.mail.address}")
    val kmuEmailAddress: String,

    val javaMailSender: JavaMailSender
    ):EmailService {

    override fun postVerificationCode(emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {

        val splitEmail = emailPostVerificationCodeReq.email.split("@")

        if(splitEmail.size != 2)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        if(splitEmail.last() != kmuEmailAddress)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val code = verificationCode(emailPostVerificationCodeReq.email)

        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(emailPostVerificationCodeReq.email)
        helper.setSubject("인증번호다")
        helper.setText("인증번호 이거다: $code")

        javaMailSender.send(mimeMessage)

        validEmailRepository.save(ValidEmail(
            email = emailPostVerificationCodeReq.email,
            code = code,
            isValid = false
        ))

        return ResponseEntity(HttpStatus.OK)
    }

    override fun postVerificationCodeNot(emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {

        val splitEmail = emailPostVerificationCodeReq.email.split("@")

        if(splitEmail.size != 2)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        val code = verificationCode(emailPostVerificationCodeReq.email)

        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(emailPostVerificationCodeReq.email)
        helper.setSubject("인증번호다")
        helper.setText("인증번호 이거다: $code")

        javaMailSender.send(mimeMessage)

        validEmailRepository.save(ValidEmail(
            email = emailPostVerificationCodeReq.email,
            code = code,
            isValid = false
        ))

        return ResponseEntity(HttpStatus.OK)
    }

    override fun patchVerificationCode(emailPatchVerificationCodeReq: EmailPatchVerificationCodeReq): ResponseEntity<HttpStatus> {
        val validEmail:ValidEmail = validEmailRepository.findById(emailPatchVerificationCodeReq.email).get()

        if(validEmail.code != emailPatchVerificationCodeReq.code)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        validEmail.isValid = true

        validEmailRepository.save(validEmail)

        return ResponseEntity(HttpStatus.OK)
    }

    fun verificationCode(email: String): String {
        return try {
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDatetime = now.format(formatter)
            val combinedString = email + formattedDatetime
            val combinedBytes = combinedString.toByteArray()
            val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
            val hashBytes: ByteArray = messageDigest.digest(combinedBytes)
            val stringBuilder = StringBuilder()
            for (hashByte in hashBytes) stringBuilder.append(String.format("%02x", hashByte))
            stringBuilder.substring(0, 8).uppercase(Locale.getDefault())
        } catch (e: NoSuchAlgorithmException) {
            throw NoSuchAlgorithmException(e)
        }
    }
}