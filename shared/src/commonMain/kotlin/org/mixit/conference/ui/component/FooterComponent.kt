package org.mixit.conference.ui.component

import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.img
import kotlinx.html.span
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CONTACT_EMAIL

/** Site footer: newsletter, contact, the same navigation groups as the header, credits. */
fun DIV.footerComponent(ctx: Context, @Suppress("UNUSED_PARAMETER") event: Event?) {
    bevel(SectionStyle.DARK, SectionEffect.START)
    footer(classes = "mxt-footer") {
        div(classes = "mxt-footer__inner") {
            div(classes = "mxt-footer__newsletter") {
                div(classes = "mxt-footer__newsletter-text") {
                    span(classes = "mxt-eyebrow") { +ctx.i18n("footer.newsletter.label") }
                    span(classes = "mxt-footer__newsletter-pitch") { +ctx.i18n("footer.newsletter.text") }
                }
                a(href = "${ctx.uriBasePath}/newsletter-subscribe", classes = "mxt-button mxt-button--outline") {
                    attributes["target"] = "newsletter"
                    +ctx.i18n("footer.newsletter.cta")
                }
            }
            div(classes = "mxt-footer__columns") {
                div(classes = "mxt-footer__brand") {
                    img(src = "/images/svg/logo/mxt-icon--logo--light.svg", alt = "MiXiT", classes = "mxt-footer__logo")
                    span { +ctx.i18n("footer.address") }
                    a(href = "mailto:$CONTACT_EMAIL", classes = "mxt-footer__contact") { +CONTACT_EMAIL }
                    socialLinks()
                }
                navigationGroups(ctx, "mxt-footer")
            }
            div(classes = "mxt-footer__bottom") {
                span { +ctx.i18n("footer.association") }
                div(classes = "mxt-footer__hosting") {
                    span { +ctx.i18n("footer.hosted") }
                    span(classes = "mxt-footer__hosting-logo") {
                        img(src = "/images/sponsors/logo-clever-cloud.png", alt = "Clever Cloud")
                    }
                }
            }
        }
    }
}
