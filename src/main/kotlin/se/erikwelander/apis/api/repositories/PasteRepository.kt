package se.erikwelander.apis.api.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import se.erikwelander.apis.api.models.PasteDbModel
import javax.transaction.Transactional

@Repository
interface PasteRepository : JpaRepository<PasteDbModel, Long> {
    @Modifying
    @Transactional
    @Query("update PasteDbModel s set s.hits = s.hits+1 where s.id = :id")
    fun increaseHitCount(@Param("id") id: Long): Int
}