package se.erikwelander.apis.api.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "paste_d3ff_se")
data class PasteDbModel(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        val id: Long? = null,

        @Column(name = "type")
        val type: Int = 0,

        @Column(name = "hits")
        val hits: Int = 0,

        @Column(name = "title")
        val title: String = "",

        @Column(name = "language")
        val language: String = "",

        @Column(name = "data")
        val data: String = "",

        @Column(name="created")
        @JsonIgnore
        val created: Timestamp = Timestamp.from(Instant.now()),

        @Column(name = "ip")
        val ip: String = ""
)