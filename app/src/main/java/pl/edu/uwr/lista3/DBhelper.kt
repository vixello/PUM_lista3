/*
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBhelper(context: Context) : SQLiteOpenHelper (
    context, DATABASE_NAME, null, DATABASE_VERSION) {
    private companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "studentsDBKotlin.db"
        private const val TABLE_STUDENTS = "StudentTable"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
//        private const val COLUMN_TASKS = "description"

    }
    //onCreate - wywoływana gdy plik o zadanej nazwie (BADABASE_NAME) nie istnieje
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_STUDENTS_TABLE =
            "CREATE TABLE $TABLE_STUDENTS("+
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "$COLUMN_NAME TEXT," +
                    "$COLUMN_DESCRIPTION STRING)"
        db?.execSQL(CREATE_STUDENTS_TABLE)

    }
    //onUpgrade - wywoływana gdy schemat bazy uległ zmianie
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    fun addStudent(listForClass: ListForClass)
    {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME,listForClass.name)
        contentValues.put(COLUMN_DESCRIPTION,listForClass.description)
//        Następnie wykonujemy metodę insert na bazie, przyjmuje ona trzy argumenty
//
//        nazwę tabeli do której wykonana jest operacja
//                nullColumnHack - umożliwia wstawienie pustej wartości
//                ContentValues - dane do wstawienia
        db.insert(TABLE_STUDENTS, null, contentValues)
        db.close()

    }

    fun deleteStudent(listForClass: ListForClass)
    {
        val db = this.writableDatabase
        db.delete(
            TABLE_STUDENTS,
            "$COLUMN_ID=${listForClass.id}",
            null)
        db.close()

    }
    fun updateStudent (id: Int, name: String, description: String){
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_NAME,name)
        contentValues.put(COLUMN_DESCRIPTION,description)

        db.update(TABLE_STUDENTS,
            contentValues,
            "$COLUMN_ID=$id",
            null)
        db.close()

    }
    fun getStudents(): List<ListForClass> {
        val listForClasses: MutableList<ListForClass> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_STUDENTS", null)
//        W bazie mamy trzy kolumny (id: Int, name: String, index: Int),
//        z Cursor wyciągamy trzy wartości (ważna kolejność)
//        i używamy ich jako argumenty konstruktora klasy Student.
        if (cursor.moveToFirst())
            do {
                listForClasses.add(ListForClass(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)),

                )
            } while (cursor.moveToNext())
        db.close()
        cursor.close()
        return listForClasses
    }

}*/

package pl.edu.uwr.lista3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBhelper(context: Context) : SQLiteOpenHelper (
    context, DATABASE_NAME, null, DATABASE_VERSION) {
    private companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "studentsDBKotlin.db"
        private const val TABLE_LISTS = "StudentTable"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_TASKS = "tasks"
        private const val KEY_IMAGE = "image"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LISTS_TABLE =
            "CREATE TABLE $TABLE_LISTS(" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "$COLUMN_NAME TEXT," +
                    "$COLUMN_DESCRIPTION STRING," +
                    "$COLUMN_TASKS STRING,"+
                    "$KEY_IMAGE TEXT)"
        db?.execSQL(CREATE_LISTS_TABLE)

    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LISTS")
        onCreate(db)
    }

    fun addList(listForClass: ListForClass)
    {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME,listForClass.name)
        contentValues.put(COLUMN_DESCRIPTION,listForClass.description)
        contentValues.put(COLUMN_TASKS,listForClass.tasks)
        contentValues.put(KEY_IMAGE, listForClass.image)

        db.insert(TABLE_LISTS, null, contentValues)
        db.close()

    }

    fun deleteList(listForClass: ListForClass)
    {
        val db = this.writableDatabase
        db.delete(
            TABLE_LISTS,
            "$COLUMN_ID=${listForClass.id}",
            null)
        db.close()

    }
    fun updateList (id: Int, name: String, description: String, tasks: String, image: String){
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_NAME,name)
        contentValues.put(COLUMN_DESCRIPTION,description)
        contentValues.put(COLUMN_TASKS,tasks)
        contentValues.put(KEY_IMAGE,image)

        db.update(TABLE_LISTS,
            contentValues,
            "$COLUMN_ID=$id",
            null)
        db.close()

    }
    fun getLists(): List<ListForClass> {
        val listForClasses: MutableList<ListForClass> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LISTS", null)

        if (cursor.moveToFirst())
            do {
                listForClasses.add(ListForClass(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
                   )
                )
            } while (cursor.moveToNext())
        db.close()
        cursor.close()
        return listForClasses
    }

}
