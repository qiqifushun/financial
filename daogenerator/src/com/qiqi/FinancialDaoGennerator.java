package com.qiqi;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class FinancialDaoGennerator {

    public static void main(String[] args) {

        Schema schema = new Schema(1, "com.qiqi.financial.dao");
        schema.enableKeepSectionsByDefault();

        Entity category = createCategory(schema);
        Entity record = createRecord(schema);

        // 表关系
        Property categoryId = record.addLongProperty("categoryId").notNull()
                .getProperty();
        record.addToOne(category, categoryId);
        ToMany categoryToRecord = category.addToMany(record, categoryId);
        categoryToRecord.setName("records");
        Property recordTime = record.addLongProperty("time").notNull()
                .getProperty();// 产生时间
        categoryToRecord.orderDesc(recordTime);

        try {
            new DaoGenerator().generateAll(schema, "../financial/src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Entity createCategory(Schema schema) {
        Entity category = schema.addEntity("Category");
        category.setTableName("category");
        category.addIdProperty().autoincrement();
        category.addStringProperty("categoryName").notNull();
        Property parentIdProperty = category.addLongProperty("parentId")
                .notNull().getProperty();
        category.addToOne(category, parentIdProperty).setName("parent");
        category.addToMany(category, parentIdProperty).setName("children");
        return category;
    }

    private static Entity createRecord(Schema schema) {
        Entity record = schema.addEntity("Record");
        record.setTableName("record");
        record.addIdProperty().autoincrement();
        record.addStringProperty("recordName").notNull();
        record.addFloatProperty("value").notNull();// 数额
        record.addBooleanProperty("isPayment").notNull();// 是否为支出
        record.addStringProperty("remarks").notNull();// 备注
        record.addLongProperty("createAt").notNull();// 创建时间
        record.addLongProperty("lastModify").notNull();// 最后修改时间
        return record;
    }
}
