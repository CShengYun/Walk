package com.txzh.walk.Function;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.R;
import com.txzh.walk.customComponents.CircleImageView;
import com.txzh.walk.customComponents.MyDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class personalData extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ib_return;             //返回按钮
    private CircleImageView iv_custom;         //头像
    private EditText et_Nickname;              //昵称
    private EditText et_account;               //帐号
    private EditText et_phone;                 //电话号码
    private TextView tv_Sex;                   //性别
    private Button btn_Edit;                   //编辑按钮
    private boolean flag = true;
    private MyDialog myDialog;                 //自定义对话框
    private Uri uritempFile;                   //头像路径

    private static final int PHOTO_REQUEST_CAREMA = 1;  //拍照
    private static final int PHOTO_REQUEST_GALLERY = 2; //从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3; //结果
    //头像名称
    private static final String PHOTO_FILE_NAME = "iv_custom.jpg";
    private File temmpFile;                     //头像文件

    Bitmap bitmaps = null;       //保存头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        init();

        checkCarema();

        //从SharedPreferences获取图片
        getBitmapFromSharedPreferences();
    }

    //动态注册使用相机权限
    private void checkCarema(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void init(){
        ib_return = findViewById(R.id.ib_Return);
        ib_return.setOnClickListener(this);
        iv_custom = findViewById(R.id.iv_custom);
        et_Nickname = findViewById(R.id.et_Nickname);
        et_account = findViewById(R.id.et_account);
        et_phone = findViewById(R.id.et_phone);
        tv_Sex = findViewById(R.id.tv_Sex);
        btn_Edit = findViewById(R.id.btn_Edit);
        btn_Edit.setOnClickListener(this);
        nonEditable();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_Return:
                if(flag){
                    finish();  //返回上一个Activity
                }else {
                    getBitmapFromSharedPreferences();
                    ibReturn();
                }
                break;
            case R.id.btn_Edit:
                btnEdit();
                break;
            case R.id.tv_Sex:
                setSex();
                break;
        }
    }

    //点击编辑资料后操作
    private void editable(){
        iv_custom.setOnClickListener(this);
        et_Nickname.setEnabled(true);
        et_phone.setEnabled(true);
        tv_Sex.setEnabled(true);
        tv_Sex.setOnClickListener(this);
        //头像点击监听事件
        iv_custom.setOnClickListener(new CircleImageView.OnClickListener() {
            @Override
            public void onClick() {
                showDialog();
            }
        });
    }

    //资料不可编辑

    private void nonEditable(){
        iv_custom.setEnabled(false);
        et_Nickname.setEnabled(false);
        et_account.setEnabled(false);
        et_phone.setEnabled(false);
        tv_Sex.setEnabled(false);
        iv_custom.setOnClickListener(new CircleImageView.OnClickListener() {
            @Override
            public void onClick() {

            }
        });
    }

    //返回按钮点击事件
    private void ibReturn(){
        nonEditable();
        btn_Edit.setText("编辑资料");
        flag = true;
    }

    //编辑按钮点击事件
    private void btnEdit(){
        if(flag){
            editable();
            btn_Edit.setText("完成编辑");
            flag = false;
        }else {
            nonEditable();
            btn_Edit.setText("编辑资料");
            //保存到SharedPreferences
            if(bitmaps!= null){
                saveBitmapToSharedPreferences(bitmaps);
            }
            flag = true;
        }
    }

    //上传照片方式
    public void showDialog() {
        myDialog = new MyDialog(this);
        myDialog.setTitle("上传照片方式");
        myDialog.setYesButton(new MyDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() { //本地上传
                //激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //开启一个带有返回值的Activity，请求吗为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent,
                        PHOTO_REQUEST_GALLERY);
                myDialog.dismiss();
            }
        });
        myDialog.setNoButton(new MyDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {//拍照上传
                //激活相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //判断存储卡是否可用，可用进行存储
                if(hasSdcard()){
                    temmpFile = new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME);
                    //从文件中创建uri
                    Uri uri = Uri.fromFile(temmpFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                }
                //开启一个带有返回值的Activity，请求吗PHOTO_REQUEST_CAREMA
                startActivityForResult(intent,PHOTO_REQUEST_CAREMA);
                myDialog.dismiss();
            }
        });
        myDialog.setOnClickListener(new MyDialog.OnClickListener() {
            @Override
            public void onClick() {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    //判断sdcard是否挂载
    private boolean hasSdcard(){
        //判断sd卡是否安装好
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else {
            return false;
        }
    }

    //裁剪图片
    private void crop(Uri uri){
        //裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        //裁剪框的比例
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX",90);
        intent.putExtra("outputY",90);
        //图片格式
        intent.putExtra("outputFormat","JPEG");
        //取消人脸识别
        intent.putExtra("onFaceDetection",true);

        //处理小米手机不兼容问题
        uritempFile = uri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uritempFile);

        //开启一个带有返回值的Activity,请求吗PHOTO_REQUEST_CUT
        startActivityForResult(intent,PHOTO_REQUEST_CUT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PHOTO_REQUEST_GALLERY){
            //从相册返回的数据
            if(data != null){
                //得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        }else if(requestCode == PHOTO_REQUEST_CAREMA){
            //从相机返回的数据
            if(hasSdcard()){
                crop(Uri.fromFile(temmpFile));
            }else {
                Toast.makeText(this, "未找到存储卡，无法存储照片", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PHOTO_REQUEST_CUT){
            //从裁剪照片返回数据

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //获得图片
            iv_custom.setImageBitmap(bitmap);
            bitmaps = bitmap;

            try {
                //将临时文件删除
                temmpFile.delete();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    //保存图片到SharedPreferences
    private void saveBitmapToSharedPreferences(Bitmap bitmap){
        //第一步：将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        //第二步：利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageString = new String(Base64.encodeToString(byteArray,Base64.DEFAULT));
        //第三步:将String保持至SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("testSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image", imageString);
        editor.commit();
        //上传头像

    }

    //从SharedPreferences获取图片
    private void getBitmapFromSharedPreferences(){
        SharedPreferences sharedPreferences=getSharedPreferences("testSP", MODE_PRIVATE);
        //第一步:取出字符串形式的Bitmap
        String imageString = sharedPreferences.getString("image","");
        //第二步：利用Base64将字符串转换为ByteArryInputStream
        byte[] byteArray = Base64.decode(imageString,Base64.DEFAULT);
        if(byteArray.length == 0){
            iv_custom.setImageResource(R.drawable.headportrait);
        }else {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            //第三步：利用ByteArrayInputStream生成Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
            iv_custom.setImageBitmap(bitmap);
        }


    }

    //选择性别
    private void setSex(){
        String sex = tv_Sex.getText().toString().trim();
        int checkedIten;
        if(sex.equals("男")){
            checkedIten = 1;
        }else {
            checkedIten = 0;
        }
        final String[] sexArry = new String[]{"女", "男"};// 性别选择
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("请选择性别");
        alertDialog.setSingleChoiceItems(sexArry, checkedIten,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_Sex.setText(sexArry[i]);
                        dialogInterface.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
                    }
                })
                .show();
    }

}
