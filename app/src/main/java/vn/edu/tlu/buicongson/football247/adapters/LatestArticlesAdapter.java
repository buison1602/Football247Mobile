package vn.edu.tlu.buicongson.football247.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

import vn.edu.tlu.buicongson.football247.R;
import vn.edu.tlu.buicongson.football247.models.Article;

public class LatestArticlesAdapter extends RecyclerView.Adapter<LatestArticlesAdapter.ArticleViewHolder> {

    private List<Article> articleList;
    private Context context;
    private FirebaseFirestore db;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LatestArticlesAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);

        loadArticleData(article.getId(), holder.titleArticleItem,
                holder.createdAtArticleItem, holder.imageHeaderArticleItem);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView titleArticleItem, createdAtArticleItem;
        ImageView imageHeaderArticleItem;

        public ArticleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            titleArticleItem = itemView.findViewById(R.id.title_article_item);
            createdAtArticleItem = itemView.findViewById(R.id.created_at_article_item);
            imageHeaderArticleItem = itemView.findViewById(R.id.image_header_article_item);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick((Article) ((LatestArticlesAdapter) getBindingAdapter()).articleList.get(position));
                }
            });
        }
    }

    private void loadArticleData(String articleId, TextView titleTextView, TextView createdAtTextView,
                                 ImageView imageHeaderArticleView) {
        if (articleId == null || articleId.isEmpty()) return;

        db.collection("news")
                .document(articleId)
                .get()
                .addOnSuccessListener(articleDocument -> {
                    if (articleDocument.exists()) {
                        String title = articleDocument.getString("title");
                        String headerImageUrl = articleDocument.getString("headerImageUrl");

                        Timestamp createdAt = articleDocument.getTimestamp("createdAt");
                        Date date = createdAt.toDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(date);

                        titleTextView.setText(title);

                        createdAtTextView.setText(formattedDate);

                        Glide.with(context)
                                .load(headerImageUrl)
                                .into(imageHeaderArticleView);
                    } else {
                        Log.w(TAG, "Không tìm thấy đội bóng với ID: " + articleId);
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin đội bóng: " + articleId, e));
    }
}
