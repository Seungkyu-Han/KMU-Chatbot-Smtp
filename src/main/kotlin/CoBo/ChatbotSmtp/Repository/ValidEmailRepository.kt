package CoBo.ChatbotSmtp.Repository

import CoBo.ChatbotSmtp.Data.Entity.ValidEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ValidEmailRepository:JpaRepository<ValidEmail, String> {
}