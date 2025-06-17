package vn.edu.tlu.buicongson.football247.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.buicongson.football247.R;
import vn.edu.tlu.buicongson.football247.adapters.ArticleNewsAdapter;
import vn.edu.tlu.buicongson.football247.adapters.LatestArticlesAdapter;
import vn.edu.tlu.buicongson.football247.models.Article;

public class NewsFragment extends Fragment {

    private FirebaseFirestore db;
    private EditText editTextSearch;
    private RecyclerView recyclerviewNews;
    private ArticleNewsAdapter articleNewsAdapter;
    private List<Article> listArticle = new ArrayList<>();

    public NewsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        editTextSearch = view.findViewById(R.id.edit_text_search);
        recyclerviewNews = view.findViewById(R.id.recyclerview_news);

        // Setup cho RecyclerViews
        articleNewsAdapter = new ArticleNewsAdapter(listArticle, requireContext());
        recyclerviewNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerviewNews.setAdapter(articleNewsAdapter);

        articleNewsAdapter.setOnItemClickListener(article -> {
            Bundle bundle = new Bundle();
            bundle.putString("articleId", article.getId());

            Navigation.findNavController(requireView()).navigate(R.id.action_news_to_article_detail, bundle);
        });

        fetchLatestNews();

        // Logic cho tìm kiếm
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = editTextSearch.getText().toString().trim();
                    searchForArticles(query);
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchLatestNews() {
        db.collection("news")
                .limit(10)
                .get()
                .addOnSuccessListener(articles -> {
                    listArticle.clear();
                    for (QueryDocumentSnapshot articleDoc : articles) {
                        Article article = articleDoc.toObject(Article.class);
                        listArticle.add(article);
                    }
                    articleNewsAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy bài viết: ", e));
    }

    private void searchForArticles(String query) {
        if (query.isEmpty()) {
//            fetchLatestNews();
            Toast.makeText(getContext(), "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
        } else {
            String formattedQuery = query.toLowerCase();

            db.collection("news")
                    .whereArrayContains("keywords", formattedQuery)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        listArticle.clear();
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getContext(), "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot articleDoc : queryDocumentSnapshots) {
                                Article article = articleDoc.toObject(Article.class);
                                listArticle.add(article);
                            }
                        }
                        articleNewsAdapter.notifyDataSetChanged();
                    });
        }
    }
}