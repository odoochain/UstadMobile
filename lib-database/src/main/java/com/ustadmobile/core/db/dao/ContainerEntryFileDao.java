package com.ustadmobile.core.db.dao;

import com.ustadmobile.lib.database.annotation.UmDao;
import com.ustadmobile.lib.database.annotation.UmQuery;
import com.ustadmobile.lib.db.entities.ContainerEntryFile;
import com.ustadmobile.lib.db.sync.dao.BaseDao;

import java.util.List;

@UmDao
public abstract class ContainerEntryFileDao implements BaseDao<ContainerEntryFile> {

    @UmQuery("SELECT ContainerEntryFile.* FROM ContainerEntryFile WHERE cefMd5 IN (:md5Sums)")
    public abstract List<ContainerEntryFile> findEntriesByMd5Sums(List<String> md5Sums);

    @UmQuery("UPDATE ContainerEntryFile SET cefPath = :path WHERE cefUid = :cefUid")
    public abstract void updateFilePath(long cefUid, String path);

}
