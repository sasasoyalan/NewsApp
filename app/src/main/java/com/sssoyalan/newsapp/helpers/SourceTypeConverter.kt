package com.sssoyalan.newsapp.helpers

import androidx.room.TypeConverter
import com.sssoyalan.newsapp.models.news.Source

class SourceTypeConverter {
    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name,name)
    }
}