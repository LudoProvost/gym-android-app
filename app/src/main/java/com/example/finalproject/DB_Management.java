package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DB_Management extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public DB_Management(Context context) {

        super(context,"Login.db", null,1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        myDB.execSQL("CREATE TABLE users(" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT)");

        myDB.execSQL("CREATE TABLE role_types(" +
                "role_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "role_name TEXT)");

        myDB.execSQL("CREATE TABLE class_types(" +
                "class_type TEXT PRIMARY KEY," +
                "description TEXT)");

        myDB.execSQL("CREATE TABLE class_difficulties(" +
                "difficulty_id TEXT PRIMARY KEY)");

        myDB.execSQL("CREATE TABLE classes(" +
                "class_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT," +
                "difficulty TEXT," +
                "start_time INTEGER," +
                "end_time INTEGER," +
                "capacity INTEGER," +
                "instructor TEXT," +
                "FOREIGN KEY(type) REFERENCES class_types(class_type)," +
                "FOREIGN KEY(difficulty) REFERENCES class_difficulties(difficulty_id)," +
                "FOREIGN KEY(instructor) REFERENCES users(username))");

        myDB.execSQL("CREATE TABLE roles(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT, " +
                "role_id TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(username) ON DELETE CASCADE," +
                "FOREIGN KEY(role_id) REFERENCES role_types(role_id)) ");

        myDB.execSQL("CREATE TABLE class_enrolment(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id TEXT," +
                "class_id TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(username) ON DELETE CASCADE," +
                "FOREIGN KEY(class_id) REFERENCES classes(class_id) ON DELETE CASCADE)");

        myDB.execSQL("INSERT INTO class_difficulties(difficulty_id) VALUES(\"easy\")");
        myDB.execSQL("INSERT INTO class_difficulties(difficulty_id) VALUES(\"medium\")");
        myDB.execSQL("INSERT INTO class_difficulties(difficulty_id) VALUES(\"hard\")");

        myDB.execSQL("INSERT INTO role_types(role_name) VALUES(\"Administrator\")");
        myDB.execSQL("INSERT INTO role_types(role_name) VALUES(\"Instructor\")");
        myDB.execSQL("INSERT INTO role_types(role_name) VALUES(\"Member\")");

        myDB.execSQL("INSERT INTO class_types(class_type, description) VALUES(\"Yoga\", \"Experience peace and relaxation!\")");
        myDB.execSQL("INSERT INTO class_types(class_type, description) VALUES(\"Kickboxing\", \"Boxing, but with kicks!\")");
        myDB.execSQL("INSERT INTO class_types(class_type, description) VALUES(\"Karate\", \"Half Art, Half Combat\")");
        myDB.execSQL("INSERT INTO class_types(class_type, description) VALUES(\"MortalCombat\", \"Nothing like Immortal Combat\")");

        myDB.execSQL("INSERT INTO users(username, password) VALUES(\"admin\", \"admin123\")");

        myDB.execSQL("INSERT INTO roles(user_id, role_id) VALUES(\"admin\",\"1\")");

        myDB.execSQL("INSERT INTO users(username, password) VALUES(\"zor\", \"1234\")");

        myDB.execSQL("INSERT INTO roles(user_id, role_id) VALUES(\"zor\",\"3\")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion) {
        myDB.execSQL("drop Table if exists users");
        // TODO: Figure out a better way of doing this.
    }

    /**
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param is_instructor A boolean if the user wants to be an instructor
     * @param is_member A boolean if the user wants to be a member.
     * @return Returns 0 if successful, 1 if the user exists, 2 if there was an error adding the instructor role, 3 if there was an error adding the member role, 4 if neither role was selected.
     */
    public int insertNewUser(String username, String password, boolean is_instructor, boolean is_member) {
        if(!is_instructor && !is_member)
            return 4;


        ContentValues insertValues = new ContentValues();
        insertValues.put("username",username);
        insertValues.put("password",password);
        long users = db.insert("users", null, insertValues);

        if(users <= 0){
            return 1;
        }

        if(is_instructor) {
            insertValues = new ContentValues();
            insertValues.put("user_id",username);
            insertValues.put("role_id",2 +"");
            long result = db.insert("roles", null, insertValues);

            if (result <= 0) {
                return 2;
            }
        }

        if(is_member) {
            insertValues = new ContentValues();
            insertValues.put("user_id",username);
            insertValues.put("role_id",3+"");
            long result = db.insert("roles", null, insertValues);

            if (result <= 0) {
                return 3;
            }
        }

        return 0;

    }

    /** Method tells the database to delete user. Will also cascade delete that user from enrolled classes and any roles they have.
     *
     * @param username the user to delete from the database.
     * @return Returns true if successful, false otherwise.
     */
    public Boolean deleteUser(String username){

        if(username.contains("admin")){
            return false;
        }

        db.delete("users", "username ='" + username + "'", null);

        return !checkUsername(username); // It was found. Did not delete.
    }

    /** Method takes in both the username and the role_id and deletes the corresponding pair in the database.
     *
     * @param username the username of the role to be deleted.
     * @param role_id the role of the user to be deleted.
     * @return returns true if successful, false otherwise.
     */
    public Boolean deleteRole(String username, int role_id){
        String query = "DELETE FROM roles WHERE user_id ='" + username + "' AND role_id = " + role_id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    /** Method deletes a record of enrolment in a given class.
     *
     * @param username the username of the user to be deleted from enrolment.
     * @param class_id the class to be unenrolled from.
     * @return returns true if successful, false otherwise.
     */
    public Boolean unenrolUser(String username, int class_id){
        String query = "DELETE FROM class_enrolment WHERE user_id ='" + username + "' AND class_id = " + class_id;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    /** Method deletes a users Member role from the role table. Helper method.
     *
     * @param username who's member role is to be deleted.
     * @return returns true if successful, false otherwise.
     */
    public Boolean deleteMemberRole(String username){
        return deleteRole(username, 3);
    }

    /** Method deletes a users Instructor role from the role table. Helper method.
     *
     * @param username who's Instructor role is to be deleted.
     * @return returns true if successful, false otherwise.
     */
    public Boolean deleteInstructorRole(String username){
        return deleteRole(username, 2);
    }

    /**
     * Method checks if the username is in the database.
     * @param username the username to be checked.
     * @return returns true if they found that user.
     */
    public Boolean checkUsername(String username) {
        Cursor cursor = db.rawQuery("select * from users where username = ?",new String[] {username});
        if(cursor.getCount()>0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    /**
     * Checks if the username and password match the database. We are aware that this is horrifically done.
     * @param username the username to validate
     * @param password the password to validate
     * @return returns true if it's a valid login.
     */
    public Boolean checkUsernameAndPassword(String username, String password) {
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ? and password = ?",new String[] {username,password});
        if(cursor.getCount()>0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    /** Method to add a class to the class table.
     *
     * @param type The type of class, like Yoga.
     * @param difficulty the difficulty of the class, must be one present in the class_difficulties table.
     * @param start_time the start time in UTC.
     * @param end_time the end time in UTC
     * @param capacity Capacity of the class.
     * @param instructor the username of the user who is instructing this class. This will technically allow a member to be an instructor.
     * @return Returns true if it has been added, false otherwise.
     */
    public Boolean createClass(String type, String difficulty, long start_time, long end_time, int capacity, String instructor){
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("difficulty", difficulty);
        contentValues.put("start_time", start_time);
        contentValues.put("end_time", end_time);
        contentValues.put("capacity", capacity);
        contentValues.put("instructor", instructor);

        long rowID = db.insert("classes", null, contentValues);
        return (rowID != -1);
    }

    /**
     * Method adds a Class Type to the Database
     * @param class_type the Type of class, like Karate
     * @param description The description
     * @return Returns true if it's been added.
     */
    public Boolean createClassType(String class_type, String description){

        ContentValues insertValues = new ContentValues();

        insertValues.put("class_type",class_type);
        insertValues.put("description",description);
        long insertionStatus = db.insert("class_types", null, insertValues);

        return insertionStatus > 0;
    }

    /**
     * Method deletes the class type.
     * @param class_type the class type to be deleted.
     * @return returns true if deleted.
     */
    public Boolean deleteClassType(String class_type){

        int rowsDeleted = db.delete("class_types", "class_type ='" + class_type + "'", null);

        return rowsDeleted > 0;
    }

    /**
     * Edits teh class type, should edit both the name and description.
     * @param old_class_type the class type to be changed.
     * @param new_class_type the class type to be updated.
     * @param description the description to be updated.
     * @return returns true if successful.
     */
    public Boolean editClassType(String old_class_type, String new_class_type, String description){

        ContentValues cv = new ContentValues();

        cv.put("class_type", new_class_type);
        cv.put("description", description);

        int rowsUpdated = db.update("class_types", cv, "class_type =?", new String[]{old_class_type});

        return rowsUpdated > 0;

    }

    /** Method edits a user's associate roles.
     *
     * @param username the username of the user to edit roles.
     * @param isInstructor Will they have the instructor role?
     * @param isMember Will they have the member role?
     * @return returns true if successful.
     */
    public Boolean editUserRoles(String username, Boolean isInstructor, Boolean isMember){

        deleteMemberRole(username);
        deleteInstructorRole(username);

        if(isInstructor)
            if(!addInstructorRole(username))
                return false;
        if(isMember)
            return addMemberRole(username);

        return true;

    }

    /** Method adds a role to the user
     *
     * @param username username of the user to have a role added to.
     * @return returns true if the user gets a member role added.
     */
    public Boolean addMemberRole(String username){

        ContentValues cv = new ContentValues();

        cv.put("user_id", username);
        cv.put("role_id", 3);
        long rowsUpdated = db.insert("roles", null, cv);

        return rowsUpdated > 0;
    }

    /** Method adds the instructor role to the user.
     *
     * @param username the username of the user who is to get the instructor role.
     * @return returns true if successful.
     */
    public Boolean addInstructorRole(String username){

        ContentValues cv = new ContentValues();

        cv.put("user_id", username);
        cv.put("role_id", 2);
        long rowsUpdated = db.insert("roles", null, cv);


        return rowsUpdated > 0;
    }

    /**
     * Method enrols a user into a given class.
     * @param username the username of the user we want to enrol.
     * @param class_id the class_id of the class they want to enrol in.
     * @return returns true if successful.
     */
    public Boolean enrollInClass(String username, String class_id){

        ContentValues cv = new ContentValues();
        int capacity = 0;

        cv.put("user_id", username);
        cv.put("class_id", class_id);
        long rowsUpdated = db.insert("class_enrolment", null, cv);

        return rowsUpdated > 0;
    }

    /** Method deletes a class from the class table using the class_id.
     *
     * @param class_id the class_id from the class table to be deleted.
     * @return returns true if deleted, otherwise returns false.
     */
    public Boolean deleteClass(int class_id){

        String query = "DELETE FROM classes WHERE class_id =" + class_id;
        Cursor cursor = db.rawQuery(query, null);
        Boolean result = cursor.getCount() <= 0;

        cursor.close();
        return result;
    }

    /**
     * Method updates a class, all fields except class_id can be updated.
     * @param class_id the class to be updated.
     * @param type type of class, which should be a class present in the class_types table.
     * @param difficulty the difficulty, which should be found in the class_difficulties table.
     * @param start_time Time in utc of when the class starts,
     * @param end_time  Time in utc of when the class ends.
     * @param capacity the capacity of the class.
     * @param instructor the instructor, of which should be a person present in the users table who has a role of instructor in the roles table.
     * @return returns true if success, false otherwise.
     */
    public Boolean updateClass(int class_id, String type, String difficulty, long start_time, long end_time, int capacity, String instructor){

        String query = "UPDATE classes" +
                " SET type = '" + type + "', difficulty = '" + difficulty + "', start_time = " + start_time + ", end_time = " + end_time + ", capacity = " + capacity + ", instructor = '" + instructor +
                "' WHERE class_id = " + class_id;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    /**
     * Method returns a list of roles a specific user has.
     * @param username the user we are checking for roles.
     * @return a array of strings that contain the results.
     */
    @SuppressLint("Range")
    public String[] getUserRoles(String username){
        String[] results = new String[3];
        String query = "SELECT role_id FROM roles WHERE user_id = '" + username + "'";
        Cursor cursor = db.rawQuery(query, null);

        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                results[i] = cursor.getString(0);
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        return results;
    }

    /**
     * Gets a list of all users in the database.
     * @return returns a list of strings with usernames of those users. These are the primary keys.
     */
    public List<String> getAllUsers() {

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM users";
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Gets a list of all users in the database.
     * @return returns a list of strings with usernames of those users. These are the primary keys.
     */
    public List<String> getAllInstructors() {

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM users LEFT JOIN roles ON users.username = roles.user_id WHERE role_id = 2";
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * gets a list of all class types.
     * @return returns a list of strings with all class types.
     */
    public List<String> getAllClassTypes() {

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM class_types";
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Method returns a list of schedules classes.
     *
     * @return returns a list of space separated elements of the classes and their info.
     */
    public List<String> getAllScheduledClasses(){

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM classes";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");


        if (cursor.moveToFirst()) {
            do {
                String s = "";
                for(int i =1; i < 7; i++){
                    if(i == 3){
                        Date date = new Date(cursor.getLong(i));
                        s += dateFormat.format(date);
                        s += " ";
                        s += timeFormat.format(date);
                    }else if(i == 4) {
                        s += "-";
                        Date date = new Date(cursor.getLong(i));
                        s += timeFormat.format(date);
                        s += " ";
                    }else {
                        s += cursor.getString(i);
                        s+= " ";
                    }
                }
                list.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Method returns a list of schedules classes WITH THE CLASS ID.
     *
     * @return returns a list of space separated elements of the classes and their info.
     */
    public List<String> getAllScheduledClassesWithID(){

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM classes";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");


        if (cursor.moveToFirst()) {
            do {
                String s = "";
                for(int i =0; i < 7; i++){
                    if(i == 3){
                        Date date = new Date(cursor.getLong(i));
                        s += dateFormat.format(date);
                        s += " ";
                        s += timeFormat.format(date);
                    }else if(i == 4) {
                        s += "-";
                        Date date = new Date(cursor.getLong(i));
                        s += timeFormat.format(date);
                        s += " ";
                    }else {
                        s += cursor.getString(i);
                        s+= " ";
                    }
                }
                list.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Method asks the Database for all classes currently enrolled by user.
     *
     * @param username the user of the classes we want to see.
     * @return a list of Strings with the specified classes.
     */
    public List<String> getAllClassesByEnrolment(String username){

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  class_id FROM class_enrolment WHERE user_id = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Method will get a count of the amount of free space left in the given class.
     * @param class_id the class_id of which we determine free space.
     * @return the amount of free spaces. 99999999 indicates an error.
     */
    public int getFreeSpotsOfClass(String class_id){
        String selectQuery = "SELECT COUNT(*) FROM class_enrolment WHERE class_id = '" + class_id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String result = "-1";
        int integerResult = 99999999;

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        integerResult = getCapacityOfClass(class_id) - Integer.parseInt(result);

        return integerResult;
    }

    /**
     * Method finds the capacity of a given class via it's class_id
     * @param class_id the class id of the class we want to find the capacity.
     * @return the capacity of the class.
     */
    public int getCapacityOfClass(String class_id){
        String selectQuery = "SELECT capacity FROM classes WHERE class_id = '" + class_id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String result = "-1";

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return Integer.parseInt(result);
    }

    /**
     * gets the start time and end time of a given class via it's class_id
     * @param class_id the class id of the class we want to find the start
     *                 time and end time.
     * @return returns the start time and end time of the class. index 0 for
     * the start time and index 1 for the end time.
     */
    public Date[] getClassTimeByClassId(String class_id) {
        Date[] times = new Date[2];
        String selectQuery = "SELECT * FROM classes WHERE class_id = '" + class_id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                for (int i = 1; i < 5; i++) {
                    if (i == 3) {
                        Date startTime = new Date(cursor.getLong(i));
                        times[0] = startTime;

                    } else if (i == 4) {
                        Date endTime = new Date(cursor.getLong(i));
                        times[1] = endTime;
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return times;
    }

    /**
     * gets all the users that are in a given class via it's class id.
     * @param class_id the id of the class we want to find the users
     *                 of.
     * @return returns a list of all the user's usernames who are
     * in the class.
     */
    public List<String> getAllUsersByClassId(String class_id) {
        List<String> allUsers = this.getAllUsers();
        List<String> allUsersInClass = new ArrayList<>();

        for (String username : allUsers) {

            List<String> userClasses = this.getAllClassesByEnrolment(username);
            for (String enrolledClass : userClasses) {

                if (enrolledClass.equals(class_id)) {
                    allUsersInClass.add(username);
                }
            }
        }

        return allUsersInClass;
    }

    /**
     * gets class info including:
     * type,
     * difficulty,
     * start time,
     * end time,
     * capacity and
     * instructor via a given class_id
     * @param class_id the id of the class which we need info on.
     * @return a concatonated string of all of the variables listed
     * above, separated by spaces.
     */
    public String getClassByClassId(String class_id) {
        StringBuilder info = new StringBuilder();
        String selectQuery = "SELECT * FROM classes WHERE class_id = '" + class_id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        if (cursor.moveToFirst()) {
            do {
                for (int i = 1; i < 7; i++) {
                    if (i == 3) {
                        Date date = new Date(cursor.getLong(i));
                        info.append(dateFormat.format(date));
                        info.append(" ");
                        info.append(timeFormat.format(date));
                    } else if (i == 4) {
                        info.append("-");
                        Date date = new Date(cursor.getLong(i));
                        info.append(timeFormat.format(date));
                        info.append(" ");
                    } else {
                        info.append(cursor.getString(i));
                        info.append(" ");
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return info.toString();
    }

    /** Method retrieves a list of classes that have the appropriate class name.
     *
     * @param className the type of class we want to see, like Kickboxing.
     * @return a list of Strings with the specified classes.
     */
    public List<String> getAllClassesByClassName(String className){

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM classes WHERE type = '" + className + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        if (cursor.moveToFirst()) {
            do {
                String s = "";
                for(int i =1; i < 7; i++){
                    if(i == 3){
                        Date date = new Date(cursor.getLong(i));
                        s += dateFormat.format(date);
                        s += " ";
                        s += timeFormat.format(date);
                    }else if(i == 4) {
                        s += "-";
                        Date date = new Date(cursor.getLong(i));
                        s += timeFormat.format(date);
                        s += " ";
                    }else {
                        s += cursor.getString(i);
                        s+= " ";
                    }
                }
                list.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Method gets a list of classes for the given instructor name.
     *
     * @param instructorName the instructor who's classes we would like to see.
     * @return returns a list of Strings of classes run by instructor.
     */
    public List<String> getAllClassesByInstructorName(String instructorName){

        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM classes WHERE instructor = '" + instructorName + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        if (cursor.moveToFirst()) {
            do {
                String s = "";
                for(int i =0; i < 7; i++){
                    if(i == 3){
                        Date date = new Date(cursor.getLong(i));
                        s += dateFormat.format(date);
                        s += " ";
                        s += timeFormat.format(date);
                    }else if(i == 4) {
                        s += "-";
                        Date date = new Date(cursor.getLong(i));
                        s += timeFormat.format(date);
                        s += " ";
                    }else {
                        s += cursor.getString(i);
                        s+= " ";
                    }
                }
                list.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (list.isEmpty()) {
            list.add(null);
        }
        return list;
    }

    /** Method returns a list of all the classes and their descriptions.
     *
     * @return returns a list of all class Descriptions.
     */
    public List<String> getAllClassDescriptions() {
        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM class_types";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Method returns a list of all the classes and their descriptions.
     *
     * @return returns a list of all class Descriptions.
     */
    public String getClassDescriptionByClassType(String classType) {
        String s = "";
        String selectQuery = "SELECT  * FROM class_types";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(classType)) {
                    s = cursor.getString(1);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return s;
    }


}
