package org.mixit.conference.ui.page

import kotlinx.html.InputType
import kotlinx.html.ScriptType
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Speaker
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CURRENT_YEAR
import org.mixit.conference.ui.HEADER_CTA
import org.mixit.conference.ui.TALKS_YEARS
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.banner
import org.mixit.conference.ui.component.editionMenu
import org.mixit.conference.ui.component.navHref
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.speakerTile
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.renderTemplate

/** Editions that have speakers: the past ones and the current one. */
private val SPEAKER_YEARS = (listOf(CURRENT_YEAR) + TALKS_YEARS).distinct()

fun renderSpeakers(
    context: Context,
    event: Event,
    speakers: List<Speaker>,
    sponsors: List<Sponsor>,
) =
    renderTemplate(context, event) {
        pageHeaderComponent(
            context,
            Rubrique.SPEAKER,
            title = context.i18n("speaker.title"),
            intro = context.i18n("speaker.intro"),
        ) {
            editionMenu(context.i18n("page.edition"), SPEAKER_YEARS, event.year) { "${context.uriBasePath}/$it/speakers" }
        }

        pageContent {
            // The next edition: speakers are selected through the call for papers
            banner(
                title = context.i18n("speaker.v2.soon.title"),
                text = context.i18n("speaker.v2.soon.text"),
                ctaLabel = context.i18n(HEADER_CTA.title),
                ctaHref = context.navHref(HEADER_CTA),
            )

            pageSection("${context.i18n("speaker.v2.list.title")} ${event.year}", context.i18n("speaker.selection")) {
                if (speakers.isEmpty()) {
                    div(classes = "mxt-empty") { +context.i18n("speaker.v2.empty") }
                } else {
                    div(classes = "mxt-search mxt-speakers__search") {
                        label(classes = "visually-hidden") {
                            htmlFor = "speaker-search"
                            +context.i18n("speaker.v2.search")
                        }
                        input(type = InputType.search, classes = "mxt-search__input") {
                            id = "speaker-search"
                            placeholder = context.i18n("speaker.v2.search")
                            attributes["data-filter-list"] = "speaker-list"
                        }
                    }
                    ul(classes = "mxt-speakers") {
                        id = "speaker-list"
                        speakers.sortedBy { it.firstname?.lowercase() }.forEach { speaker ->
                            li { speakerTile(speaker, "${context.uriBasePath}/speakers/${event.year}/${speaker.id}") }
                        }
                    }
                    p(classes = "mxt-empty") {
                        id = "speaker-list-empty"
                        attributes["hidden"] = "hidden"
                        +context.i18n("speaker.v2.noresult")
                    }
                    script(type = ScriptType.textJavaScript) {
                        unsafe {
                            raw(
                                """
                                (function () {
                                  var input = document.getElementById('speaker-search');
                                  var items = document.querySelectorAll('#speaker-list > li');
                                  var empty = document.getElementById('speaker-list-empty');
                                  input.addEventListener('input', function () {
                                    var term = input.value.trim().toLowerCase();
                                    var shown = 0;
                                    items.forEach(function (item) {
                                      var match = item.firstElementChild.dataset.search.indexOf(term) >= 0;
                                      item.hidden = !match;
                                      if (match) shown++;
                                    });
                                    empty.hidden = shown > 0;
                                  });
                                })();
                                """.trimIndent()
                            )
                        }
                    }
                }
            }
        }

        sponsorsSection(context, event, sponsors)
    }
