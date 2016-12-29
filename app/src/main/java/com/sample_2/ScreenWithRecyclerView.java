package com.sample_2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;

public class ScreenWithRecyclerView extends Activity {
    private static final String TAG = "ScreenWithRecyclerView";

    private ReactPackage reactPackage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_recycler);
        RecyclerView listView = (RecyclerView) findViewById(R.id.list);
        final MyAdapter adapter = new MyAdapter(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
        reactPackage = new DefaultPackage(new Resizable() {
            @Override
            public void onSizeChanged(int newWidth, int newHeight) {
                Log.d(TAG, "onSizeChanged() called with: newWidth = ["
                        + newWidth
                        + "], newHeight = ["
                        + newHeight
                        + "]");
                adapter.setNewSize(newHeight, newWidth);
                adapter.notifyDataSetChanged();
            }
        });
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {
        ScreenWithRecyclerView activity;
        private int newHeight;
        private int newWidth;

        public MyAdapter(ScreenWithRecyclerView activity) {
            this.activity = activity;
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(activity)
                    .inflate(R.layout.item_without_react_root_view, parent, false);
            return new VH(itemView);
        }

        static class VH extends RecyclerView.ViewHolder {
            ReactRootView reactRootView;

            public VH(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {

            ReactInstanceManager mReactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(activity.getApplication())
                    .setBundleAssetName("main.jsbundle")
                    .setJSMainModuleName("index.android")
                    .addPackage(new MainReactPackage())
                    .addPackage(activity.reactPackage)
                    .setUseDeveloperSupport(false)
                    .setInitialLifecycleState(LifecycleState.RESUMED)
                    .build();

            // it appeared that we can not reuse react root view
            holder.reactRootView = new ReactRootView(activity);
            holder.reactRootView.startReactApplication(mReactInstanceManager, "Sample_2", null);
            holder.reactRootView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));

            ViewGroup.LayoutParams layoutParamsRoot = holder.itemView.getLayoutParams();
            layoutParamsRoot.height = newHeight;
            layoutParamsRoot.width = newWidth;
            holder.itemView.setLayoutParams(layoutParamsRoot);

            ((ViewGroup) holder.itemView).addView(holder.reactRootView);
        }

        public void setNewSize(int newHeight, int newWidth) {
            this.newHeight = newHeight;
            this.newWidth = newWidth;

            notifyDataSetChanged();
        }
    }
}
