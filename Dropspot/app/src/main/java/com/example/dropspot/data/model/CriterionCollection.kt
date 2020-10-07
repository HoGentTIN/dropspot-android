package com.example.dropspot.data.model

import java.io.Serializable

data class CriterionCollection(private val criterionCollectionId: Long,
                          private var collectionName: String = "undefined",
                          private val criteria: Set<Criterion>) : Serializable {

}