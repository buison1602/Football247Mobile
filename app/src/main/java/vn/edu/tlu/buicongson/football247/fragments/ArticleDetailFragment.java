package vn.edu.tlu.buicongson.football247.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import vn.edu.tlu.buicongson.football247.R;
import vn.edu.tlu.buicongson.football247.models.Article;

public class ArticleDetailFragment extends Fragment {

    private FirebaseFirestore db;
    private String articleId;

    private ImageView imageHeader, buttonBack ;
    private LinearLayout imageContainer;
    private TextView textTitle, textContent;

    public ArticleDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nhận argument từ Bundle
        if (getArguments() != null) {
            articleId = getArguments().getString("articleId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        textTitle = view.findViewById(R.id.text_detail_title);
        textContent = view.findViewById(R.id.text_detail_content);
        imageContainer = view.findViewById(R.id.image_container);

        buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Nếu có articleId, tiến hành lấy dữ liệu
        if (articleId != null) {
            fetchArticleDetails();
        } else {
            Toast.makeText(requireContext(), "Lỗi: Không có thông tin bài viết", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchArticleDetails() {
        db.collection("news").document(articleId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Article article = documentSnapshot.toObject(Article.class);
                        if (article != null) {
                            textTitle.setText(article.getTitle());
                            textContent.setText(article.getContent());

                            if (article.getImageUrls() != null && !article.getImageUrls().isEmpty()) {
                                displayImages(article.getImageUrls(), article.getContent());
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayImages(List<String> imageUrls, String content) {
        imageContainer.removeAllViews();

        for (String url : imageUrls) {
            ImageView imageView = new ImageView(requireContext());
            TextView textView = new TextView(requireContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            imageView.setLayoutParams(params);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            textView.setLayoutParams(params);

            Glide.with(this).load(url).into(imageView);
            textView.setText(content);

            imageContainer.addView(imageView);
            imageContainer.addView(textView);
        }
    }
}