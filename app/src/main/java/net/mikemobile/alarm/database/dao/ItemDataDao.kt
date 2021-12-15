package net.mikemobile.alarm.database.dao

import androidx.room.*
import net.mikemobile.alarm.database.entity.Item


@Dao
interface ItemDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(item: Item):Long

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)

    @Query("SELECT * FROM item WHERE id = :id LIMIT 1")
    fun find(id:Int): Item?

    @Query("SELECT * FROM item WHERE owner_id = 0 ORDER BY hour,minute")
    fun findAll(): List<Item>

    @Query("SELECT * FROM item WHERE id IN (:regions) ORDER BY hour,minute")
    fun findAll(regions:List<Int>): List<Item>

    @Query("DELETE FROM item WHERE id = :id OR owner_id = :id")
    fun delete(id:Int)

    @Query("UPDATE item SET onoff = :onoff WHERE id = :id")
    fun updateOnOff(id:Int,onoff:Boolean)


    @Query("SELECT * FROM item WHERE owner_id = :id")
    fun findAllToSunuzu(id:Int): List<Item>


    // owner_idが指定のものをすべて削除する
    @Query("DELETE FROM item WHERE owner_id = :owner_id")
    fun deleteSunuzuItemAll(owner_id:Int)



    // owner_idが指定のもので、対象のIDではないものをすべて削除する
    @Query("DELETE FROM item WHERE owner_id = :owner_id AND id NOT IN (:regions)")
    fun deleteSunuzuItem(owner_id:Int,regions:List<Int>)
}