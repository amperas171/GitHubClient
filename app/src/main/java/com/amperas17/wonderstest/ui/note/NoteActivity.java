package com.amperas17.wonderstest.ui.note;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.amperas17.wonderstest.R;
import com.amperas17.wonderstest.model.pojo.Issue;
import com.amperas17.wonderstest.model.pojo.Repo;
import com.amperas17.wonderstest.model.realm.RealmNote;

import io.realm.Realm;

public class NoteActivity extends AppCompatActivity {
    public static final String ITEM_KEY = "itemKey";

    private Realm realm;

    private EditText etTitle;
    private EditText etText;

    public static Intent newIntent(Context context, Parcelable itemKey) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(ITEM_KEY, itemKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        realm = Realm.getDefaultInstance();

        getSupportActionBar().setTitle(getString(R.string.note_to) + " " + getItemName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etText = (EditText) findViewById(R.id.etText);

        fillFields();

        etTitle.addTextChangedListener(noteWatcher);
        etText.addTextChangedListener(noteWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillFields(){
        RealmNote note = getNote();
        if (note != null) {
            etTitle.setText(note.getTitle());
            etText.setText(note.getText());
        }
    }

    private String getItemKey() {
        Parcelable item = getIntent().getParcelableExtra(ITEM_KEY);
        if (item instanceof Repo) return ((Repo) item).getName();
        if (item instanceof Issue) return ((Issue) item).getId().toString();
        return "";
    }

    private String getItemName() {
        Parcelable item = getIntent().getParcelableExtra(ITEM_KEY);
        if (item instanceof Repo) return ((Repo) item).getName();
        if (item instanceof Issue) return ((Issue) item).getTitle();
        return "";
    }

    private RealmNote getNote() {
        return realm.where(RealmNote.class).equalTo(RealmNote.REPO_KEY, getItemKey()).findFirst();
    }

    private void setNoteValues() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmNote note = new RealmNote(getItemKey(), etTitle.getText().toString(), etText.getText().toString());
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insertOrUpdate(note);
                    }
                });
            }
        });
    }

    private TextWatcher noteWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            setNoteValues();
        }
    };

}
