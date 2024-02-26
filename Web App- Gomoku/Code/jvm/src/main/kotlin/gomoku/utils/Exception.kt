package gomoku.utils

class GameOver(message: String) : Exception(message)
class NotYourTurn(message: String) : Exception(message)
class PositionTaken(message: String) : Exception(message)
