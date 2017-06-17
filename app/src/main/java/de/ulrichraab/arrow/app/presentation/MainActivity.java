package de.ulrichraab.arrow.app.presentation;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.ulrichraab.arrow.Arrow;
import de.ulrichraab.arrow.app.R;
import de.ulrichraab.arrow.app.model.Device;
import de.ulrichraab.arrow.app.model.User;
import de.ulrichraab.arrow.app.rng.Rng;


public class MainActivity extends AppCompatActivity {

    @Inject
    Device device;
    @Inject
    User user;

    @BindView(R.id.device)
    TextView deviceTextView;
    @BindView(R.id.random)
    TextView randomTextView;
    @BindView(R.id.user)
    TextView userTextView;

    private final Rng rng = new Rng();

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        ButterKnife.bind(this);

        // Inject dependencies
        Arrow.subcomponentBuilder(MainSubcomponent.Builder.class)
             .user(new User("John Doe"))
             .build()
             .inject(this);
    }

    @Override
    protected void onResume () {
        super.onResume();

        deviceTextView.setText(device.name());
        randomTextView.setText(String.valueOf(rng.generate()));
        userTextView.setText(user.name());

    }
}
