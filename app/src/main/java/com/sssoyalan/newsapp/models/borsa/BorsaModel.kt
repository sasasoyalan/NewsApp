package com.sssoyalan.newsapp.models.borsa

import java.io.Serializable

data class BorsaModel(
    val BCH: modelInside,
    val BTC: modelInside,
    val ETH: modelInside,
    val EUR: modelInside,
    val GA: modelInside,
    val GAG: modelInside,
    val GBP: modelInside,
    val LTC: modelInside,
    val USD: modelInside,
    val XRP: modelInside,
    val XU100: modelInside
)

data class Borsalar (
    val borsalar : List<modelInside>
) : Serializable

data class modelInside(
    val alis: String,
    val degisim: String,
    val isim: String,
    val satis: String
) : Serializable