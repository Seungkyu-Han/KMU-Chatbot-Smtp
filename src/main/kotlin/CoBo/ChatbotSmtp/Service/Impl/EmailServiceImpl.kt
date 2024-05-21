package CoBo.ChatbotSmtp.Service.Impl

import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPatchVerificationCodeReq
import CoBo.ChatbotSmtp.Data.Dto.Email.Req.EmailPostVerificationCodeReq
import CoBo.ChatbotSmtp.Data.Entity.ValidEmail
import CoBo.ChatbotSmtp.Repository.UserRepository
import CoBo.ChatbotSmtp.Repository.ValidEmailRepository
import CoBo.ChatbotSmtp.Service.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.abs


@Service
class EmailServiceImpl(
    val validEmailRepository: ValidEmailRepository,
    val userRepository: UserRepository,
    @Value("\${kmu.mail.address}")
    val kmuEmailAddress: String,

    val javaMailSender: JavaMailSender
    ):EmailService {
    val emailLast = kmuEmailAddress.split(",")

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    @Volatile
    private var flag: Boolean = true

    override fun postVerificationCode(emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {

        val splitEmail = emailPostVerificationCodeReq.email.split("@")

        if(splitEmail.size != 2)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        if(userRepository.existsByEmail(emailPostVerificationCodeReq.email))
            return ResponseEntity(HttpStatus.CONFLICT)

        if(!emailLast.contains(splitEmail.last()))
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val code = verificationCode(emailPostVerificationCodeReq.email)

        println("REQUEST EMAIL: ${emailPostVerificationCodeReq.email}, CODE: $code, TIME: ${LocalDateTime.now()}")

        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(emailPostVerificationCodeReq.email)
        helper.setSubject("계명대학교 챗봇 인증번호입니다.")
        helper.setText(mailContent(code), true)
        helper.setFrom("크무톡톡 <kmutoktok@gmail.com>")

        try{
            javaMailSender.send(mimeMessage)
        }catch (mailSendException: MailSendException){
            flag = false
            scheduler.schedule({
                flag = true
            }, 1, TimeUnit.DAYS)
            println("FAIL TO SEND EMAIL: ${emailPostVerificationCodeReq.email}, CODE: $code, TIME: ${LocalDateTime.now()}")
            return ResponseEntity(HttpStatus.BAD_GATEWAY)
        }

        println("SEND EMAIL: ${emailPostVerificationCodeReq.email}, CODE: $code, TIME: ${LocalDateTime.now()}")
        println()

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
        helper.setSubject("계명대학교 챗봇 인증번호입니다.")
        helper.setText(mailContent(code), true)
        helper.setFrom("크무톡톡 <kmutoktok@gmail.com>")

        try{
            javaMailSender.send(mimeMessage)
        }catch (mailSendException: MailSendException){
            flag = false
            scheduler.schedule({
                flag = true
            }, 1, TimeUnit.DAYS)
        }

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

    override fun getCheck(): ResponseEntity<HttpStatus> {
        return if(flag)
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    override fun register(emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {
        val splitEmail = emailPostVerificationCodeReq.email.split("@")

        if(splitEmail.size != 2)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        if(userRepository.existsByEmail(emailPostVerificationCodeReq.email))
            return ResponseEntity(HttpStatus.CONFLICT)

        if(!emailLast.contains(splitEmail.last()))
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val code = verificationCode(emailPostVerificationCodeReq.email)

        println("REQUEST EMAIL: ${emailPostVerificationCodeReq.email}, CODE: $code, TIME: ${LocalDateTime.now()}")

        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(emailPostVerificationCodeReq.email)
        helper.setSubject("크무톡톡 가입 확인 메일입니다.")
        helper.setText(mailContent(code), true)
        helper.setFrom("크무톡톡 <kmutoktok@gmail.com>")

        try{
            javaMailSender.send(mimeMessage)
        }catch (mailSendException: MailSendException){
            flag = false
            scheduler.schedule({
                flag = true
            }, 1, TimeUnit.DAYS)
            println("FAIL TO SEND EMAIL: ${emailPostVerificationCodeReq.email}, CODE: $code, TIME: ${LocalDateTime.now()}")
            return ResponseEntity(HttpStatus.BAD_GATEWAY)
        }

        println("SEND EMAIL: ${emailPostVerificationCodeReq.email}, CODE: $code, TIME: ${LocalDateTime.now()}")
        println()


        return ResponseEntity(HttpStatus.OK)
    }

    override fun registerNot(emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {
        val splitEmail = emailPostVerificationCodeReq.email.split("@")

        if(splitEmail.size != 2)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        val code = verificationCode(emailPostVerificationCodeReq.email)

        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(emailPostVerificationCodeReq.email)
        helper.setSubject("계명대학교 챗봇 가입 확인 메일입니다.")
        helper.setText(mailContent(code), true)
        helper.setFrom("크무톡톡 <kmutoktok@gmail.com>")

        try{
            javaMailSender.send(mimeMessage)
        }catch (mailSendException: MailSendException){
            flag = false
            scheduler.schedule({
                flag = true
            }, 1, TimeUnit.DAYS)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    private fun mailContent(code: String): String{
        return """
                <!DOCTYPE html>
                    <html>
                        <head>
                        <meta charset="utf-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>가입 완료 메일</title>
                        <style>
                                @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');
                        body {
                            font-family: "Noto Sans KR", sans-serif;
                        }
                            .main-content{
                            box-sizing: border-box;
                            width: 100%;
                            max-width: 580px;
                            padding: 40px;
                        }
                            .text1{
                                font-size: 26px;
                                color: #000;
                            }
                            .text2{
                                font-size: 14px;
                                color: #606060;
                                margin-top: 30px;
                            }
                        hr{
                            margin: 0;
                            border: 0;
                            border-bottom: 2px #E6E8EB solid;
                        }
                        </style>
                        </head>
                        <body>
                        <div class="main-content">
                        <p class="text1">가입이 완료되었습니다!</p>
                        <hr>
                        <p class="text2">
                        안녕하세요, 크무톡톡입니다.<br/>회원가입이 완료되어 이제부터 크무톡톡에서 궁금한 것을 질문할 수 있게 되었어요!
                        </p>
                        </div>
                        </body>
                    </html>
                   """
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
            for (hashByte in hashBytes) stringBuilder.append(abs(hashByte % 10))
            stringBuilder.substring(0, 6)
        } catch (e: NoSuchAlgorithmException) {
            throw NoSuchAlgorithmException(e)
        }
    }
}