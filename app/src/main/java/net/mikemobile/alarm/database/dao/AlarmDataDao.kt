package net.mikemobile.alarm.database.dao

import androidx.room.*
import net.mikemobile.alarm.database.entity.Alarm


@Dao
interface AlarmDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(item: Alarm):Long

    @Update
    fun update(item: Alarm)

    @Delete
    fun delete(item: Alarm)

    @Query("SELECT * FROM alarm WHERE id = :id LIMIT 1")
    fun find(id:Int): Alarm?

    @Query("SELECT * FROM alarm WHERE owner_id = :id LIMIT 1")
    fun findIsOnOwner(id:Int): Alarm?


    @Query("SELECT * FROM alarm ORDER BY datetime")
    fun findAll(): List<Alarm>

    @Query("SELECT * FROM alarm WHERE id IN (:regions)")
    fun findAll(regions:List<Int>): List<Alarm>

    @Query("DELETE FROM alarm WHERE id = :id")
    fun delete(id:Int)

    @Query("DELETE FROM alarm WHERE owner_id = :owner_id")
    fun deleteToOwnerItem(owner_id:Int)

    @Query("SELECT * FROM alarm WHERE datetime <= :time ORDER BY datetime")
    fun findIsOnTimer(time:Long): List<Alarm>

}
