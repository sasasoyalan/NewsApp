package com.sssoyalan.newsapp.generic

import androidx.room.TypeConverter
import com.sssoyalan.newsapp.models.Source
import org.json.JSONObject

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