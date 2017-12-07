package qbabor4.pl.alarmmanagertry;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jakub on 07-Dec-17.
 */

public class AddNewClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_class);
        setToolbar();
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

    /** TOOLBAR */
    private void setToolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                // ikona powrotu
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Toast.makeText(this, "Clicked action menu", Toast.LENGTH_SHORT).show();
        }

        // dodac ikonę powrotu i dac finish jak kliknie na nią
        // dać ikonę z lewej

        return super.onOptionsItemSelected(item);
    }

    /** end Toolbar */

    // zrobić ikonę wracającą


}
