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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.buicongson.football247.adapters.UpcomingMatchesAdapter;
import vn.edu.tlu.buicongson.football247.models.Match;
import vn.edu.tlu.buicongson.football247.models.Team;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private UpcomingMatchesAdapter upcomingMatchesAdapter;
    private RecyclerView recyclerviewUpcomingMatches;
    private List<Match> upcomingMatchList = new ArrayList<>();
    ImageView imageTeamLeft, imageTeamRight;
    TextView textTeamNameLeft, textTeamNameRight, textScore, textMatchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        upcomingMatchesAdapter = new UpcomingMatchesAdapter(upcomingMatchList, this);

        recyclerviewUpcomingMatches = findViewById(R.id.recyclerview_upcoming_matches);

        // Tạo một LayoutManager với hướng là HORIZONTAL
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // Gán LayoutManager đã được cấu hình vào RecyclerView
        recyclerviewUpcomingMatches.setLayoutManager(layoutManager);

        recyclerviewUpcomingMatches.setAdapter(upcomingMatchesAdapter);

        imageTeamLeft = findViewById(R.id.image_team_left);
        imageTeamRight = findViewById(R.id.image_team_right);
        textTeamNameLeft = findViewById(R.id.text_team_name_left);
        textTeamNameRight = findViewById(R.id.text_team_name_right);
        textScore = findViewById(R.id.text_score);
        textMatchTime = findViewById(R.id.text_match_time);

        fetchFeaturedMatch();
    }

    private void fetchFeaturedMatch() {
        // Load data for FEATURED MATCH
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

                        textMatchTime.setText(featuredMatch.getMatch_timestamp());

                        textScore.setText(featuredMatch.getScore());

                        loadTeamData(featuredMatch.getHome_team_id(), textTeamNameLeft, imageTeamLeft);
                        loadTeamData(featuredMatch.getAway_team_id(), textTeamNameRight, imageTeamRight);
                    } else {
                        Toast.makeText(this, "Không tìm thấy trận đấu nổi bật.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin trận đấu nổi bật", e));

        // Load data for Upcomming Matches RecyclerView
        db.collection("matches")
                .whereEqualTo("status","upcoming")
                .get()
                .addOnSuccessListener(task -> {
                    //
                    upcomingMatchList.clear();
                    if (task.isEmpty()) {
                        Toast.makeText(this, "Không có trận đấu nào sắp diễn ra.", Toast.LENGTH_SHORT).show();
                    }

                    // Duyệt qua từng match
                    for (QueryDocumentSnapshot matchDoc : task) {
                        Match match = matchDoc.toObject(Match.class);
                        upcomingMatchList.add(match);
                    }
                    upcomingMatchesAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin đội bóng: ", e));
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