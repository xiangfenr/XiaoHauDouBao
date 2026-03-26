package com.yundou.loans.base

import com.yundou.loans.entity.MatchResData
import java.util.concurrent.CopyOnWriteArrayList

object OrgMatchResStore {
    val orgMatchRes: CopyOnWriteArrayList<MatchResData> = CopyOnWriteArrayList()
}

