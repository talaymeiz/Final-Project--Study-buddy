package com.example.studybuddy.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studybuddy.model.api.RetrofitClient;
import com.example.studybuddy.objects.Student;
import com.example.studybuddy.viewModel.MainActivity;
import com.example.studybuddy.viewModel.StudentProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class StudentProfileModel {


    private Activity activity;
    private String userUID;
    private DocumentReference documentReference;
    private FirebaseUser user;
    private FirebaseFirestore db;


    public StudentProfileModel (Activity activity){
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
        this.userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.documentReference = db.collection("students").document(userUID);
        this.user  = FirebaseAuth.getInstance().getCurrentUser();
    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public String getUserUID() {
        return userUID;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void modelOnStart(EditText name, EditText age, EditText year, EditText degree, EditText phone_number, StudentProfileActivity thisactivity) {

        Call<Student> call = RetrofitClient.getInstance().getAPI().getStudentDetails(userUID);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                Student curr_student;
                curr_student = response.body();
                String nameResult = curr_student.getName();
                String ageResult = curr_student.getAge();
                String yearResult = curr_student.getYear();
                String degreeResult = curr_student.getDegree();
                String phoneResult = curr_student.getPhone();
                name.setText(nameResult);
                age.setText(ageResult);
                year.setText(yearResult);
                degree.setText(degreeResult);
                phone_number.setText(phoneResult);
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.d("Fail", t.getMessage());
                Toast.makeText(thisactivity, "no profile yet" , Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void updateProfileM(String textName, String textYear, String textDegree, String textGender, String textAge, String textPhone) {

        assert user != null;

        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().updateStudentDetails(userUID, textName, textYear, textDegree, textGender, textAge, textPhone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("done", "done");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });

    }

}
