package CoBo.ChatbotSmtp.Data.Entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Data
@NoArgsConstructor
data class User(
    @Id
    val kakaoId: Int,
    var email: String
)
