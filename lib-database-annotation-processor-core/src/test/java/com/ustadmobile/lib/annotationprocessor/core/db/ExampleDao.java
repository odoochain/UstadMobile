package com.ustadmobile.lib.annotationprocessor.core.db;

import com.ustadmobile.lib.database.annotation.UmDao;
import com.ustadmobile.lib.database.annotation.UmInsert;
import com.ustadmobile.lib.database.annotation.UmQuery;

import java.util.List;

@UmDao
public abstract class ExampleDao {

    @UmInsert
    public abstract void insertE(ExampleEntity entity);

    @UmQuery("SELECT * FROM ExampleEntity")
    public abstract List<ExampleEntity> getAllEntities();

    @UmQuery("SELECT * FROM ExampleEntity")
    public abstract ExampleEntity[] getAllEntitiesAsArray();

    @UmQuery("SELECT * FROM ExampleEntity WHERE uid = :uid ")
    public abstract ExampleEntity findByUid(int uid);

    @UmQuery("SELECT COUNT(*) FROM ExampleEntity")
    public abstract int countNumElements();

    @UmQuery("SELECT * FROM ExampleEntity " +
            "LEFT JOIN ExampleLocation ON ExampleEntity.locationPk = ExampleLocation.locationUid")
    public abstract List<ExampleEntityWithLocation> getAllEntitiesWithLocation();

}
