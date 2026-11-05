package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.img
import kotlinx.html.nav
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.strong
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.picture.Album
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.ui.MEDIA_YEARS
import org.mixit.conference.ui.component.Crumb
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.chipLink
import org.mixit.conference.ui.component.editionMenu
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.videoCard
import org.mixit.conference.ui.renderTemplate

fun renderMedia(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    photoAlbum: Album?,
    talksWithVideo: Map<TalkFormat, List<Talk>>
) =
    renderTemplate(context, event) {
        val all = talksWithVideo.values.flatten()
        val keynotes = all.filter { it.format.isKeynote() }
        val others = all.filterNot { it.format.isKeynote() }
            .sortedWith(compareBy({ it.topic.ordinal }, { it.title }))
        val albums = photoAlbum?.sections?.filter { it.photos.isNotEmpty() }.orEmpty()
        val photoCount = albums.sumOf { it.photos.size }

        pageHeaderComponent(
            context,
            Rubrique.VIDEO,
            title = "MiXiT ${event.year} ${context.i18n("media.v2.title")}",
            intro = listOfNotNull(
                all.size.takeIf { it > 0 }?.let { "$it ${context.i18n("media.v2.videos")}" },
                photoCount.takeIf { it > 0 }?.let { "$it ${context.i18n("media.v2.photos")}" },
            ).joinToString(" · ").ifBlank { null },
            breadcrumb = listOf(
                Crumb(context.i18n("media.v2.crumb"), "${context.uriBasePath}/archives"),
                Crumb("MiXiT ${event.year}"),
            ),
        ) {
            editionMenu(context.i18n("page.edition"), MEDIA_YEARS, event.year) { "${context.uriBasePath}/$it/media" }
        }

        pageContent {
            nav(classes = "mxt-media-nav") {
                attributes["aria-label"] = context.i18n("media.v2.onpage")
                if (keynotes.isNotEmpty()) chipLink(context.i18n("talk.keynotes.title"), "#keynotes", false, count = keynotes.size)
                if (albums.isNotEmpty()) chipLink(context.i18n("media.v2.chip.albums"), "#photos", false, count = albums.size)
                if (others.isNotEmpty()) chipLink(context.i18n("media.v2.all.videos"), "#videos", false, count = others.size)
            }

            if (all.isEmpty() && albums.isEmpty()) {
                div(classes = "mxt-empty") { +context.i18n("media.v2.empty") }
            }

            if (keynotes.isNotEmpty()) {
                pageSection(context.i18n("talk.keynotes.title"), context.i18n("talk.keynotes.intro"), id = "keynotes") {
                    div(classes = "mxt-video-grid") {
                        keynotes.forEach { videoCard(context, it, "${context.i18n("talk.format.KEYNOTE")} · ${event.year}") }
                    }
                }
            }

            if (photoAlbum != null && albums.isNotEmpty()) {
                pageSection(context.i18n("talk.photos.title"), context.i18n("talk.photos.intro"), id = "photos") {
                    div(classes = "mxt-album-grid") {
                        albums.forEach { section ->
                            a(href = "${context.uriBasePath}/${event.year}/medias/images/${section.sectionId}", classes = "mxt-album-card") {
                                span(classes = "mxt-album-card__cover") {
                                    img(
                                        src = "${photoAlbum.url}${event.year}/${section.sectionId}/${section.photos.first().name}",
                                        alt = "",
                                    ) { attributes["loading"] = "lazy" }
                                }
                                span(classes = "mxt-album-card__body") {
                                    strong { +context.i18n(section.i18nKey) }
                                    span { +"${section.photos.size} ${context.i18n("media.v2.photos")}" }
                                }
                            }
                        }
                    }
                }
            }

            if (others.isNotEmpty()) {
                pageSection(context.i18n("media.v2.all.videos"), context.i18n("talk.videos.intro"), id = "videos") {
                    div(classes = "mxt-video-grid") {
                        others.forEach {
                            videoCard(context, it, "${context.i18n("topics.${it.topic.value}.title")} · ${event.year}")
                        }
                    }
                }
            }
        }

        sponsorsSection(context, event, sponsors)
    }
