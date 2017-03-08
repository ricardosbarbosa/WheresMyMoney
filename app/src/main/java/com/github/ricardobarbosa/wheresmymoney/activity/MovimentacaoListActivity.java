package com.github.ricardobarbosa.wheresmymoney.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.facebook.stetho.Stetho;
import com.github.clans.fab.FloatingActionButton;
import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.adapters.MovimentacaoCursorAdapter;
import com.github.ricardobarbosa.wheresmymoney.adapters.OnStartDragListener;
import com.github.ricardobarbosa.wheresmymoney.adapters.SimpleItemTouchHelperCallback;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMDbHelper;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.model.EnumMovimentacaoTipo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.*;

/**
 * An activity representing a list of Movimentacoes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovimentacaoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovimentacaoListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,
        LoaderManager.LoaderCallbacks<Cursor>, OnStartDragListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private MovimentacaoCursorAdapter mAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private ItemTouchHelper mItemTouchHelper;

    private static final int FORECAST_LOADER = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimentacao_list);

        Stetho.initializeWithDefaults(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        FloatingActionButton fabDespesas = (FloatingActionButton) findViewById(R.id.fab_despesas);
        fabDespesas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haContasCadastradas(view)) {
                    Intent intent = new Intent(view.getContext(), MovimentacaoFormActivity.class);
                    intent.putExtra("tipo", EnumMovimentacaoTipo.DESPESA.name());
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton fabReceitas = (FloatingActionButton) findViewById(R.id.fab_receitas);
        fabReceitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haContasCadastradas(view)) {
                    Intent intent = new Intent(view.getContext(), MovimentacaoFormActivity.class);
                    intent.putExtra("tipo", EnumMovimentacaoTipo.RECEITA.name());
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton fabTransferencia = (FloatingActionButton) findViewById(R.id.fab_transferencia);
        fabTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haContasCadastradas(view)) {
                    Intent intent = new Intent(view.getContext(), TransferenciaFormActivity.class);
                    startActivity(intent);
                }
            }
        });


        View view = findViewById(R.id.movimentacao_list);
        assert view != null;
        recyclerView = (RecyclerView) view;
        getSupportLoaderManager().initLoader(FORECAST_LOADER, null, this);
        mAdapter = new MovimentacaoCursorAdapter(this, null, mTwoPane, this);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        if (findViewById(R.id.movimentacao_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        TextView nameUserTextView = (TextView) headerLayout.findViewById(R.id.nameUserTextView);
        TextView emailUserTextView = (TextView) headerLayout.findViewById(R.id.emailUserTextView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName() ;
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            nameUserTextView.setText(name);
            emailUserTextView.setText(email);

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
        else {
            Intent intent = new Intent(this, LoginActivity2.class);
            this.startActivity(intent);
        }

    }

    private boolean haContasCadastradas(View view) {
//        WIMMDbHelper mOpenHelper = new WIMMDbHelper(this);
//        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor cursor = getContentResolver().query(ContaEntry.CONTENT_URI, null, null, null, null);
        boolean temConta = false;
        if (cursor != null) {
            temConta = cursor.moveToFirst();
            cursor.close();
        }


        if (!temConta) {
            Snackbar snackbar = Snackbar.make(view, getResources().getText(R.string.falta_criar_conta), Snackbar.LENGTH_LONG);
            snackbar.setAction(getResources().getText(R.string.cadastrar_conta), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ContaFormActivity.class);
                    startActivity(intent);
                }
            });
            snackbar.show();
        }

        return temConta;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contas) {
            Intent intent = new Intent(this, ContaListActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_categorias) {
            Intent intent = new Intent(this, CategoriaListActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Snackbar snackbar = Snackbar.make(this.recyclerView, "Ao sair seus dados de contas e movimentacoes serão perdidos.", Snackbar.LENGTH_LONG);
            snackbar.setAction("Confirmar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    WIMMDbHelper mOpenHelper = new WIMMDbHelper(v.getContext());
                    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    db.execSQL("delete from "+ TransferenciaEntry.TABLE_NAME);
                    db.execSQL("delete from "+ MovimentacaoEntry.TABLE_NAME);
                    db.execSQL("delete from "+ CategoriaEntry.TABLE_NAME);
                    db.execSQL("delete from "+ ContaEntry.TABLE_NAME);

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Intent intent = new Intent(v.getContext(), LoginActivity2.class);
                    startActivity(intent);
                }
            });
            snackbar.show();


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovimentacaoEntry.COLUMN_DATA + " DESC";

        return new CursorLoader(this,
                MovimentacaoEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            recyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
