package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.fangyuan.weipanbao.R;
import com.fangyuan.weipanbao.util.BitmapUtil;

/**
 * Created by Administrator on 2016/12/12 0012.
 *
 * note:the app support system version 4.0+
 */

public class SettingsActivity extends Activity implements View.OnClickListener {

    public static final String KEY_NICK_NAME ="nickName" ;
    private ImageView ivHeadPhoto;
    /**
     * 各种请求码
     */
    private static  final int REQ_CODE_TAKE_PHOTO=1000;
    private static  final int REQ_CODE_CROP_PHOTO=2000;
    private static  final int REQ_CODE_ALBUM_PICK=3000;
    private static  final int REQ_CODE_MODIFY_NICK_NAME=4000;
    private AlertDialog alertDialog;
    private TextView tvNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.rl_head_photo).setOnClickListener(this);
        findViewById(R.id.rl_modify_nickName).setOnClickListener(this);
        findViewById(R.id.tv_login_out).setOnClickListener(this);

        tvNickName = (TextView) findViewById(R.id.tv_nick_name);
        ivHeadPhoto = (ImageView) findViewById(R.id.iv_head_photo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.rl_head_photo:
                showPhotoSelectDialog();
                 break;
            case R.id.rl_modify_nickName:
                Intent intent=new Intent(this,ModifyNickNameActivity.class);
                String nickNameStr = tvNickName.getText().toString();
                intent.putExtra(KEY_NICK_NAME,nickNameStr);
                startActivityForResult(intent,REQ_CODE_MODIFY_NICK_NAME);
                break;
            case R.id.tv_login_out:
                break;
            case R.id.tv_take_photo:
                takePhoto();
                dismissDialog();
                break;
            case R.id.tv_select_from_album:
                selectFromAlbum();
                dismissDialog();
                break;
            case R.id.tv_cancel:
                if(null!=alertDialog&&alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
                break;
        }
    }

    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        //intent.putExtra("output", Uri.fromFile(sdcardTempFile));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);// 输出图片大小
        intent.putExtra("outputY", 128);
        startActivityForResult(intent, REQ_CODE_ALBUM_PICK);

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQ_CODE_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQ_CODE_TAKE_PHOTO://orignal photo return
                    final Bitmap photo = data.getParcelableExtra("data");
                    if(photo!=null){
                        requestCropPhoto(photo);
                    }
                    break;
                case REQ_CODE_CROP_PHOTO://crop photo return,then make photo to round corner photo
                    Bitmap photo1 = data.getParcelableExtra("data");
                    if(photo1!=null){
                        photo1=BitmapUtil.makeRoundCorner(photo1);
                        ivHeadPhoto.setImageBitmap(photo1);
                    }
                    break;
                case REQ_CODE_ALBUM_PICK:
                        if(null!=data){
                       Bitmap pic=     data.getParcelableExtra("data");
                        if(null!=pic){
                            pic= BitmapUtil.makeRoundCorner(pic);
                            ivHeadPhoto.setImageBitmap(pic);

                        }
                            /* Uri uri=   data.getData();

                            try {
                               Bitmap bm = Media.getBitmap(getContentResolver(), uri);
                                ivHeadPhoto.setImageBitmap(bm);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                    break;
                case REQ_CODE_MODIFY_NICK_NAME:
                    //modify nick name success,update nick name
                    String newNickName = data.getStringExtra(KEY_NICK_NAME);
                    tvNickName.setText(newNickName);
                    break;


            }
        }
    }

    private void requestCropPhoto(Bitmap photo) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        //intent.putExtra("data", data);
        intent.putExtra("data", photo);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQ_CODE_CROP_PHOTO);
    }
    private void requestCropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        //intent.setType("image/*");
        //intent.putExtra("data", data);
        //intent.putExtra("data", photo);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQ_CODE_CROP_PHOTO);
    }
    private void dismissDialog(){
        if(null!=alertDialog&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }
    private void showPhotoSelectDialog() {

        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.popwindow_head_photo_select, null);

        TextView tvTakePhoto = (TextView) rootView.findViewById(R.id.tv_take_photo);
        TextView tvSelectFromAlbum = (TextView) rootView.findViewById(R.id.tv_select_from_album);
        TextView tvCancel = (TextView) rootView.findViewById(R.id.tv_cancel);

        tvCancel.getPaint().setFakeBoldText(true);

        tvTakePhoto.setOnClickListener(this);
        tvSelectFromAlbum.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(rootView);

        alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim);

        alertDialog.show();
    }
}
