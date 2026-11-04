package org.mixit.conference.ui.page

import kotlinx.html.div
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Speaker
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.ui.component.Crumb
import org.mixit.conference.ui.component.fullName
import org.mixit.conference.ui.component.iconLinks
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.speakerPortrait
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.talkLinkCard
import org.mixit.conference.ui.renderTemplate

fun renderSpeaker(context: Context, speaker: Speaker, talks: List<Talk>, sponsors: List<Sponsor>, event: Event?) =
    renderTemplate(context, null) {
        val sortedTalks = talks.sortedWith(compareByDescending<Talk> { it.event }.thenBy { it.startLocalTime() })
        val year = event?.year?.toString() ?: sortedTalks.firstOrNull()?.event

        pageHeaderComponent(
            context,
            rubrique = null,
            title = speaker.fullName(),
            intro = speaker.company?.takeIf { it.isNotBlank() },
            breadcrumb = listOfNotNull(
                Crumb(context.i18n("speaker.title"), "${context.uriBasePath}/${year ?: ""}${if (year != null) "/" else ""}speakers"),
                year?.let { Crumb("MiXiT $it") },
            ),
            meta = { iconLinks(speaker.links) },
            leading = { speakerPortrait(speaker, "mxt-page-header__portrait") },
        )

        pageContent {
            val bio = speaker.description[context.language] ?: speaker.description.values.firstOrNull()
            if (!bio.isNullOrBlank()) {
                pageSection(context.i18n("speaker.v2.bio")) {
                    div(classes = "mxt-talk__text mxt-speaker__bio") { unsafe { raw(context.markdown(bio)) } }
                }
            }
            if (sortedTalks.isNotEmpty()) {
                pageSection(context.i18n("speaker.v2.talks")) {
                    div(classes = "mxt-talk-links") {
                        sortedTalks.forEach { talkLinkCard(context, it) }
                    }
                }
            }
        }

        if (event != null) sponsorsSection(context, event, sponsors)
    }
