package com.ustadmobile.lib.db.entities;

import com.ustadmobile.lib.database.annotation.UmEntity;
import com.ustadmobile.lib.database.annotation.UmPrimaryKey;
import com.ustadmobile.lib.database.annotation.UmSyncLastChangedBy;
import com.ustadmobile.lib.database.annotation.UmSyncLocalChangeSeqNum;
import com.ustadmobile.lib.database.annotation.UmSyncMasterChangeSeqNum;

@UmEntity(tableId = 67)
public class SaleProduct {

    @UmPrimaryKey
    private long saleProductUid;

    private String saleProductName;

    private String saleProcutDesc;

    private long saleProductDateAdded;

    private long saleProductPersonAdded;

    private long saleProductPictureUid;

    private boolean saleProductActive;

    @UmSyncMasterChangeSeqNum
    private long saleProductMCSN;

    @UmSyncLocalChangeSeqNum
    private long saleProductLCSN;

    @UmSyncLastChangedBy
    private int saleProductLCB;


    public long getSaleProductUid() {
        return saleProductUid;
    }

    public void setSaleProductUid(long saleProductUid) {
        this.saleProductUid = saleProductUid;
    }

    public String getSaleProductName() {
        return saleProductName;
    }

    public void setSaleProductName(String saleProductName) {
        this.saleProductName = saleProductName;
    }

    public String getSaleProcutDesc() {
        return saleProcutDesc;
    }

    public void setSaleProcutDesc(String saleProcutDesc) {
        this.saleProcutDesc = saleProcutDesc;
    }

    public long getSaleProductDateAdded() {
        return saleProductDateAdded;
    }

    public void setSaleProductDateAdded(long saleProductDateAdded) {
        this.saleProductDateAdded = saleProductDateAdded;
    }

    public long getSaleProductPersonAdded() {
        return saleProductPersonAdded;
    }

    public void setSaleProductPersonAdded(long saleProductPersonAdded) {
        this.saleProductPersonAdded = saleProductPersonAdded;
    }

    public long getSaleProductPictureUid() {
        return saleProductPictureUid;
    }

    public void setSaleProductPictureUid(long saleProductPictureUid) {
        this.saleProductPictureUid = saleProductPictureUid;
    }

    public boolean isSaleProductActive() {
        return saleProductActive;
    }

    public void setSaleProductActive(boolean saleProductActive) {
        this.saleProductActive = saleProductActive;
    }

    public long getSaleProductMCSN() {
        return saleProductMCSN;
    }

    public void setSaleProductMCSN(long saleProductMCSN) {
        this.saleProductMCSN = saleProductMCSN;
    }

    public long getSaleProductLCSN() {
        return saleProductLCSN;
    }

    public void setSaleProductLCSN(long saleProductLCSN) {
        this.saleProductLCSN = saleProductLCSN;
    }

    public int getSaleProductLCB() {
        return saleProductLCB;
    }

    public void setSaleProductLCB(int saleProductLCB) {
        this.saleProductLCB = saleProductLCB;
    }
}
