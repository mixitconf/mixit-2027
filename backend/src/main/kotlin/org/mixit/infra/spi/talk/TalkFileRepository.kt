package org.mixit.infra.spi.talk

import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.domain.spi.PeopleRepository
import org.mixit.domain.spi.TalkRepository
import org.springframework.stereotype.Component

@Component
class TalkFileRepository(
    private val repository: TalkStaticFileRepository,
    private val peopleRepository: PeopleRepository,
) : TalkRepository {
    companion object {
        // We filter the keynote displayed on the home page to avoid too old videos (before our captation)
        val keynoteDisplayedOnHomePage = listOf(2017, 2018, 2019, 2021, 2022, 2023, 2024, 2025)
    }

    override fun findByYear(year: Int): List<Talk> =
        (repository.findAll()[year] ?: emptyList()).map {
            it.toTalk(peopleRepository.findSpeakerByIds(it.speakerIds))
        }

    override fun findAllWithMedia(): List<Talk> =
        repository
            .findAll()
            .values
            .flatten()
            .filter { it.video != null || it.video2 != null || it.format == TalkFormat.ON_AIR }
            .map { it.toTalk(peopleRepository.findSpeakerByIds(it.speakerIds)) }

    override fun findRandomKeynote(nb: Int): List<Talk> =
        repository
            .findAll()
            .filter { keynoteDisplayedOnHomePage.contains(it.key) }
            .asSequence()
            .flatMap { (_, value) -> value.filter { it.format == TalkFormat.KEYNOTE && it.video != null } }
            .shuffled()
            .take(nb)
            .map { it.toTalk(peopleRepository.findSpeakerByIds(it.speakerIds)) }
            .toList()

    override fun findSpeakerTalks(speakerId: String): List<Talk> =
        repository
            .findAll()
            .entries
            .flatMap { (_, talks) -> talks.filter { it.speakerIds.contains(speakerId) } }
            .map { it.toTalk(peopleRepository.findSpeakerByIds(it.speakerIds)) }
            .sortedBy { it.event }

    override fun findById(id: String): Talk? =
        repository
            .findAll()
            .values
            .flatten()
            .firstOrNull { it.id == id }
            ?.let {
                it.toTalk(peopleRepository.findSpeakerByIds(it.speakerIds))
            }

    override fun findBySlug(
        year: Int,
        slug: String,
    ): Talk? = findByYear(year).firstOrNull { it.slug.startsWith(slug.trim()) }

    override fun exportByYear(year: Int): List<TalkDto> = (repository.findAll()[year] ?: emptyList())
}
