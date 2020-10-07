package com.example.dropspot.data.model

data class Relation(val id: RelationId,
                    private val senderId: Long,
                    private val recipientId: Long,
                    private var status: RelationStatus) {

    enum class RelationStatus {
        PENDING, ACCEPTED, DECLINED, BLOCKED
    }

    fun getOtherUserId(id: Long): Long {
        return if (senderId == id) recipientId else senderId
    }

}