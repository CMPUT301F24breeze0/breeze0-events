
package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AdminOperateActivity class serves as the main hub for administrative operations within the
 * Breeze0Events application. This activity provides navigation options for the admin to manage
 * organizations and entrants, as well as a logout option.
 */


/**
 * this class is for  do the main browse profile activity which choose which type of profile you want to view like organizer and entrant
 */
public class AdminOperateActivity extends AppCompatActivity {
    @Override
    /**
     * it is to click the button which can go to the type of profile you want to view
     * @param sacedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_activity);
        Button organization_button=findViewById(R.id.organization);
        Button entrant_button=findViewById(R.id.entrant);
        Button back_button=findViewById(R.id.organizer_facility_activity_back_button);
        back_button.setOnClickListener(v->{
            finish();
        });
        entrant_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminOperateActivity.this,AdminEntrantProfileActivity.class);
                startActivity(intent);
            }
        });
        //by clicking organization profile button
        organization_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Intent intent = new Intent(AdminOperateActivity.this, AdminOrganizationProfileActivity.class);
                startActivity(intent);
            }
        });
    }




}