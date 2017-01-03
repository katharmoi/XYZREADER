package com.example.xyzreader.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.Collections;
import java.util.List;

public class ShareActivity extends AppCompatActivity {
    private FrameLayout mShareMainLayout;
    private View mFabHolder;
    private RecyclerView mRecycler;
    private ImageView mClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share);
        mShareMainLayout = (FrameLayout) findViewById(R.id.share_main);
        mFabHolder = findViewById(R.id.fab_holder);
        mClose = (ImageView) findViewById(R.id.share_close);
        mClose.setVisibility(View.INVISIBLE);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCloseClicked();
            }
        });
        PackageManager pm = getPackageManager();
        Intent shareIntent = new Intent(Intent.ACTION_SEND,null);
        shareIntent.setType("text/plain");
        List<ResolveInfo> apps=pm.queryIntentActivities(shareIntent, 0);

        Collections.sort(apps,
                new ResolveInfo.DisplayNameComparator(pm));

        mRecycler = (RecyclerView) findViewById(R.id.share_recycler_view);
        mRecycler.setAdapter(new ShareAdapter(pm,apps));
        GridLayoutManager glm = new GridLayoutManager(this,3,
                GridLayoutManager.VERTICAL,false);
        mRecycler.setLayoutManager(glm);
        if(savedInstanceState != null){
            mRecycler.setVisibility(View.VISIBLE);
            mFabHolder.setVisibility(View.INVISIBLE);
            mShareMainLayout.setBackgroundColor(getResources().getColor(R.color.accent));
            mClose.setVisibility(View.VISIBLE);
        }else {
            mRecycler.setVisibility(View.INVISIBLE);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation();
            setupExitAnimation();
        } else {
            initViews();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(mShareMainLayout);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    private void animateRevealShow(final View viewRoot) {
        int cx = viewRoot.getWidth()/2;
        int cy = viewRoot.getHeight()/2;
        Animations.revealEffectShow(this,mShareMainLayout, mFabHolder.getWidth() / 2, R.color.accent,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    private void initViews() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                animation.setDuration(300);
                mRecycler.startAnimation(animation);
                mClose.setAnimation(animation);
                mRecycler.setVisibility(View.VISIBLE);
                mClose.setVisibility(View.VISIBLE);
                mFabHolder.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mFabHolder.setVisibility(View.VISIBLE);
        Animations.revealEffectHide(this, mShareMainLayout, R.color.accent, mFabHolder.getWidth() / 2,
                new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        backPressed();
                    }

                    @Override
                    public void onRevealShow() {

                    }
                });
    }

    public void onCloseClicked() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onBackPressed();
        } else {
            backPressed();
        }
    }

    private void backPressed() {
        super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimation() {
        Fade fade = new Fade();
        getWindow().setReturnTransition(fade);
        fade.setDuration(getResources().getInteger(R.integer.animation_duration));
    }

    class ShareAdapter extends RecyclerView.Adapter<ShareHolder>{
        private PackageManager pm ;
        private List<ResolveInfo> apps;

        public ShareAdapter(PackageManager pm,List<ResolveInfo> apps){
            this.pm = pm;
            this.apps = apps;
        }

        @Override
        public ShareHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.share_app_item,parent,false);
            return  new ShareHolder(view);
        }

        @Override
        public void onBindViewHolder(ShareHolder holder, final int position) {
            holder.mThum.setImageDrawable(apps.get(position).loadIcon(pm));
            holder.mTitle.setText(apps.get(position).loadLabel(pm));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResolveInfo appInfo=apps.get(position);
                    ActivityInfo activity=appInfo.activityInfo;
                    ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                            activity.name);
                    Intent appOpenIntent=new Intent(Intent.ACTION_MAIN);

                    appOpenIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    appOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    appOpenIntent.setComponent(name);

                    startActivity(appOpenIntent);
                }
            });

        }

        @Override
        public int getItemCount() {

            return apps.size();
        }


    }

    public static class ShareHolder extends RecyclerView.ViewHolder{
        private ImageView mThum;
        private TextView mTitle;

        public ShareHolder(View itemView) {
            super(itemView);
            mThum = (ImageView) itemView.findViewById(R.id.app_thumb_view);
            mTitle = (TextView) itemView.findViewById(R.id.app_title_view);
        }
    }
}
