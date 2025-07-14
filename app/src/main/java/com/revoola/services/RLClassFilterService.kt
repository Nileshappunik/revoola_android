package com.revoola.services

import com.revoola.model.RLMindBodyFilterGroupItemModel
import com.revoola.model.RLVideoModel
import java.util.Locale

object RLClassFilterService {

     fun rl_getFilterVideoList(selectedChildData: List<RLMindBodyFilterGroupItemModel>, videoList_Filter:MutableList<RLVideoModel>):List<RLVideoModel>{
        return videoList_Filter.filter { video ->
            selectedChildData.all { filterGroup ->
                when (filterGroup.title.lowercase(Locale.getDefault())) {
                     "difficulty" ->{
                         filterGroup.childItems.contains(video.difficulty)
                     }
                    "instructor" -> {
                        filterGroup.childItems.contains(video.instructor)
                    }
                    "class type" -> {
                        filterGroup.childItems.contains(video.classtype)
                    }
                    "duration" -> {
                        filterGroup.childItems.contains(video.duration)
                    }
                    else -> true // If no matching filter, include the video
                }
            }
        }
    }
}
