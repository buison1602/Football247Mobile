package vn.edu.tlu.buicongson.football247.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.buicongson.football247.DetailArticleActivity;
import vn.edu.tlu.buicongson.football247.R;
import vn.edu.tlu.buicongson.football247.adapters.LatestArticlesAdapter;
import vn.edu.tlu.buicongson.football247.adapters.UpcomingMatchesAdapter;
import vn.edu.tlu.buicongson.football247.models.Article;
import vn.edu.tlu.buicongson.football247.models.Match;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerviewUpcomingMatches, recyclerviewLatestArticles;
    private UpcomingMatchesAdapter upcomingMatchesAdapter;
    private List<Match> upcomingMatchList = new ArrayList<>();
    private LatestArticlesAdapter latestArticlesAdapter;
    private List<Article> latestArticleList = new ArrayList<>();
    ImageView imageTeamLeft, imageTeamRight;
    TextView textTeamNameLeft, textTeamNameRight, textScore, textMatchTime;

    public HomeFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View (Quan trọng: phải dùng view.findViewById)
        imageTeamLeft = view.findViewById(R.id.image_team_left);
        imageTeamRight = view.findViewById(R.id.image_team_right);
        textTeamNameLeft = view.findViewById(R.id.text_team_name_left);
        textTeamNameRight = view.findViewById(R.id.text_team_name_right);
        textScore = view.findViewById(R.id.text_score);
        textMatchTime = view.findViewById(R.id.text_match_time);
        recyclerviewUpcomingMatches = view.findViewById(R.id.recyclerview_upcoming_matches);
        recyclerviewLatestArticles = view.findViewById(R.id.recyclerview_latest_news);

        // Setup cho RecyclerViews
        setupUpcomingMatchesRecyclerView();
        setupLatestNewsRecyclerView();

        // Gọi các hàm để lấy dữ liệu
        fetchFeaturedMatch();
        fetchUpcomingMatches();
        fetchLatestNews();
    }

    private void setupUpcomingMatchesRecyclerView() {
        upcomingMatchesAdapter = new UpcomingMatchesAdapter(upcomingMatchList, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerviewUpcomingMatches.setLayoutManager(layoutManager);
        recyclerviewUpcomingMatches.setAdapter(upcomingMatchesAdapter);
    }

    private void setupLatestNewsRecyclerView() {
        latestArticlesAdapter = new LatestArticlesAdapter(latestArticleList, requireContext());
        recyclerviewLatestArticles.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerviewLatestArticles.setAdapter(latestArticlesAdapter);

        latestArticlesAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(requireActivity(), DetailArticleActivity.class);
            intent.putExtra("article_object", article); // Giả sử Article đã implements Serializable
            startActivity(intent);
        });
    }

    private void fetchFeaturedMatch() {
        db.collection("matches")
                .whereEqualTo("isFeatured", true)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        Match featuredMatch = documentSnapshot.toObject(Match.class);
                        if(featuredMatch != null) {
                            textScore.setText(featuredMatch.getScore());
                            textMatchTime.setText(String.valueOf(featuredMatch.getMatch_timestamp())); // Chuyển sang String
                            loadTeamData(featuredMatch.getHome_team_id(), textTeamNameLeft, imageTeamLeft);
                            loadTeamData(featuredMatch.getAway_team_id(), textTeamNameRight, imageTeamRight);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy trận đấu nổi bật.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error getting featured match: ", task.getException());
                    }
                });
    }

    private void fetchUpcomingMatches() {
        db.collection("matches")
                .whereEqualTo("status","upcoming")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    upcomingMatchList.clear();
                    for (QueryDocumentSnapshot matchDoc : queryDocumentSnapshots) {
                        Match match = matchDoc.toObject(Match.class);
                        upcomingMatchList.add(match);
                    }
                    upcomingMatchesAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin trận sắp diễn ra: ", e));
    }

    private void fetchLatestNews() {
        // Đổi tên collection thành "articles" cho khớp với gợi ý trước
        db.collection("news")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    latestArticleList.clear();
                    for (QueryDocumentSnapshot articleDoc : queryDocumentSnapshots) {
                        Article article = articleDoc.toObject(Article.class);
                        latestArticleList.add(article);
                    }
                    latestArticlesAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin bài viết: ", e));
    }

    private void loadTeamData(String teamId, TextView nameTextView, ImageView logoImageView) {
        if (teamId == null || teamId.isEmpty()) return;

        db.collection("teams").document(teamId).get().addOnSuccessListener(teamDocument -> {
            if (teamDocument.exists()) {
                String teamName = teamDocument.getString("name");
                String logoUrl = teamDocument.getString("logoUrl");
                nameTextView.setText(teamName);
                // Dùng requireContext() cho an toàn
                if (isAdded()) {
                    Glide.with(requireContext()).load(logoUrl).into(logoImageView);
                }
            } else {
                Log.w(TAG, "Không tìm thấy đội bóng với ID: " + teamId);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin đội bóng: " + teamId, e));
    }
}