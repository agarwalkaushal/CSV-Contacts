package codexllc.csvcontacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import rx.Subscription;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity {

    Cursor cursor ;
    File file;
    String name, phonenumber;
    ConstraintLayout constrainLayout;
    Button contact;
    private static final int  PERMISSION_REQUEST =  477;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constrainLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        contact= (Button) findViewById(R.id.contact);
        askForPermission();
        Subscription buttonSub = RxView.clicks(contact).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                GetContactsIntoArrayList();


            }
        });

    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS
                }, PERMISSION_REQUEST);
    }

    public void GetContactsIntoArrayList(){

        List<String[]> stringList = new ArrayList<String[]>();
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            String[] row = new String[]{name,phonenumber};
            stringList.add(row);
            Log.e("Contact: ",phonenumber);
        }

        save(stringList);
        cursor.close();


    }


    public void save(List<String[]> list){
        String csv ="contacts.csv";
        String zip = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/contacts.zip";
        CSVWriter writer = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zip);
            ZipOutputStream zos = new ZipOutputStream(fileOutputStream);
            ZipEntry entry = new ZipEntry(csv);
            zos.putNextEntry(entry);
            writer = new CSVWriter(new OutputStreamWriter(zos));

            writer.writeAll(list);
            Log.e("CSV", String.valueOf(writer));
            Snackbar snackbar1 = Snackbar
                    .make(constrainLayout, "File saved to: "+ zip, Snackbar.LENGTH_LONG);
            snackbar1.show();
            writer.close();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0){
                    boolean storage  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean contacts = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (storage && contacts ) {

                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                                     }
                return;
            }
        }
    }

}
