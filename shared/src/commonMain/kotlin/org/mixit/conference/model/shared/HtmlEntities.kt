package org.mixit.conference.model.shared

private val NAMED_ENTITIES = mapOf(
    "amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"", "apos" to "'", "nbsp" to "\u00A0",
)
private val ENTITY_REGEX = Regex("&(#[xX][0-9a-fA-F]+|#[0-9]+|[a-zA-Z]+);")

/** Decodes HTML entities (numeric and common named ones) so the text is escaped only once when rendered. */
fun String.decodeHtmlEntities(): String =
    if (!contains('&')) this
    else ENTITY_REGEX.replace(this) { m ->
        val e = m.groupValues[1]
        when {
            e.startsWith("#x") || e.startsWith("#X") -> e.substring(2).toIntOrNull(16)?.let { codePointToString(it) }
            e.startsWith("#") -> e.substring(1).toIntOrNull()?.let { codePointToString(it) }
            else -> NAMED_ENTITIES[e]
        } ?: m.value
    }

private fun codePointToString(cp: Int): String? =
    if (cp in 0..0xFFFF) cp.toChar().toString()
    else if (cp in 0x10000..0x10FFFF) {
        val c = cp - 0x10000
        charArrayOf((0xD800 + (c shr 10)).toChar(), (0xDC00 + (c and 0x3FF)).toChar()).concatToString()
    } else null
