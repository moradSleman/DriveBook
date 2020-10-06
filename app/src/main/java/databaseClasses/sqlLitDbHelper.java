package databaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class sqlLitDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = Constants.pupilDatabase;

    public static final String LESSON_PAYMENT_TABLE_NAME = Constants.lessonPaymentDatabase;
    public static final String LESSON_PAYMENT_COLUMN_DATE_ID = Constants.lessonDate;
    public static final String LESSON_PAYMENT_COLUMN_STARTING_DATE = Constants.lessonStartingDate;
    public static final String LESSON_PAYMENT_COLUMN_ENDING_DATE = Constants.lessonEndingDate;
    public static final String LESSON_PAYMENT_COST = Constants.lessonPayCost;
    public static final String LESSO_PAYMENT_PAID_SITUATION = Constants.lessonPaidSituation;
    public static final String LESSON_PAYMENT_PAID_DATE = Constants.lessonPaidDate;

    public static final String PUPIL_TOKEN_ID=Constants.TokenId;
    public static final String PUPIL_USER_NAME=Constants.tokenUserName;
    public static final String PUPIL_USER_PASSWORD=Constants.tokenPassword;
    public static final String PUPIL_PROFILE_TABLE_NAME=Constants.pupilProfileDatabase;
    public static final String PUPIL_PROFILE_NAME=Constants.pupilName;
    public static final String PUPIL_PROFILE_MAIL=Constants.pupilMail;
    public static final String PUPIL_PROFILE_PHONE=Constants.pupilPhone;
    public static final String PUPIL_PROFILE_BIRTHDATE=Constants.pupilBirthdate;
    public static final String PUPIL_PROFILE_LOCATION=Constants.pupilLocation;
    public static final String PUPIL_PROFILE_THEORY_END=Constants.pupilTheoryEnd;
    public static final String PUPIL_TIME_ALERT_BEFORE_LESSON=Constants.pupilAlertLesson;
    public static final String PUPIL_PROFILE_PRIVATE_ID=Constants.pupilprivateId;
    public static final String PUPIL_PROFILE_IMAGE=Constants.pupilPhoto;
    public static final String TEACH_UID=Constants.teacherId;
    public static final String SCHOOL_UID=Constants.schoolId;
    public static final String PUPIL_UID=Constants.pupilId;

    public static final String PUPIL_OR_TEACHER_OR_SCHOOL=Constants.typeOfUser;
    public static final String TYPE_OF_USER_TABLE=Constants.typeUserTable;

    public static final String TEST_TABLE_NAME=Constants.testTableName;
    public static final String TEST_DATE=Constants.testDate;
    public static final String TEST_STARTING_TIME=Constants.testTime;
    public static final String TEST_COST=Constants.testCost;
    public static final String TEST__IS_INTERNAL=Constants.testIsInternalTest;
    public static final String TEST_PAID_SITUATION=Constants.testPaidSituation;
    public static final String TEST_RESULT=Constants.testResult;

    public static final String NOTIFICATION_TABL_NAME=Constants.notificationTableName;
    public static final String NOTIFICATION_TIME=Constants.timeNoty;
    public static final String NOTIFICATION_TITLE=Constants.notyTitle;
    public static final String NOTIFICATION_CONTENT=Constants.notyContent;
    public static final String NOTIFICATION_IS_READ=Constants.notyIsRead;

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public sqlLitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TYPE_OF_USER_TABLE + " (" +
                PUPIL_OR_TEACHER_OR_SCHOOL +" String"+")");

        sqLiteDatabase.execSQL("CREATE TABLE " + LESSON_PAYMENT_TABLE_NAME + " (" +
                LESSON_PAYMENT_COLUMN_DATE_ID + " INTEGER PRIMARY KEY, " +
                LESSON_PAYMENT_COLUMN_STARTING_DATE + " INTEGER, " +
                LESSON_PAYMENT_COLUMN_ENDING_DATE + " INTEGER, " +
                LESSON_PAYMENT_COST + " INTEGER," +
                LESSO_PAYMENT_PAID_SITUATION + " INTEGER,"+
                LESSON_PAYMENT_PAID_DATE+" INTEGER"+")");

        sqLiteDatabase.execSQL("CREATE TABLE " + PUPIL_PROFILE_TABLE_NAME + " (" +
                PUPIL_PROFILE_PHONE + " INTEGER PRIMARY KEY, " +
                PUPIL_PROFILE_BIRTHDATE + " STRING, " +
                PUPIL_PROFILE_LOCATION + " STRING, " +
                PUPIL_PROFILE_MAIL + " STRING," +
                PUPIL_PROFILE_NAME + " STRING,"+
                PUPIL_PROFILE_PRIVATE_ID+" STRING,"+
                PUPIL_PROFILE_THEORY_END + " STRING,"+
                PUPIL_TIME_ALERT_BEFORE_LESSON + " INTEGER,"+
                PUPIL_TOKEN_ID+" INTEGER,"+
                PUPIL_USER_NAME+" INTEGER,"+
                PUPIL_USER_PASSWORD+" INTEGER,"+
                PUPIL_PROFILE_IMAGE+" BLOB,"+
                PUPIL_UID+" STRING,"+
                TEACH_UID+" STRING,"+
                SCHOOL_UID+" STRING"+")");

        sqLiteDatabase.execSQL("CREATE TABLE " + TEST_TABLE_NAME + " (" +
                TEST_STARTING_TIME + " INTEGER PRIMARY KEY, " +
                TEST_DATE + " INTEGER, " +
                TEST__IS_INTERNAL + " INTEGER, " +
                TEST_COST + " INTEGER," +
                TEST_PAID_SITUATION + " INTEGER,"+
                TEST_RESULT+" STRING"+")");

        sqLiteDatabase.execSQL("CREATE TABLE " + NOTIFICATION_TABL_NAME + " (" +
                NOTIFICATION_TIME + " INTEGER PRIMARY KEY, " +
                NOTIFICATION_TITLE + " STRING, " +
                NOTIFICATION_CONTENT + " STRING, " +
                NOTIFICATION_IS_READ + " BOOLEAN"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TYPE_OF_USER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LESSON_PAYMENT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PUPIL_PROFILE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TEST_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABL_NAME);
        onCreate(sqLiteDatabase);
    }
    public void saveToNotyDb(notification notyToAdd){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sqlLitDbHelper.NOTIFICATION_TIME,notyToAdd.getTimeNoty().getTime());
        values.put(sqlLitDbHelper.NOTIFICATION_TITLE,notyToAdd.getTitle());
        values.put(sqlLitDbHelper.NOTIFICATION_CONTENT,notyToAdd.getBody());
        values.put(sqlLitDbHelper.NOTIFICATION_IS_READ,notyToAdd.isRead());

        database.insert(this.NOTIFICATION_TABL_NAME, null, values);
    }

    public ArrayList<notification> readNotyFromDb(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<notification> notys=new ArrayList<>();
        String[] projection = {
                this.NOTIFICATION_TIME,
                this.NOTIFICATION_TITLE,
                this.NOTIFICATION_CONTENT,
                this.NOTIFICATION_IS_READ
        };

        Cursor cursor = database.query(
                NOTIFICATION_TABL_NAME,   // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                this.NOTIFICATION_TIME+" DESC"// sort
        );

        try {
            while (cursor.moveToNext()) {
                notification t=new notification();
                Date notytime=new Date(cursor.getLong(cursor.getColumnIndex(this.NOTIFICATION_TIME)));
                String notyTitle=cursor.getString(cursor.getColumnIndex(this.NOTIFICATION_TITLE));
                String notyContent=cursor.getString(cursor.getColumnIndex(this.NOTIFICATION_CONTENT));
                boolean isRead=cursor.getInt(cursor.getColumnIndex(this.NOTIFICATION_IS_READ))==1?true:false;

                t.setTitle(notyTitle);
                t.setBody(notyContent);
                t.setRead(isRead);
                t.setTimeNoty(notytime);

                notys.add(t);
            }
        } finally {
            cursor.close();
        }
        return notys;
    }

    public void updaateNoty(notification note){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sqlLitDbHelper.NOTIFICATION_IS_READ,note.isRead());

        database.update(this.NOTIFICATION_TABL_NAME,values, NOTIFICATION_TIME +" = "+note.getTimeNoty().getTime(),null);
    }
    public void saveTestToDb(Test testToAdd){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sqlLitDbHelper.TEST_DATE,testToAdd.getTestDate().getTime());
        values.put(sqlLitDbHelper.TEST__IS_INTERNAL,testToAdd.isInternalTest()?1:0);
        values.put(sqlLitDbHelper.TEST_COST,testToAdd.getTestCost());
        values.put(sqlLitDbHelper.TEST_PAID_SITUATION,testToAdd.isPaidSituation()?1:0);
        values.put(sqlLitDbHelper.TEST_RESULT,testToAdd.getResult());
        values.put(sqlLitDbHelper.TEST_STARTING_TIME,testToAdd.getTestTimeStart().getTime());

        database.delete(this.TEST_TABLE_NAME, null, null);
        database.insert(this.TEST_TABLE_NAME, null, values);

    }

    public ArrayList<Test> readTestFromDb(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<Test> tests=new ArrayList<>();
        String[] projection = {
                this.TEST_STARTING_TIME,
                this.TEST_DATE,
                this.TEST__IS_INTERNAL,
                this.TEST_COST,
                this.TEST_PAID_SITUATION,
                this.TEST_RESULT
        };

        Cursor cursor = database.query(
                this.TEST_TABLE_NAME,   // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                this.TEST_STARTING_TIME+" DESC"// sort
        );

        try {
            while (cursor.moveToNext()) {
                Test t=new Test();
                Date testDate=new Date(cursor.getLong(cursor.getColumnIndex(this.TEST_DATE)));
                Date testTime=new Date(cursor.getLong(cursor.getColumnIndex(this.TEST_STARTING_TIME)));
                String result=cursor.getString(cursor.getColumnIndex(this.TEST_RESULT));
                boolean isPaid=cursor.getInt(cursor.getColumnIndex(this.TEST_PAID_SITUATION))==1?true:false;
                Integer cost=cursor.getInt(cursor.getColumnIndex(this.TEST_COST));
                boolean isEnternal=cursor.getInt(cursor.getColumnIndex(this.TEST__IS_INTERNAL))==1?true:false;

                t.setInternalTest(isEnternal);
                t.setPaidSituation(isPaid);
                t.setResult(result);
                t.setTestCost(cost);
                t.setTestDate(testDate);
                t.setTestTimeStart(testTime);

                tests.add(t);
            }
        } finally {
            cursor.close();
        }
        return tests;
    }

    public void saveTypeOfUser(String type){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sqlLitDbHelper.PUPIL_OR_TEACHER_OR_SCHOOL,type);
        database.delete(this.TYPE_OF_USER_TABLE, null, null);
        database.insert(this.TYPE_OF_USER_TABLE, null, values);

    }

    public String readTypeOfUser(){
        SQLiteDatabase database = this.getReadableDatabase();

        String[] projection = {
               this.PUPIL_OR_TEACHER_OR_SCHOOL
        };

        Cursor cursor = database.query(
                this.TYPE_OF_USER_TABLE,   // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null// sort
        );

        String type=null;
        try {
            while (cursor.moveToNext()) {

               type=cursor.getString(cursor.getColumnIndex(this.PUPIL_OR_TEACHER_OR_SCHOOL));
            }
        } finally {
            cursor.close();
        }
        return type;
    }

    public void savePupilProfileToSqlLite(pupilSqlLite pupilToAdd) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sqlLitDbHelper.PUPIL_PROFILE_BIRTHDATE, pupilToAdd.getPupilBirthDate().toString());
        values.put(sqlLitDbHelper.PUPIL_PROFILE_LOCATION, pupilToAdd.getPupilLocation().toString());
        values.put(sqlLitDbHelper.PUPIL_PROFILE_MAIL, pupilToAdd.getPupilMail().toString());
        values.put(sqlLitDbHelper.PUPIL_PROFILE_NAME, pupilToAdd.getPupilName().toString());
        values.put(sqlLitDbHelper.PUPIL_PROFILE_PHONE, pupilToAdd.getPupilPhone().toString());
        values.put(sqlLitDbHelper.PUPIL_PROFILE_PRIVATE_ID, pupilToAdd.getPupilId());
        values.put(sqlLitDbHelper.PUPIL_PROFILE_THEORY_END, pupilToAdd.getPupilTheoryEnd().toString());
        values.put(sqlLitDbHelper.PUPIL_TIME_ALERT_BEFORE_LESSON, pupilToAdd.getTimeAlertBeforeLesson());
        values.put(sqlLitDbHelper.PUPIL_TOKEN_ID, pupilToAdd.getTokenId());
        values.put(sqlLitDbHelper.PUPIL_USER_NAME, pupilToAdd.getTokenUserName());
        values.put(sqlLitDbHelper.PUPIL_USER_PASSWORD, pupilToAdd.getTokenPassword());
        values.put(sqlLitDbHelper.PUPIL_UID, pupilToAdd.getPupilUId());
        values.put(sqlLitDbHelper.TEACH_UID, pupilToAdd.getTeacherUId());
        values.put(sqlLitDbHelper.SCHOOL_UID, pupilToAdd.getSchoolUId());

        Bitmap b = pupilToAdd.getPupilImage();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] img = bos.toByteArray();

        values.put(sqlLitDbHelper.PUPIL_PROFILE_IMAGE, img);

        database.delete(this.PUPIL_PROFILE_TABLE_NAME, this.PUPIL_PROFILE_PHONE + "=" + pupilToAdd.getPupilPhone(), null);
        database.insert(this.PUPIL_PROFILE_TABLE_NAME, null, values);


    }

    public void savelessonPayToSqlLite(lessonPayment lessPay) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sqlLitDbHelper.LESSON_PAYMENT_COLUMN_DATE_ID,lessPay.getLessonDate().getTime() );
        values.put(sqlLitDbHelper.LESSON_PAYMENT_COLUMN_STARTING_DATE,lessPay.getLessonStartingDate().getTime() );
        values.put(sqlLitDbHelper.LESSON_PAYMENT_COLUMN_ENDING_DATE,lessPay.getLessonEndingDate().getTime() );
        values.put(sqlLitDbHelper.LESSON_PAYMENT_COST,lessPay.getLessonCost());
        values.put(sqlLitDbHelper.LESSO_PAYMENT_PAID_SITUATION,lessPay.isLessonPaidSituation()? 1 : 0 );
        Long time=lessPay.getLessonPaidDate()==null ? new Long(0):lessPay.getLessonPaidDate().getTime();
        values.put(sqlLitDbHelper.LESSON_PAYMENT_PAID_DATE,time);

        database.insert(this.LESSON_PAYMENT_TABLE_NAME, null, values);
    }

    public ArrayList<pupilSqlLite> readPupilProfileFromSqlLite() {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] projection = {
                this.PUPIL_PROFILE_PHONE,
                this.PUPIL_PROFILE_BIRTHDATE,
                this.PUPIL_PROFILE_LOCATION,
                this.PUPIL_PROFILE_MAIL,
                this.PUPIL_PROFILE_NAME,
                this.PUPIL_PROFILE_PRIVATE_ID,
                this.PUPIL_PROFILE_THEORY_END,
                this.PUPIL_TIME_ALERT_BEFORE_LESSON,
                this.PUPIL_TOKEN_ID,
                this.PUPIL_USER_NAME,
                this.PUPIL_USER_PASSWORD,
                this.PUPIL_UID,
                this.TEACH_UID,
                this.SCHOOL_UID,
                this.PUPIL_PROFILE_IMAGE
        };

        Cursor cursor = database.query(
                this.PUPIL_PROFILE_TABLE_NAME,   // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null// sort
        );

        ArrayList<pupilSqlLite> pupilProfile=new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String theoryEnd = cursor.getString(cursor.getColumnIndex(this.PUPIL_PROFILE_THEORY_END));
                String pupilBirthDate =cursor.getString(cursor.getColumnIndex(this.PUPIL_PROFILE_BIRTHDATE));
                Integer alertTime = cursor.getInt(cursor.getColumnIndex(this.PUPIL_TIME_ALERT_BEFORE_LESSON));
                String pupilName=cursor.getString(cursor.getColumnIndex(this.PUPIL_PROFILE_NAME));
                String pupilMail=cursor.getString(cursor.getColumnIndex(this.PUPIL_PROFILE_MAIL));
                String pupilLocation=cursor.getString(cursor.getColumnIndex(this.PUPIL_PROFILE_LOCATION));
                String pupilPrivateId=cursor.getString(cursor.getColumnIndex(this.PUPIL_PROFILE_PRIVATE_ID));
                String pupilPhone=cursor.getString(cursor.getColumnIndex(this.PUPIL_PROFILE_PHONE));
                String tokenId=cursor.getString(cursor.getColumnIndex(this.PUPIL_TOKEN_ID));
                String pupilUserName=cursor.getString(cursor.getColumnIndex(this.PUPIL_USER_NAME));
                String pupilPassword=cursor.getString(cursor.getColumnIndex(this.PUPIL_USER_PASSWORD));
                String pupilUId=cursor.getString(cursor.getColumnIndex(this.PUPIL_UID));
                String teacherUId=cursor.getString(cursor.getColumnIndex(this.TEACH_UID));
                String schoolUId=cursor.getString(cursor.getColumnIndex(this.SCHOOL_UID));

                byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(this.PUPIL_PROFILE_IMAGE));
                Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);

                pupilSqlLite pupilProfileToAdd=new pupilSqlLite();
                pupilProfileToAdd.setTimeAlertBeforeLesson(alertTime);
                pupilProfileToAdd.setPupilBirthDate(pupilBirthDate);
                pupilProfileToAdd.setPupilLocation(pupilLocation);
                pupilProfileToAdd.setPupilMail(pupilMail);
                pupilProfileToAdd.setPupilId(pupilPrivateId);
                pupilProfileToAdd.setPupilName(pupilName);
                pupilProfileToAdd.setPupilPhone(pupilPhone);
                pupilProfileToAdd.setPupilTheoryEnd(theoryEnd);
                pupilProfileToAdd.setTokenId(tokenId);
                pupilProfileToAdd.setTokenUserName(pupilUserName);
                pupilProfileToAdd.setTokenPassword(pupilPassword);
                pupilProfileToAdd.setPupilImage(bm);
                pupilProfileToAdd.setTeacherUId(teacherUId);
                pupilProfileToAdd.setSchoolUId(schoolUId);
                pupilProfileToAdd.setPupilUId(pupilUId);

                pupilProfile.add(pupilProfileToAdd);
            }
        } finally {
            cursor.close();
        }
        return pupilProfile;
    }
    public ArrayList<lessonPayment> readLessonPayFromSqlLite() {
        SQLiteDatabase database = this.getReadableDatabase();

        String[] projection = {
        this.LESSON_PAYMENT_COLUMN_DATE_ID,
        this.LESSON_PAYMENT_COLUMN_STARTING_DATE,
        this.LESSON_PAYMENT_COLUMN_ENDING_DATE,
        this.LESSON_PAYMENT_COST,
        this.LESSO_PAYMENT_PAID_SITUATION,
        this.LESSON_PAYMENT_PAID_DATE
        };

        Cursor cursor = database.query(
                this.LESSON_PAYMENT_TABLE_NAME,   // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                this.LESSON_PAYMENT_COLUMN_DATE_ID+" DESC"// sort
        );

        ArrayList<lessonPayment> lessonPayments=new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Date date = new Date(cursor.getLong(cursor.getColumnIndex(this.LESSON_PAYMENT_COLUMN_DATE_ID)));
                Date startingDate =new Date(cursor.getLong(cursor.getColumnIndex(this.LESSON_PAYMENT_COLUMN_STARTING_DATE)));
                Date endingDate =new Date(cursor.getLong(cursor.getColumnIndex(this.LESSON_PAYMENT_COLUMN_ENDING_DATE)));
                Integer cost = cursor.getInt(cursor.getColumnIndex(this.LESSON_PAYMENT_COST));
                Boolean paymentSituation = cursor.getInt(cursor.getColumnIndex(this.LESSO_PAYMENT_PAID_SITUATION))==1?true:false;
                Date paidDate =new Date(cursor.getLong(cursor.getColumnIndex(this.LESSON_PAYMENT_PAID_DATE)));

                lessonPayment lesPayToAdd=new lessonPayment();
                lesPayToAdd.setLessonDate(date);
                lesPayToAdd.setLessonStartingDate(startingDate);
                lesPayToAdd.setLessonEndingDate(endingDate);
                lesPayToAdd.setLessonCost(cost);
                lesPayToAdd.setLessonPaidSituation(paymentSituation);
                lesPayToAdd.setLessonPaidDate(paidDate);

                lessonPayments.add(lesPayToAdd);
            }
        } finally {
            cursor.close();
        }
        return lessonPayments;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}