package net.mikemobile.alarm.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.mikemobile.alarm.database.dao.AlarmDataDao
import net.mikemobile.alarm.database.dao.ItemDataDao
import net.mikemobile.alarm.database.dao.SunuzuItemDataDao
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem


/**
 * Dao =  Data Access Object
 * データへのアクセス（追加、削除、編集、取り出し）を実際に設定するためのインターフェースクラス
 */





/**
 * database
 * DAOを呼び出して使用するためのDBまとめ用クラス。
 * データの実際の取り出しは、このクラスを呼び出して使用します。
 */

@Database(entities = [Item::class, Alarm::class, SunuzuItem::class], version = DataBaseManager.DB_VER, exportSchema = false)
abstract class DataBaseManager : RoomDatabase() {

    companion object {
        const val DB_VER = 8
        const val DB_NAME = "sample-timer-database"

        enum class ERROR_TYPE {
            READ,
            SAVE,
            DELETE
        }

        /**
         * addMigrationsにはカンマ区切りで複数の値を入れることが出来ます。
         * バージョンアップ時に変更がある場合は、その都度のものを作成・追加してください。
         */
        fun getInstance(context: Context): DataBaseManager{
            return Room.databaseBuilder(context, DataBaseManager::class.java, DB_NAME)
                //.addMigrations(object : Migration(DB_VER-1, DB_VER) {
                //    override fun migrate(database: SupportSQLiteDatabase) {}
                //})
                .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_5_8)
                .build()
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'item' ADD COLUMN 'stopMode' INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'item' ADD COLUMN 'stopModeSelect' INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'alarm' ADD COLUMN 'stopMode' INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE 'alarm' ADD COLUMN 'stopModeSelect' INTEGER NOT NULL DEFAULT 0")
            }
        }
        // リリース時のバージョン差用
        val MIGRATION_5_8 = object : Migration(5, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'item' ADD COLUMN 'stopMode' INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE 'item' ADD COLUMN 'stopModeSelect' INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE 'alarm' ADD COLUMN 'stopMode' INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE 'alarm' ADD COLUMN 'stopModeSelect' INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

    //アイテム情報
    abstract fun onItemDataDao(): ItemDataDao
    abstract fun onAlarmDataDao(): AlarmDataDao
    abstract fun onSunuzuItemDataDao(): SunuzuItemDataDao




}