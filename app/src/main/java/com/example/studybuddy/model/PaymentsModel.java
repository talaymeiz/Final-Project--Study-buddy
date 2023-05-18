package com.example.studybuddy.model;

import static java.lang.Integer.parseInt;

import android.util.Log;
import android.widget.Toast;

import com.example.studybuddy.model.api.RetrofitClient;
import com.example.studybuddy.objects.Class;
import com.example.studybuddy.viewModel.MyPaymentsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentsModel {
    private MyPaymentsActivity activity;
    private String userID;
    private CollectionReference classesRef;

    public PaymentsModel(MyPaymentsActivity activity, String userID, String classes) {
        this.activity = activity;
        this.userID = userID;
        this.classesRef = FirebaseFirestore.getInstance().collection(classes);
    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    public void updatePastCourses() {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().PastCourses();
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

    public Query buildClassQuery(String field){
        return this.classesRef.whereEqualTo(field, this.userID).whereEqualTo("past","yes");
    }

    public void onApprovePayment(String studentName, String subject, String date) {

        Call<String> call = RetrofitClient.getInstance().getAPI().approvePaymentTeacher(this.userID,  studentName, date, subject);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String getStudentApproval = response.body();
                if (parseInt(getStudentApproval) == 0) {
                    Toast.makeText(activity, "student didn't pay yet", Toast.LENGTH_SHORT).show();
                } else{
                    activity.approvePaymentQuestionPopup(studentName, subject, date);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }


    public void click_yes(String studentName, String subject, String date) {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().deletePaidClass(userID, studentName, subject, date);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("done", "done");
                    Toast.makeText(activity, "paid successfully", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Failed to delete class", t.getMessage());
                Toast.makeText(activity, "error in removing class", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
