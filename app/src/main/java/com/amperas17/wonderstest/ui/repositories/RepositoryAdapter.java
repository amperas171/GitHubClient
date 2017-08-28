package com.amperas17.wonderstest.ui.repositories;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.realm.RealmRepository;
import com.amperas17.wonderstest.ui.utils.AdapterItemClicksListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.ViewHolder> {

    RealmResults<RealmRepository> repositories;
    private AdapterItemClicksListener<Repository> listener;

    RepositoryAdapter(AdapterItemClicksListener<Repository> listener, RealmResults<RealmRepository> list) {
        this.repositories = list;
        this.listener = listener;
    }

    RepositoryAdapter(AdapterItemClicksListener<Repository> listener) {
        this.listener = listener;
    }

    public void setRepositories(RealmResults<RealmRepository> repositories) {
        this.repositories = repositories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repository, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (repositories != null && !repositories.isEmpty())
            holder.bind(repositories.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if (repositories != null) return repositories.size();
        else return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvRepositoryName) TextView tvRepositoryName;
        @BindView(R.id.tvPrivacy) TextView tvPrivacy;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.rlRepositoryContainer) RelativeLayout rlRepositoryContainer;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvRepositoryName = (TextView) itemView.findViewById(R.id.tvRepositoryName);
            tvPrivacy = (TextView) itemView.findViewById(R.id.tvPrivacy);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            rlRepositoryContainer = (RelativeLayout) itemView.findViewById(R.id.rlRepositoryContainer);
        }

        private void bind(final RealmRepository item, final AdapterItemClicksListener<Repository> listener) {

            tvRepositoryName.setText(item.getName());

            if (item.isPrivate()) {
                tvPrivacy.setText(itemView.getContext().getString(R.string.private_string));
                rlRepositoryContainer.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_purple));
            } else {
                tvPrivacy.setText(itemView.getContext().getString(R.string.public_string));
                rlRepositoryContainer.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
            }

            if (item.getDescription() != null && !item.getDescription().isEmpty())
                tvDescription.setText(item.getDescription());
            else tvDescription.setText(itemView.getContext().getString(R.string.no_description));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item.toRepository());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(item.toRepository());
                    return true;
                }
            });
        }

    }
}