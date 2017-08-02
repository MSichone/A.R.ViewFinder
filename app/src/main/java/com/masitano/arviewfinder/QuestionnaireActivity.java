package com.masitano.arviewfinder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.masitano.arviewfinder.models.QuestionnaireResponse;
import com.masitano.arviewfinder.utilities.QuestionnaireManager;

import java.util.HashMap;
import java.util.Map;

public class QuestionnaireActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnToPage1, btnToPage2, btnToPage3 ;
    private LinearLayout page1, page2, page3;
    private NestedScrollView nestedScrollView;
    private int pageNumber;

    //answers
    private QuestionnaireManager questManager;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupAge;
    private RadioGroup radioGroupGps;
    private CheckBox checkbox_googleMaps,checkbox_foursquare,checkbox_uber,checkbox_snapmap;

    //Firebase
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbQuestionnaireResponses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Application Questionnaire Answer Store
        questManager = new QuestionnaireManager(this);

        //get firebase authorization instance & current user
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //load the questionnaire database
        mFirebaseInstance = FirebaseDatabase.getInstance();
        dbQuestionnaireResponses = mFirebaseInstance.getReference("questionnaireResponses");


        pageNumber = 1;

        // get layouts
        page1 = (LinearLayout) findViewById(R.id.layoutPage1);
        page2 = (LinearLayout) findViewById(R.id.layoutPage2);
        page3 = (LinearLayout) findViewById(R.id.layoutPage3);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        //get reference to answers
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioGroupAge = (RadioGroup) findViewById(R.id.radioGroupAge);
        radioGroupGps = (RadioGroup) findViewById(R.id.radioGroupGPS);
        checkbox_googleMaps = (CheckBox) findViewById(R.id.checkbox_googleMaps);
        checkbox_foursquare = (CheckBox) findViewById(R.id.checkbox_foursquare);
        checkbox_uber = (CheckBox) findViewById(R.id.checkbox_uber);
        checkbox_snapmap = (CheckBox) findViewById(R.id.checkbox_snapmap);

        //start with page 1
        page1.setVisibility(View.VISIBLE);
        page2.setVisibility(View.GONE);
        page3.setVisibility(View.GONE);

        btnToPage1 = (Button) findViewById(R.id.btnToPage1);
        btnToPage2 = (Button) findViewById(R.id.btnForwardToPage2);
        btnToPage3 = (Button) findViewById(R.id.btnForwardToPage3);

        //attaching listener to button
        btnToPage1.setOnClickListener(this);
        btnToPage2.setOnClickListener(this);
        btnToPage3.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.btnForwardToPage2){

            //save page 1 answers
            int genderSelected = radioGroupGender.getCheckedRadioButtonId();
            int ageSelected = radioGroupAge.getCheckedRadioButtonId();

            if (genderSelected != -1 && ageSelected != -1){
                RadioButton gender = (RadioButton) findViewById(genderSelected);
                RadioButton age = (RadioButton) findViewById(ageSelected);

                questManager.setGender(gender.getText().toString());
                questManager.setAge(age.getText().toString());
            }else{
                String message = "Please select your gender and age bracket to continue";
                openDialog(message);
            }

            // proceed to next page scrolling to top
            nestedScrollView.scrollTo(0,0);
            page2.setVisibility(View.VISIBLE);
            page1.setVisibility(View.GONE);
            page3.setVisibility(View.GONE);
        }

        if(v.getId()== R.id.btnToPage1){
            page1.setVisibility(View.VISIBLE);
            page2.setVisibility(View.GONE);
            page3.setVisibility(View.GONE);
        }

        if(v.getId()== R.id.btnBackToPage2){
            page2.setVisibility(View.VISIBLE);
            page1.setVisibility(View.GONE);
            page3.setVisibility(View.GONE);
        }

        if(v.getId()== R.id.btnForwardToPage3){
            page3.setVisibility(View.VISIBLE);
            page2.setVisibility(View.GONE);
            page1.setVisibility(View.GONE);

            //save page 2 answers
            int gpsKnowledgeSelected = radioGroupGps.getCheckedRadioButtonId();
            if (gpsKnowledgeSelected != -1){
                RadioButton gpsKnowledge = (RadioButton) findViewById(gpsKnowledgeSelected);

                questManager.setGpsKnowledge(gpsKnowledge.getText().toString());

            }else{
                String message = "Please select your knowledge of GPS and/or Geo Location Services";
                openDialog(message);
            }

            questManager.setHasUsedGoogleMaps(checkbox_googleMaps.isChecked());
            questManager.setHasUsedFoursquare(checkbox_foursquare.isChecked());
            questManager.setHasUsedUber(checkbox_uber.isChecked());
            questManager.setHasSnapMap(checkbox_snapmap.isChecked());

            //upload response
            uploadQuestionnaireResponses();
        }
    }

    private void openDialog(String message) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(QuestionnaireActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message_error, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        txtMessage.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        //capturing the cancel button
        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    public void uploadQuestionnaireResponses(){
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();

        questionnaireResponse.setUserId(user.getUid());
        questionnaireResponse.setGender(questManager.getGender());
        questionnaireResponse.setAge(questManager.getAge());
        questionnaireResponse.setKnowledgeOfGps(questManager.getGpsKnowledge());
        questionnaireResponse.setUsedGoogleMaps(questManager.hasUsedGoogleMaps());
        questionnaireResponse.setUsedFourSquare(questManager.hasUsedFourSquare());
        questionnaireResponse.setUsedUber(questManager.hasUsedUber());
        questionnaireResponse.setUsedSnapMap(questManager.hasUsedSnapMap());

        //send responses to cloud
        dbQuestionnaireResponses.child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Write new post
                        String key = dbQuestionnaireResponses.child(user.getUid()).push().getKey();

                        Map<String, Object> postValues = questionnaireResponse.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid() + "/" + key, postValues);

                        dbQuestionnaireResponses.updateChildren(childUpdates);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Error:"+ databaseError.toString());
                    }
                }
        );
    }
}
