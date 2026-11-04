package org.mixit.conference.ui.component

import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.nav
import kotlinx.html.p
import kotlinx.html.span
import org.mixit.conference.model.shared.Context

/** Section icons (v2): one file per icon, readable on both themes. */
enum class Rubrique(val file: String) {
    ABOUT("about"), ACCESSIBILITY("accessibility"), BUDGET("budget"), CODE_OF_CONDUCT("code"), FAQ("faq"),
    MIXETTE("mixette"), SPEAKER("speaker"), SPONSOR("sponsor"), TALK("talk"), TICKET("ticket"), VENUE("venue"), VIDEO("video");

    val src: String get() = "/images/svg/rubrique/mxt-rubrique-$file.svg"
}

/** One step of a breadcrumb: an i18n key or a literal label, and an optional link. */
data class Crumb(val label: String, val href: String? = null)

/**
 * Header shared by the inner pages: violet band with the section icon, the title, an intro,
 * an optional breadcrumb, an optional meta row (badges...) and an optional aside (edition selector, buttons...),
 * closed by a bevel.
 */
fun DIV.pageHeaderComponent(
    ctx: Context,
    rubrique: Rubrique?,
    title: String,
    intro: String? = null,
    breadcrumb: List<Crumb> = emptyList(),
    meta: (DIV.() -> Unit)? = null,
    leading: (DIV.() -> Unit)? = null,
    aside: (DIV.() -> Unit)? = null,
) {
    div(classes = "mxt-page-header") {
        div(classes = "mxt-page-header__inner") {
            if (breadcrumb.isNotEmpty()) {
                nav(classes = "mxt-page-header__breadcrumb") {
                    attributes["aria-label"] = ctx.i18n("header.breadcrumb")
                    breadcrumb.forEachIndexed { index, crumb ->
                        if (index > 0) span(classes = "mxt-page-header__separator") {
                            attributes["aria-hidden"] = "true"
                            +"›"
                        }
                        if (crumb.href != null) a(href = crumb.href) { +crumb.label } else span { +crumb.label }
                    }
                }
            }
            div(classes = "mxt-page-header__row") {
                div(classes = "mxt-page-header__heading") {
                    if (leading != null) leading()
                    else if (rubrique != null) img(src = rubrique.src, alt = "", classes = "mxt-page-header__icon")
                    div(classes = "mxt-page-header__text") {
                        h1(classes = "mxt-page-header__title") { +title }
                        if (intro != null) p(classes = "mxt-page-header__intro") { +intro }
                        if (meta != null) div(classes = "mxt-page-header__meta") { meta() }
                    }
                }
                if (aside != null) div(classes = "mxt-page-header__aside") { aside() }
            }
        }
    }
    bevel(SectionStyle.DARK, SectionEffect.END)
}
