package com.txzh.walk.Function;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.DeleteCaChe;
import com.txzh.walk.ToolClass.FileProviderUtils;
import com.txzh.walk.ToolClass.Tools;
import com.txzh.walk.customComponents.CircleImageView;
import com.txzh.walk.customComponents.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.txzh.walk.HomePage.WalkHome.context;

public class personalData extends AppCompatActivity implements View.OnClickListener {
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
    private boolean isFlag = false;
    private Handler handler;
    private String phone;   //修改后的电话号码
    private String nickName; //修改后的昵称
    private String sex;    //修改后的性别
    private int id;         //id
    private boolean headPicChange = false; //头像是否改变
    private DeleteCaChe deleteCaChe;
    private boolean isFirst = true;   //是否第一次使用

    private DeleteCaChe deleteCaChes;         // 清除CircleImage 缓存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        init();

        checkCarema();
    }

    //动态注册使用相机权限
    private void checkCarema() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void init() {
        handler = new Handler();
        deleteCaChes = new DeleteCaChe(context,handler);
        ib_return = findViewById(R.id.ib_Return);
        ib_return.setOnClickListener(this);
        iv_custom = findViewById(R.id.iv_custom);
        et_Nickname = findViewById(R.id.et_Nickname);
        et_account = findViewById(R.id.et_account);
        et_phone = findViewById(R.id.et_phone);
        tv_Sex = findViewById(R.id.tv_Sex);
        btn_Edit = findViewById(R.id.btn_Edit);
        btn_Edit.setOnClickListener(this);

        et_account.setText(Tools.getAccounts());
        et_Nickname.setText(Tools.getNickName());
        et_phone.setText(Tools.getPhongNumber());

        tv_Sex.setText(Tools.getSex());
        deleteCaChes.remove(Tools.getHeadPhoto());
        iv_custom.setImageUrl(Tools.getHeadPhoto(), R.drawable.headportrait, null);
        isFirst = false;
        nonEditable();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_Return:
                if (flag) {
                    headPicChange = false;
                    finish();  //返回上一个Activity
                } else {
                    getBitmapFromSharedPreferences();
                    //iv_custom.setImageUrl(Tools.getHeadPhoto(),R.drawable.headportrait,null);
                    isFlag = false;
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
    private void editable() {
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

    private void nonEditable() {
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
    private void ibReturn() {
        nonEditable();
        btn_Edit.setText("编辑资料");
        flag = true;
        finish();
    }


    //编辑按钮点击事件
    private void btnEdit() {
        if (flag) {
            editable();
            btn_Edit.setText("完成编辑");
            flag = false;
        } else {
            nonEditable();
            //保存到SharedPreferences
            if (bitmaps != null) {
                saveBitmapToSharedPreferences(bitmaps);
            } else {
                getBitmapFromSharedPreferences();
            }
            getChangePersonalMessage();
            if (headPicChange) {
                Tools.setHeadPicIfChange(true);
            }
            btn_Edit.setText("编辑资料");

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
               /* //激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //开启一个带有返回值的Activity，请求吗为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent,
                        PHOTO_REQUEST_GALLERY);*/
                zhaopian();


                myDialog.dismiss();
            }
        });
        myDialog.setNoButton(new MyDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {//拍照上传
               /* //激活相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //判断存储卡是否可用，可用进行存储
                if (hasSdcard()) {
                    temmpFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    Uri uri = Uri.fromFile(temmpFile);

                    //从文件中创建uri
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);


                }
                //开启一个带有返回值的Activity，请求吗PHOTO_REQUEST_CAREMA
                startActivityForResult(intent, PHOTO_REQUEST_CAREMA);*/
                paizhao(personalData.this, new File("/mnt/sdcard/tupian.jpg"));


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

    //拍照上传
    public void paizhao(Activity activity, File outputFile) {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri = FileProviderUtils.uriFromFile(activity, outputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    //相册上传
    public void zhaopian() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    //判断sdcard是否挂载
    private boolean hasSdcard() {
        //判断sd卡是否安装好
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    //裁剪图片
    private void crop(Activity activity, Uri uri, File outputFile) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        FileProviderUtils.setIntentDataAndType(activity, intent, "image/*", uri, true);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        //return-data为true时，直接返回bitmap，可能会很占内存，不建议，小米等个别机型会出异常！！！
        // 所以适配小米等个别机型，裁切后的图片，不能直接使用data返回，应使用uri指向
        // 裁切后保存的URI，不属于我们向外共享的，所以可以使用fill:
        // 类型的URI
        Uri outputUri = Uri.fromFile(outputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filtUri;
        File outputFile = new File("/mnt/sdcard/tupian_out.jpg");//裁切后输出的图片
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            /*//从相册返回的数据
            if (data != null) {
                //得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }*/
            if (data == null || data.getData() == null) {
                return;
            }
            filtUri = data.getData();
            crop(personalData.this, filtUri, outputFile);


        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            //从相机返回的数据
            /*if (hasSdcard()) {
                crop(Uri.fromFile(temmpFile));
            } else {
                Toast.makeText(this, "未找到存储卡，无法存储照片", Toast.LENGTH_SHORT).show();
            }*/
            File file = new File("/mnt/sdcard/tupian.jpg");
            filtUri = FileProviderUtils.uriFromFile(personalData.this, file);
            crop(personalData.this, filtUri, outputFile);


        } else if (requestCode == PHOTO_REQUEST_CUT) {
           /* //从裁剪照片返回数据
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //获得图片
            iv_custom.setImageBitmap(bitmap);
            isFlag = true;
            bitmaps = bitmap;

            try {
                //将临时文件删除
                temmpFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            //图片裁切完成，显示裁切后的图片
            try {
                Uri uri = Uri.fromFile(outputFile);
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                iv_custom.setImageBitmap(bitmap);
                isFlag = true;
                bitmaps = bitmap;
                headPicChange = true;
                iv_custom.setImageBitmap(bitmap);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        if (requestCode != RESULT_OK) {
            return;
        }
    }

    //保存图片到SharedPreferences
    private void saveBitmapToSharedPreferences(Bitmap bitmap) {
        //第一步：将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        //第二步：利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        //第三步:将String保持至SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("testSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image", imageString);
        editor.commit();
        //上传头像

    }

    //从SharedPreferences获取图片
    private void getBitmapFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("testSP", MODE_PRIVATE);
        //第一步:取出字符串形式的Bitmap
        String imageString = sharedPreferences.getString("image", "");
        //第二步：利用Base64将字符串转换为ByteArryInputStream
        byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
        if (byteArray.length == 0) {
            iv_custom.setImageResource(R.drawable.headportrait);
            isFlag = false;
            @SuppressLint("ResourceType")
            InputStream is = getResources().openRawResource(R.drawable.headportrait);
            Bitmap mBitmap = BitmapFactory.decodeStream(is);
            bitmaps = mBitmap;

        } else {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            //第三步：利用ByteArrayInputStream生成Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
            iv_custom.setImageBitmap(bitmap);
            bitmaps = bitmap;
            iv_custom.setImageBitmap(bitmap);
            isFlag = true;
        }


    }

    //选择性别
    private void setSex() {
        String sex = tv_Sex.getText().toString().trim();
        int checkedIten;
        if (sex.equals("男")) {
            checkedIten = 1;
        } else {
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

    //获取修改后的个人信息
    private void getChangePersonalMessage() {
        phone = et_phone.getText().toString().trim();
        nickName = et_Nickname.getText().toString().trim();
        sex = tv_Sex.getText().toString().trim();
        id = Tools.getUserID();

        if (!Tools.getPhongNumber().equals(phone)) {
            checkPhoneNumberIfTure(phone);
        } else if(bitmaps != null || !Tools.getNickName().equals(nickName) || !Tools.getSex().equals(sex)){
            uploadPersonalMessage(id, nickName, phone, sex, getFile(bitmaps));
        }
    }

    //检查修改后的电话号码是否被修改过
    public void checkPhoneNumberIfTure(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String URl = NetWorkIP.URL_checkPhone;
                OkHttpClient client = new OkHttpClient();
                RequestBody fromBody = new FormBody.Builder()
                        .add("phone", phone)
                        .build();
                Request request = new Request.Builder()
                        .url(URl)
                        .post(fromBody)
                        .build();

                Response response;
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Toast.makeText(personalData.this, "无响应", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject object = null;
                        String success = null;
                        String message = null;
                        try {
                            object = new JSONObject(response.body().string());
                            success = object.getString("success");
                            message = object.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String finalSuccess = success;
                        final String finalMessage = message;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if ("true".equals(finalSuccess)) {
                                    uploadPersonalMessage(id, nickName, phone, sex, getFile(bitmaps));
                                } else {
                                    Toast.makeText(personalData.this, "" + finalMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });
            }
        }).start();
    }

    //上传修改后的资料
    private void uploadPersonalMessage(final int id, final String nickName, final String phone, final String sex, final File headPic) {
        Log.i("aaaaaaaaaaa", "" + sex + headPic);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                //设置文件类型
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, headPic);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userID", "" + id)
                        .addFormDataPart("nickName", nickName)
                        .addFormDataPart("phone", phone)
                        .addFormDataPart("sex", sex)
                        .addFormDataPart("image", "head_image", fileBody)
                        .build();

                Request request = new Request.Builder()
                        .url(NetWorkIP.URL_modifyUserData)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        JSONObject object = null;
                        String success = null;
                        String message = null;
                        try {
                            object = new JSONObject(response.body().string());
                            success = object.getString("success");
                            message = object.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String finalSuccess = success;
                        final String finalMessage = message;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if ("true".equals(finalSuccess)) {
                                    Tools.setNickName(nickName);
                                    Tools.setSex(sex);
                                    Tools.setPhongNumber(phone);
                                    Toast.makeText(personalData.this, "" + finalMessage, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(personalData.this, "" + finalMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    //把bitmap变成图片文件
    public File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

   /* //从新获取头像
    public void onRestart() {

        super.onRestart();

        getBitmapFromSharedPreferences();
    }*/

}
