package com.example.uventawh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;

//import android.support.annotation.NonNull;

public class DB {

//    public ArrayList<Change> changesForResponse = new ArrayList<>();
    public ArrayList<String> changesRefs = new ArrayList<>();

    public String emptyRef;

    private static final String DB_NAME = "UVDRDB";
    private static final int DB_VERSION = 2;

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public Boolean opened;

    public boolean requested;
    public String error;

//    private TextView tvExchangeInGrogress;

    public DB(Context ctx) {

        mCtx = ctx;

        opened = false;

        requested = false;
        error = "";

        changesRefs = new ArrayList<>();
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();

        opened = true;

    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();

        opened = false;
    }

    public Cursor rawQuery(String query, String[] args){

        return mDB.rawQuery(query, args);

    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData(String db_name) {
        return mDB.query(db_name, null, null, null, null, null, null);
    }

    public Cursor getAllData(String db_name, String orderBy) {
        return mDB.query(db_name, null, null, null, null, null, orderBy);
    }

    public Cursor getAllDataByRef(String db_name, String ref) {
        return mDB.query(db_name, null, "ref = ?", new String[] { ref }, null, null, null, null);
    }

    public Cursor getAllDataByFilter(String db_name, String selection, String[] selectionArgs, String orderBy) {
        return mDB.query(db_name, null, selection, selectionArgs, null, null, orderBy);
    }

    public Cursor getAllDataByOwner(String db_name, String owner) {
        return mDB.query(db_name, null, "owner = ?", new String[] { owner }, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String db_name, ContentValues cv) {
        Long inserted = mDB.insert(db_name, null, cv);
    }

    public void delAll(String db_name) {
        mDB.delete(db_name, null, null);
    }

    // удалить запись из DB_TABLE
    public void delRec(String db_name, long id) {
        mDB.delete(db_name, "_id = " + String.valueOf(id), null);
    }

    public void delRecByRef(String db_name, String ref) {
        mDB.delete(db_name, "ref = ?", new String[] { ref });
    }

    public void delRecByFilter(String db_name, String whereClause, String[] where) {
        mDB.delete(db_name, whereClause, where);
    }

    public int update(String table,
                      ContentValues values,
                      String whereClause,
                      String[] whereArgs) {
        return mDB.update(table,  values,  whereClause, whereArgs);
    }

    public long insert(String table,
                       String nullColumnHack,
                       ContentValues values) {
        return mDB.insertOrThrow(table, nullColumnHack, values);
    }

    public Cursor query(String table,
                        String[] columns,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderBy) {
        return mDB.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public boolean updateConstant(String name, String value) {

        ContentValues cv = new ContentValues();

        cv.put("value", value);
        // обновляем по id
        int updCount = mDB.update("constants", cv, "name = ?", new String[] { name });

        if (updCount == 0) {
            cv.put("name", name);
            cv.put("value", value);

            updCount = (int) mDB.insert("constants", null, cv);
        }

        return updCount == 1;

    }

    public String getConstant(String name) {

        Cursor c = mDB.query("constants", null, "name = ?", new String[] { name }, null, null, null);

        String result = null;
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int valueColIndex = c.getColumnIndex("value");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                result = c.getString(valueColIndex);
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false -
                // выходим из цикла
            } while (c.moveToNext());
        } ;

        c.close();

        return result;

    }

    public ContentValues getRecById(String table, Integer id) {

        Cursor c = mDB.query(table, null, "_id = ?", new String[] { String.valueOf(id) }, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public ContentValues getRecByRef(String table, String ref) {

        Cursor c = mDB.query(table, null, "ref = ?", new String[] { ref }, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public ContentValues getRecByName(String table, String name) {

        Cursor c = mDB.query(table, null, "name = ?", new String[] { name }, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public ContentValues getRecByFilter(String table, String selection, String[] where) {

        Cursor c = mDB.query(table, null, selection, where, null, null, null);

        ContentValues cv = new ContentValues();

        if (c.moveToFirst()) {

            for (String column: c.getColumnNames()) {

                int curColumnIndex = c.getColumnIndex(column);
                int curType = c.getType(curColumnIndex);

                if(curType == 1) {
                    cv.put(column, c.getLong(curColumnIndex));
                }
                else if(curType == 2) {
                    cv.put(column, c.getFloat(curColumnIndex));
                }
                else if(curType == 3) {
                    cv.put(column, c.getString(curColumnIndex));
                }
            }

        } ;

        c.close();

        return cv;

    }

    public boolean updateRecord(String table, ContentValues cv) {

        int updCount = mDB.update(table, cv, "ref = ?", new String[] { cv.getAsString("ref") });

        if (updCount == 0) {

            updCount = (int) mDB.insert(table, null, cv);
        }

        return true;

    }

    public boolean updateChanges(String ref, String type, String name){

        ContentValues cvch = new ContentValues();
        cvch.put("ref", ref);
        cvch.put("type", type);
        cvch.put("name", name);

        updateRecord("changes", cvch);

        return true;

    }

//    public boolean getChanges() {
//
//        HttpClient client = new HttpClient(mCtx);
//
//        requested = true;
//
//        error = "";
//
//        client.get(mCtx, "changes/" + getConstant("user_id") + "/" + getConstant("prog_id"), new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                String result = null;
//                try {
//                    result = new String(responseBody, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//
//                    error = e.getLocalizedMessage();
//
////                    Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//                JSONObject readerArray = null;
//                try {
//                    readerArray = new JSONObject(result);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                    error = e.getLocalizedMessage();
//
////                    Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//                JSONArray changes = null;
//                try {
//                    changes = (JSONArray) readerArray.get("Changes");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                    error = e.getLocalizedMessage();
//
////                    Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//                changesForResponse = new ArrayList<Change>();
//
//                ObjectMapper m = new ObjectMapper();
//
//                if (changes.length() == 0 ) {
//
////                    tvExchangeInGrogress.setVisibility(View.GONE);
//
//                }
//
//                for (int i = 0; i < changes.length(); i++) {
//
//                    JSONObject curChange = null;
//                    try {
//                        curChange = changes.getJSONObject(i);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                        error = e.getLocalizedMessage();
//
////                        Toast.makeText(mCtx, "curChange:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                    }
//
//                    String curType = null;
//                    try {
//                        curType = (String) curChange.get("_Тип");
//                    } catch (JSONException e) {
//
//                        error = e.getLocalizedMessage();
//
//                        e.printStackTrace();
//                    }
//
//                    String curName = null;
//                    try {
//                        curName = (String) curChange.get("_Вид");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                        error = e.getLocalizedMessage();
//
//                    }
//
////                    if (curType.equals("Справочник")) {
////
////                        getReferencesChanges(changes, m, i, curName);
////
////                    }
////                    else if (curType.equals("Документ")) {
////
////                        getDocumentsChanges(changes, m, i, curName);
////                    }
////                    else if (curType.equals("РегистрСведений")) {
////
////                        getInfoRegChanges(changes, m, i, curName);
////                    }
//                }
//
//                if (changesForResponse.size() > 0) {
//
////                    postChanges();
//
//                }
//
//                requested = false;
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
//
////                tvExchangeInGrogress.setVisibility(View.GONE);
//
//                requested = false;
//                error = e.getLocalizedMessage();
//
//                Toast.makeText(mCtx, "getChanges statusCode:  " + statusCode, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        return false;
//    }
//
//    public void getDocumentsChanges(JSONArray changes, ObjectMapper m, int i, String curName) {
//
//        if (curName.equals("ЗаказКлиента")) {
//
//            ChangeOrder change = getChangeOrder(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//
//            if(updateRecord("orders", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//        else if (curName.equals("НазначениеПосещения")) {
//
//            ChangeDestination change = getChangeDestination(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//
//            SimpleDateFormat format = new SimpleDateFormat();
//            format.applyPattern("yyyyMMddHHmmss");
//            Date docDate = new Date();
//            try {
//                docDate = format.parse(change.Дата);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            cv.put("date", docDate.getTime());
//            cv.put("lead", change.Лид._Ссылка);
//            cv.put("user", change.Ответственный._Ссылка);
//            cv.put("author", change.Автор._Ссылка);
//
//            if(updateRecord("destinations", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//
//        }
//
//        else if (curName.equals("ПосещениеКонтрагента")) {
//
//            ChangeVisit change = getChangeVisit(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//
//            SimpleDateFormat format = new SimpleDateFormat();
//            format.applyPattern("yyyyMMddHHmmss");
//            Date docDate = new Date();
//            try {
//                docDate = format.parse(change.Дата.toString());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            cv.put("date", docDate.getTime());
//            cv.put("lead", change.Лид._Ссылка);
//            cv.put("user", change.Ответственный._Ссылка);
//            cv.put("contractor", change.Контрагент._Ссылка);
//            cv.put("type_operation", change.ВидОперации._Ссылка);
//            cv.put("interviewed", change.ПроизведенОпросОбИспользуемыхМатериалах ? 1 : 0);
//
//            if(updateRecord("visits", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//            delRecByFilter("visits_demos", "visit_ref = ?", new String[] { change._Ссылка });
//
//            Integer vdCount = 0;
//            for (ChangeVisitDemo visitDemo: change.МатериалыДемонстраций) {
//
//                ContentValues cvg = new ContentValues();
//                cvg.put("visit_ref", change._Ссылка);
//                cvg.put("order_number", vdCount);
//                cvg.put("good", visitDemo.Номенклатура._Ссылка);
//                cvg.put("characteristic", visitDemo.Характеристика._Ссылка);
//                cvg.put("quantity", visitDemo.Количество);
//
//                addRec("visits_demos", cvg);
//
//                vdCount++;
//
//            }
//
//            delRecByFilter("visits_materials", "visit_ref = ?", new String[] { change._Ссылка });
//
//            for (ChangeVisitMaterials changeVisitMaterials : change.ИспользуемыеМатериалы) {
//
//                ContentValues cvg = new ContentValues();
//                cvg.put("visit_ref", change._Ссылка);
//                cvg.put("good", changeVisitMaterials.Номенклатура._Ссылка);
//                cvg.put("characteristic", changeVisitMaterials.Характеристика._Ссылка);
//
//                addRec("visits_materials", cvg);
//
//            }
//
//            delRecByFilter("visits_fotos", "visit_ref = ?", new String[] { change._Ссылка });
//
//            for (ChangeVisitFoto visitFoto: change.Фото) {
//
//                ContentValues cvg = new ContentValues();
//                cvg.put("visit_ref", change._Ссылка);
//                cvg.put("foto", visitFoto.Фото._Ссылка);
//                cvg.put("date", visitFoto.ДатаФото.getTime());
//
//                addRec("visits_fotos", cvg);
//
//            }
//
//        }
//
//    }
//
//    public void getInfoRegChanges(JSONArray changes, ObjectMapper m, int i, String curName) {
//
//        if (curName.equals("СкидкиКонтрагентовПоТоварнымГруппам")) {
//
//            ChangeDiscountsContractorsGoodsGroups change = getChangeDiscountsContractorsGoodsGroups(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            for (ChangeDiscountsContractorsGoodsGroupsRecord Запись : change.Записи){
//
//                cv.put("contractor", Запись.Контрагент._Ссылка);
//                cv.put("goods_group", Запись.ТоварнаяГруппа._Ссылка);
//                cv.put("discount", Запись.Скидка);
//
//                int updCount = mDB.update("discounts_contractors_goods_groups", cv, "contractor = ? and goods_group = ?",
//                        new String[] { cv.getAsString("contractor"), cv.getAsString("goods_group") });
//
//                if (updCount == 0) {
//
//                    updCount = (int) mDB.insert("discounts_contractors_goods_groups", null, cv);
//                }
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = Запись.Контрагент._Ссылка + "@@" + Запись.ТоварнаяГруппа._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//        else if (curName.equals("ЦеныНоменклатуры")) {
//
//            ChangeGoodsPrices change = getChangeGoodsPrices(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            for (ChangeGoodsPricesRecord Запись : change.Записи){
//
//                SimpleDateFormat format = new SimpleDateFormat();
//                format.applyPattern("yyyyMMddHHmmss");
//                Date docDate = new Date();
//                try {
//                    docDate = format.parse(Запись.Период);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                cv.put("date", docDate.getTime());
//
//                cv.put("goods", Запись.Номенклатура._Ссылка);
//                cv.put("characteristic", Запись.Характеристика._Ссылка);
//                cv.put("measurement_unit", Запись.ЕдиницаИзмерения._Ссылка);
//                cv.put("currency", Запись.Валюта._Ссылка);
//                cv.put("price", Запись.Цена);
//
//                int updCount = mDB.update("goods_prices", cv, "date = ? and goods = ? and characteristic = ? and measurement_unit = ? and currency = ?",
//                        new String[] { cv.getAsString("date"), cv.getAsString("goods"), cv.getAsString("characteristic"), cv.getAsString("measurement_unit"), cv.getAsString("currency") });
//
//                if (updCount == 0) {
//
//                    updCount = (int) mDB.insert("goods_prices", null, cv);
//                }
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = Запись.Период + "@@" + Запись.Номенклатура._Ссылка + "@@" + Запись.Характеристика._Ссылка + "@@" + Запись.ЕдиницаИзмерения._Ссылка + "@@" + Запись.Валюта._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ДоступКПосещениям")) {
//
//            ChangeVisitsAccess change = getChangeVisitsAccess(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            for (ChangeVisitsAccessRecord Запись : change.Записи){
//
//                cv.put("user", Запись.Пользователь._Ссылка);
//                cv.put("visit_user", Запись.ПользовательПосещений._Ссылка);
//                cv.put("exchange", Запись.ОбменДанными);
//
//                int updCount = mDB.update("visits_access", cv, "user = ? and visit_user = ?",
//                        new String[] { cv.getAsString("user"), cv.getAsString("visit_user") });
//
//                if (updCount == 0) {
//
//                    updCount = (int) mDB.insert("visits_access", null, cv);
//                }
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = Запись.Пользователь._Ссылка + "@@" + Запись.ПользовательПосещений._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ДоступКГородам")) {
//
//            ChangeCitiesAccess change = getChangeCitiesAccess(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            for (ChangeCitiesAccessRecord Запись : change.Записи){
//
//                cv.put("user", Запись.Пользователь._Ссылка);
//                cv.put("city", Запись.Город._Ссылка);
//
//                int updCount = mDB.update("cities_access", cv, "user = ? and city = ?",
//                        new String[] { cv.getAsString("user"), cv.getAsString("city") });
//
//                if (updCount == 0) {
//
//                    updCount = (int) mDB.insert("cities_access", null, cv);
//                }
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = Запись.Пользователь._Ссылка + "@@" + Запись.Город._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("НормыРасходаМатериаловПриДемонстрациях")) {
//
//            ChangeMaterialConsumptionRatesForDemonstrations change = getChangeMaterialConsumptionRatesForDemonstrations(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            for (ChangeMaterialConsumptionRatesForDemonstrationsRecord Запись : change.Записи){
//
//                cv.put("demonstration_type", Запись.ВидДемонстрации._Ссылка);
//                cv.put("demonstration_step", Запись.ЭтапДемонстрации._Ссылка);
//                cv.put("material_group", Запись.ГруппаМатериаловДемонстраций._Ссылка);
//                cv.put("good", Запись.Номенклатура._Ссылка);
//                cv.put("quantity", Запись.Количество);
//                cv.put("is_default", Запись.ПоУмолчанию ? 1 : 0);
//
//                int updCount = mDB.update("material_consumption_rates_for_demonstrations", cv,
//                        "demonstration_type = ? and demonstration_step = ? and material_group = ? and good = ?",
//                        new String[] { cv.getAsString("demonstration_type"), cv.getAsString("demonstration_step"),
//                                cv.getAsString("material_group"), cv.getAsString("good")});
//
//                if (updCount == 0) {
//
//                    updCount = (int) mDB.insert("material_consumption_rates_for_demonstrations", null, cv);
//                }
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = Запись.ВидДемонстрации._Ссылка + "@@" + Запись.ЭтапДемонстрации._Ссылка + "@@" + Запись.ГруппаМатериаловДемонстраций._Ссылка + "@@" + Запись.Номенклатура._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Продажи")) {
//
//            ChangeSales change = getChangeSales(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            for (ChangeSalesRecord Запись : change.Записи){
//
//                SimpleDateFormat format = new SimpleDateFormat();
//                format.applyPattern("yyyyMMddHHmmss");
//                Date docDate = new Date();
//                try {
//                    docDate = format.parse(Запись.Период);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                cv.put("date", docDate.getTime());
//
//                cv.put("contractor", Запись.Контрагент._Ссылка);
//                cv.put("price_group", Запись.ЦеноваяГруппа._Ссылка);
//                cv.put("okp", Запись.ОКП._Ссылка);
//                cv.put("quantity", Запись.Количество);
//                cv.put("sum", Запись.Сумма);
//
//                int updCount = mDB.update("sales", cv,
//                        "date = ? and contractor = ? and price_group = ? and okp = ?",
//                        new String[] { cv.getAsString("date"),
//                                cv.getAsString("contractor"),
//                                cv.getAsString("price_group"),
//                                cv.getAsString("okp")});
//
//                if (updCount == 0) {
//
//                    updCount = (int) mDB.insert("sales", null, cv);
//                }
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = Запись.Период + "@@" + Запись.Контрагент._Ссылка + "@@" + Запись.ЦеноваяГруппа._Ссылка + "@@" + Запись.ОКП._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//    }
//
//    public void getReferencesChanges(JSONArray changes, ObjectMapper m, int i, String curName) {
//
//        if (curName.equals("Бренды")) {
//
//            ChangeBrand change = getChangeBrand(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("code", change.Код);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("brands", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//        else if (curName.equals("Лиды")) {
//
//            ChangeLead change = getChangeLead(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//            cv.put("description", change.Описание);
//
//            SimpleDateFormat format = new SimpleDateFormat();
//            format.applyPattern("yyyyMMddHHmmss");
//            Date docDate = new Date();
//            try {
//                docDate = format.parse(change.ДатаСоздания);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            cv.put("date_creation", docDate.getTime());
//            cv.put("address", change.Адрес);
//            cv.put("address_s", change.Адрес.toLowerCase().replaceAll("\\W", ""));
//            cv.put("owner_fio", change.ФИОВладельца);
//            cv.put("owner_phone", change.ТелефонВладельца);
//            cv.put("purchaser_fio", change.ФИОЗакупщика);
//            cv.put("purchaser_phone", change.ТелефонЗакупщика);
//            cv.put("area", change.Площадь);
//            cv.put("rooms", change.КоличествоПомещений);
//            cv.put("cars_in_mounth", change.КоличествоМашинВМесяц);
//            cv.put("elements_in_mounth", change.КоличествоЭлементовВМесяц);
//            cv.put("output", change.ОбъемВыборкиВМесяц);
//            cv.put("opening_hours", change.РежимРаботы);
//            cv.put("changes", change.КоличествоСмен);
//            cv.put("preparing_zones", change.КоличествоЗонПодготовки);
//            cv.put("osks", change.КоличествоОСК);
//            cv.put("mixsystem", change.МиксСистема);
//            cv.put("armourers", change.КоличествоАрматурщиков);
//            cv.put("bodybuilders", change.КоличествоКузовщиков);
//            cv.put("preparers", change.КоличествоПодготовщиков);
//            cv.put("painters", change.КоличествоМаляров);
//            cv.put("colourers", change.КоличествоКолористов);
//            cv.put("latitude", change.Широта);
//            cv.put("longitude", change.Долгота);
//            cv.put("user", change.Автор._Ссылка);
//            cv.put("main_supplier", change.ОсновнойПоставщик._Ссылка);
//            cv.put("this_contractor", change.ЭтотКлиент._Ссылка);
//            cv.put("services_group", change.ГруппаСервиса._Ссылка);
//            cv.put("normohours_in_mounth", change.КоличествоНормочасовВМесяц);
//            cv.put("this_contractor", change.ЭтотКлиент._Ссылка);
//            cv.put("city", change.Город._Ссылка);
//            cv.put("city_district", change.РайонГорода._Ссылка);
//            cv.put("icon", change.ОсновноеИзображение._Ссылка);
//            cv.put("territory", change.Территория._Ссылка);
//
//            delRecByFilter("leads_suppliers", "lead_ref = ?", new String[] { change._Ссылка });
//
//            Integer vdCount = 0;
//            for (ChangeLeadSupplier visitDemo: change.Поставщики) {
//
//                ContentValues cvg = new ContentValues();
//                cvg.put("lead_ref", change._Ссылка);
//                cvg.put("order_number", vdCount);
//                cvg.put("supplier", visitDemo.Поставщик._Ссылка);
//
//                addRec("leads_suppliers", cvg);
//
//                vdCount++;
//
//            }
//
//            if(updateRecord("leads", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//        else if (curName.equals("Должности")) {
//
//            ChangePosition change = getChangePosition(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("positions", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Территории")) {
//
//            ChangeTerritory change = getChangeTerritory(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("territories", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ТоварныеГруппы")) {
//
//            ChangeGoodsGroup change = getChangeGoodsGroup(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//            cv.put("color_red", change.ЦветКрасный);
//            cv.put("color_green", change.ЦветЗеленый);
//            cv.put("color_blue", change.ЦветСиний);
//
//            if(updateRecord("goods_groups", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ОбщероссийскийКлассификаторПродукции")) {
//
//            ChangeClassificOKP change = getChangeClassificOKP(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("classific_okp", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Склады")) {
//
//            ChangeWarehouse change = getChangeWarehouse(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("warehouses", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Валюты")) {
//
//            ChangeCurrency change = getChangeCurrency(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//
//            if(updateRecord("currencies", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Организации")) {
//
//            ChangeOrganisation change = getChangeOrganisation(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("organisations", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("КлассификаторЕдиницИзмерения")) {
//
//            ChangeClassificMeasurementUnit change = getChangeClassificMeasurementUnit(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("classific_measurement_unit", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ЕдиницыИзмерения")) {
//
//            ChangeMeasurementUnit change = getChangeMeasurementUnit(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("owner", change.Владелец._Ссылка);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("measurement_unit", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ХарактеристикиНоменклатуры")) {
//
//            ChangeCharacterictic change = getChangeCharacterictic(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("owner", change.Владелец._Ссылка);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("characteristics", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Номенклатура")) {
//
//            ChangeGoods change = getChangeGoods(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//            cv.put("code", change.Код);
//            cv.put("artikle", change.Артикул);
//            cv.put("fullname", change.НаименованиеПолное);
//            cv.put("base_mu", change.БазоваяЕдиницаИзмерения._Ссылка);
//            cv.put("save_mu", change.ЕдиницаХраненияОстатков._Ссылка);
//            cv.put("box_mu", change.ЕдиницаИзмеренияМест._Ссылка);
//            cv.put("nds", change.СтавкаНДС._Ссылка);
//            cv.put("using_characteristic", change.ВестиУчетПоХарактеристикам);
//            cv.put("interview_good", change.ТоварДляОпроса._Ссылка);
//            cv.put("price_group", change.ЦеноваяГруппа._Ссылка);
//            cv.put("okp", change.ОКП._Ссылка);
//
//            if(updateRecord("goods", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ТоварыДляОпроса")) {
//
//            ChangeInterviewGoods change = getChangeInterviewGoods(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//            cv.put("code", change.Код);
//            cv.put("price_group", change.ЦеноваяГруппа._Ссылка);
//            cv.put("okp", change.ОКП._Ссылка);
//
//            if(updateRecord("interview_goods", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Пользователи")) {
//
//            ChangeUser change = getChangeUser(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("warehouse", change.Склад._Ссылка);
//            cv.put("planing", change.Планирование ? 1 : 0);
//
//            if(updateRecord("users", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Контрагенты")) {
//
//            ChangeContractor change = getChangeContractor(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//            cv.put("code", change.Код);
//            cv.put("jur", change.ЮрФизЛицо._Ссылка);
//            cv.put("inn", change.ИНН);
//            cv.put("kpp", change.КПП);
//            cv.put("main_contractor", change.ГоловнойКонтрагент._Ссылка);
//            cv.put("manager", change.ОсновнойМенеджерПокупателя._Ссылка);
//            cv.put("territory", change.Территория._Ссылка);
//
//            if(updateRecord("contractors", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("ВидыДемонстрации")) {
//
//            ChangeDemonstrationType change = getChangeDemonstrationType(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("demonstration_types", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("Города")) {
//
//            ChangeCity change = getChangeCity(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("cities", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//        else if (curName.equals("РайоныГородов")) {
//
//            ChangeCityDistrict change = getChangeCityDistrict(changes, m, i);
//
//            ContentValues cv = new ContentValues();
//
//            cv.put("ref", change._Ссылка);
//            cv.put("name", change.Наименование);
//            cv.put("owner", change.Владелец._Ссылка);
//            cv.put("name_s", change.Наименование.toLowerCase().replaceAll("\\W", ""));
//
//            if(updateRecord("city_districts", cv)) {
//
//                Change changefr = new Change();
//                changefr._Тип = change._Тип;
//                changefr._Вид = change._Вид;
//                changefr._Ссылка = change._Ссылка;
//
//                changesForResponse.add(changefr);
//
//            }
//
//        }
//
//    }

//    private Change getChange(JSONArray changes, ObjectMapper m, int i) {
//
//        Change change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), Change.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeDiscountsContractorsGoodsGroups getChangeDiscountsContractorsGoodsGroups(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeDiscountsContractorsGoodsGroups change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeDiscountsContractorsGoodsGroups.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeGoodsPrices getChangeGoodsPrices(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeGoodsPrices change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeGoodsPrices.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeVisitsAccess getChangeVisitsAccess(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeVisitsAccess change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeVisitsAccess.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeCitiesAccess getChangeCitiesAccess(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeCitiesAccess change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeCitiesAccess.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeMaterialConsumptionRatesForDemonstrations getChangeMaterialConsumptionRatesForDemonstrations(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeMaterialConsumptionRatesForDemonstrations change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeMaterialConsumptionRatesForDemonstrations.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeSales getChangeSales(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeSales change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeSales.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeBrand getChangeBrand(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeBrand change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeBrand.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeLead getChangeLead(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeLead change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeLead.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangePosition getChangePosition(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangePosition change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangePosition.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeTerritory getChangeTerritory(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeTerritory change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeTerritory.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeClassificOKP getChangeClassificOKP(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeClassificOKP change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeClassificOKP.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeGoodsGroup getChangeGoodsGroup(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeGoodsGroup change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeGoodsGroup.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeWarehouse getChangeWarehouse(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeWarehouse change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeWarehouse.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeCurrency getChangeCurrency(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeCurrency change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeCurrency.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeOrganisation getChangeOrganisation(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeOrganisation change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeOrganisation.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeClassificMeasurementUnit getChangeClassificMeasurementUnit(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeClassificMeasurementUnit change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeClassificMeasurementUnit.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeUser getChangeUser(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeUser change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeUser.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeMeasurementUnit getChangeMeasurementUnit(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeMeasurementUnit change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeMeasurementUnit.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeCharacterictic getChangeCharacterictic(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeCharacterictic change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeCharacterictic.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeContractor getChangeContractor(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeContractor change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeContractor.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeGoods getChangeGoods(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeGoods change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeGoods.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeInterviewGoods getChangeInterviewGoods(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeInterviewGoods change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeInterviewGoods.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeOrder getChangeOrder(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeOrder change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeOrder.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeDestination getChangeDestination(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeDestination change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeDestination.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeVisit getChangeVisit(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeVisit change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeVisit.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeDemonstrationType getChangeDemonstrationType(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeDemonstrationType change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeDemonstrationType.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeCity getChangeCity(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeCity change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeCity.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }
//
//    private ChangeCityDistrict getChangeCityDistrict(JSONArray changes, ObjectMapper m, int i) {
//
//        ChangeCityDistrict change = null;
//        try {
//
//            change = m.readValue(changes.getJSONObject(i).toString(), ChangeCityDistrict.class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//        }
//        return change;
//    }

//    public boolean postChanges() {
//
////        tvExchangeInGrogress.setVisibility(View.VISIBLE);
////        tvExchangeInGrogress.setBackgroundColor(mCtx.getResources().getColor(R.color.colorPrimary));
////
//        HttpClient client = new HttpClient(mCtx);
//
//        JSONArray jsonArray = getPostChangesJsonArray();
//
//        requested = true;
//        error = "";
//
//        client.post(mCtx, "changes/" + getConstant("user_id") + "/" + getConstant("prog_id"), jsonArray, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                String result = null;
//                try {
//                    result = new String(responseBody, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//
//                    error = e.getLocalizedMessage();
//
////                    Toast.makeText(mCtx, "postChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//                JSONObject readerArray = null;
//                try {
//                    readerArray = new JSONObject(result);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                    error = e.getLocalizedMessage();
//
////                    Toast.makeText(mCtx, "postChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//                String error = null;
//                try {
//                    error = (String) readerArray.get("Error");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                    error = e.getLocalizedMessage();
//
////                    Toast.makeText(mCtx, "postChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//
//                if (error.equals("OK")){
//
//                    changesForResponse.clear();
//
//                    for (String curRef : changesRefs) {
//
//                        if (!curRef.substring(0, 2).equals("@@")) {
//
//                            delRecByRef("changes", curRef);
//
//                        }
//                    }
//
//                    changesRefs.clear();
//
//                    postChangesFiles();
//
////                    getChanges();
//
//                }
//                else
//                {
//                    requested = false;
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
//
////                tvExchangeInGrogress.setVisibility(View.GONE);
//
//                requested = false;
//
//                error = e.getLocalizedMessage();
//
//
//                //                Toast.makeText(mCtx, "postChanges statusCode:  " + statusCode, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        return false;
//    }
//
    @NonNull
//    public JSONArray getPostChangesJsonArray() {
//
//        JSONArray jsonArray = new JSONArray();
//
//        for (Change changeFR: changesForResponse) {
//            try {
//
//                switch (changeFR._Тип) {
//
//                    case "Справочник": {jsonArray.put(changeFR.getJSONObject()); break;}
//
//                    case "Документ": {jsonArray.put(changeFR.getJSONObject()); break;}
//
//                    case "РегистрСведений": {
//
//                        switch (changeFR._Вид) {
//
////                            case "СкидкиКонтрагентовПоТоварнымГруппам": {
////
////                                putChangeDiscountsContractorsGoodsGroups(jsonArray, changeFR._Ссылка);
////
////                                break;
////
////                            }
////
////                            case "ЦеныНоменклатуры": {
////
////                                putChangeGoodsPrices(jsonArray, changeFR._Ссылка);
////
////                                break;
////
////                            }
////
////                            case "ДоступКПосещениям": {
////
////                                putChangeVisitsAccess(jsonArray, changeFR._Ссылка);
////
////                                break;
////
////                            }
////
////                            case "ДоступПоГородам": {
////
////                                putChangeCitiesAccess(jsonArray, changeFR._Ссылка);
////
////                                break;
////
////                            }
////
////                            case "НормыРасходаМатериаловПриДемонстрациях": {
////
////                                putChangeMaterialConsumptionRatesForDemonstrations(jsonArray, changeFR._Ссылка);
////
////                                break;
////
////                            }
////
////                            case "Продажи": {
////
////                                putChangeSales(jsonArray, changeFR._Ссылка);
////
////                                break;
////
////                            }
//
//                        }
//                    }
//
//                }
//
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        Integer changesCount = 20;
//        Cursor cc = getAllData("changes");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                String name = cc.getString(cc.getColumnIndex("name"));
//                String curRef = cc.getString(cc.getColumnIndex("ref"));
//
////                switch (name){
////
////                    case "Лиды": {
////
////                        putChangeLead(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                    case "ЗаказКлиента": {
////
////                        putChangeOrder(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                    case "Координаты": {
////
////                        putChangeCoords(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                    case "ТелефонныеЗвонки": {
////
////                        putChangePhoneCalls(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                    case "ПосещениеКонтрагента": {
////
////                        putChangeVisit(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                    case "НазначениеПосещения": {
////
////                        putChangeDestination(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                    case "ОтчетОРаботеНаТорговойТочке": {
////
////                        putChangeSalePointReport(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                    case "ИнвентаризацияОстатковТехника": {
////
////                        putChangeInvent(jsonArray, curRef);
////
////                        break;
////
////                    }
////
////                }
//
//                changesRefs.add(curRef);
//
//                changesCount = changesCount - 1;
//
//            } while (cc.moveToNext() && changesCount > 0);
//        }
//
//        cc.close();
//        return jsonArray;
//    }
//
//    private void postChangesFiles(){
//
//        HttpClient client = new HttpClient(mCtx);
//
//        JSONArray jsonArray = new JSONArray();
//
//        Cursor cc = rawQuery("select * from changes where name like '%ПрисоединенныеФайлы'", new String[]{  });
//
//        if (cc.moveToFirst()) {
//
//                String name = cc.getString(cc.getColumnIndex("name"));
//                String curRef = cc.getString(cc.getColumnIndex("ref"));
//
//                switch (name){
//
//                    case "ЛидыПрисоединенныеФайлы": {
//
//                        String[] arCurRef = curRef.split("@@");
//
//                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//
//                        /* example for setting a HttpMultipartMode */
//                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//                        /* example for adding an image part */
//                        FileBody fileBody = new FileBody(new File(Environment.getExternalStorageDirectory(), arCurRef[2] + ".jpg")); //image should be a String
//                        builder.addPart("icon", fileBody);
//
//                        client.postBinary(mCtx, "files/ref/Лиды/" + arCurRef[1] + "/" + arCurRef[2], builder.build(), new AsyncHttpResponseHandler() {
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                                String result = null;
//                                try {
//                                    result = new String(responseBody, "UTF-8");
//                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                    Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                                }
//
//                                delRecByRef("changes", result);
//
//                                postChangesFiles();
//
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
//
//                                requested = false;
//
//                                error = e.getLocalizedMessage();
//
////                                Toast.makeText(mCtx, "postChangesFiles statusCode:  " + statusCode, Toast.LENGTH_LONG).show();
//
//                            }
//                        });
//
//
//                        break;
//
//                    }
//
//                    case "ПосещениеКонтрагентаПрисоединенныеФайлы": {
//
//                        String[] arCurRef = curRef.split("@@");
//
//                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//
//                        /* example for setting a HttpMultipartMode */
//                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//                        /* example for adding an image part */
//                        FileBody fileBody = new FileBody(new File(Environment.getExternalStorageDirectory(), arCurRef[2] + ".jpg")); //image should be a String
//                        builder.addPart("icon", fileBody);
//
//                        client.postBinary(mCtx, "files/ref/ПосещениеКонтрагента/" + arCurRef[1] + "/" + arCurRef[2], builder.build(), new AsyncHttpResponseHandler() {
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                                String result = null;
//                                try {
//                                    result = new String(responseBody, "UTF-8");
//                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                    Toast.makeText(mCtx, "getChanges:  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                                }
//
//                                delRecByRef("changes", result);
//
//                                postChangesFiles();
//
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
//
//                                requested = false;
//
//                                error = e.getLocalizedMessage();
//
////                                Toast.makeText(mCtx, "postChangesFiles statusCode:  " + statusCode, Toast.LENGTH_LONG).show();
//
//                            }
//                        });
//
//
//                        break;
//
//                    }
//
//                }
//
//        }
//
//        else
//        {
//            requested = false;
//        }
//
//        cc.close();
//
//    }

    public String getRequestUserProg() {

        open();

        String url = "request/" + getConstant("user_id") + "/" + getConstant("prog_id");

        close();

        return url;
    }

    public static String getProgId(Context context) {

        DB db = new DB(context);

        db.open();

        String url = db.getConstant("prog_id");

        db.close();

        return url;
    }

    public static String getConstant(Context context, String name) {

        DB db = new DB(context);

        db.open();

        String url = db.getConstant(name);

        db.close();

        return url;
    }
    @NonNull
//    private void putChangeLead(JSONArray jsonArray, String curRef) {
//
//        ContentValues ccref = getRecByRef("leads", curRef);
//
//        Change changeServiceGroup = new Change();
//        changeServiceGroup._Ссылка = ccref.getAsString("services_group");
//        changeServiceGroup._Вид = "ГруппыСервиса";
//        changeServiceGroup._Тип = "Перечисление";
//
//        Change changeUser = new Change();
//        changeUser._Ссылка = ccref.getAsString("user");
//        changeUser._Вид = "Пользователи";
//        changeUser._Тип = "Справочник";
//
//        Change changeMainSupplier = new Change();
//        changeMainSupplier._Ссылка = ccref.getAsString("main_supplier");
//        changeMainSupplier._Вид = "Контрагенты";
//        changeMainSupplier._Тип = "Справочник";
//
//        Change changeThisContractor = new Change();
//        changeThisContractor._Ссылка = ccref.getAsString("this_contractor");
//        changeThisContractor._Вид = "Контрагенты";
//        changeThisContractor._Тип = "Справочник";
//
//        Change changeCity = new Change();
//        changeCity._Ссылка = ccref.getAsString("city");
//        changeCity._Вид = "Города";
//        changeCity._Тип = "Справочник";
//
//        Change changeCityDistrict = new Change();
//        changeCityDistrict._Ссылка = ccref.getAsString("city_district");
//        changeCityDistrict._Вид = "РайоныГородов";
//        changeCityDistrict._Тип = "Справочник";
//
//        Change changeIcon = new Change();
//
//        String[] arIconPath = ccref.getAsString("icon").split("/");
//        String[] arIconFileName = arIconPath[arIconPath.length - 1].split("[.]");
//
//        changeIcon._Ссылка = arIconFileName[0];
//        changeIcon._Вид = "ЛидыПрисоединенныеФайлы";
//        changeIcon._Тип = "Справочник";
//
//        Change changeTerritory = new Change();
//        changeTerritory._Ссылка = ccref.getAsString("territory");
//        changeTerritory._Вид = "Территории";
//        changeTerritory._Тип = "Справочник";
//
//        ChangeLead changeLead = new ChangeLead(curRef, ccref.getAsLong("date_creation"), ccref.getAsString("name"), changeServiceGroup, ccref.getAsString("address"), ccref.getAsString("description"),
//                ccref.getAsString("owner_fio"), ccref.getAsString("owner_phone"),ccref.getAsString("purchaser_fio"),ccref.getAsString("purchaser_phone"),
//                ccref.getAsInteger("area"), ccref.getAsInteger("rooms"),ccref.getAsInteger("cars_in_mounth"), ccref.getAsInteger("elements_in_mounth"), ccref.getAsInteger("normohours_in_mounth"),
//                ccref.getAsInteger("output"), ccref.getAsString("opening_hours"),ccref.getAsInteger("changes"),ccref.getAsInteger("preparing_zones"),
//                ccref.getAsInteger("osks"), ccref.getAsString("mixsystem"),ccref.getAsInteger("armourers"),ccref.getAsInteger("bodybuilders"),
//                ccref.getAsInteger("preparers"), ccref.getAsInteger("painters"),ccref.getAsInteger("colourers"),ccref.getAsDouble("latitude"),
//                ccref.getAsDouble("longitude"), changeUser, changeMainSupplier, changeThisContractor, changeCity, changeCityDistrict, changeIcon, changeTerritory,
//                getLeadWorkers(curRef), getLeadSuppliers(curRef), getLeadMixSystems(curRef));
//
//        try {
//            jsonArray.put(changeLead.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public ArrayList<ChangeWorker> getLeadWorkers(String ref) {
//
//        ArrayList<ChangeWorker> leadWorkers = new ArrayList<>();
//
//        Cursor cc = getAllDataByFilter("leads_workers", "lead_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeWorker leadWorker = new ChangeWorker();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "fio": { leadWorker.ФИО = cc.getString(cc.getColumnIndex(name)); break; }
//                        case "email": { leadWorker.Email = cc.getString(cc.getColumnIndex(name)); break; }
//                        case "phone": { leadWorker.Телефон = cc.getString(cc.getColumnIndex(name)); break; }
//                        case "position": {
//
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Должности";
//                            changeGood._Тип = "Справочник";
//
//                            leadWorker.Должность = changeGood;
//
//                            break; }
//
//                    }
//                }
//
//                leadWorkers.add(leadWorker);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return leadWorkers;
//
//
//    }
//
//    public ArrayList<ChangeMixSystem> getLeadMixSystems(String ref) {
//
//        ArrayList<ChangeMixSystem> leadMixSystems = new ArrayList<>();
//
//        Cursor cc = getAllDataByFilter("leads_mixsystems", "lead_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeMixSystem leadMixSystem = new ChangeMixSystem();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "mixsystem": { leadMixSystem.МиксСистема = cc.getString(cc.getColumnIndex(name)); break; }
//                        case "active": { leadMixSystem.Активность = cc.getInt(cc.getColumnIndex(name)) == 1; break; }
//
//                    }
//                }
//
//                leadMixSystems.add(leadMixSystem);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return leadMixSystems;
//
//    }
//
//    public ArrayList<ChangeLeadSupplier> getLeadSuppliers(String ref){
//
//        ArrayList<ChangeLeadSupplier> leadSuppliers = new ArrayList<>();
//
//        Cursor cc = getAllDataByFilter("leads_suppliers", "lead_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeLeadSupplier leadSupplier = new ChangeLeadSupplier();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "supplier": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Контрагенты";
//                            changeGood._Тип = "Справочник";
//
//                            leadSupplier.Поставщик = changeGood;
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                leadSuppliers.add(leadSupplier);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return leadSuppliers;
//
//    }
//
//    private void putChangeOrder(JSONArray jsonArray, String curRef) {
//
//        ContentValues ccref = getRecByRef("orders", curRef);
//
//        Change changeUser = new Change();
//        changeUser._Ссылка = ccref.getAsString("user");
//        changeUser._Вид = "Пользователи";
//        changeUser._Тип = "Справочник";
//
//        Change changeContractor = new Change();
//        changeContractor._Ссылка = ccref.getAsString("contractor");
//        changeContractor._Вид = "Контрагенты";
//        changeContractor._Тип = "Справочник";
//
//        Change changeOrganisation = new Change();
//        changeOrganisation._Ссылка = ccref.getAsString("organisation");
//        changeOrganisation._Вид = "Организации";
//        changeOrganisation._Тип = "Справочник";
//
//        Change changeCurrency = new Change();
//        changeCurrency._Ссылка = ccref.getAsString("currency");
//        changeCurrency._Вид = "Валюты";
//        changeCurrency._Тип = "Справочник";
//
//        Change changeWarehouse = new Change();
//        changeWarehouse._Ссылка = ccref.getAsString("warehouse");
//        changeWarehouse._Вид = "Склады";
//        changeWarehouse._Тип = "Справочник";
//
//        ChangeOrder changeOrder = new ChangeOrder(curRef, ccref.getAsLong("date"), changeContractor, changeOrganisation, changeCurrency, changeWarehouse, changeUser, getOrderGoods(curRef));
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public ArrayList<ChangeOrderGood> getOrderGoods(String ref){
//
//        ArrayList<ChangeOrderGood> order_goods = new ArrayList<ChangeOrderGood>();
//
//        Cursor cc = getAllDataByFilter("orders_goods", "order_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeOrderGood orderGood = new ChangeOrderGood();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
////                        case "order_number": orderGood. = cc.getInt(cc.getColumnIndex(name));
//                        case "good": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Номенклатура";
//                            changeGood._Тип = "Справочник";
//
//                            orderGood.Номенклатура = changeGood;
//
//                            break;
//
//                        }
//                        case "characteristic": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ХарактеристикиНоменклатуры";
//                            changeGood._Тип = "Справочник";
//
//                            orderGood.Характеристика = changeGood;
//
//                            break;
//
//                        }
//                        case "quantity": {
//                            orderGood.Количество = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//                        case "price": {
//                            orderGood.Цена = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//                        case "sum": {
//                            orderGood.Сумма = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//
//                        case "measurement_unit": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ЕдиницыИзмерения";
//                            changeGood._Тип = "Справочник";
//
//                            orderGood.ЕдиницаИзмерения = changeGood;
//
//                            break;
//
//                        }
//                        case "measurement_unit_box": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ЕдиницыИзмерения";
//                            changeGood._Тип = "Справочник";
//
//                            orderGood.ЕдиницаИзмеренияМест = changeGood;
//
//                            break;
//
//                        }
//
//                        case "quantity_box": {
//                            orderGood.КоличествоМест = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//                        case "coefficient": {
//                            orderGood.Коэффициент = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//                        case "auto_discount_percent": {
//                            orderGood.ПроцентАвтоматическихСкидок = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//                        case "discount_percent": {
//                            orderGood.ПроцентСкидкиНаценки = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//                        case "nds_percent": {
//                            orderGood.СтавкаНДС = cc.getString(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//                        case "nds_sum": {
//                            orderGood.СуммаНДС = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//
//                        case "currency": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Валюты";
//                            changeGood._Тип = "Справочник";
//
//                            orderGood.Валюта = changeGood;
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                order_goods.add(orderGood);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return order_goods;
//
//    }
//
//    private void putChangeVisit(JSONArray jsonArray, String curRef) {
//
//        ContentValues ccref = getRecByRef("visits", curRef);
//
//        Change changeUser = new Change();
//        changeUser._Ссылка = ccref.getAsString("user");
//        changeUser._Вид = "Пользователи";
//        changeUser._Тип = "Справочник";
//
//        Change changeContractor = new Change();
//        changeContractor._Ссылка = ccref.getAsString("contractor");
//        changeContractor._Вид = "Контрагенты";
//        changeContractor._Тип = "Справочник";
//
//        Change changeLead = new Change();
//        changeLead._Ссылка = ccref.getAsString("lead");
//        changeLead._Вид = "Лиды";
//        changeLead._Тип = "Справочник";
//
//        Change changeDestination = new Change();
//        changeDestination._Ссылка = ccref.getAsString("destination_ref");
//        changeDestination._Вид = "НазначениеПосещения";
//        changeDestination._Тип = "Документ";
//
//        Change changeTypeOperation = new Change();
//        changeTypeOperation._Ссылка = ccref.getAsString("type_operation");
//        changeTypeOperation._Вид = "ВидыОперацийПосещениеКонтрагента";
//        changeTypeOperation._Тип = "Перечисление";
//
//        ChangeVisit changeOrder = new ChangeVisit(curRef, ccref.getAsLong("date"), changeDestination, changeContractor, changeLead, changeTypeOperation, changeUser,
//                ccref.getAsInteger("interviewed") == 1,
//                getVisitMaterials(curRef), getVisitDemos(curRef), getVisitFotos(curRef));
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public ArrayList<ChangeVisitDemo> getVisitDemos(String ref){
//
//        ArrayList<ChangeVisitDemo> visitDemos = new ArrayList<ChangeVisitDemo>();
//
//        Cursor cc = getAllDataByFilter("visits_demos", "visit_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeVisitDemo visitDemo = new ChangeVisitDemo();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
////                        case "order_number": orderGood. = cc.getInt(cc.getColumnIndex(name));
//                        case "good": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Номенклатура";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Номенклатура = changeGood;
//
//                            break;
//
//                        }
//                        case "characteristic": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ХарактеристикиНоменклатуры";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Характеристика = changeGood;
//
//                            break;
//
//                        }
//                        case "quantity": {
//                            visitDemo.Количество = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                visitDemos.add(visitDemo);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return visitDemos;
//
//    }
//
//    public ArrayList<ChangeVisitMaterials> getVisitMaterials(String ref){
//
//        ArrayList<ChangeVisitMaterials> visitMaterials = new ArrayList<ChangeVisitMaterials>();
//
//        Cursor cc = getAllDataByFilter("visits_materials", "visit_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeVisitMaterials visitMaterial = new ChangeVisitMaterials();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "good": {
//
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ТоварыДляОпроса";
//                            changeGood._Тип = "Справочник";
//
//                            visitMaterial.Номенклатура = changeGood;
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                visitMaterials.add(visitMaterial);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return visitMaterials;
//
//    }
//
//    public ArrayList<ChangeVisitFoto> getVisitFotos(String ref){
//
//        ArrayList<ChangeVisitFoto> visitFotos = new ArrayList<ChangeVisitFoto>();
//
//        Cursor cc = getAllDataByFilter("visits_fotos", "visit_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeVisitFoto visitFoto = new ChangeVisitFoto();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "foto": {
//
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ПосещениеКонтрагентаПрисоединенныеФайлы";
//                            changeGood._Тип = "Справочник";
//
//                            visitFoto.Фото = changeGood;
//
//                            break;
//
//                        }
//
//                        case "date": {
//
//                            visitFoto.ДатаФото = new Date(cc.getLong(cc.getColumnIndex(name)));
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                visitFotos.add(visitFoto);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return visitFotos;
//
//    }
//
//    private void putChangeDestination(JSONArray jsonArray, String curRef) {
//
//        ContentValues ccref = getRecByRef("destinations", curRef);
//
//        Change changeUser = new Change();
//        changeUser._Ссылка = ccref.getAsString("user");
//        changeUser._Вид = "Пользователи";
//        changeUser._Тип = "Справочник";
//
//        Change changeAuthor = new Change();
//        changeAuthor._Ссылка = ccref.getAsString("author");
//        changeAuthor._Вид = "Пользователи";
//        changeAuthor._Тип = "Справочник";
//
//        Change changeLead = new Change();
//        changeLead._Ссылка = ccref.getAsString("lead");
//        changeLead._Вид = "Лиды";
//        changeLead._Тип = "Справочник";
//
//        ChangeDestination changeOrder = new ChangeDestination(curRef, ccref.getAsLong("date"), changeLead, changeUser, changeAuthor);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeSalePointReport(JSONArray jsonArray, String curRef) {
//
//        ContentValues ccref = getRecByRef("sale_point_reports", curRef);
//
//        Change changeContractor = new Change();
//        changeContractor._Ссылка = ccref.getAsString("contractor");
//        changeContractor._Вид = "Контрагенты";
//        changeContractor._Тип = "Справочник";
//
//        Change changeUser = new Change();
//        changeUser._Ссылка = ccref.getAsString("user");
//        changeUser._Вид = "Пользователи";
//        changeUser._Тип = "Справочник";
//
//        ChangeSalePointReport changeOrder = new ChangeSalePointReport(curRef, new Date(ccref.getAsLong("date")), new Date(ccref.getAsLong("date_end")),
//                changeContractor, changeUser, ccref.getAsString("comment"),
//                getSalePointReportNewLeads(curRef), getSalePointReportOldLeads(curRef), getSalePointReportSalesGoods(curRef));
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public ArrayList<ChangeSalePointNewLeads> getSalePointReportNewLeads(String ref){
//
//        ArrayList<ChangeSalePointNewLeads> visitDemos = new ArrayList<ChangeSalePointNewLeads>();
//
//        Cursor cc = getAllDataByFilter("sale_point_reports_new_leads", "ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeSalePointNewLeads visitDemo = new ChangeSalePointNewLeads();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "lead": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Лиды";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Лид = changeGood;
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                visitDemos.add(visitDemo);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return visitDemos;
//
//    }
//
//    public ArrayList<ChangeSalePointOldLeads> getSalePointReportOldLeads(String ref){
//
//        ArrayList<ChangeSalePointOldLeads> visitDemos = new ArrayList<ChangeSalePointOldLeads>();
//
//        Cursor cc = getAllDataByFilter("sale_point_reports_old_leads", "ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeSalePointOldLeads visitDemo = new ChangeSalePointOldLeads();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "lead": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Лиды";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Лид = changeGood;
//
//                            break;
//
//                        }
//
//                        case "comment": {
//
//                            visitDemo.Комментарий = cc.getString(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                visitDemos.add(visitDemo);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return visitDemos;
//
//    }
//
//    public ArrayList<ChangeSalePointSaledGoods> getSalePointReportSalesGoods(String ref){
//
//        ArrayList<ChangeSalePointSaledGoods> visitDemos = new ArrayList<ChangeSalePointSaledGoods>();
//
//        Cursor cc = getAllDataByFilter("sale_point_reports_goods_saled", "ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeSalePointSaledGoods visitDemo = new ChangeSalePointSaledGoods();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
//                        case "good": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Номенклатура";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Номенклатура = changeGood;
//
//                            break;
//
//                        }
//                        case "characteristic": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ХарактеристикиНоменклатуры";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Характеристика = changeGood;
//
//                            break;
//
//                        }
//                        case "quantity": {
//                            visitDemo.Количество = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//
//                        case "lead": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Лиды";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Лид = changeGood;
//
//                            break;
//
//                        }
//                    }
//                }
//
//                visitDemos.add(visitDemo);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return visitDemos;
//
//    }
//
//    private void putChangeDiscountsContractorsGoodsGroups(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeContractor = new Change();
//        changeContractor._Ссылка = strRef[0];
//        changeContractor._Вид = "Контрагенты";
//        changeContractor._Тип = "Справочник";
//
//        Change changeWarehouse = new Change();
//        changeWarehouse._Ссылка = strRef[1];
//        changeWarehouse._Вид = "ТоварныеГруппы";
//        changeWarehouse._Тип = "Справочник";
//
//        ArrayList<ChangeDiscountsContractorsGoodsGroupsFilter> arFilter = new ArrayList<ChangeDiscountsContractorsGoodsGroupsFilter>();
//
//        ChangeDiscountsContractorsGoodsGroupsFilter filter = new ChangeDiscountsContractorsGoodsGroupsFilter();
//        filter.Имя = "Контрагент";
//        filter.Значение = changeContractor;
//
//        arFilter.add(filter);
//
//        ChangeDiscountsContractorsGoodsGroupsFilter filter2 = new ChangeDiscountsContractorsGoodsGroupsFilter();
//        filter2.Имя = "ТоварнаяГруппа";
//        filter2.Значение = changeWarehouse;
//
//        arFilter.add(filter2);
//
//        ChangeDiscountsContractorsGoodsGroups changeOrder = new ChangeDiscountsContractorsGoodsGroups(arFilter);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeGoodsPrices(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeGoods = new Change();
//        changeGoods._Ссылка = strRef[1];
//        changeGoods._Вид = "Номенклатура";
//        changeGoods._Тип = "Справочник";
//
//        Change changeCharacteristic = new Change();
//        changeCharacteristic._Ссылка = strRef[2];
//        changeCharacteristic._Вид = "ХарактеристикиНоменклатуры";
//        changeCharacteristic._Тип = "Справочник";
//
//        Change changeMeasurement = new Change();
//        changeMeasurement._Ссылка = strRef[3];
//        changeMeasurement._Вид = "ЕдиницыИзмерения";
//        changeMeasurement._Тип = "Справочник";
//
//        Change changeCurrency = new Change();
//        changeCurrency._Ссылка = strRef[4];
//        changeCurrency._Вид = "Валюты";
//        changeCurrency._Тип = "Справочник";
//
//        ArrayList<ChangeGoodsPricesFilter> arFilter = new ArrayList<ChangeGoodsPricesFilter>();
//
//        ChangeGoodsPricesFilter filter = new ChangeGoodsPricesFilter();
//        filter.Имя = "Период";
//        filter.ЗначениеСтр = strRef[0];
//        filter.Значение = new Change();
//
//        arFilter.add(filter);
//
//        ChangeGoodsPricesFilter filter2 = new ChangeGoodsPricesFilter();
//        filter2.Имя = "Номенклатура";
//        filter2.Значение = changeGoods;
//
//        arFilter.add(filter2);
//
//        ChangeGoodsPricesFilter filter3 = new ChangeGoodsPricesFilter();
//        filter3.Имя = "Характеристика";
//        filter3.Значение = changeCharacteristic;
//
//        arFilter.add(filter3);
//
//        ChangeGoodsPricesFilter filter4 = new ChangeGoodsPricesFilter();
//        filter4.Имя = "ЕдиницаИзмерения";
//        filter4.Значение = changeMeasurement;
//
//        arFilter.add(filter4);
//
//        ChangeGoodsPricesFilter filter5 = new ChangeGoodsPricesFilter();
//        filter5.Имя = "Валюта";
//        filter5.Значение = changeCurrency;
//
//        arFilter.add(filter5);
//
//        ChangeGoodsPrices changeOrder = new ChangeGoodsPrices(arFilter);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeCoords(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeGoods = new Change();
//        changeGoods._Ссылка = strRef[1];
//        changeGoods._Вид = "Пользователи";
//        changeGoods._Тип = "Справочник";
//
//        ArrayList<ChangeCoordsFilter> arFilter = new ArrayList<ChangeCoordsFilter>();
//
//        ChangeCoordsFilter filter = new ChangeCoordsFilter();
//        filter.Имя = "Период";
//        filter.ЗначениеСтр = (String) DateFormat.format("yyyyMMddHHmmss", Long.valueOf(strRef[0]));
//        filter.Значение = new Change();
//
//        arFilter.add(filter);
//
//        ChangeCoordsFilter filter2 = new ChangeCoordsFilter();
//        filter2.Имя = "Пользователь";
//        filter2.Значение = changeGoods;
//
//        arFilter.add(filter2);
//
//        ArrayList<ChangeCoordsRecord> arRecord = new ArrayList<ChangeCoordsRecord>();
//
//        ChangeCoordsRecord record = new ChangeCoordsRecord();
//
//        ContentValues cv = getRecByFilter("coords", "date = ? and user = ?", new String[]{ strRef[0], strRef[1] });
//
//        record.Период = filter.ЗначениеСтр;
//        record.Пользователь = changeGoods;
//        record.Широта = cv.getAsFloat("latitude");
//        record.Долгота = cv.getAsFloat("longitude");
//
//        arRecord.add(record);
//
//        ChangeCoords changeOrder = new ChangeCoords(arFilter, arRecord);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangePhoneCalls(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeGoods = new Change();
//        changeGoods._Ссылка = strRef[1];
//        changeGoods._Вид = "Пользователи";
//        changeGoods._Тип = "Справочник";
//
//        ArrayList<ChangePhoneCallsFilter> arFilter = new ArrayList<ChangePhoneCallsFilter>();
//
//        ChangePhoneCallsFilter filter = new ChangePhoneCallsFilter();
//        filter.Имя = "Период";
//        filter.ЗначениеСтр = (String) DateFormat.format("yyyyMMddHHmmss", Long.valueOf(strRef[0]));
//        filter.Значение = new Change();
//
//        arFilter.add(filter);
//
//        ChangePhoneCallsFilter filter2 = new ChangePhoneCallsFilter();
//        filter2.Имя = "Пользователь";
//        filter2.Значение = changeGoods;
//
//        arFilter.add(filter2);
//
//        ArrayList<ChangePhoneCallsRecord> arRecord = new ArrayList<ChangePhoneCallsRecord>();
//
//        ChangePhoneCallsRecord record = new ChangePhoneCallsRecord();
//
//        ContentValues cv = getRecByFilter("phone_calls", "date = ? and user = ?", new String[]{ strRef[0], strRef[1] });
//
//        Change changeType = new Change();
//        changeType._Ссылка = cv.getAsString("type");
//        changeType._Вид = "ТипыТелефонногоЗвонка";
//        changeType._Тип = "Перечисление";
//
//        record.Период = filter.ЗначениеСтр;
//        record.Пользователь = changeGoods;
//        record.Телефон = cv.getAsString("phone");
//        record.ТипТелефонногоЗвонка = changeType;
//
//        arRecord.add(record);
//
//        ChangePhoneCalls changeOrder = new ChangePhoneCalls(arFilter, arRecord);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeVisitsAccess(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeGoods = new Change();
//        changeGoods._Ссылка = strRef[0];
//        changeGoods._Вид = "Пользователи";
//        changeGoods._Тип = "Справочник";
//
//        Change changeCharacteristic = new Change();
//        changeCharacteristic._Ссылка = strRef[1];
//        changeCharacteristic._Вид = "Пользователи";
//        changeCharacteristic._Тип = "Справочник";
//
//        ArrayList<ChangeVisitsAccessFilter> arFilter = new ArrayList<ChangeVisitsAccessFilter>();
//
//        ChangeVisitsAccessFilter filter = new ChangeVisitsAccessFilter();
//        filter.Имя = "Пользователь";
//        filter.Значение = changeGoods;
//
//        arFilter.add(filter);
//
//        ChangeVisitsAccessFilter filter2 = new ChangeVisitsAccessFilter();
//        filter2.Имя = "ПользовательПосещений";
//        filter2.Значение = changeCharacteristic;
//
//        arFilter.add(filter2);
//
//        ChangeVisitsAccess changeOrder = new ChangeVisitsAccess(arFilter);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeMaterialConsumptionRatesForDemonstrations(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeGoods = new Change();
//        changeGoods._Ссылка = strRef[0];
//        changeGoods._Вид = "ВидыДемонстрации";
//        changeGoods._Тип = "Справочник";
//
//        Change changeCharacteristic = new Change();
//        changeCharacteristic._Ссылка = strRef[1];
//        changeCharacteristic._Вид = "ЭтапыДемонстраций";
//        changeCharacteristic._Тип = "Перечисление";
//
//        Change changeOkp = new Change();
//        changeOkp._Ссылка = strRef[2];
//        changeOkp._Вид = "ОбщероссийскийКлассификаторПродукции";
//        changeOkp._Тип = "Справочник";
//
//        Change changeGood = new Change();
//        changeGood._Ссылка = strRef[3];
//        changeGood._Вид = "Номенклатура";
//        changeGood._Тип = "Справочник";
//
//        ArrayList<ChangeMaterialConsumptionRatesForDemonstrationsFilter> arFilter = new ArrayList<ChangeMaterialConsumptionRatesForDemonstrationsFilter>();
//
//        ChangeMaterialConsumptionRatesForDemonstrationsFilter filter = new ChangeMaterialConsumptionRatesForDemonstrationsFilter();
//        filter.Имя = "ВидДемонстрации";
//        filter.Значение = changeGoods;
//
//        arFilter.add(filter);
//
//        ChangeMaterialConsumptionRatesForDemonstrationsFilter filter2 = new ChangeMaterialConsumptionRatesForDemonstrationsFilter();
//        filter2.Имя = "ЭтапДемонстрации";
//        filter2.Значение = changeCharacteristic;
//
//        arFilter.add(filter2);
//
//        ChangeMaterialConsumptionRatesForDemonstrationsFilter filter3 = new ChangeMaterialConsumptionRatesForDemonstrationsFilter();
//        filter3.Имя = "ГруппаМатериаловДемонстраций";
//        filter3.Значение = changeOkp;
//
//        arFilter.add(filter3);
//
//        ChangeMaterialConsumptionRatesForDemonstrationsFilter filter4 = new ChangeMaterialConsumptionRatesForDemonstrationsFilter();
//        filter4.Имя = "Номенклатура";
//        filter4.Значение = changeGood;
//
//        arFilter.add(filter4);
//
//        ChangeMaterialConsumptionRatesForDemonstrations changeOrder = new ChangeMaterialConsumptionRatesForDemonstrations(arFilter);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeSales(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeContractor = new Change();
//        changeContractor._Ссылка = strRef[1];
//        changeContractor._Вид = "Контрагенты";
//        changeContractor._Тип = "Справочник";
//
//        Change changePriceGroup = new Change();
//        changePriceGroup._Ссылка = strRef[2];
//        changePriceGroup._Вид = "ТоварныеГруппы";
//        changePriceGroup._Тип = "Справочник";
//
//        Change changeOkp = new Change();
//        changeOkp._Ссылка = strRef[3];
//        changeOkp._Вид = "ОбщероссийскийКлассификаторПродукции";
//        changeOkp._Тип = "Справочник";
//
//        ArrayList<ChangeSalesFilter> arFilter = new ArrayList<ChangeSalesFilter>();
//
//        ChangeSalesFilter filterp = new ChangeSalesFilter();
//        filterp.Имя = "Период";
//        filterp.ЗначениеСтр = strRef[0];
//        filterp.Значение = new Change();
//
//        arFilter.add(filterp);
//
//        ChangeSalesFilter filter = new ChangeSalesFilter();
//        filter.Имя = "Контрагент";
//        filter.Значение = changeContractor;
//
//        arFilter.add(filter);
//
//        ChangeSalesFilter filter2 = new ChangeSalesFilter();
//        filter2.Имя = "ЦеноваяГруппа";
//        filter2.Значение = changePriceGroup;
//
//        arFilter.add(filter2);
//
//        ChangeSalesFilter filter3 = new ChangeSalesFilter();
//        filter3.Имя = "ОКП";
//        filter3.Значение = changeOkp;
//
//        arFilter.add(filter3);
//
//        ChangeSales changeOrder = new ChangeSales(arFilter);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeCitiesAccess(JSONArray jsonArray, String curRef) {
//
//        String[] strRef = curRef.split("@@");
//
//        Change changeGoods = new Change();
//        changeGoods._Ссылка = strRef[0];
//        changeGoods._Вид = "Пользователи";
//        changeGoods._Тип = "Справочник";
//
//        Change changeCharacteristic = new Change();
//        changeCharacteristic._Ссылка = strRef[1];
//        changeCharacteristic._Вид = "Города";
//        changeCharacteristic._Тип = "Справочник";
//
//        ArrayList<ChangeCitiesAccessFilter> arFilter = new ArrayList<ChangeCitiesAccessFilter>();
//
//        ChangeCitiesAccessFilter filter = new ChangeCitiesAccessFilter();
//        filter.Имя = "Пользователь";
//        filter.Значение = changeGoods;
//
//        arFilter.add(filter);
//
//        ChangeCitiesAccessFilter filter2 = new ChangeCitiesAccessFilter();
//        filter2.Имя = "Город";
//        filter2.Значение = changeCharacteristic;
//
//        arFilter.add(filter2);
//
//        ChangeCitiesAccess changeOrder = new ChangeCitiesAccess(arFilter);
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void putChangeInvent(JSONArray jsonArray, String curRef) {
//
//        ContentValues ccref = getRecByRef("invents", curRef);
//
//        Change changeUser = new Change();
//        changeUser._Ссылка = ccref.getAsString("user");
//        changeUser._Вид = "Пользователи";
//        changeUser._Тип = "Справочник";
//
//        ContentValues cvu = getRecByRef("users", changeUser._Ссылка);
//        Change changeWarehouse = new Change();
//        changeWarehouse._Ссылка = cvu.getAsString("warehouse");
//        changeWarehouse._Вид = "Склады";
//        changeWarehouse._Тип = "Справочник";
//
//        ChangeInvent changeOrder = new ChangeInvent(curRef, ccref.getAsLong("date"), changeUser, changeWarehouse, getInventGoods(curRef));
//
//        try {
//            jsonArray.put(changeOrder.getJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public ArrayList<ChangeInventGoods> getInventGoods(String ref){
//
//        ArrayList<ChangeInventGoods> visitDemos = new ArrayList<ChangeInventGoods>();
//
//        Cursor cc = getAllDataByFilter("invents_goods", "invent_ref = ?", new String[] { ref }, "order_number");
//
//        if (cc.moveToFirst()) {
//
//            do {
//
//                ChangeInventGoods visitDemo = new ChangeInventGoods();
//
//                for (String name : cc.getColumnNames()) {
//
//                    switch (name) {
//
////                        case "order_number": orderGood. = cc.getInt(cc.getColumnIndex(name));
//                        case "good": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "Номенклатура";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Номенклатура = changeGood;
//
//                            break;
//
//                        }
//                        case "characteristic": {
//                            Change changeGood = new Change();
//                            changeGood._Ссылка = cc.getString(cc.getColumnIndex(name));
//                            changeGood._Вид = "ХарактеристикиНоменклатуры";
//                            changeGood._Тип = "Справочник";
//
//                            visitDemo.Характеристика = changeGood;
//
//                            break;
//
//                        }
//                        case "quantity": {
//                            visitDemo.Количество = cc.getFloat(cc.getColumnIndex(name));
//
//                            break;
//
//                        }
//
//                    }
//                }
//
//                visitDemos.add(visitDemo);
//
//            } while (cc.moveToNext());
//        }
//
//        cc.close();
//
//        return visitDemos;
//
//    }

    public String getExternalRef(String ref) {

        String external = null;

        Cursor cc = getAllDataByFilter("refs", "internal = ? or external = ?", new String[] { ref, ref }, null);

        if (cc.moveToFirst()) {

            do {

                for (String name : cc.getColumnNames()) {

                    switch (name) {

                        case "external": {
                            external = cc.getString(cc.getColumnIndex(name));
                            break;
                        }

                    }
                }


            } while (cc.moveToNext());
        }

        cc.close();

        return external;

    }

    public String getStringByRef(String table, String ref, String props, String def) {

        String result = def;
        if(ref != null && !ref.isEmpty() && !ref.equals(emptyRef)){

            ContentValues cvref = getRecByRef(table, ref);

            result = cvref.getAsString(props);
        }
        return result;
    }

//    public DataBaseAdapter getDBA(Context context, String dbname, String[] from, int[] to, int refs_list_item, int llMain){
//
//        DB db = new DB(context);
//        db.open();
//        Cursor cursor = db.getAllData(dbname, "_id");
//
//        // создааем адаптер и настраиваем список
//        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context, cursor, from, to, refs_list_item, llMain);
//
//        db.close();
//
//        return dataBaseAdapter;
//
//    }

//    public DataBaseAdapter getDBA(Context context, String dbname, String[] from, int[] to, int refs_list_item, int llMain, int idIvFoto, String fieldFoto){
//
//        DB db = new DB(context);
//        db.open();
//        Cursor cursor = db.getAllData(dbname, "_id");
//
//        // создааем адаптер и настраиваем список
//        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context, cursor, from, to, refs_list_item, llMain, idIvFoto, fieldFoto);
//
//        db.close();
//
//        return dataBaseAdapter;
//
//    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table constants ("
                    + "_id integer primary key autoincrement,"
                    + "name text,"
                    + "value text" + ");");

            db.execSQL("create table messages ("
                    + "_id integer primary key autoincrement,"
                    + "date text, "
                    + "text text, "
                    + "isNew integer "
                    + ");");

            db.execSQL("create table refs ("
                    + "_id integer primary key autoincrement,"
                    + "internal text, "
                    + "external text "
                    + ");");

            db.execSQL("create table requests ("
                    + "_id integer primary key autoincrement,"
                    + "method text, "
                    + "params text, "
                    + "response text "
                    + ");");

            db.execSQL("create table filesToSend ("
                    + "_id integer primary key autoincrement,"
                    + "method text, "
                    + "url text, "
                    + "path text "
                    + ");");

        }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                if (oldVersion == 1 && newVersion == 2) {

                }
        }
    }

    public Boolean isTest(){

        open();

        String isTest = getConstant("test");

        close();

        return isTest == null || isTest.equals("true");

    }

    public void setTest(Boolean isTest){

        open();

        updateConstant("test", isTest ? "true" : "false");

        close();

    }

//    public static Driver getDriverFromDB(Context context){
//
//        Driver driver = new Driver();
//
//        DB db = new DB(context);
//        db.open();
//
//        driver.ref = db.getConstant("driver_ref");
//        driver.description = db.getConstant("driver_description");
//        driver.transportRef = db.getConstant("transport_ref");
//        driver.transportDescription = db.getConstant("transport_description");
//        driver.transportDate = db.getConstant("transport_date");
//
//        db.close();
//
//        return driver;
//
//    }
//
}


