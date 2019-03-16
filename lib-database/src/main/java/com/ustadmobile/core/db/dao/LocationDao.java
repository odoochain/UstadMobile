package com.ustadmobile.core.db.dao;

import com.ustadmobile.core.db.UmLiveData;
import com.ustadmobile.core.db.UmProvider;
import com.ustadmobile.core.impl.UmCallback;
import com.ustadmobile.lib.database.annotation.UmDao;
import com.ustadmobile.lib.database.annotation.UmInsert;
import com.ustadmobile.lib.database.annotation.UmQuery;
import com.ustadmobile.lib.database.annotation.UmRepository;
import com.ustadmobile.lib.database.annotation.UmUpdate;
import com.ustadmobile.lib.db.entities.Location;
import com.ustadmobile.lib.db.entities.LocationWithSubLocationCount;
import com.ustadmobile.lib.db.sync.dao.SyncableDao;

import java.util.List;

@UmDao(updatePermissionCondition = RoleDao.SELECT_ACCOUNT_IS_ADMIN,
insertPermissionCondition = RoleDao.SELECT_ACCOUNT_IS_ADMIN)
@UmRepository
public abstract class LocationDao implements SyncableDao<Location, LocationDao> {


    @UmInsert
    public abstract long insert(Location entity);

    @UmInsert
    public abstract void insertAsync(Location entity, UmCallback<Long> resultObject);

    @UmUpdate
    public abstract void update(Location entity);

    @UmUpdate
    public abstract void updateAsync(Location entity, UmCallback<Integer> resultObject);

    @UmQuery("SELECT * FROM Location WHERE locationUid = :uid")
    public abstract Location findByUid(long uid);

    @UmQuery("SELECT * FROM Location WHERE locationUid = :uid")
    public abstract void findByUidAsync(long uid, UmCallback<Location> resultObject);

    @UmQuery("SELECT * FROM Location WHERE locationUid = :uid")
    public abstract UmLiveData<Location> findByUidLive(long uid);

    @UmQuery("SELECT * FROM Location WHERE parentLocationUid = 0 AND locationActive = 1")
    public abstract void findTopLocationsAsync(UmCallback<List<Location>> resultList);

    @UmQuery("SELECT * FROM Location WHERE parentLocationUid = :uid AND locationActive = 1")
    public abstract void findAllChildLocationsForUidAsync(long uid, UmCallback<List<Location>> resultList);

    @UmQuery("SELECT * FROM Location WHERE title = :name AND locationActive = 1")
    public abstract void findByTitleAsync(String name, UmCallback<List<Location>> resultList);

    @UmQuery("SELECT *, 0 AS subLocations  FROM Location WHERE parentLocationUid = 0")
    public abstract UmProvider<LocationWithSubLocationCount> findAllTopLocationsWithCount();

    @UmQuery("SELECT *, 0 AS subLocations FROM Location")
    public abstract UmProvider<LocationWithSubLocationCount> findAllLocationsWithCount();

    @UmQuery("UPDATE Location SET locationActive = 0 WHERE locationUid = :uid")
    public abstract void inactivateLocationAsync(long uid, UmCallback<Integer> resultObject);


}
