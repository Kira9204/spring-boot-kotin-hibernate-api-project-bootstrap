package se.erikwelander.apis.api.models

import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "s_d3ff_se")
data class ShortLinkDBModel (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        val id: Long? = null,

        @Column(name = "hits")
        val hits: Int = 0,

        @Column(name = "link")
        val link: String = "",

        @Column(name="created")
        val created: Timestamp = Timestamp.from(Instant.now()),

        @Column(name="ip")
        val ip: String = ""
)