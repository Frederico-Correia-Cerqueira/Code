package gomoku.repository

interface TransactionManager {
    fun <R> executeTransaction(block: (Transaction) -> R): R
}