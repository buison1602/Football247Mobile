package vn.edu.tlu.buicongson.football247;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import vn.edu.tlu.buicongson.football247.models.Match;
import vn.edu.tlu.buicongson.football247.models.Team;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    ImageView image_team_left, image_team_right;
    TextView text_team_name_left, text_team_name_right, text_score, text_match_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        image_team_left = findViewById(R.id.image_team_left);
        image_team_right = findViewById(R.id.image_team_right);
        text_team_name_left = findViewById(R.id.text_team_name_left);
        text_team_name_right = findViewById(R.id.text_team_name_right);
        text_score = findViewById(R.id.text_score);
        text_match_time = findViewById(R.id.text_match_time);

        fetchFeaturedMatch();
    }

    private void fetchFeaturedMatch() {
        db.collection("matches")
                .whereEqualTo("isFeatured", true)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "Không có trận đấu hấp dẫn nào.", Toast.LENGTH_SHORT).show();
                        }

                        // QuerySnapshot là DANH SÁCH(mảng) các trận đấu
                        QuerySnapshot querySnapshot = task.getResult();
                        // DocumentSnapshot là 1 TRẬN ĐẤU bên trong DANH SÁCH
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        // Chuyển 1 TRẬN ĐẤU thành 1 đối tượng Match
                        Match featuredMatch = documentSnapshot.toObject(Match.class);

                        String matchTime = featuredMatch.getMatch_timestamp();
                        text_match_time.setText(matchTime);

                        String textScore = featuredMatch.getScore();
                        text_score.setText(textScore);

                        loadTeamData(featuredMatch.getHome_team_id(), text_team_name_left, image_team_left);
                        loadTeamData(featuredMatch.getAway_team_id(), text_team_name_right, image_team_right);
                    } else {
                        Toast.makeText(this, "Không tìm thấy trận đấu nổi bật.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin trận đấu nổi bật", e));
    }

    private void loadTeamData(String teamId, TextView nameTextView, ImageView logoImageView) {
        if (teamId == null || teamId.isEmpty()) return; // Kiểm tra an toàn

        db.collection("teams").document(teamId).get().addOnSuccessListener(teamDocument -> {
            if (teamDocument.exists()) {
                String teamName = teamDocument.getString("name");
                String logoUrl = teamDocument.getString("logoUrl");

                nameTextView.setText(teamName);

                Glide.with(this)
                        .load(logoUrl)
                        .into(logoImageView);
            } else {
                Log.w(TAG, "Không tìm thấy đội bóng với ID: " + teamId);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin đội bóng: " + teamId, e));
    }

}