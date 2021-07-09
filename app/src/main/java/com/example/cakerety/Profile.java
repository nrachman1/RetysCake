package com.example.cakerety;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener{

    View rootView;

    private TextView fullnameTextView, emailTextView, addressTextView, phoneNumTextView, changePasswordTextView, editProfileTextView;
    private ImageView userImageView;
    private Button buttonSignOut;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    private StorageReference storageReference;

    private String TAG = "ProfileFragment";

    private MediaPlayer mediaPlayer;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
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
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        InitView();

        return rootView;
    }

    private void InitView() {

        fullnameTextView = rootView.findViewById(R.id.fullNameFragmentCus);
        emailTextView = rootView.findViewById(R.id.emailFragmentCus);
        changePasswordTextView = rootView.findViewById(R.id.changePasswordFragmentCus);
        changePasswordTextView.setOnClickListener(this);
        addressTextView = rootView.findViewById(R.id.addressFragmentCus);
        phoneNumTextView = rootView.findViewById(R.id.phoneNumFragmentCus);
        userImageView = rootView.findViewById(R.id.userImageFragmentCus);
        userImageView.setOnClickListener(this);
        editProfileTextView = rootView.findViewById(R.id.editProfileFragmentCus);
        editProfileTextView.setOnClickListener(this);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.logout);

        buttonSignOut = rootView.findViewById(R.id.singOutFragmentCus);
        buttonSignOut.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/Profile");


        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userImageView);
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    fullnameTextView.setText(documentSnapshot.getString("fullName"));
                    emailTextView.setText(documentSnapshot.getString("email"));
                    addressTextView.setText(documentSnapshot.getString("address"));
                    phoneNumTextView.setText(documentSnapshot.getString("phoneNum"));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "error saat ambil data");
            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.singOutFragmentCus:
                SingOut();
                break;
            case R.id.editProfileFragmentCus:
                Intent i = new Intent(v.getContext(), EditProfileAdmin.class);
                i.putExtra("fullName", fullnameTextView.getText().toString());
                i.putExtra("email", emailTextView.getText().toString());
                i.putExtra("address", addressTextView.getText().toString());
                i.putExtra("phoneNum", phoneNumTextView.getText().toString());
                startActivity(i);
                break;
        }

    }

    private void SingOut() {
        fAuth.signOut();
        startActivity(new Intent(getActivity(), SingIn.class));
        mediaPlayer.start();
    }
}