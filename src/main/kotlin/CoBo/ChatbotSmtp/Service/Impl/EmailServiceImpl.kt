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
import kotlin.math.abs


@Service
class EmailServiceImpl(
    val validEmailRepository: ValidEmailRepository,

    @Value("\${kmu.mail.address}")
    val kmuEmailAddress: String,

    val javaMailSender: JavaMailSender
    ):EmailService {
    val emailLast = kmuEmailAddress.split(",")

    override fun postVerificationCode(emailPostVerificationCodeReq: EmailPostVerificationCodeReq): ResponseEntity<HttpStatus> {

        val splitEmail = emailPostVerificationCodeReq.email.split("@")

        if(splitEmail.size != 2)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        if(!emailLast.contains(splitEmail.last()))
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val code = verificationCode(emailPostVerificationCodeReq.email)

        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(emailPostVerificationCodeReq.email)
        helper.setSubject("계명대학교 챗봇 인증번호입니다.")
        helper.setText(mailContent(code), true)
        helper.setFrom("크무톡톡 <kmutoktok@gmail.com>")

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
        helper.setSubject("계명대학교 챗봇 인증번호입니다.")
        helper.setText(mailContent(code), true)
        helper.setFrom("크무톡톡 <kmutoktok@gmail.com>")

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

    private fun mailContent(code: String): String{
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Frame 9</title>\n" +
                "    <link href=\"style.css\" rel=\"stylesheet\">\n" +
                "    <style>\n" +
                "        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');\n" +
                "\n" +
                "        body {\n" +
                "            font-family: \"Noto Sans KR\", sans-serif;\n" +
                "        }\n" +
                "\n" +
                "        #Frame-9 {\n" +
                "            box-sizing: border-box;\n" +
                "            width: 100%; height: 292px;\n" +
                "            max-width: 580px;\n" +
                "            background-color: #FFFFFF;\n" +
                "            padding: 40px;\n" +
                "        }\n" +
                "\n" +
                "        #text-1 {\n" +
                "            margin: 0;\n" +
                "            margin-bottom: 24px;\n" +
                "            font-weight: 400;\n" +
                "            font-size: 26px;\n" +
                "            color: #000000;\n" +
                "            line-height: 30px;\n" +
                "            letter-spacing: -0.025em;\n" +
                "        }\n" +
                "\n" +
                "        #Frame-9 > hr {\n" +
                "            margin: 0;\n" +
                "            border: 0;\n" +
                "            border-bottom: 1px #E6E8EB solid;\n" +
                "        }\n" +
                "\n" +
                "        #text-2 {\n" +
                "            margin: 0;\n" +
                "            margin-top: 39px; margin-bottom: 20px;\n" +
                "            font-weight: 400;\n" +
                "            font-size: 14px;\n" +
                "\n" +
                "            color: #606060;\n" +
                "            line-height: 18px;\n" +
                "            letter-spacing: -0.025em;\n" +
                "        }\n" +
                "\n" +
                "        #text-number {\n" +
                "            box-sizing: border-box;\n" +
                "            display: inline-block;\n" +
                "            margin: 0;\n" +
                "            padding: 14px 11px 14px 18px;\n" +
                "            \n" +
                "            border-radius: 6px;\n" +
                "            background-color: #E6E8EB;\n" +
                "            font-weight: 400;\n" +
                "            font-size: 26px;\n" +
                "\n" +
                "            color: #001832;\n" +
                "            line-height: 26px;\n" +
                "            letter-spacing: 7px;\n" +
                "        }\n" +
                "\n" +
                "        @media screen and (max-width: 425px) {\n" +
                "            #Frame-9 {\n" +
                "                width: 100%; height: 240px;\n" +
                "                max-width: 345px;\n" +
                "                padding: 27px;\n" +
                "            }\n" +
                "\n" +
                "            #text-1 {\n" +
                "                margin-bottom: 20px;\n" +
                "                font-size: 18px;\n" +
                "                line-height: 24px;\n" +
                "            }\n" +
                "\n" +
                "            #Frame-9 > hr {\n" +
                "                max-width: 290px;\n" +
                "            }\n" +
                "\n" +
                "            #text-2 {\n" +
                "                margin-top: 50px; margin-bottom: 16px;\n" +
                "                font-size: 12px;\n" +
                "                line-height: 16px;\n" +
                "            }\n" +
                "\n" +
                "            #text-number {\n" +
                "                padding: 7px 9px 7px 18px;\n" +
                "                \n" +
                "                font-size: 20px;\n" +
                "\n" +
                "                line-height: 28px;\n" +
                "                letter-spacing: 9px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"Frame-9\">\n" +
                "        <p id=\"text-1\">인증번호를 보내드립니다.</p>\n" +
                "        <hr>\n" +
                "        <p id=\"text-2\">\n" +
                "            본인 확인을 위한 인증번호입니다.<br>\n" +
                "            서비스로 돌아가서 아래 6개의 숫자를 입력해주세요.\n" +
                "        </p>\n" +
                "        <p id=\"text-number\">$code</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>"
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