package CoBo.ChatbotSmtp.Data.Entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@Data
@NoArgsConstructor
data class ValidEmail(
    @Id
    @Email
    var email: String,

    @Column(length = 10)
    var code: String,

    var isValid: Boolean
)