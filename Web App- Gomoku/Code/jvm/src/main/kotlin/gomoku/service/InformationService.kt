package gomoku.service

import gomoku.domainEntities.*
import gomoku.repository.TransactionManager
import gomoku.utils.catchError
import org.springframework.stereotype.Component

@Component
class InformationService(private val transactionManager: TransactionManager) {
    val list = listOf(
        Developer(
            "'Catarina Pedro'",
            "49494",
            listOf(
                Social("'https://www.linkedin.com/in/catarina-pedro-a3758224b/'", "ic_linkedin"),
                Social("'https://www.instagram.com/__cataariina__/'", "ic_instagram")
            ),
            "A49494@alunos.isel.pt",
            "catarinap_img"
        ),
        Developer(
            "'Frederico Correia Cerqueira'",
            "49450",
            listOf(
                Social("'https://www.linkedin.com/in/frederico-correia-cerqueira'", "ic_linkedin"),
                Social("'https://www.instagram.com/frederico_cc/'", "ic_instagram")
            ),
            "a49450@alunos.isel.pt",
            "fredericoc_img"
        ),
        Developer(
            "'Pedro Silva'",
            "49489",
            listOf(
                Social("'https://www.linkedin.com/in/catarina-pedro-a3758224b/'", "ic_linkedin"),
                Social("'https://www.instagram.com/silv4_02/'", "ic_instagram")
            ),
            "a49489@alunos.isel.pt",
            "pedros_img"
        )
    )
    fun getAllStats(): StatsResult =
        transactionManager.executeTransaction {
            try {
                val stats = it.informationRepository.getStats()
                success(StatsList(stats))
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun credits(): InfoResult {
        val version = "Beta"
        return success(Information(version = version, developers = list))
    }
}
