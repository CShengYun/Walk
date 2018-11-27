package com.txzh.walk.Group;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.EmbossMaskFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.txzh.walk.HomePage.WalkHome;
import com.txzh.walk.NetWork.NetWorkIP;
import com.txzh.walk.R;
import com.txzh.walk.ToolClass.FileProviderUtils;
import com.txzh.walk.ToolClass.Tools;
import com.txzh.walk.customComponents.CircleImageView;
import com.txzh.walk.customComponents.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.txzh.walk.Fragment.GroupFragment.manageGroupInfoBeanList;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_Return;                    //返回
    private CircleImageView iv_groupCustom;           //群组头像
    private EditText et_groupNickName;                //群组昵称
    private EditText et_groupSynopsis;                //群描述
    private Button btn_groupEstablish;                //创建群按钮
    private MyDialog myDialog;                        //自定义对话框
    private static final int PHOTO_REQUEST_CAREMA = 1;  //拍照
    private static final int PHOTO_REQUEST_GALLERY = 2; //从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3; //结果
    private boolean flag = true;
    Bitmap bitmaps = null;       //保存头像
    private boolean isFlag = false;

    private String groupNickName;     //保存群昵称
    private String groupSynopsis;     //保存群简介
    private Handler handler;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();                                                       //去掉标题栏
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        init();
        checkCarema();
    }

    protected void init(){
        ib_Return = findViewById(R.id.ib_Return);
        ib_Return.setOnClickListener(this);
        et_groupNickName = findViewById(R.id.et_groupNickName);
        et_groupSynopsis = findViewById(R.id.et_groupSynopsis);
        btn_groupEstablish = findViewById(R.id.btn_groupEstablish);
        btn_groupEstablish.setOnClickListener(this);
        iv_groupCustom = findViewById(R.id.iv_groupCustom);
        iv_groupCustom.setOnClickListener(new CircleImageView.OnClickListener() {
            @Override
            public void onClick() {
                showDialog();
            }
        });
        myDialog = new MyDialog(this);

        handler = new Handler();
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_Return:
                CreateGroup.this.finish();
                break;
            case R.id.btn_groupEstablish:
                groupNickName = et_groupNickName.getText().toString().trim();
                groupSynopsis = et_groupSynopsis.getText().toString().trim();
                Log.i("aaaa",groupNickName);
                Log.i("aaaa",groupSynopsis);
                if(groupNickName.isEmpty()){
                    Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
                }else {

                    if(bitmaps == null){
                        @SuppressLint("ResourceType")
                        InputStream is = getResources().openRawResource(R.drawable.headportrait);
                        Bitmap mBitmap = BitmapFactory.decodeStream(is);
                        uploadGroupMessage(Tools.getUserID(),groupNickName,groupSynopsis,getFile(mBitmap));
                    }else {
                        uploadGroupMessage(Tools.getUserID(),groupNickName,groupSynopsis,getFile(bitmaps));
                    }
                }
                break;
        }

    }

    //动态注册使用相机权限
    private void checkCarema() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    //自定义对话框
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
                paizhao(CreateGroup.this, new File("/mnt/sdcard/tupian.jpg"));


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

    //裁剪图片
    private void crop(Activity activity, Uri uri, File outputFile) {
        /*//裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        FileProviderUtils.setIntentDataAndType(this, intent, "image/*", uri, true);

        intent.putExtra("crop", "true");
        //裁剪框的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 90);
        intent.putExtra("outputY", 90);

        uritempFile = uri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);

        intent.putExtra("return-data", false);
        //图片格式
        //intent.putExtra("outputFormat","PNG");
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        //取消人脸识别
        intent.putExtra("onFaceDetection", true);

       //处理小米手机不兼容问题
        uritempFile = uri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uritempFile);

        //开启一个带有返回值的Activity,请求吗PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);*/

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
            crop(CreateGroup.this, filtUri, outputFile);


        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            //从相机返回的数据
            /*if (hasSdcard()) {
                crop(Uri.fromFile(temmpFile));
            } else {
                Toast.makeText(this, "未找到存储卡，无法存储照片", Toast.LENGTH_SHORT).show();
            }*/
            File file = new File("/mnt/sdcard/tupian.jpg");
            filtUri = FileProviderUtils.uriFromFile(CreateGroup.this, file);
            crop(CreateGroup.this, filtUri, outputFile);


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
                iv_groupCustom.setImageBitmap(bitmap);
                isFlag = true;
                bitmaps = bitmap;
                iv_groupCustom.setImageBitmap(bitmap);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        if (requestCode != RESULT_OK) {
            return;
        }
    }


    //把bitmap变成图片文件
    public File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/grouptemp.png");
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

    //
    //上传修改后的资料
    private void uploadGroupMessage(final int id, final String groupNickName, final String groupSynopsis, final File headPic) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String [] str={};
                EMGroupOptions option = new EMGroupOptions();
                option.maxUsers = 200;
                option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                EMClient.getInstance().groupManager().asyncCreateGroup(groupNickName, groupSynopsis, str, Tools.getAccounts(), option, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup emGroup) {

                        Log.i("hhhhhh", "我是群ID:"+emGroup.getGroupId());

                        OkHttpClient client = new OkHttpClient();
                        //设置文件类型
                        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, headPic);
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("userID", "" + id)
                                .addFormDataPart("groupHXID",emGroup.getGroupId())
                                .addFormDataPart("groupName", groupNickName)
                                .addFormDataPart("groupDescribe", groupSynopsis)
                                .addFormDataPart("image", "grouphead_image", fileBody)
                                .build();

                        Request request = new Request.Builder()
                                .url(NetWorkIP.URL_createGroup)
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
                                            Toast.makeText(CreateGroup.this, "" + finalMessage, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(CreateGroup.this, "" + finalMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });



                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

            }
        }).start();
    }


}
