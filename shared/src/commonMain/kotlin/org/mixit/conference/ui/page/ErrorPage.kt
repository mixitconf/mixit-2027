package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.strong
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CURRENT_TALK_YEAR
import org.mixit.conference.ui.renderTemplate

/** Shortcuts proposed on the error page: i18n suffix and path. */
private val ERROR_SHORTCUTS = listOf(
    "programme" to "/$CURRENT_TALK_YEAR",
    "archives" to "/archives",
    "venue" to "/venue",
    "faq" to "/faq",
)

fun renderError(context: Context, status: String?) =
    renderTemplate(context, null) {
        val state = when (status) {
            "404", "401", "403" -> status.toInt()
            else -> 500
        }
        val needsLogin = state == 401

        div(classes = "mxt-error") {
            img(src = "/images/png/error.png", alt = context.i18n("error.v2.image"), classes = "mxt-error__image")
            div(classes = "mxt-error__body") {
                span(classes = "mxt-eyebrow mxt-eyebrow--muted") { +"${context.i18n("error.v2.label")} $state" }
                h1(classes = "mxt-error__title") { +context.i18n("error.$state.title") }
                p(classes = "mxt-error__text") { +context.i18n("error.$state.description") }
                a(
                    href = if (needsLogin) "${context.uriBasePath}/login" else "${context.uriBasePath}/",
                    classes = "mxt-button mxt-button--primary mxt-error__cta",
                ) { +context.i18n("error.$state.action") }
                div(classes = "mxt-error__shortcuts") {
                    ERROR_SHORTCUTS.forEach { (key, path) ->
                        a(href = "${context.uriBasePath}$path", classes = "mxt-error__shortcut") {
                            strong { +context.i18n("error.v2.shortcut.$key") }
                            span { +context.i18n("error.v2.shortcut.$key.text") }
                        }
                    }
                }
            }
        }
    }
