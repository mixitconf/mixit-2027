package org.mixit.infra.api

import org.mixit.conference.model.feedback.TalkFeedback
import org.mixit.conference.model.people.Role
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.ui.form.FormDescriptor
import org.mixit.conference.ui.page.TalksCriteria
import org.mixit.conference.ui.page.renderTalk
import org.mixit.conference.ui.page.renderTalkFeedbacks
import org.mixit.conference.ui.page.renderTalks
import org.mixit.domain.spi.EventRepository
import org.mixit.domain.spi.PeopleRepository
import org.mixit.domain.spi.TalkRepository
import org.mixit.infra.config.MixitProperties
import org.mixit.infra.config.WebContext
import org.mixit.infra.spi.manager.ManagerFavoriteApi
import org.mixit.infra.spi.manager.ManagerFeedbackApi
import org.mixit.infra.spi.manager.ManagerUserApi
import org.mixit.infra.util.rest.QrCodeService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.function.ServerResponse

@Component
class TalkHandler(
    private val talkRepository: TalkRepository,
    private val peopleRepository: PeopleRepository,
    private val favoriteApi: ManagerFavoriteApi,
    private val eventRepository: EventRepository,
    private val mixitProperties: MixitProperties,
    private val managerFeedbackApi: ManagerFeedbackApi,
    private val managerUserApi: ManagerUserApi,
    private val webContext: WebContext,
) {
    fun findTalkByYear(
        year: Int,
        filter: FormDescriptor<TalksCriteria>,
    ): ServerResponse {
        val filterValues = filter.value()
        val event = eventRepository.findByYear(year) ?: return ServerResponse.notFound().build()
        val favorites =
            try {
                webContext.context?.email?.let { favoriteApi.getFavorites(it).map { it.talkId } } ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }

        val yearTalks = talkRepository.findByYear(year).filter { it.format != TalkFormat.ON_AIR }
        val talks =
            yearTalks
                .filter {
                    it.search(filterValues.filter) &&
                        it.searchByTopic(filterValues.topic) &&
                        it.searchByFavorite(favorites, filterValues.favorites)
                }.sortedBy { it.startLocalTime() }
                .groupBy { it.startLocalTime() }

        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(
            renderTalks(
                webContext.context!!,
                event,
                peopleRepository.findSponsorByYear(year),
                favorites,
                talks,
                filter,
                yearTalks,
            ),
        )
    }

    fun findTalkFeedbacks(
        year: Int,
        filter: FormDescriptor<TalksCriteria>,
    ): ServerResponse {
        val filterValues = filter.value()
        val event = eventRepository.findByYear(year) ?: return ServerResponse.notFound().build()

        val talks =
            talkRepository
                .findByYear(year)
                .filter {
                    it.search(filterValues.filter) &&
                        it.format != TalkFormat.ON_AIR &&
                        it.searchByTopic(filterValues.topic)
                }.associateWith {
                    QrCodeService.generateSvg("https://mixitconf.org/$year/${it.slug}#feedback")
                }

        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(
            renderTalkFeedbacks(
                webContext.context!!,
                event,
                peopleRepository.findSponsorByYear(year),
                talks,
                filter,
            ),
        )
    }

    fun findTalkBySlug(
        year: Int,
        slug: String,
    ): ServerResponse {
        val event = eventRepository.findByYear(year) ?: return ServerResponse.notFound().build()
        val talk =
            talkRepository
                .findBySlug(year, slug)
                ?: return ServerResponse.notFound().build()

        val favorite =
            try {
                if (webContext.context?.email == null) {
                    false
                } else {
                    favoriteApi.getFavorite(webContext.context!!.email!!, talk.id)
                }
            } catch (_: Exception) {
                false
            }

        val displayFeedback = mixitProperties.features.feedback
        val isATalkSpeakerOrAdmin =
            (webContext.context?.email != null) &&
                (
                    webContext.context?.role == Role.STAFF ||
                        talk.speakers.any { it.email == webContext.context?.email }
                )

        val (feedback: TalkFeedback?, is401) =
            try {
                managerFeedbackApi.getAllTalkFeedback(talk.id) to false
            } catch (_: HttpClientErrorException.Unauthorized) {
                null to true
            }

        val userFeedback =
            try {
                if (webContext.context?.isAuthenticated ?: false) {
                    managerFeedbackApi.getUserFeedback(talk.id)
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }

        val page =
            renderTalk(
                webContext.context!!,
                event,
                peopleRepository.findSponsorByYear(year),
                talk,
                isFavorite = favorite,
                isATalkSpeakerOrAdmin = isATalkSpeakerOrAdmin,
                displayFeedback = displayFeedback,
                talkFeedback = feedback,
                userFeedback,
                related =
                    talkRepository
                        .findByYear(year)
                        .filter { it.id != talk.id && it.topic == talk.topic && it.format != TalkFormat.ON_AIR }
                        .shuffled()
                        .take(3),
            )

        return if (is401) {
            ServerResponse
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .cookie(managerUserApi.removeCookie())
                .body(page)
        } else {
            ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(page)
        }
    }

    fun findByYearIsJson(year: Int): ServerResponse =
        ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
            talkRepository.exportByYear(year),
        )
}
