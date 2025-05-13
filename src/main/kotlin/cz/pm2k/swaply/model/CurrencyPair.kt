package cz.pm2k.swaply.model

data class CurrencyPair(
    val base: String,
    val quote: String,
    val rate: Double,
) {
    val code: String = "$base$quote"
}
