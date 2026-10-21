package org.mixit.conference.ui.page

import kotlinx.html.*
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.picture.Album
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.ui.MEDIA_YEARS
import org.mixit.conference.ui.component.*
import org.mixit.conference.ui.renderTemplate

fun renderMedia(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    photoAlbum: Album?,
    talksWithVideo: Map<TalkFormat, List<Talk>>
) =
    renderTemplate(context, event) {
        sectionComponent(context) {
            archiveBackButton(context)
            val pageTitle = context.i18n("talks.media")
            h1 {
                img(classes = "mxt-img__header-video me-2") {
                    alt = pageTitle
                }
                +pageTitle
            }

            yearSelectorComponent(context, event, url = "/media", alt = pageTitle, years = MEDIA_YEARS)

            val keynotes = talksWithVideo[TalkFormat.KEYNOTE] ?: emptyList()
            val others = talksWithVideo.entries
                .map { it.value.filter { it.format != TalkFormat.KEYNOTE } }
                .flatten()
                .groupBy { it.topic }

            if (keynotes.isNotEmpty()) {
                h2 {
                    +context.i18n("talk.keynotes.title")
                }
                p(classes = "lead") {
                    +context.i18n("talk.keynotes.intro")
                }

                div(classes = "mxt-talk-cards mxt-talk-cards--featured") {
                    keynotes.shuffled().forEach {
                        videoComponent(context, it, true)
                    }
                }
            }
            if (photoAlbum != null && photoAlbum.sections.isNotEmpty()) {
                h2(classes = "mt-5") {
                    +context.i18n("talk.photos.title")
                }
                p {
                    +context.i18n("talk.photos.intro")
                }
                pictureAlbumComponent(context, event, photoAlbum)
            }

            if (others.isNotEmpty()) {
                h2(classes = "mt-5") {
                    +context.i18n("talk.videos.title")
                }
                p {
                    +context.i18n("talk.videos.intro")
                }

                others.forEach { (topic, talks) ->
                    div(classes = "mt-5") {
                        topicComponent(context, topic, event.year)
                        div(classes = "mxt-talk-cards mt-3") {
                            talks.shuffled().forEach {
                                videoComponent(context, it)
                            }
                        }
                    }
                }
            }

        }
        sectionComponent(context) {
            sponsorGroupComponent(context, event, sponsors)
        }
    }
