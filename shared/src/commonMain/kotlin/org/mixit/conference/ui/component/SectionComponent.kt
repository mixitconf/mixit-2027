package org.mixit.conference.ui.component

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h2
import org.mixit.conference.model.shared.Context

/**
 * Diagonal bevels around a coloured band.
 * - [END_INVERTED]: the band ends with the complement of the next band's opening bevel (the footer for
 *   instance), so that bevel starts right on this colour, without a white area in between.
 * - [BOTH_INVERTED]: [START] + [END_INVERTED].
 */
enum class SectionEffect {
    START, END, NONE, BOTH, END_INVERTED, BOTH_INVERTED;

    val hasStart: Boolean get() = this == START || this == BOTH || this == BOTH_INVERTED
    val hasEnd: Boolean get() = this == END || this == BOTH
    val hasInvertedEnd: Boolean get() = this == END_INVERTED || this == BOTH_INVERTED
}

enum class SectionStyle(private val effectStyle: String) {
    DARK("mxt-layout__section-effect mxt-layout__section-effect-%%-dark"),
    TRANSPARENT(""),
    LIGHT("mxt-layout__section-effect mxt-layout__section-effect-%%-light");

    fun classes(effect: SectionEffect) = effectStyle.replace("%%", effect.name.lowercase().replace('_', '-')).trim()
}

/**
 * Diagonal bevel that opens ([SectionEffect.START]) or closes ([SectionEffect.END], [SectionEffect.END_INVERTED])
 * a coloured band.
 */
fun FlowContent.bevel(style: SectionStyle, effect: SectionEffect) {
    if (style == SectionStyle.TRANSPARENT || effect !in setOf(SectionEffect.START, SectionEffect.END, SectionEffect.END_INVERTED)) return
    div(classes = style.classes(effect)) {
        attributes["aria-hidden"] = "true"
    }
}

fun DIV.sectionComponent(
    context: Context?,
    i18nKey: String? = null,
    effect: SectionEffect = SectionEffect.NONE,
    style: SectionStyle = SectionStyle.TRANSPARENT,
    block: DIV.() -> Unit
) {
    if (effect.hasStart) {
        div(classes = "mt-4 " + style.classes(SectionEffect.START)) {
            attributes["aria-hidden"] = "true"
        }
    }
    div(classes = "mxt-layout__section-${style.name.lowercase()}") {
        div(classes = "container-xxl") {
            if (i18nKey != null) {
                h2 {
                    attributes["id"] = i18nKey
                    +(context?.i18n(i18nKey) ?: "")
                }
            }
            block()
        }
    }
    if (effect.hasEnd) bevel(style, SectionEffect.END)
    if (effect.hasInvertedEnd) bevel(style, SectionEffect.END_INVERTED)
}
