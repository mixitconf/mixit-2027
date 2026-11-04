package org.mixit.conference.ui.security

import kotlinx.html.DIV
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.mixit.conference.model.shared.Context

/**
 * Layout shared by the sign-in steps (e-mail, first sign-in, code): a centered card on a light band,
 * what an account is for, and how personal data is used.
 */
fun DIV.authLayout(context: Context, title: String, intro: String, card: DIV.() -> Unit) {
    div(classes = "mxt-auth") {
        div(classes = "mxt-auth__card") {
            div(classes = "mxt-auth__head") {
                h1(classes = "mxt-auth__title") { +title }
                p(classes = "mxt-auth__intro") { +intro }
            }
            card()
        }
        ul(classes = "mxt-auth__benefits") {
            li {
                span(classes = "mxt-auth__benefit-icon") {
                    unsafe { raw("""<svg width="18" height="18" viewBox="0 0 24 24" fill="#f42d76" aria-hidden="true"><path d="M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z"></path></svg>""") }
                }
                +context.i18n("login.v2.benefit.favorites")
            }
            li {
                span(classes = "mxt-auth__benefit-icon") {
                    unsafe { raw("""<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#22ba90" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="5 12 10 17 19 7"></polyline></svg>""") }
                }
                +context.i18n("login.v2.benefit.feedback")
            }
        }
        div(classes = "mxt-auth__notes") {
            p { +context.i18n("login.cookies") }
            p { +context.i18n("login.data") }
        }
    }
}
