package org.mixit.conference.ui.component

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.img
import org.mixit.conference.model.link.SocialNetwork
import org.mixit.conference.ui.SOCIALS

/** Round social network button (white icon, for dark backgrounds). */
fun FlowContent.socialLink(social: SocialNetwork) {
    a(href = social.url, target = "_blank", classes = "mxt-social") {
        attributes["rel"] = "noopener"
        attributes["aria-label"] = social.title
        img(src = "/images/svg/social/mxt-icon--social-${social.key}.svg", alt = "", classes = "mxt-social__icon")
    }
}

/** All MiXiT social networks, in a row. */
fun FlowContent.socialLinks() {
    div(classes = "mxt-socials") {
        SOCIALS.forEach { socialLink(it) }
    }
}
