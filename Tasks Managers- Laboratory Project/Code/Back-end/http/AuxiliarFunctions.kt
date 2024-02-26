import java.time.LocalDate

/** ----------------------------------- Auxiliar Functions ----------------------------------- */
fun String.isEmailValid(): Boolean {
    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return emailRegex.matches(this)
}

fun String.isValidDate(): Boolean {
    val currentDate = LocalDate.now()
    val args = split("-")
    if (args.size != 3 || args.first().length != 4 || args[1].length != 2 || args.last().length != 2) return false
    if (args.first().toInt() < currentDate.year) return false
    return if (args.first().toInt() > currentDate.year) {
        true
    } else {
        if (args[1].toInt() > currentDate.month.value) {
            true
        } else {
            args.last().toInt() > currentDate.dayOfMonth
        }
    }
}
