package org.mixit.conference.ui

import org.mixit.conference.model.link.SocialNetwork
import org.mixit.conference.model.menu.Menu
import org.mixit.conference.model.menu.MenuItem
import org.mixit.conference.model.talk.Podcast

const val CURRENT_YEAR = 2027
const val CURRENT_TALK_YEAR = 2026
const val CURRENT_MEDIA_YEAR = 2026


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

val MENU = listOf(
    Menu(
        title = "menu.content.title",
        items = listOf(
            MenuItem(title = "menu.content.cod", href = "codeofconduct"),
            //MenuItem(title = "menu.content.billetweb", href = "billetweb"),
            // MenuItem(title = "menu.content.cfp", href = "cfp"),
            //MenuItem(title = "menu.content.sponsors", href = "$CURRENT_YEAR/sponsors"),
            //MenuItem(title = "menu.content.speakers", href = "$CURRENT_TALK_YEAR/speakers"),
            //MenuItem(title = "menu.content.agenda", href = CURRENT_TALK_YEAR.toString()),
            MenuItem(title = "menu.content.media", href = "$CURRENT_MEDIA_YEAR/media"),
        )
    ),
    Menu(
        title = "menu.facilities.title",
        items = listOf(
            MenuItem(title = "menu.facilities.accessibility", href = "accessibility"),
            MenuItem(title = "menu.facilities.venue", href = "venue"),
            MenuItem(title = "menu.facilities.mixette", href = "mixette"),
            MenuItem(title = "menu.facilities.budget", href = "budget"),
            MenuItem(title = "menu.content.faq", href = "faq")
        )
    ),
    Menu(
        title = "menu.mixit.title",
        items = listOf(
            MenuItem(title = "menu.mixit.news", href = "#home.section.news.title"),
            MenuItem(title = "menu.mixit.podcast", href = "#home.section.onair.title"),
            MenuItem(title = "menu.mixit.values", href = "#home.section.values.title"),
            MenuItem(title = "menu.mixit.editorialline", href = "#home.section.editorialline.title"),
            MenuItem(title = "menu.facilities.us", href = "about"),
        )
    ),
)

