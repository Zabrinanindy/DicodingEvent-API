package com.aplikasi.dicodingevents.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aplikasi.dicodingevents.data.remote.response.ListEventsItem

@Entity(tableName = "events")
data class EventEntity (
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id: Int,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "ownerName")
    val ownerName: String,

    @field:ColumnInfo(name = "description")
    val description: String,

    @field:ColumnInfo(name = "summary")
    val summary: String,

    @field:ColumnInfo(name = "mediaCover")
    val mediaCover: String,

    @field:ColumnInfo(name = "imageLogo")
    val imageLogo: String,

    @field:ColumnInfo(name = "link")
    val link: String,

    @field:ColumnInfo(name = "registrants")
    val registrants: Int,

    @field:ColumnInfo(name = "quota")
    val quota: Int,

    @field:ColumnInfo(name = "beginTime")
    val beginTime: String,

    @field:ColumnInfo(name = "endTime")
    val endTime: String,

    @ColumnInfo(name = "status", typeAffinity = ColumnInfo.TEXT)
    var status: String,

    @field:ColumnInfo(name = "favorited")
    var isFavorited: Boolean
) {
    companion object {
        fun fromListEventsItem(item: ListEventsItem): EventEntity {
            return EventEntity(
                id = item.id!!,
                name = item.name!!,
                ownerName = item.ownerName!!,
                description = item.description!!,
                summary = item.summary!!,
                mediaCover = item.mediaCover!!,
                imageLogo = item.imageLogo!!,
                link = item.link!!,
                registrants = item.registrants!!,
                quota = item.quota!!,
                beginTime = item.beginTime!!,
                endTime = item.endTime!!,
                status = "", // Set a default or pass it through the function if needed
                isFavorited = false // Default to false; this will be updated later
            )
        }
    }
}