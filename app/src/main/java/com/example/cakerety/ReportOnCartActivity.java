package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PDFPrint;
import android.util.Log;

import com.example.cakerety.model.MyCartModel;
import com.example.cakerety.utils.FileManager;
import com.example.cakerety.utils.PDFUtil;
import com.example.cakerety.utils.PdfViewerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportOnCartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReport;

    private FirebaseFirestore fStore;
    private FirebaseAuth auth;

    private HistoryAllCartAdapter adapter;

    private List<MyCartModel> list;

    private String TAG = "ReportOnCartActivity";
    String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_on_cart);

        recyclerViewReport = findViewById(R.id.recyclerviewReportAllCart);
        recyclerViewReport.setHasFixedSize(true);
        recyclerViewReport.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        list = new ArrayList<>();
        adapter = new HistoryAllCartAdapter(this, list);
        recyclerViewReport.setAdapter(adapter);

        showData();
    }

    private void showData() {

        ArrayList<String> idUser = new ArrayList<>();

        fStore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> listt = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : listt){
                            fStore.collection("CurrentUser").document(snapshot.getId())
                                    .collection("AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final File savedPDFFile = FileManager.getInstance().createTempFile(ReportOnCartActivity.this, "pdf", false);

                                        html = "<!DOCTYPE html>\n" +
                                                "<html>\n" +
                                                "<head>\n" +
                                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                                                "<style>\n" +
                                                "* {\n" +
                                                "  box-sizing: border-box;\n" +
                                                "  font-family: arial, sans-serif;\n" +
                                                "}\n" +
                                                "\n" +
                                                ".column1 {\n" +
                                                "text-align:center;\n" +
                                                "  float: left;\n" +
                                                "  width: 30%;\n" +
                                                "  padding: 10px;\n" +
                                                "}\n" +
                                                ".column2 {\n" +
                                                "text-align:center;\n" +
                                                "  float: left;\n" +
                                                "  width: 70%;\n" +
                                                "  padding: 10px;\n" +
                                                "}\n" +
                                                "\n" +
                                                ".row:after {\n" +
                                                "  content: \"\";\n" +
                                                "  display: table;\n" +
                                                "  clear: both;\n" +
                                                "}\n" +
                                                "table {\n" +
                                                "font-family: arial, sans-serif;\n" +
                                                "border-collapse: collapse;\n" +
                                                "width: 100%;\n" +
                                                "}\n" +
                                                "\n" +
                                                "td {\n" +
                                                "border: 1px solid #dddddd;\n" +
                                                "text-align: left;\n" +
                                                "padding: 8px;\n" +
                                                "}\n" +
                                                "th {\n" +
                                                "border: 1px solid #dddddd;\n" +
                                                "text-align: center;\n" +
                                                "padding: 8px;\n" +
                                                "}\n" +
                                                "</style>\n" +
                                                "</head>\n" +
                                                "<body>\n" +
                                                "\n" +
                                                "<div class=\"row\">\n" +
                                                "  <div class=\"column1\">\n" +
                                                "    <img src='https://i0.wp.com/kopitekno.com/wp-content/uploads/2020/12/kopitekno-544.png' style='width: 170px; height: 70px;margin-top:30px;' alt='IMP Studio'>\n" +
                                                "  </div>\n" +
                                                "  <div class=\"column2\">\n" +
                                                "    <h2>CakeRety</h2>\n" +
                                                "    <p style=\"margin-top: -20px\">RetyCake</p>\n" +
                                                "    <p style=\"margin-top: -10px\"> Alamat : </p>\n" +
                                                "    <p style=\"margin-top: -10px\">Email : admin@cakerety</p>\n" +
                                                "  </div>\n" +
                                                "</div>\n" +
                                                "\n" +
                                                "<table>\n" +
                                                "<tr>\n" +
                                                "<th colspan=\"4\" style=\"background-color: #e74c3c; color: white;\"> Laporan Preorder </th>\n" +
                                                "</tr>\n" +
                                                "<tr style=\"background-color: #e74c3c; color: white;\">\n" +
                                                "  <th>Produk</th>\n" +
                                                "  <th>Harga Satuan</th>\n" +
                                                "  <th>Jumlah Pesanan</th>\n" +
                                                "  <th>Total Harga</th>\n" +
                                                "</tr>";

                                        int i = 0;
                                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                            String documentId = documentSnapshot.getId();

                                            MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);

                                            cartModel.setDocumentId(documentId);

//                                            list.add(cartModel);
//                                            adapter.notifyDataSetChanged();
                                            Log.d("Tag", "documentSnapshot : "+documentSnapshot);
                                            Log.d("Tag", "i aaa  : "+(i == (task.getResult().getDocuments().size()-1)));

                                            html += "<tr>\n" +
                                                    "    <td>\n" +
                                                    documentSnapshot.getString("productName")+
                                                    "    </td>\n" +
                                                    "    <td>\n" +
                                                    documentSnapshot.getString("productPrice")+
                                                    "    </td>\n" +
                                                    "    <td>\n" +
                                                    documentSnapshot.getString("totalQuantity")+
                                                    "    </td>\n" +
                                                    "    <td>\n" +
                                                    documentSnapshot.getDouble("totalPrice")+
                                                    "    </td>\n" +
                                                    "  </tr>";

                                            if(i == (task.getResult().getDocuments().size()-1)){

                                                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                                                Date now = new Date();
                                                String yearStr = year.format(now);

                                                SimpleDateFormat month = new SimpleDateFormat("MM");
                                                Date nowMonth = new Date();
                                                String monthStr = month.format(nowMonth);

                                                String bulan = null;
                                                if(monthStr.equals("01")){
                                                    bulan = "Januari";
                                                }else if(monthStr.equals("02")){
                                                    bulan = "Febuari";
                                                }else if(monthStr.equals("03")){
                                                    bulan = "Maret";
                                                }else if(monthStr.equals("04")){
                                                    bulan = "April";
                                                }else if(monthStr.equals("05")){
                                                    bulan = "Mei";
                                                }else if(monthStr.equals("06")){
                                                    bulan = "Juni";
                                                }else if(monthStr.equals("07")){
                                                    bulan = "Juli";
                                                }else if(monthStr.equals("08")){
                                                    bulan = "Agustus";
                                                }else if(monthStr.equals("09")){
                                                    bulan = "September";
                                                }else if(monthStr.equals("10")){
                                                    bulan = "Oktober";
                                                }else if(monthStr.equals("11")){
                                                    bulan = "November";
                                                }else if(monthStr.equals("12")){
                                                    bulan = "Desember";
                                                }

                                                SimpleDateFormat day = new SimpleDateFormat("dd");
                                                Date nowDay = new Date();
                                                String dayStr = day.format(nowDay);

                                                html += "</table>\n" +
                                                        "<div style='margin-left: 500px;margin-top:30px;text-align: center;'>\n" +
                                                        "Jakarta, " +dayStr+" "+bulan+" "+yearStr+
                                                        "<br>\n" +
                                                        "Direktur Utama\n" +
                                                        "<br>\n" +
                                                        "<br>\n" +
                                                        "<br>\n" +
                                                        "<br>\n" +
                                                        "<br>\n" +
                                                        "Nama Pimpinan\n" +
                                                        "</div>\n" +
                                                        "</body>\n" +
                                                        "</html>\n";

                                                PDFUtil.generatePDFFromHTML(ReportOnCartActivity.this, savedPDFFile, html, new PDFPrint.OnPDFPrintListener() {
                                                    @Override
                                                    public void onSuccess(File newFile) {

                                                        String inputPath = newFile.toPath().toString();
                                                        String outputPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                                        InputStream in = null;
                                                        OutputStream out = null;
                                                        try {

                                                            //create output directory if it doesn't exist
                                                            File dir = new File (outputPath);
                                                            if (!dir.exists())
                                                            {
                                                                dir.mkdirs();
                                                            }


                                                            in = new FileInputStream(inputPath);
                                                            out = new FileOutputStream(outputPath + "/laporan preorder.pdf");

                                                            byte[] buffer = new byte[1024];
                                                            int read;
                                                            while ((read = in.read(buffer)) != -1) {
                                                                out.write(buffer, 0, read);
                                                            }
                                                            in.close();
                                                            in = null;

                                                            // write the output file (You have now copied the file)
                                                            out.flush();
                                                            out.close();
                                                            out = null;

                                                        }  catch (FileNotFoundException fnfe1) {
                                                            Log.e("tag", fnfe1.getMessage());
                                                        }
                                                        catch (Exception e) {
                                                            Log.e("tag", e.getMessage());
                                                        }

                                                        // Open Pdf Viewer
                                                        Uri pdfUri = Uri.fromFile(savedPDFFile);

                                                        Intent intentPdfViewer = new Intent(ReportOnCartActivity.this, PdfViewerActivity.class);
                                                        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

                                                        startActivity(intentPdfViewer);
                                                    }

                                                    @Override
                                                    public void onError(Exception exception) {
                                                        exception.printStackTrace();
                                                    }
                                                });
                                            }

                                            i++;

                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.d(TAG, "error saat ambil data");
                                }
                            });
                        }
                    }
                });
    }
}