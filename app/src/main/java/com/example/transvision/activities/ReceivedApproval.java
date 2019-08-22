package com.example.transvision.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transvision.R;
import com.example.transvision.adapters.ManagementAdapter;
import com.example.transvision.adapters.ReceivedAdapter;
import com.example.transvision.adapters.ViewApprovalAdapter;
import com.example.transvision.invoke.SendingData;
import com.example.transvision.model.EquipmentDetails;
import com.example.transvision.values.FunctionCall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.example.transvision.values.constants.DLG_RECEIVED_SUBMIT;
import static com.example.transvision.values.constants.RECEIVED_APPROVAL_FAILURE;
import static com.example.transvision.values.constants.RECEIVED_APPROVAL_SUCCESS;
import static com.example.transvision.values.constants.REQUEST_RESULT_FAILURE;
import static com.example.transvision.values.constants.REQUEST_RESULT_SUCCESS;

public class ReceivedApproval extends AppCompatActivity {
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    Toolbar toolbar;
    RecyclerView received_approvals;
    ArrayList<EquipmentDetails> equipmentDetails;
    SendingData sendingData;
    static FunctionCall functionCall;
    ProgressDialog progressDialog;
    ReceivedAdapter receivedAdapter;
    ManagementAdapter managementAdapter;
    ViewApprovalAdapter viewApprovalAdapter;
    File destination;
    static String pathname = "";
    static String pathextension = "";
    static String filename = "";
    static File mediaFile;
    String cons_ImgAdd = "", cons_imageextension = "";
    Bitmap bitmap, bitmap1;
    String SUBDIV_CODE = "";
    ImageView imageView;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    finish();
                    break;

                case RECEIVED_APPROVAL_SUCCESS:
                    progressDialog.dismiss();
                    functionCall.showToast(ReceivedApproval.this,"Success");
                    startActivity(getIntent());
                    finish();
                    break;

                case RECEIVED_APPROVAL_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(ReceivedApproval.this,"Failure");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_approval);
        initialize();
    }

    public void initialize() {
        Intent intent = getIntent();
        SUBDIV_CODE = intent.getStringExtra("subdiv_code");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle("Received Approval");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendingData = new SendingData();
        functionCall = new FunctionCall();
        progressDialog = new ProgressDialog(this);

        managementAdapter = new ManagementAdapter(this, equipmentDetails);
        received_approvals = findViewById(R.id.approval_recycler);
        equipmentDetails = new ArrayList<>();
        viewApprovalAdapter = new ViewApprovalAdapter(this, equipmentDetails);
        receivedAdapter = new ReceivedAdapter(this, equipmentDetails);
        received_approvals.setHasFixedSize(true);
        received_approvals.setLayoutManager(new LinearLayoutManager(this));
        received_approvals.setAdapter(receivedAdapter);

        functionCall.showprogressdialog("Please wait to complete....", progressDialog, "Fetching requests");
        SendingData.Fetch_Received_Requests fetch_received_requests = sendingData.new Fetch_Received_Requests(handler, equipmentDetails, receivedAdapter);
        fetch_received_requests.execute(SUBDIV_CODE);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, getApplicationContext());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                cons_ImgAdd = pathname;
                cons_imageextension = pathextension;
                bitmap = null;
                try {
                    bitmap = functionCall.getImage(cons_ImgAdd, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                destination = functionCall.filestorepath("Item_Pics", cons_imageextension);
                if (bitmap != null) {
                    saveExternalPrivateStorage(destination, timestampItAndSave(bitmap));
                }
                String destinationfile = functionCall.filepath("Item_Pics") + File.separator + cons_imageextension;
                bitmap1 = null;
                try {
                    bitmap1 = functionCall.getImage(destinationfile, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap1);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getOutputMediaFileUri(int type, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", Objects.requireNonNull(getOutputMediaFile(type)));
        else return Uri.fromFile(getOutputMediaFile(type));
    }


    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(), functionCall.Appfoldername() + File.separator +
                "Item_Pics");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
            pathname = mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg";
            pathextension = timeStamp + ".jpg";
            filename = timeStamp;
        } else {
            return null;
        }
        return mediaFile;
    }

    //**********************************Below code is for adding Watermark****************************************************
    public Bitmap timestampItAndSave(Bitmap bitmap) {
        Bitmap watermarkimage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(42);
        tPaint.setColor(Color.RED);
        tPaint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(bitmap, 0f, 0f, null);
        float height = tPaint.measureText("yY");
        cs.drawText(filename, 20f, height + 15f, tPaint);
        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(new File(pathname)));
            File directory = new File(pathname);
            FileInputStream watermarkimagestream = new FileInputStream(directory);
            watermarkimage = BitmapFactory.decodeStream(watermarkimagestream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return watermarkimage;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveExternalPrivateStorage(File folderDir, Bitmap bitmap) {
        if (folderDir.exists()) {
            folderDir.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(folderDir);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showdialog(int id, final int position, ArrayList<EquipmentDetails> arrayList) {
        final AlertDialog alertDialog;
        final EquipmentDetails equipmentDetails = arrayList.get(position);
        if (id == DLG_RECEIVED_SUBMIT) {
            AlertDialog.Builder insert_out = new AlertDialog.Builder(this);
            @SuppressLint("InflateParams") LinearLayout received_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.received_layout, null);
            insert_out.setView(received_layout);
            insert_out.setCancelable(false);
            alertDialog = insert_out.create();
            Button btn_submit = received_layout.findViewById(R.id.btn_submit);
            TextView tv_close = received_layout.findViewById(R.id.txt_close);
            tv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            imageView = received_layout.findViewById(R.id.items_pic);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    captureImage();
                }
            });
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    receivedSubmit(equipmentDetails.getID());
                }
            });
            alertDialog.show();
        }
    }

    public void receivedSubmit(String id) {
        File file = new File(pathname);
        String imageNameOnly = file.getName();
        String encode = functionCall.encoded(pathname);
        if (TextUtils.isEmpty(imageNameOnly)) {
            functionCall.showToast(ReceivedApproval.this, "Please take pic & proceed.");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete", progressDialog, "Uploading Data");
        SendingData.Received_Approval received_approval = sendingData.new Received_Approval(handler);
        received_approval.execute(id, imageNameOnly, encode);
    }
}
