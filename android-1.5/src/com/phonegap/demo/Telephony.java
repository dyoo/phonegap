package com.phonegap.demo;


import android.content.Context;
// import android.telephony.TelephonyManager;
// import android.telephony.NeighboringCellInfo;

import java.util.List;
import java.util.ArrayList;

public class Telephony {
    private Context ctx;

    public Telephony(Context ctx) {
	this.ctx = ctx;
    }


    public List<CellInfo> getSignalStrengths() {
// 	TelephonyManager mgr = (TelephonyManager)
// 	    ctx.getSystemService(Context.TELEPHONY_SERVICE);

	//	List infos = mgr.getNeighboringCellInfo();

	List result = new ArrayList();
	return result;
    }


}