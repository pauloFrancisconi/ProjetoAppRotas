
package com.example.projetoapprotas.util

import android.content.Context

fun obterUsuarioSalvo(context: Context): Pair<String, String>? {
    val sharedPref = context.getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
    val email = sharedPref.getString("email", null)
    val nome = sharedPref.getString("nome", null)

    return if (email != null && nome != null) Pair(email, nome) else null
}
