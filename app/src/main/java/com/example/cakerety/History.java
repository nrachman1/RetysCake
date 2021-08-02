package com.example.cakerety;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.print.PDFPrint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cakerety.model.Cake;
import com.example.cakerety.model.MyCartModel;
import com.example.cakerety.utils.FileManager;
import com.example.cakerety.utils.PDFUtil;
import com.example.cakerety.utils.PdfViewerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link History#newInstance} factory method to
 * create an instance of this fragment.
 */
public class History extends Fragment {

    View rootView;

    private FirebaseFirestore fStore;
    private FirebaseAuth auth;

    private RecyclerView recyclerViewHistoryOrder;

    private HistoryAdapter adapter;

    private List<MyCartModel> list;

    private String TAG = "HistoryFragment";
    String html;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public History() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment History.
     */
    // TODO: Rename and change types and number of parameters
    public static History newInstance(String param1, String param2) {
        History fragment = new History();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_history, container, false);

        InitView();

        return rootView;
    }

    private void InitView() {

        recyclerViewHistoryOrder = rootView.findViewById(R.id.recyclerviewHistoryUser);
        recyclerViewHistoryOrder.setHasFixedSize(true);
        recyclerViewHistoryOrder.setLayoutManager(new LinearLayoutManager(getActivity()));

        fStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        list = new ArrayList<>();
        adapter = new HistoryAdapter(getContext(), list);
        recyclerViewHistoryOrder.setAdapter(adapter);

        showData();
    }

    private void showData() {
        fStore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("MyOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    Log.d(TAG, "sukses1");
//                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
//
//                        String documentId = documentSnapshot.getId();
//
//                        MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);
//
//                        cartModel.setDocumentId(documentId);
//
//                        list.add(cartModel);
//                        adapter.notifyDataSetChanged();
//                    }
//                }

                if (task.isSuccessful()) {

                    final File savedPDFFile = FileManager.getInstance().createTempFile((Context) getActivity(), "pdf", false);

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
                            "    <img src='https://i0.wp.com/kopitekno.com/wp-content/uploads/2020/12/kopitekno-544.png' style='width: 170px; height: 70px;margin-top:30px;' alt=''>\n" +
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
                            "<th colspan=\"4\" style=\"background-color: #e74c3c; color: white;\"> History Pemesanan </th>\n" +
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

//                        MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);
//
//                        cartModel.setDocumentId(documentId);

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

                            PDFUtil.generatePDFFromHTML((Context) getActivity(), savedPDFFile, html, new PDFPrint.OnPDFPrintListener() {
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
                                        out = new FileOutputStream(outputPath + "/laporan pemesanan.pdf");

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

                                    Intent intentPdfViewer = new Intent((Context) getActivity(), PdfViewerActivity.class);
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