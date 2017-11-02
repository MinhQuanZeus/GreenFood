package greenlife.com.vn.greenfood.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.fragments.LoginFragment;
import greenlife.com.vn.greenfood.managers.ScreenManager;

public class LoginActivity extends AppCompatActivity {
    private final String prefname = "my_data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ScreenManager.openFragment(getSupportFragmentManager(),new LoginFragment(), R.id.content,false);
    }
}
