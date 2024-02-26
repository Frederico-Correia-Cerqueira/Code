package gomoku.http.models

data class UserTokenCreateOutputModel(val token: String, val userID: Int)
data class UserTokenRemoveOutputModel(val sucess: Boolean)
data class UserTokenValidationOutputModel(val token: String, val present: Boolean, val playerID: Int)