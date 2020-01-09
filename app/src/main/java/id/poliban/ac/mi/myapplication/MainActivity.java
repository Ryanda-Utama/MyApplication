package id.poliban.ac.mi.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.style.BulletSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public  static  final  int REQUEST_CODE_STORAGE = 100;
    ListView listView;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Aplikasi Catatan Proyek 1");
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent   = new Intent(MainActivity.this, InsertAndViewActivity.class);
                //ambil data yang telah anda klik pada list, kemudian simpan ke variable data
                Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
                intent.putExtra("File name", data.get("name").toString());
                //Menapilkan pesan anda
                Toast.makeText(MainActivity.this, "You clicked" + data.get("name"),
                        Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Map<String, Object> data = (Map<String ,Object>) parent.getAdapter().getItem(position);
            tampilkanDialogKonfirmasiHapusCatatan(data.get("name").toString());
            return true;
        });
    }

    private void tampilkanDialogKonfirmasiHapusCatatan(String filename) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Catatan ini?" )
                .setMessage("Yakin, mau hapus" + filename +"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", ((dialog, which) ->  hapusfile(filename)))
                .setNegativeButton("NO", null)
                .show();
    }

    private void hapusfile(String filename) {
   //String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File file = new File(getFilesDir(), filename);
        if (file.exists()) {
            file.delete();
        }
        mengambilListFilePadaFolder();
    }

     void mengambilListFilePadaFolder() {
      File directory = new File(getFilesDir(), ".");
      if(directory.exists()){
          File[] files = directory.listFiles();
          String[] filenames = new String[files.length];
          String[] dateCreated = new String[files.length];
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
          ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();

          for (int i = 0; i < files.length; i++){
              filenames[i] = files[i].getName();
              Date lastModDate = new Date(files[i].lastModified());
              dateCreated[i] =simpleDateFormat.format(lastModDate);
              Map<String, Object> listItemMap = new HashMap<>();
              listItemMap.put("name", filenames[i]);
              listItemMap.put("date",dateCreated[i]);
              itemDataList.add(listItemMap);
          }
          SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemDataList, android.R.layout.simple_list_item_2,
                  new String[]
                          {"name", "date"}, new int[] {android.R.id.text1, android.R.id.text2});
          listView.setAdapter(simpleAdapter);
          simpleAdapter.notifyDataSetChanged();
      }
    }


    @Override
    protected void onResume(){
        super.onResume();
        if (Build.VERSION.SDK_INT>=23){
            if (periksaIzinPenyimpanan()){
                mengambilListFilePadaFolder();
            }
            else{
                mengambilListFilePadaFolder();
            }
        }}

        public boolean periksaIzinPenyimpanan() {
            if (Build.VERSION.SDK_INT >= 23) {
                if
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                        .PERMISSION_GRANTED) {
                    return true;
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                    return false;
                }
                }else{
                    return true;
                }


            }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Menu_add:
                Intent intent = new Intent(this, InsertAndViewActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
