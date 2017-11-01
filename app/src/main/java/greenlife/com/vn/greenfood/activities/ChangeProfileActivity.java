package greenlife.com.vn.greenfood.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import greenlife.com.vn.greenfood.BuildConfig;
import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.models.User;
import greenlife.com.vn.greenfood.utils.LibrarySupportManager;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChangeProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    public static final int RequestPermissionCode = 1;
    private String mImagePathToBeAttached;
    private Bitmap mImageToBeAttached;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;
    private User user;
    ImageView isAvatar;
    TextView changeAvatar;
    EditText username;
    EditText description;
    EditText link;
    EditText mail;
    EditText phone;
    Toolbar statusToolBar;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        // get ID user
        String uId = mAuth.getCurrentUser().getUid();
        // define control
        setContentView(R.layout.activity_change_profile);
        isAvatar = (ImageView)findViewById(R.id.iv_profile_image);
        changeAvatar = (TextView)findViewById(R.id.tv_changeAvatar);
        username = (EditText)findViewById(R.id.et_username);
        description = (EditText)findViewById(R.id.et_description);
        link = (EditText)findViewById(R.id.et_link);
        mail = (EditText)findViewById(R.id.et_mail);
        phone = (EditText)findViewById(R.id.et_phone);
        // set toolbar
        statusToolBar = (Toolbar)findViewById(R.id.tb_status);
        statusToolBar.setTitle("Chỉnh sửa trang cá nhân");
        statusToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(statusToolBar);
        statusToolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        statusToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //
        getUser(this,uId);
    }
    public void setUser(User user) {
        this.user = user;
    }
    private void getUser(final Context context, String userID) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        setUser(user);
                        Picasso.with(context)
                                .load(user.getAvatar())
                                .transform(new CropCircleTransformation())
                                .into(isAvatar);
                        // set user name
                        username.setText(user.getName());
                        // get description
                        if(user.getDescription()!= null){
                            description.setText(user.getDescription());
                        }
                        else {
                            description.setText("");
                        }
                        // get link
                        if(user.getLink()!=null){
                            link.setText(user.getLink());
                        }
                        else {
                            link.setText("");
                        }
                        // get mail
                        if(mAuth.getCurrentUser().getEmail()!=null){
                            mail.setText(mAuth.getCurrentUser().getEmail());
                        }
                        // get phone
                        if(user.getPhone()!=null){
                            phone.setText(user.getPhone());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //avatar
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
//            if (mCurrentTaskToAttachImage != null)
//                mCurrentTaskToAttachImage = null;
            return;
        }

        // final int size = THUMBNAIL_SIZE;
        Bitmap thumbnail = null;
        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(mImagePathToBeAttached);
            if (file.exists()) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                options.inJustDecodeBounds = false;
                mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                try{
                    ExifInterface exif = new ExifInterface(mImagePathToBeAttached);
                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;
                    int rotationAngle = 0;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle, (float) mImageToBeAttached.getWidth() / 2, (float) mImageToBeAttached.getHeight() / 2);
                    mImageToBeAttached = Bitmap.createBitmap(mImageToBeAttached, 0, 0, options.outWidth, options.outHeight, matrix, true);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

//                if (mCurrentTaskToAttachImage == null) {
//                    thumbnail = ThumbnailUtils.extractThumbnail(mImageToBeAttached, size, size);
//                }

                // Delete the temporary image file
                file.delete();
            }
            mImagePathToBeAttached = null;
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            try {

                Uri uri = data.getData();
                ContentResolver resolver = this.getContentResolver();
                int rotationAngle = getOrientation(this,uri);
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(resolver, uri);
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) mImageToBeAttached.getWidth() / 2, (float) mImageToBeAttached.getHeight() / 2);
                mImageToBeAttached = Bitmap.createBitmap(mImageToBeAttached, 0, 0, mImageToBeAttached.getWidth(), mImageToBeAttached.getHeight(), matrix, true);
//                if (mCurrentTaskToAttachImage == null) {
//                    AssetFileDescriptor asset = resolver.openAssetFileDescriptor(uri, "r");
//                    thumbnail = ImageUtil.thumbnailFromDescriptor(asset.getFileDescriptor(), size, size);
//                }
            } catch (IOException e) {
            }
        }

//        if (mImageToBeAttached != null) {
//            if (mCurrentTaskToAttachImage != null) {
//                attachImage(mCurrentTaskToAttachImage, mImageToBeAttached);
//                mImageToBeAttached = null;
//            }
//        }

//        if (thumbnail != null) {
//            ImageView imageView = (ImageView) findViewById(R.id.image);
//            imageView.setImageBitmap(thumbnail);
//        }

        // Ensure resetting the task to attach an image
        updateUI();
    }

    private void updateUI() {
    }

    public int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", photoFile));
                if (checkPermission()) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } else {
                    requestPermission();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "TODO_LITE-" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }

    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSE_PHOTO);
    }

    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;
        }
//        ViewGroup view = (ViewGroup) findViewById(R.id.create_task);
//        ImageView imageView = (ImageView) view.findViewById(R.id.image);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
    }

    private void displayAttachImageDialog() {
        CharSequence[] items;
        if (mImageToBeAttached != null)
            items = new CharSequence[]{"Take photo", "Choose photo", "Delete photo"};
        else
            items = new CharSequence[]{"Take photo", "Choose photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anh dai dien");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    dispatchTakePhotoIntent();
                } else if (item == 1) {
                    dispatchChoosePhotoIntent();
                } else {
                    deleteCurrentPhoto();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getBaseContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_profile_image:
                displayAttachImageDialog();
                break;
            case R.id.tv_changeAvatar:
                displayAttachImageDialog();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_done:
                String uID = mAuth.getCurrentUser().getUid();
                updateInfor();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean updateInfor(){
        try {
            String userID = LibrarySupportManager.getInstance().randomUserTokenID();
            mDatabaseReference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users").child(mAuth.getCurrentUser().getUid());
            String key = mDatabaseReference.push().getKey();
            mStorageReference = FirebaseStorage.getInstance()
                    .getReference()
                    .child(mAuth.getCurrentUser().getUid());
        }catch (Exception ex){

        }
        mDatabaseReference.child("name").setValue(username.getText());
        mDatabaseReference.child("phone").setValue(phone.getText());
        mDatabaseReference.child("description").setValue(description.getText());
        mDatabaseReference.child("link").setValue(link.getText());
        mDatabaseReference.child("mail").setValue(mail.getText());

        // upload image
        if(mImageToBeAttached !=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImageToBeAttached.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = mStorageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getBaseContext(), "Vui lòng kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mDatabaseReference.child("avatar").setValue(downloadUrl.toString());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }

        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.toolbar_profile, menu);
        return true;
    }

}
