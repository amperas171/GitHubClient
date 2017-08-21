package com.amperas17.wonderstest.ui.userinfo;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.Repo;
import com.amperas17.wonderstest.model.realm.RealmRepo;
import com.amperas17.wonderstest.ui.AdapterItemClickListener;

import io.realm.RealmResults;

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> {

    RealmResults<RealmRepo> repos;
    private AdapterItemClickListener<Repo> listener;

    RepoAdapter(AdapterItemClickListener<Repo> listener, RealmResults<RealmRepo> list) {
        this.repos = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bind(repos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRepoName;
        private TextView tvPrivacy;
        private TextView tvDescription;

        private ViewHolder(final View itemView) {
            super(itemView);
            tvRepoName = (TextView) itemView.findViewById(R.id.tvRepoName);
            tvPrivacy = (TextView) itemView.findViewById(R.id.tvPrivacy);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        }

        private void bind(final RealmRepo repoItem, final AdapterItemClickListener<Repo> listener) {

            tvRepoName.setText(repoItem.getName());

            if (repoItem.isPrivate()) {
                tvPrivacy.setText(itemView.getContext().getString(R.string.private_string));
                tvPrivacy.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
            } else {
                tvPrivacy.setText(itemView.getContext().getString(R.string.public_string));
                tvPrivacy.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
            }

            if (repoItem.getDescription() != null && !repoItem.getDescription().isEmpty())
                tvDescription.setText(repoItem.getDescription());
            else tvDescription.setText(itemView.getContext().getString(R.string.no_description));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(repoItem.toRepo());
                }
            });
        }

    }
}