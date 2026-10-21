package org.mixit.domain.spi

import org.mixit.conference.model.talk.Talk
import org.mixit.infra.spi.talk.TalkDto

interface TalkRepository {
    fun findByYear(year: Int): List<Talk>

    fun findAllWithMedia(): List<Talk>

    fun findRandomKeynote(nb: Int): List<Talk>

    fun findSpeakerTalks(speakerId: String): List<Talk>

    fun findById(id: String): Talk?

    fun findBySlug(
        year: Int,
        slug: String,
    ): Talk?

    fun exportByYear(year: Int): List<TalkDto>
}
