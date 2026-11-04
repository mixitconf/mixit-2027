package org.mixit.conference.ui.component

import kotlinx.html.*
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.people.SponsorshipLevel
import org.mixit.conference.model.shared.Context

fun DIV.sponsorGroupComponent(context: Context, event: Event, sponsors: List<Sponsor>) {
    if(sponsors.isNotEmpty()) {
        h2 {
            +"${context.i18n("home.section.sponsors.title")} ${event.year}"
        }
        val mainLevels = listOf(SponsorshipLevel.GOLD, SponsorshipLevel.LANYARD, SponsorshipLevel.PARTY)
        val mainSponsors = sponsors.filter { mainLevels.contains(it.level) }.sortedBy { it.subscriptionDate }
            .map { it.name to it.photoUrl!! }.toSet()
        val otherSponsors = sponsors.filterNot { mainLevels.contains(it.level) }.sortedBy { it.subscriptionDate }
            .map { it.name to it.photoUrl!! }

        div {
            h5 {
                +context.i18n("sponsor.level.main")
            }
            sponsorBlockComponent(event, mainSponsors)

            h5(classes = "mt-3") {
                +context.i18n("sponsor.level.partner")
            }
            sponsorBlockComponent(
                event,
                otherSponsors.filter { !mainSponsors.map { it.first }.contains(it.first) }.toSet()
            )
        }
    }
    div(classes = "mt-5") {
        + " "
    }
}

fun DIV.sponsorBlockComponent(event: Event, sponsors: Set<Pair<String, String>>) {
    div(classes = "mxt-sponsor__grid-home") { //"text-center row justify-content-md-center align-items-center") {
        sponsors.forEach { (sponsorName, photoUrl) ->
            div(classes = "mxt-sponsor__image-container") {
                sponsorThumbnailComponent(event, sponsorName, photoUrl)
            }
        }
    }
}

fun DIV.sponsorThumbnailComponent(event: Event, sponsorName: String, photoUrl: String) {
    a {
        href = "/${event.year}/sponsors#${sponsorName.replace(" ", "-")}"
        img(classes = "mxt-img__sponsors", alt = sponsorName, src = photoUrl.replace("https://mixitconf.org", ""))
    }
}

/** Sponsors band at the bottom of a page, rendered only when the edition has sponsors. */
fun DIV.sponsorsSection(context: Context, event: Event, sponsors: List<Sponsor>) {
    if (sponsors.isEmpty()) return
    sectionComponent(context) { sponsorGroupComponent(context, event, sponsors) }
}
