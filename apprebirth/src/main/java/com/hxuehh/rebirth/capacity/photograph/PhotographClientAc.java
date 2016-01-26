package com.hxuehh.rebirth.capacity.photograph;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.hxuehh.appCore.app.SuApplication;
import com.hxuehh.appCore.faceFramework.faceEcxeption.FaceException;
import com.hxuehh.rebirth.R;
import com.hxuehh.rebirth.client.service.ClientService_TCPLongLink_;
import com.hxuehh.rebirth.device.domain.DeviceCapacityInParameter;
import com.hxuehh.rebirth.device.faceAc.BaseDeviceCapacityAc_2;
import com.hxuehh.rebirth.suMessage.domain.MidMessage;
import com.hxuehh.rebirth.suMessage.domain.MidMessageCMDKeys;
import com.hxuehh.rebirth.suMessage.domain.imp.MidMessageBack_2;
import com.hxuehh.rebirth.suMessage.domain.imp.MidMessageOrderForDeviceCap_4;
import com.hxuehh.rebirth.suMessage.domain.imp.MidMessageOrder_2;
import com.hxuehh.reuse_Process_Imp.FaceUIImp.viewsImp.ProView;
import com.hxuehh.reuse_Process_Imp.staicUtil.commonUtil.DialogUtil;
import com.hxuehh.reuse_Process_Imp.staicUtil.store.file.FileUtil;
import com.hxuehh.reuse_Process_Imp.staticKey.ViewKeys;

import java.io.File;

/**
 * Created by suwg on 2015/9/11.
 */


public class PhotographClientAc extends BaseDeviceCapacityAc_2 {

    @Override
    public int getViewKey() {
        return ViewKeys.PhotographClientAc;
    }

    public void ShowBack(MidMessageBack_2 mMidMessageBack) {
        Object err = mMidMessageBack.getByKey(MidMessage.Key_ErrInfo);
        if (err != null) {
            mProView.setErrorInfo(err.toString() + "");
            return;
        }
        byte bb[] = mMidMessageBack.getBytes();
        File file = FileUtil.getFileExist(bb, SuApplication.getInstance().getCacheDir().getPath()+ "/get_photo", "getimage.jpg");
        if(file!=null){
            photo_image_view.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            photo_image_view.setImageURI(Uri.fromFile(file));
        }

        if (mMidMessageBack.isOK()) {
            mProView.setOk("完成" , true);
        } else {
            mProView.setErrorInfo("失败");
        }

    }


    View mainView;
    ImageView photo_image_view;
    CheckBox light_is_open_check_box;

    public void setMainView() {
        mainView = View.inflate(this, R.layout.photograph_lin, (ViewGroup) findViewById(R.id.main_lin));
        mProView = new ProView(this);
        ViewKeys.addIntoLin(R.id.ProView_lin, mProView.getMainView(), this);
        mProView.gone_all();
        photo_image_view= (ImageView) findViewById(R.id.photo_image_view);
        light_is_open_check_box= (CheckBox) findViewById(R.id.light_is_open_check_box);

    }

    public void onClick_doChange(View v) {
        doChange(0);
    }


    public void doChange(int changeKey) {

        if (mProView.isLoading()) {
            DialogUtil.showShortToast(getFaceContext(), "请稍后....");
            return;
        }

        mProView.setLoadingName("改变中...");
        ClientService_TCPLongLink_ mClientService_TCPLongLink_ = getLinkShow();
        if (mClientService_TCPLongLink_ == null) return;
        MidMessageOrder_2 midMessageOrder = getCmdForChange(changeKey);
        try {
            mClientService_TCPLongLink_.sendSyncAddCache(midMessageOrder);
        } catch (FaceException e) {
            e.printStackTrace();
            mProView.setErrorInfo(e.getMessage() + "");
        } catch (Exception e) {
            e.printStackTrace();
            mProView.setErrorInfo(e.getMessage() + "");
        }
    }

    public MidMessageOrder_2 getCmdForChange(int changeKey) {
       boolean is= light_is_open_check_box.isChecked();
        DeviceCapacityInParameter mDeviceCapacityParameter = new Photograph.PhotographParameter(Photograph.PhotographParameter.GetPhotoGraoh,is);

        MidMessageOrderForDeviceCap_4 mid = new MidMessageOrderForDeviceCap_4(MidMessageCMDKeys.MidMessageCMD_Device_Cmd_Opstion,
                mDeviceInfo.getSU_UUID(),
                mDeviceCapacity.getType(), mDeviceCapacityParameter);
        mid.setmFaceCommCallBack(mFaceCommCallBackForLoad);
        return mid;
    }


}
