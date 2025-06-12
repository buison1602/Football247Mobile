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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import vn.edu.tlu.buicongson.football247.R;
import vn.edu.tlu.buicongson.football247.models.Match;

public class UpcomingMatchesAdapter extends RecyclerView.Adapter<UpcomingMatchesAdapter.MatchViewHolder> {

    private List<Match> matchList;
    private Context context;
    private FirebaseFirestore db;

    public UpcomingMatchesAdapter(List<Match> matchList, Context context) {
        this.matchList = matchList;
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }


    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upcoming_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.text_match_time_item.setText(match.getMatch_timestamp());

        loadTeamData(match.getHome_team_id(), holder.text_home_name_item, holder.image_home_logo_item);
        loadTeamData(match.getAway_team_id(), holder.text_away_name_item, holder.image_away_logo_item);
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView text_match_time_item, text_home_name_item, text_away_name_item;
        ImageView image_home_logo_item, image_away_logo_item;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            text_match_time_item = itemView.findViewById(R.id.text_match_time_item);
            text_home_name_item = itemView.findViewById(R.id.text_home_name_item);
            text_away_name_item = itemView.findViewById(R.id.text_away_name_item);
            image_home_logo_item = itemView.findViewById(R.id.image_home_logo_item);
            image_away_logo_item = itemView.findViewById(R.id.image_away_logo_item);
        }
    }

    private void loadTeamData(String teamId, TextView nameTextView, ImageView logoImageView) {
        if (teamId == null || teamId.isEmpty()) return; // Kiểm tra an toàn

        db.collection("teams")
                .document(teamId)
                .get()
                .addOnSuccessListener(teamDocument -> {
                    if (teamDocument.exists()) {
                        String teamName = teamDocument.getString("name");
                        String logoUrl = teamDocument.getString("logoUrl");

                        nameTextView.setText(teamName);

                        Glide.with(context)
                                .load(logoUrl)
                                .into(logoImageView);
                    } else {
                        Log.w(TAG, "Không tìm thấy đội bóng với ID: " + teamId);
                    }
        }).addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lấy thông tin đội bóng: " + teamId, e));
    }
}
