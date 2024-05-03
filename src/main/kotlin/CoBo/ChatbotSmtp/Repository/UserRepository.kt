package CoBo.ChatbotSmtp.Repository

import CoBo.ChatbotSmtp.Data.Entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int> {


    fun existsByEmail(email: String): Boolean
}