package com.example.projetoapprotas.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun saudacaoAtual(cal: Calendar = Calendar.getInstance()) =
        when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }

    fun dataExtenso(cal: Calendar = Calendar.getInstance()) =
        SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
            .format(cal.time)
            .replaceFirstChar { it.uppercase() }
}
