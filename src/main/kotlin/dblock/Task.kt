package dblock

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull
import java.util.*

@Entity
class Task() {

    @Id
    lateinit var id: UUID

    @NotNull
    @Enumerated(EnumType.STRING)
    lateinit var status: Status

    constructor(id: UUID, status: Status) : this() {
        this.id = id
        this.status = status
    }

    enum class Status {
        TODO,
        IN_PROGRESS,
        SUCCESS,
        FAILURE,
    }
}
