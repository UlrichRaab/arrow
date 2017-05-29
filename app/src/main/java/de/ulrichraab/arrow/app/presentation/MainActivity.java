package de.ulrichraab.arrow.app.presentation;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.ulrichraab.arrow.Arrow;
import de.ulrichraab.arrow.app.R;
import de.ulrichraab.arrow.app.model.Device;
import de.ulrichraab.arrow.app.model.User;


public class MainActivity extends AppCompatActivity {

    @Inject
    Device device;
    @Inject
    Long random;
    @Inject
    User user;

    @BindView(R.id.device)
    TextView deviceTextView;
    @BindView(R.id.random)
    TextView randomTextView;
    @BindView(R.id.user)
    TextView userTextView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        ButterKnife.bind(this);

        // Inject dependencies

        MainActivityInjector injector = Arrow.injector("di://main-activity", MainActivityInjector.class);
        injector.inject(this);

        Arrow.injectorBuilder("di://main-activity", MainActivityInjector.Builder.class)
             .user(new User("John Doe"))
             .mainActivityModule(new MainActivityModule())
             .build()
             .inject(this);
    }

    @Override
    protected void onResume () {
        super.onResume();

        deviceTextView.setText(device.name());
        randomTextView.setText(String.valueOf(random));
        userTextView.setText(user.name());

    }
}
