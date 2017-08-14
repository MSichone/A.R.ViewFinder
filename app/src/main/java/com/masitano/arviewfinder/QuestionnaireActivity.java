package com.masitano.arviewfinder;

import android.content.DialogInterface;
import android.content.Intent;
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

    private Button btnForwardToPage2, btnForwardToPage3, btnForwardToPage4;
    private Button btnToPage1, btnBackToPage2, btnBackToPage3, btnSubmit;
    private LinearLayout page1, page2, page3, page4;
    private NestedScrollView nestedScrollView;
    private int pageNumber;

    //answers
    private QuestionnaireManager questManager;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupAge;
    private RadioGroup radioGroupGps;
    private RadioGroup radioGroupAr;
    private RadioGroup radioGroupPrivacy, radioGroupUsability;
    private CheckBox checkbox_googleMaps,checkbox_foursquare,checkbox_uber,checkbox_snapmap;
    private CheckBox checkbox_pokemon,checkbox_yelp,checkbox_wallame,checkbox_snapface,checkbox_social;
    private CheckBox checkbox_advertising,checkbox_navigation,checkbox_tourism, checkbox_surveillance;
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

        if (!questManager.isFirstLaunch()) {
            String message = "Your response was already recorded.";
            dialogResponseRecorded(message);
        }

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
        page4 = (LinearLayout) findViewById(R.id.layoutPage4);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        //get reference to answers
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioGroupAge = (RadioGroup) findViewById(R.id.radioGroupAge);
        radioGroupGps = (RadioGroup) findViewById(R.id.radioGroupGPS);
        radioGroupAr = (RadioGroup) findViewById(R.id.radioGroupAR);
        radioGroupUsability = (RadioGroup) findViewById(R.id.radioGroupUsability);
        radioGroupPrivacy = (RadioGroup) findViewById(R.id.radioGroupPrivacy);
        checkbox_googleMaps = (CheckBox) findViewById(R.id.checkbox_googleMaps);
        checkbox_foursquare = (CheckBox) findViewById(R.id.checkbox_foursquare);
        checkbox_uber = (CheckBox) findViewById(R.id.checkbox_uber);
        checkbox_snapmap = (CheckBox) findViewById(R.id.checkbox_snapmap);
        checkbox_pokemon = (CheckBox) findViewById(R.id.checkbox_pokemon);
        checkbox_yelp = (CheckBox) findViewById(R.id.checkbox_yelp);
        checkbox_wallame = (CheckBox) findViewById(R.id.checkbox_wallame);
        checkbox_snapface = (CheckBox) findViewById(R.id.checkbox_snapface);
        checkbox_advertising = (CheckBox) findViewById(R.id.checkbox_advertising);
        checkbox_navigation = (CheckBox) findViewById(R.id.checkbox_navigation);
        checkbox_tourism = (CheckBox) findViewById(R.id.checkbox_tourism);
        checkbox_surveillance = (CheckBox) findViewById(R.id.checkbox_surveillance);
        checkbox_social = (CheckBox) findViewById(R.id.checkbox_social);

        //start with page 1
        page1.setVisibility(View.VISIBLE);
        page2.setVisibility(View.GONE);
        page3.setVisibility(View.GONE);
        page4.setVisibility(View.GONE);


        btnToPage1 = (Button) findViewById(R.id.btnToPage1);
        btnBackToPage2 = (Button) findViewById(R.id.btnBackToPage2);
        btnBackToPage3 = (Button) findViewById(R.id.btnBackToPage3);
        btnForwardToPage2 = (Button) findViewById(R.id.btnForwardToPage2);
        btnForwardToPage3 = (Button) findViewById(R.id.btnForwardToPage3);
        btnForwardToPage4 = (Button) findViewById(R.id.btnForwardToPage4);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //attaching listener to buttons
        btnToPage1.setOnClickListener(this);
        btnBackToPage2.setOnClickListener(this);
        btnBackToPage3.setOnClickListener(this);
        btnForwardToPage2.setOnClickListener(this);
        btnForwardToPage3.setOnClickListener(this);
        btnForwardToPage4.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

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

                // proceed to next page scrolling to top
                nestedScrollView.scrollTo(0,0);
                page2.setVisibility(View.VISIBLE);
                page1.setVisibility(View.GONE);
                page3.setVisibility(View.GONE);
                page4.setVisibility(View.GONE);

            }else{
                String message = "Please select your gender and age bracket to continue";
                openDialog(message);
            }

        }

        if(v.getId()== R.id.btnToPage1){
            page1.setVisibility(View.VISIBLE);
            page2.setVisibility(View.GONE);
            page3.setVisibility(View.GONE);
            page4.setVisibility(View.GONE);
        }

        if(v.getId()== R.id.btnBackToPage2){
            page2.setVisibility(View.VISIBLE);
            page1.setVisibility(View.GONE);
            page3.setVisibility(View.GONE);
            page4.setVisibility(View.GONE);
        }

        if(v.getId()== R.id.btnBackToPage3){
            page3.setVisibility(View.VISIBLE);
            page2.setVisibility(View.GONE);
            page1.setVisibility(View.GONE);
            page4.setVisibility(View.GONE);
        }

        if(v.getId()== R.id.btnForwardToPage3){

            //save page 2 answers
            int gpsKnowledgeSelected = radioGroupGps.getCheckedRadioButtonId();
            if (gpsKnowledgeSelected != -1){
                RadioButton gpsKnowledge = (RadioButton) findViewById(gpsKnowledgeSelected);

                questManager.setGpsKnowledge(gpsKnowledge.getText().toString());

                questManager.setHasUsedGoogleMaps(checkbox_googleMaps.isChecked());
                questManager.setHasUsedFoursquare(checkbox_foursquare.isChecked());
                questManager.setHasUsedUber(checkbox_uber.isChecked());
                questManager.setHasSnapMap(checkbox_snapmap.isChecked());

                // proceed to next page scrolling to top
                nestedScrollView.scrollTo(0,0);
                page3.setVisibility(View.VISIBLE);
                page2.setVisibility(View.GONE);
                page1.setVisibility(View.GONE);
                page4.setVisibility(View.GONE);

            }else{
                String message = "Please select your knowledge of GPS and/or Geo Location Services";
                openDialog(message);
            }

        }

        if(v.getId()== R.id.btnForwardToPage4){

            //save page 3 answers
            int arKnowledgeSelected = radioGroupAr.getCheckedRadioButtonId();
            if (arKnowledgeSelected != -1){
                RadioButton arKnowledge = (RadioButton) findViewById(arKnowledgeSelected);

                questManager.setArKnowledge(arKnowledge.getText().toString());

                questManager.setHasUsedPokemon(checkbox_pokemon.isChecked());
                questManager.setHasUsedYelp(checkbox_yelp.isChecked());
                questManager.setHasUsedWallaMe(checkbox_wallame.isChecked());
                questManager.setHasUsedSnapFilters(checkbox_snapface.isChecked());

                // proceed to next page scrolling to top
                nestedScrollView.scrollTo(0,0);
                page4.setVisibility(View.VISIBLE);
                page3.setVisibility(View.GONE);
                page2.setVisibility(View.GONE);
                page1.setVisibility(View.GONE);


            }else{
                String message = "Please select your knowledge of Augmented Reality";
                openDialog(message);
            }

        }

        if(v.getId()== R.id.btnSubmit){

            //save page 3 answers
            int privacySelected = radioGroupPrivacy.getCheckedRadioButtonId();
            int usabilitySelected = radioGroupUsability.getCheckedRadioButtonId();
            if (privacySelected != -1 && usabilitySelected != -1){
                RadioButton privacy = (RadioButton) findViewById(privacySelected);
                RadioButton usability = (RadioButton) findViewById(usabilitySelected);

                questManager.setPrivacy(privacy.getText().toString());
                questManager.setUsability(usability.getText().toString());

                questManager.setHasSelectedAdvertising(checkbox_advertising.isChecked());
                questManager.setHasSelectedNavigation(checkbox_navigation.isChecked());
                questManager.setHasSelectedTourism(checkbox_tourism.isChecked());
                questManager.setHasSelectedTracking(checkbox_surveillance.isChecked());
                questManager.setHasSelectedSocial(checkbox_social.isChecked());

                //upload response
                uploadQuestionnaireResponses();

                String message = "Thank you for taking part in this prototype review. Your responses have been recorded.";
                questManager.setFirstLaunch(false);
                dialogResponseRecorded(message);

            }else{
                String message = "Please select your privacy concerns and experience using the prototype";
                openDialog(message);
            }

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

    private void dialogResponseRecorded(String message) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(QuestionnaireActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message_error, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        txtMessage.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(new Intent(QuestionnaireActivity.this, MapsActivity.class));
                messageDialog.dismiss();
                finish();
            }
        });
        //capturing the cancel button
        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                //startActivity(new Intent(QuestionnaireActivity.this, MapsActivity.class));
                messageDialog.dismiss();
                finish();
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
        questionnaireResponse.setKnowledgeOfAr(questManager.getArKnowledge());
        questionnaireResponse.setUsedPokemon(questManager.hasUsedPokemon());
        questionnaireResponse.setUsedYelp(questManager.hasUsedYelp());
        questionnaireResponse.setUsedWallame(questManager.hasUsedWallaMe());
        questionnaireResponse.setUsedSnapFace(questManager.hasUsedSnapFilters());
        questionnaireResponse.setPrivacyConcerns(questManager.getPrivacy());
        questionnaireResponse.setPrototypeUsability(questManager.getUsability());
        questionnaireResponse.setSelectedAdvertising(questManager.hasSelectedAdvertising());
        questionnaireResponse.setSelectedNavigation(questManager.hasSelectedNavigation());
        questionnaireResponse.setSelectedSocial(questManager.hasSelectedSocial());
        questionnaireResponse.setSelectedTourism(questManager.hasSelectedTourism());
        questionnaireResponse.setSelectedTracking(questManager.hasSelectedTracking());

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
