package vn.edu.tlu.buicongson.football247;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // findViewById tìm view
        // findFragmentById tìm fragment. Muốn tìm thì phải tìm thông qua FragmentManager
        // FragmentManager được gọi ra bởi getSupportFragmentManager()
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            navView.setOnItemSelectedListener(item -> {
                NavOptions.Builder builder = new NavOptions.Builder();
                builder.setLaunchSingleTop(true);
                int startDestinationId = navController.getGraph().getStartDestinationId();

                builder.setPopUpTo(startDestinationId, false);

                NavOptions options = builder.build();

                try {
                    // Thực hiện điều hướng đến đích có ID trùng với ID của menu item
                    navController.navigate(item.getItemId(), null, options);
                    return true;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, e.getMessage());
                    return false;
                }
            });
        } else {
            Log.e(TAG, "NavHostFragment not found!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}