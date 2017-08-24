package com.amperas17.wonderstest.ui.issues;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.Issue;
import com.amperas17.wonderstest.model.realm.RealmIssue;
import com.amperas17.wonderstest.ui.AdapterItemClickListener;

import org.apmem.tools.layouts.FlowLayout;

import io.realm.RealmResults;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {

    private RealmResults<RealmIssue> issues;
    private AdapterItemClickListener<Issue> listener;

    public IssueAdapter(AdapterItemClickListener<Issue> listener, RealmResults<RealmIssue> list) {
        this.issues = list;
        this.listener = listener;
    }

    public IssueAdapter(AdapterItemClickListener<Issue> listener) {
        this.listener = listener;
    }

    public void setIssues(RealmResults<RealmIssue> issues) {
        this.issues = issues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (issues != null && !issues.isEmpty())
            holder.bind(issues.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if (issues != null) return issues.size();
        else return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvIssueTitle;
        private TextView tvBody;
        private TextView tvNoLabels;
        private FlowLayout fl;

        private ViewHolder(final View itemView) {
            super(itemView);
            tvIssueTitle = (TextView) itemView.findViewById(R.id.tvIssueTitle);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvNoLabels = (TextView) itemView.findViewById(R.id.tvNoLabels);
            fl = (FlowLayout) itemView.findViewById(R.id.flLabelsContainer);
        }

        private void bind(final RealmIssue issueItem, final AdapterItemClickListener<Issue> listener) {

            tvIssueTitle.setText(issueItem.getTitle());

            if (issueItem.getBody() != null && !issueItem.getBody().isEmpty())
                tvBody.setText(issueItem.getBody());
            else tvBody.setText(itemView.getContext().getString(R.string.no_description));

            if (issueItem.getLabels().isEmpty()) {
                tvNoLabels.setVisibility(View.VISIBLE);
                fl.removeAllViews();
            } else {
                tvNoLabels.setVisibility(View.GONE);
                LayoutInflater li = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                fl.removeAllViews();
                for (int i = 0; i < issueItem.getLabels().size(); i++) {
                    CardView view = (CardView) li.inflate(R.layout.item_label, fl, false);
                    TextView textView = (TextView) view.findViewById(R.id.tvLabelName);
                    textView.setText(issueItem.getLabels().get(i).getName());
                    String colorStr = issueItem.getLabels().get(i).getColor();
                    if (colorStr.equals("ffffff")) {
                        textView.setTextColor(Color.BLACK);
                    } else {
                        textView.setTextColor(Color.WHITE);
                    }
                    view.setCardBackgroundColor(Color.parseColor('#' + colorStr));
                    fl.addView(view);
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(issueItem.toIssue());
                }
            });
        }

    }
}
