package org.mixit.conference.ui

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.mixit.conference.model.link.SocialNetwork
import org.mixit.conference.model.menu.Menu
import org.mixit.conference.model.menu.MenuItem
import org.mixit.conference.model.talk.Podcast

const val CURRENT_YEAR = 2027
const val CURRENT_TALK_YEAR = 2026
const val CURRENT_MEDIA_YEAR = 2026

/** Opening day of the call for papers of [CURRENT_YEAR]: the "Propose a talk" buttons stay inactive before it. */
val CFP_OPENING_DATE = LocalDate(2026, 11, 30)

/** True from [CFP_OPENING_DATE] on, Paris time. */
fun isCfpOpen(today: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Paris")).date): Boolean =
    today >= CFP_OPENING_DATE


val TALKS_YEARS = (2012..CURRENT_TALK_YEAR).sortedDescending().filterNot { it == 2020 }

val MEDIA_YEARS = (2012..CURRENT_MEDIA_YEAR).sortedDescending().filterNot { it == 2020 }
val YEARS = (2012..CURRENT_YEAR).sortedDescending().filterNot { it == 2020 }
val MIXETTE_YEARS = (2022..CURRENT_TALK_YEAR).sortedDescending()

const val DEFAULT_IMG_URL = "/images/png/mxt-icon--default-avatar.png"

val SOCIALS = listOf(
    SocialNetwork(key = "instagram", url = "https://www.instagram.com/mixitconf/", title = "Instagram"),
    SocialNetwork(key = "bsky", url = "https://bsky.app/profile/mixitconf.org", title = "Blue Sky"),
    SocialNetwork(key = "masto", url = "https://piaille.fr/@mixitconf", title = "Mastodon"),
    SocialNetwork(key = "linkedin", url = "https://www.linkedin.com/company/mixitconf/", title = "Linkedin"),
    SocialNetwork(key = "youtube", url = "https://www.youtube.com/@mixitconf", title = "Youtube"),
    SocialNetwork(key = "twitch", url = "https://twitch.tv/mixitconf/", title = "Twitch"),
    SocialNetwork(key = "github", url = "https://github.com/mixitconf/mixit", title = "GitHub"),
)

val PODCASTS = listOf(
    Podcast("apple", "Apple podcast", "https://podcasts.apple.com/fr/podcast/mixit/id1809671856"),
    Podcast("spotify","Spotify", "https://open.spotify.com/show/1VjQM9KdqFVRRFzkgPf7b3"),
    Podcast("deezer","Deezer", "https://www.deezer.com/show/1001819911"),
    Podcast("podcast-addict","Podcast Addict", "https://podcastaddict.com/podcast/mixit-on-air/5835412"),
    Podcast("overcast","Overcast", "https://overcast.fm/itunes1809671856"),
    Podcast("ausha","Ausha", "https://podcast.ausha.co/mixit"),
)

/**
 * Site navigation, single source for the desktop header, the mobile menu and the footer columns.
 * An `inline` group is shown as top-level links in the desktop header, the others as dropdowns.
 */
val NAVIGATION = listOf(
    Menu(
        title = "nav.edition.title",
        inline = true,
        items = listOf(
            MenuItem(title = "nav.edition.current", href = ""),
            MenuItem(title = "nav.edition.archives", href = "archives"),
            MenuItem(title = "nav.edition.sponsors", href = "sponsors"),
        )
    ),
    Menu(
        title = "nav.infos.title",
        items = listOf(
            MenuItem(title = "nav.infos.venue", href = "venue"),
            MenuItem(title = "nav.infos.accessibility", href = "accessibility"),
            MenuItem(title = "nav.infos.mixette", href = "mixette"),
            MenuItem(title = "nav.infos.budget", href = "budget"),
            MenuItem(title = "nav.infos.faq", href = "faq"),
        )
    ),
    Menu(
        title = "nav.about.title",
        items = listOf(
            MenuItem(title = "nav.about.us", href = "about"),
            MenuItem(title = "nav.about.values", href = "#home.section.values.title"),
            MenuItem(title = "nav.about.podcast", href = "#home.section.onair.title", inHeader = false),
            MenuItem(title = "nav.about.coc", href = "codeofconduct"),
        )
    ),
)

/** Call to action shown in the header and the mobile menu: it follows the edition calendar (CFP, then ticketing). */
val HEADER_CTA = MenuItem(title = "header.cta.cfp", href = "cfp")

const val CONTACT_EMAIL = "contact@mixitconf.org"
