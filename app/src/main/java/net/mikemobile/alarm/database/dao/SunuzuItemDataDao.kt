package net.mikemobile.alarm.database.dao

import androidx.room.*
import net.mikemobile.alarm.database.entity.SunuzuItem


@Dao
interface SunuzuItemDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(item: SunuzuItem):Long

    @Update
    fun update(item: SunuzuItem)

    @Delete
    fun delete(item: SunuzuItem)

    @Query("SELECT * FROM sunuzuItem WHERE id = :id LIMIT 1")
    fun find(id:Int): SunuzuItem?

    @Query("SELECT * FROM sunuzuItem")
    fun findAll(): List<SunuzuItem>
    @Query("SELECT * FROM sunuzuItem WHERE owner_id = :owner_id ORDER BY plusMinute")
    fun findAll(owner_id:Int): List<SunuzuItem>

    @Query("DELETE FROM sunuzuItem WHERE owner_id = :owner_id")
    fun delete(owner_id:Int)

    @Query("UPDATE sunuzuItem SET onoff = :onoff WHERE id = :id")
    fun updateOnOff(id:Int,onoff:Boolean)

    // owner_idが指定のものをすべて削除する
    @Query("DELETE FROM sunuzuItem WHERE owner_id = :owner_id")
    fun deleteSunuzuItemAll(owner_id:Int)
}