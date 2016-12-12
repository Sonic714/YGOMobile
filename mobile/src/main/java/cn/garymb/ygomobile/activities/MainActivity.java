package cn.garymb.ygomobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.ServerLists;
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.core.ResCheckTask;
import cn.garymb.ygomobile.core.YGOStarter;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.DialogPlus;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.SettingsActivity;

import static cn.garymb.ygomobile.Constants.ACTION_OPEN_DECK;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private boolean enableStart;
    private RecyclerView mServerList;
    private AppsSettings mAppsSettings;
    private ServerLists.ServerAdapater mServerAdapater;
    protected DrawerLayout mDrawerlayout;
    private View editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setExitAnimEnable(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = bind(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerlayout = bind(R.id.drawer_layout);
        editView = bind(R.id.button_edit);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, toolbar, R.string.search_open, R.string.search_close);
        mDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = bind(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(this);
        mAppsSettings = AppsSettings.get();
        mServerList = bind(R.id.list_server);
        mServerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mServerAdapater = ServerLists.attch(this, mServerList);
        //加载服务器列表
        mServerAdapater.loadData();
        //侧边
        navigationView.getHeaderView(0).findViewById(R.id.nav_donation).setOnClickListener((v)->{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ALIPAY_URL));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        YGOStarter.onCreated(this);
        mServerAdapater.setOnEditListener((edit)->{
            if(edit){
                editView.setVisibility(View.VISIBLE);
            }else {
                editView.setVisibility(View.GONE);
            }
        });
        editView.setOnClickListener((v)->{
            mServerAdapater.setEditMode(false);
        });
        //资源复制
        checkResourceDownload((error, isNew) -> {
            if (error < 0) {
                enableStart = false;
            } else {
                enableStart = true;
            }
            if (isNew) {
                new DialogPlus(this)
                        .setTitle(getString(R.string.settings_about_change_log))
                        .loadUrl("file:///android_asset/changelog.html")
                        .show();
            }else{
                Intent intent=getIntent();
                if(ACTION_OPEN_DECK.equals(intent.getAction())){
                    File deck=new File(intent.getData().getPath());
                    Intent startdeck = new Intent(this, DeckManagerActivity.class);
                    startdeck.putExtra(Intent.EXTRA_TEXT, deck.getAbsoluteFile());
                    startActivity(startdeck);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        YGOStarter.onResumed(this);
    }

    @Override
    protected void onDestroy() {
        YGOStarter.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (doMenu(item.getItemId())) {
            closeDrawer();
            return true;
        }
        return false;
    }

    private void closeDrawer() {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (doMenu(item.getItemId())) {
            closeDrawer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean doMenu(int id) {
        switch (id) {
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_game:
                if (enableStart) {
                    YGOStarter.startGame(this, null);
                } else {
                    VUiKit.show(this, R.string.dont_start_game);
                }
                break;
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_quit: {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.quit_tip);
                builder.setMessageGravity(Gravity.CENTER_HORIZONTAL);
                builder.setLeftButtonListener((dlg, s) -> {
                    dlg.dismiss();
                    finish();
                });
                builder.show();
            }
            break;
            case R.id.action_add_server:
                mServerAdapater.addServer();
                break;
            case R.id.action_card_search:
                startActivity(new Intent(this, CardSearchAcitivity.class));
                break;
            case R.id.action_deck_manager:
                startActivity(new Intent(this, DeckManagerActivity.class));
                break;
            case R.id.action_help: {
                WebActivity.open(this, getString(R.string.help), Constants.URL_HELP);
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_HELP));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                try {
//                    startActivity(intent);
//                } catch (Exception e) {
//                    Toast.makeText(this, R.string.no_webbrowser, Toast.LENGTH_SHORT).show();
//                }
            }
            break;
            default:
                return false;
        }
        return true;
    }

    private void checkResourceDownload(ResCheckTask.ResCheckListener listener) {
        ResCheckTask task = new ResCheckTask(this, listener);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    long exitLasttime = 0;

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if (System.currentTimeMillis() - exitLasttime <= 3000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.back_tip, Toast.LENGTH_SHORT).show();
            exitLasttime = System.currentTimeMillis();
        }
    }

}
