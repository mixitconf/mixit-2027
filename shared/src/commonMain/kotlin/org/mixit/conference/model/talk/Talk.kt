package org.mixit.conference.model.talk

import kotlinx.datetime.LocalDateTime
import org.mixit.conference.model.favorite.Favorite
import org.mixit.conference.model.people.Speaker
import org.mixit.conference.model.shared.Language
import org.mixit.conference.model.link.Link
import org.mixit.conference.model.link.Video
import org.mixit.conference.shared.model.Topic

data class Talk(
    val id: String,
    val format: TalkFormat,
    val event: String,
    val title: String,
    val summary: String,
    val speakers: List<Speaker>,
    val language: Language,
    val description: String,
    val topic: Topic,
    val themes: List<Theme> = emptyList(),
    val room: Room,
    val start: String?,
    val end: String?,
    val photos: List<Link>,
    val videos: List<Video>,
    val slug: String,
    val level: TalkLevel?
) {
    fun startLocalTime(): LocalDateTime? = start?.let { LocalDateTime.parse(it) }
    fun endLocalTime(): LocalDateTime? = end?.let { LocalDateTime.parse(it) }

    fun search(term: String?): Boolean {
        val lowerTerm = term?.lowercase() ?: return true
        return title.lowercase().contains(lowerTerm) ||
            summary.lowercase().contains(lowerTerm) ||
            description.lowercase().contains(lowerTerm) ||
            speakers.any { speaker ->
                (speaker.firstname?.lowercase()?.contains(lowerTerm) == true) ||
                    (speaker.lastname?.lowercase()?.contains(lowerTerm) == true) ||
                    (speaker.company?.lowercase()?.contains(lowerTerm) == true) ||
                    speaker.description.values.any { it.lowercase().contains(lowerTerm) }
            }
    }

    fun searchByTopic(topic: Topic?): Boolean =
        this.topic == topic || topic == null

    fun searchByFavorite(favorites: List<String>, searchBy: Boolean): Boolean =
        if(!searchBy) true else favorites.contains(id)
}


