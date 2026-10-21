package org.mixit.infra.spi.talk

import kotlinx.serialization.Serializable
import org.mixit.conference.model.link.Link
import org.mixit.conference.model.link.LinkType
import org.mixit.conference.model.link.Video
import org.mixit.conference.model.people.Speaker
import org.mixit.conference.model.shared.Language
import org.mixit.conference.model.shared.decodeHtmlEntities
import org.mixit.conference.model.talk.Room
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.model.talk.TalkLevel
import org.mixit.conference.model.talk.Theme
import org.mixit.conference.shared.model.Topic
import org.mixit.infra.spi.event.LinkDto

@Serializable
data class TalkDto(
    val format: TalkFormat,
    val event: String,
    val title: String,
    val summary: String,
    val speakerIds: List<String> = emptyList(),
    val language: Language,
    val addedAt: String,
    val description: String? = null,
    val topic: String? = null,
    val themes: List<String> = emptyList(),
    val video: String? = null,
    val video2: String? = null,
    val room: Room? = Room.UNKNOWN,
    val start: String? = null,
    val end: String? = null,
    val photoUrls: List<LinkDto> = emptyList(),
    val slug: String,
    val level: TalkLevel? = null,
    val id: String? = null,
) {
    fun toTalk(speakers: List<Speaker>) =
        Talk(
            id = id!!,
            title = title.decodeHtmlEntities(),
            format = format,
            language = language,
            summary = summary.decodeHtmlEntities(),
            description = description ?: "",
            speakers = speakers,
            event = event,
            start = start,
            end = end,
            room = room ?: Room.UNKNOWN,
            level = level,
            photos = photoUrls.map { Link(type = LinkType.PHOTO, url = it.url) },
            videos = listOfNotNull(video?.let { Video(it) }, video2?.let { Video(it) }),
            topic = Topic.of(topic),
            themes = themes.mapNotNull { Theme.of(it) }.distinct(),
            slug = slug,
        )
}
