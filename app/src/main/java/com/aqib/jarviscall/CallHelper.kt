package com.aqib.jarviscall

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract

object CallHelper {
    private val filler = Regex("\\b(call|phone|dial|lagao|lagana|laga do|karo|kar do|ko|mujhe|please|ek|baat|se)\\b")
    private val triggers = listOf("call", "phone", "dial", "lagao", "lagana")

    fun handle(context: Context, text: String): String? {
        val t = text.lowercase().trim()
        if (triggers.none { t.contains(it) }) return null

        val name = t.replace(filler, "").replace(Regex("\\s+"), " ").trim()
        if (name.isEmpty()) return "Kisko call karna hai?"

        val digits = name.replace(" ", "")
        if (digits.all { it.isDigit() || it == '+' } && digits.length >= 5) {
            dial(context, digits)
            return "Number $digits ko call laga raha hu"
        }

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
            arrayOf("%$name%"), null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                dial(context, it.getString(1))
                return "${it.getString(0)} ko call laga raha hu"
            }
        }
        return "$name naam ka contact nahi mila"
    }

    private fun dial(context: Context, number: String) {
        val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(number)))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }
}
