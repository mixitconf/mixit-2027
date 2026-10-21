package org.mixit.infra.api

import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.Theme
import org.mixit.conference.shared.model.Topic
import org.mixit.conference.ui.CURRENT_YEAR
import org.mixit.conference.ui.page.renderArchiveTheme
import org.mixit.conference.ui.page.renderArchiveTopic
import org.mixit.conference.ui.page.renderArchiveYear
import org.mixit.conference.ui.page.renderArchives
import org.mixit.domain.spi.EventRepository
import org.mixit.domain.spi.PeopleRepository
import org.mixit.domain.spi.TalkRepository
import org.mixit.infra.config.WebContext
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.ServerResponse

@Component
class ArchiveHandler(
    private val talkRepository: TalkRepository,
    private val peopleRepository: PeopleRepository,
    private val eventRepository: EventRepository,
    private val webContext: WebContext,
) {
    // Talks are static data loaded at startup: the grouping is computed once
    private val archivedTalks: List<Talk> by lazy { talkRepository.findAllWithMedia() }

    private val talksByTheme: Map<Theme, List<Talk>> by lazy {
        Theme.entries.associateWith { theme -> archivedTalks.filter { it.themes.contains(theme) } }
    }

    private val talksByTopic: Map<Topic, List<Talk>> by lazy {
        Topic.entries.associateWith { topic -> archivedTalks.filter { it.topic == topic } }
    }

    fun findThemes(): ServerResponse {
        val event = eventRepository.findByYear(CURRENT_YEAR) ?: return ServerResponse.notFound().build()
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(
            renderArchives(
                context = webContext.context ?: Context.default(),
                event = event,
                sponsors = peopleRepository.findSponsorByYear(CURRENT_YEAR),
                talksByTheme = talksByTheme,
                talksByTopic = talksByTopic,
            ),
        )
    }

    fun findTheme(slug: String): ServerResponse {
        val event = eventRepository.findByYear(CURRENT_YEAR) ?: return ServerResponse.notFound().build()
        val theme = Theme.ofSlug(slug) ?: return ServerResponse.notFound().build()
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(
            renderArchiveTheme(
                context = webContext.context ?: Context.default(),
                event = event,
                sponsors = peopleRepository.findSponsorByYear(CURRENT_YEAR),
                theme = theme,
                talks = talksByTheme.getValue(theme),
            ),
        )
    }

    fun findYear(year: Int): ServerResponse {
        val event = eventRepository.findByYear(CURRENT_YEAR) ?: return ServerResponse.notFound().build()
        val talks = archivedTalks.filter { it.event == year.toString() }
        if (talks.isEmpty()) return ServerResponse.notFound().build()
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(
            renderArchiveYear(
                context = webContext.context ?: Context.default(),
                event = event,
                sponsors = peopleRepository.findSponsorByYear(CURRENT_YEAR),
                year = year,
                talks = talks,
            ),
        )
    }

    fun findTopic(value: String): ServerResponse {
        val event = eventRepository.findByYear(CURRENT_YEAR) ?: return ServerResponse.notFound().build()
        val topic = Topic.ofValue(value) ?: return ServerResponse.notFound().build()
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(
            renderArchiveTopic(
                context = webContext.context ?: Context.default(),
                event = event,
                sponsors = peopleRepository.findSponsorByYear(CURRENT_YEAR),
                topic = topic,
                talks = talksByTopic.getValue(topic),
            ),
        )
    }
}
