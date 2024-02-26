package gomoku.repository

interface Transaction {
    val playersRepository: PlayersRepository
    val gamesRepository: GamesRepository
    val waitingRoomRepository: WaitingRoomRepository
    val informationRepository: InformationRepository
    fun rollback()
}