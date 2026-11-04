package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.ul
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.picture.AlbumSection
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.component.Crumb
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.renderTemplate

/** Breadcrumb shared by an album and its photos. */
internal fun albumCrumbs(context: Context, event: Event) = listOf(
    Crumb(context.i18n("media.v2.crumb"), "${context.uriBasePath}/archives"),
    Crumb("MiXiT ${event.year}", "${context.uriBasePath}/${event.year}/media"),
)

fun renderImages(
    context: Context,
    event: Event,
    @Suppress("UNUSED_PARAMETER") sponsors: List<Sponsor>,
    albumUrl: String,
    section: AlbumSection
) =
    renderTemplate(context, event) {
        val albumName = context.i18n(section.i18nKey)
        pageHeaderComponent(
            context,
            rubrique = null,
            title = albumName,
            intro = "${section.photos.size} ${context.i18n("media.v2.photos")} · MiXiT ${event.year}",
            breadcrumb = albumCrumbs(context, event),
        )
        pageContent {
            ul(classes = "mxt-photo-grid") {
                section.photos.forEachIndexed { index, photo ->
                    li {
                        a(
                            href = "${context.uriBasePath}/${event.year}/medias/images/${section.sectionId}/${photo.name.substringBeforeLast('.')}",
                            classes = "mxt-photo-grid__link",
                        ) {
                            attributes["aria-label"] = "${context.i18n("media.v2.photo")} ${index + 1} / ${section.photos.size}"
                            img(src = "$albumUrl${event.year}/${section.sectionId}/${photo.name}", alt = "") {
                                attributes["loading"] = "lazy"
                            }
                        }
                    }
                }
            }
            div {
                a(href = "${context.uriBasePath}/${event.year}/media#photos", classes = "mxt-back-link") {
                    +"← ${context.i18n("media.v2.back")} ${event.year}"
                }
            }
        }
    }
