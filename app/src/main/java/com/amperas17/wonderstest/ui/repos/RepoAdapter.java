package com.amperas17.wonderstest.ui.repos;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.pojo.Repo;
import com.amperas17.wonderstest.model.realm.RealmRepo;
import com.amperas17.wonderstest.ui.utils.AdapterItemClicksListener;

import io.realm.RealmResults;

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> {

    RealmResults<RealmRepo> repos;
    private AdapterItemClicksListener<Repo> listener;

    RepoAdapter(AdapterItemClicksListener<Repo> listener, RealmResults<RealmRepo> list) {
        this.repos = list;
        this.listener = listener;
    }

    RepoAdapter(AdapterItemClicksListener<Repo> listener) {
        this.listener = listener;
    }

    public void setRepos(RealmResults<RealmRepo> repos) {
        this.repos = repos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (repos != null && !repos.isEmpty())
            holder.bind(repos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if (repos != null) return repos.size();
        else return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRepoName;
        private TextView tvPrivacy;
        private TextView tvDescription;
        private RelativeLayout rlRepoContainer;

        private ViewHolder(final View itemView) {
            super(itemView);
            tvRepoName = (TextView) itemView.findViewById(R.id.tvRepoName);
            tvPrivacy = (TextView) itemView.findViewById(R.id.tvPrivacy);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            rlRepoContainer = (RelativeLayout) itemView.findViewById(R.id.rlRepoContainer);
        }

        private void bind(final RealmRepo repoItem, final AdapterItemClicksListener<Repo> listener) {

            tvRepoName.setText(repoItem.getName());

            if (repoItem.isPrivate()) {
                tvPrivacy.setText(itemView.getContext().getString(R.string.private_string));
                rlRepoContainer.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_purple));
            } else {
                tvPrivacy.setText(itemView.getContext().getString(R.string.public_string));
                rlRepoContainer.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(repoItem.toRepo());
                    return true;
                }
            });
        }

    }
}