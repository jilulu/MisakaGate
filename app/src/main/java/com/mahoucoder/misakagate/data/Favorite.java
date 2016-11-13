package com.mahoucoder.misakagate.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by jamesji on 13/11/2016.
 */

@SuppressWarnings("WeakerAccess")
@Table(database = Animations.class)
public class Favorite extends BaseModel {
    @PrimaryKey @Column @Index
    public String tid;

    public void setTid(String tid) {
        this.tid = tid;
    }
}
