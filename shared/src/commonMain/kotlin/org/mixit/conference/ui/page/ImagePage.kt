package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.figure
import kotlinx.html.img
import kotlinx.html.span
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.picture.AlbumSection
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.component.Crumb
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.renderTemplate

private const val CHEVRON_LEFT = """<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="15 6 9 12 15 18"></polyline></svg>"""
private const val CHEVRON_RIGHT = """<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="9 6 15 12 9 18"></polyline></svg>"""

fun renderImage(
    context: Context,
    event: Event,
    @Suppress("UNUSED_PARAMETER") sponsors: List<Sponsor>,
    albumUrl: String,
    section: AlbumSection,
    index: Int,
) =
    renderTemplate(context, event) {
        val photos = section.photos
        val photo = photos[index]
        val albumName = context.i18n(section.i18nKey)
        val albumHref = "${context.uriBasePath}/${event.year}/medias/images/${section.sectionId}"
        fun photoHref(i: Int) = "$albumHref/${photos[(i + photos.size) % photos.size].name.substringBeforeLast('.')}"

        pageHeaderComponent(
            context,
            rubrique = null,
            title = albumName,
            intro = "${context.i18n("media.v2.photo")} ${index + 1} / ${photos.size}",
            breadcrumb = albumCrumbs(context, event) + Crumb(albumName, albumHref),
        )
        pageContent {
            div(classes = "mxt-viewer") {
                if (photos.size > 1) {
                    a(href = photoHref(index - 1), classes = "mxt-viewer__nav") {
                        attributes["aria-label"] = context.i18n("talks.previous")
                        attributes["rel"] = "prev"
                        unsafe { raw(CHEVRON_LEFT) }
                    }
                }
                figure(classes = "mxt-viewer__figure") {
                    img(src = "$albumUrl${event.year}/${section.sectionId}/${photo.name}", alt = "$albumName, ${context.i18n("media.v2.photo")} ${index + 1}", classes = "mxt-viewer__img")
                }
                if (photos.size > 1) {
                    a(href = photoHref(index + 1), classes = "mxt-viewer__nav") {
                        attributes["aria-label"] = context.i18n("talks.next")
                        attributes["rel"] = "next"
                        unsafe { raw(CHEVRON_RIGHT) }
                    }
                }
            }
            div(classes = "mxt-viewer__footer") {
                a(href = albumHref, classes = "mxt-back-link") { +"← ${context.i18n("media.v2.back.album")}" }
                span { +"${index + 1} / ${photos.size}" }
            }
        }
    }
